package meteordevelopment.meteorclient.systems.modules.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import it.unimi.dsi.fastutil.ints.IntFloatImmutablePair;
import java.nio.ByteBuffer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.ResolutionChangedEvent;
import meteordevelopment.meteorclient.events.render.RenderAfterWorldEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.renderer.FixedUniformStorage;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.listeners.ConsumerListener;
import net.minecraft.class_11280;
import net.minecraft.class_408;
import net.minecraft.class_437;
import net.minecraft.class_465;

public class Blur extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgScreens;
   private final IntFloatImmutablePair[] strengths;
   private final Setting<Integer> strength;
   private final Setting<Integer> fadeTime;
   private final Setting<Boolean> meteor;
   private final Setting<Boolean> inventories;
   private final Setting<Boolean> chat;
   private final Setting<Boolean> other;
   private final GpuTextureView[] fbos;
   private GpuBufferSlice[] ubos;
   private boolean enabled;
   private long fadeEndAt;
   private float previousOffset;
   private static final int UNIFORM_SIZE = (new Std140SizeCalculator()).putVec2().putFloat().get();
   private static final FixedUniformStorage<BlurUniformData> UNIFORM_STORAGE;

   public Blur() {
      super(Categories.Render, "blur", "Blurs background when in GUI screens.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgScreens = this.settings.createGroup("Screens");
      this.strengths = new IntFloatImmutablePair[]{IntFloatImmutablePair.of(1, 1.25F), IntFloatImmutablePair.of(1, 2.25F), IntFloatImmutablePair.of(2, 2.0F), IntFloatImmutablePair.of(2, 3.0F), IntFloatImmutablePair.of(2, 4.25F), IntFloatImmutablePair.of(3, 2.5F), IntFloatImmutablePair.of(3, 3.25F), IntFloatImmutablePair.of(3, 4.25F), IntFloatImmutablePair.of(3, 5.5F), IntFloatImmutablePair.of(4, 3.25F), IntFloatImmutablePair.of(4, 4.0F), IntFloatImmutablePair.of(4, 5.0F), IntFloatImmutablePair.of(4, 6.0F), IntFloatImmutablePair.of(4, 7.25F), IntFloatImmutablePair.of(4, 8.25F), IntFloatImmutablePair.of(5, 4.5F), IntFloatImmutablePair.of(5, 5.25F), IntFloatImmutablePair.of(5, 6.25F), IntFloatImmutablePair.of(5, 7.25F), IntFloatImmutablePair.of(5, 8.5F)};
      this.strength = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("strength")).description("How strong the blur should be.")).defaultValue(5)).min(1).max(20).sliderRange(1, 20).build());
      this.fadeTime = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("fade-time")).description("How long the fade will last in milliseconds.")).defaultValue(100)).min(0).sliderMax(500).build());
      this.meteor = this.sgScreens.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("meteor")).description("Applies blur to Meteor screens.")).defaultValue(true)).build());
      this.inventories = this.sgScreens.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("inventories")).description("Applies blur to inventory screens.")).defaultValue(true)).build());
      this.chat = this.sgScreens.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("chat")).description("Applies blur when in chat.")).defaultValue(false)).build());
      this.other = this.sgScreens.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("other")).description("Applies blur to all other screen types.")).defaultValue(true)).build());
      this.fbos = new GpuTextureView[6];
      this.previousOffset = -1.0F;

      for(int i = 0; i < this.fbos.length; ++i) {
         this.fbos[i] = this.createFbo(i);
      }

      MeteorClient.EVENT_BUS.subscribe(new ConsumerListener(ResolutionChangedEvent.class, (event) -> {
         for(int i = 0; i < this.fbos.length; ++i) {
            if (this.fbos[i] != null) {
               this.fbos[i].close();
            }

            this.fbos[i] = this.createFbo(i);
         }

         this.previousOffset = -1.0F;
      }));
      MeteorClient.EVENT_BUS.subscribe(new ConsumerListener(RenderAfterWorldEvent.class, (event) -> this.onRenderAfterWorld()));
   }

   private GpuTextureView createFbo(int i) {
      double scale = (double)1.0F / Math.pow((double)2.0F, (double)i);
      int width = (int)((double)this.mc.method_22683().method_4489() * scale);
      int height = (int)((double)this.mc.method_22683().method_4506() * scale);
      return RenderSystem.getDevice().createTextureView(RenderSystem.getDevice().createTexture("Blur - " + i, 15, TextureFormat.RGBA8, width, height, 1, 1));
   }

   private void onRenderAfterWorld() {
      boolean shouldRender = this.shouldRender();
      long time = System.currentTimeMillis();
      if (this.enabled) {
         if (!shouldRender) {
            if (this.fadeEndAt == -1L) {
               this.fadeEndAt = System.currentTimeMillis() + (long)(Integer)this.fadeTime.get();
            }

            if (time >= this.fadeEndAt) {
               this.enabled = false;
               this.fadeEndAt = -1L;
            }
         }
      } else if (shouldRender) {
         this.enabled = true;
         this.fadeEndAt = System.currentTimeMillis() + (long)(Integer)this.fadeTime.get();
      }

      if (this.enabled) {
         double progress = (double)1.0F;
         if (time < this.fadeEndAt) {
            if (shouldRender) {
               progress = (double)1.0F - (double)(this.fadeEndAt - time) / ((Integer)this.fadeTime.get()).doubleValue();
            } else {
               progress = (double)(this.fadeEndAt - time) / ((Integer)this.fadeTime.get()).doubleValue();
            }
         } else {
            this.fadeEndAt = -1L;
         }

         IntFloatImmutablePair strength = this.strengths[(int)((double)((Integer)this.strength.get() - 1) * progress)];
         int iterations = strength.leftInt();
         float offset = strength.rightFloat();
         if (this.previousOffset != offset) {
            this.updateUniforms(offset);
            this.previousOffset = offset;
         }

         this.renderToFbo(this.fbos[0], this.mc.method_1522().method_71639(), MeteorRenderPipelines.BLUR_DOWN, this.ubos[0]);

         for(int i = 0; i < iterations; ++i) {
            this.renderToFbo(this.fbos[i + 1], this.fbos[i], MeteorRenderPipelines.BLUR_DOWN, this.ubos[i + 1]);
         }

         for(int i = iterations; i >= 1; --i) {
            this.renderToFbo(this.fbos[i - 1], this.fbos[i], MeteorRenderPipelines.BLUR_UP, this.ubos[i - 1]);
         }

         MeshRenderer.begin().attachments(this.mc.method_1522()).pipeline(MeteorRenderPipelines.BLUR_PASSTHROUGH).fullscreen().sampler("u_Texture", this.fbos[0], RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR)).end();
      }
   }

   private void renderToFbo(GpuTextureView targetFbo, GpuTextureView sourceTexture, RenderPipeline pipeline, GpuBufferSlice ubo) {
      MeshRenderer.begin().attachments(targetFbo, (GpuTextureView)null).pipeline(pipeline).fullscreen().uniform("BlurData", ubo).sampler("u_Texture", sourceTexture, RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR)).end();
   }

   private boolean shouldRender() {
      if (!this.isActive()) {
         return false;
      } else {
         class_437 screen = this.mc.field_1755;
         if (screen instanceof WidgetScreen) {
            return (Boolean)this.meteor.get();
         } else if (screen instanceof class_465) {
            return (Boolean)this.inventories.get();
         } else if (screen instanceof class_408) {
            return (Boolean)this.chat.get();
         } else {
            return screen != null ? (Boolean)this.other.get() : false;
         }
      }
   }

   private void updateUniforms(float offset) {
      UNIFORM_STORAGE.clear();
      BlurUniformData[] uboData = new BlurUniformData[6];

      for(int i = 0; i < uboData.length; ++i) {
         GpuTextureView fbo = this.fbos[i];
         uboData[i] = new BlurUniformData(0.5F / (float)fbo.getWidth(0), 0.5F / (float)fbo.getHeight(0), offset);
      }

      this.ubos = UNIFORM_STORAGE.writeAll(uboData);
   }

   static {
      UNIFORM_STORAGE = new FixedUniformStorage<BlurUniformData>("Meteor - Blur UBO", UNIFORM_SIZE, 6);
   }

   private static record BlurUniformData(float halfTexelSizeX, float halfTexelSizeY, float offset) implements class_11280.class_11281 {
      public void method_71104(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer).putVec2(this.halfTexelSizeX, this.halfTexelSizeY).putFloat(this.offset);
      }
   }
}
