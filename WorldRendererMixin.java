package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Function;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.IEntityRenderState;
import meteordevelopment.meteorclient.mixininterface.IWorldRenderer;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BlockSelection;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import meteordevelopment.meteorclient.utils.OutlineRenderCommandQueue;
import meteordevelopment.meteorclient.utils.render.NoopImmediateVertexConsumerProvider;
import meteordevelopment.meteorclient.utils.render.NoopOutlineVertexConsumerProvider;
import meteordevelopment.meteorclient.utils.render.WrapperImmediateVertexConsumerProvider;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.render.postprocess.EntityShader;
import meteordevelopment.meteorclient.utils.render.postprocess.PostProcessShaders;
import net.minecraft.class_10017;
import net.minecraft.class_11658;
import net.minecraft.class_11659;
import net.minecraft.class_11684;
import net.minecraft.class_12074;
import net.minecraft.class_12077;
import net.minecraft.class_12078;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_638;
import net.minecraft.class_761;
import net.minecraft.class_897;
import net.minecraft.class_898;
import net.minecraft.class_9779;
import net.minecraft.class_9922;
import net.minecraft.class_9925;
import net.minecraft.class_9960;
import net.minecraft.class_9976;
import net.minecraft.class_9978;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_761.class})
public abstract class WorldRendererMixin implements IWorldRenderer {
   @Unique
   private NoRender noRender;
   @Unique
   private ESP esp;
   @Unique
   private final OutlineRenderCommandQueue outlineRenderCommandQueue = new OutlineRenderCommandQueue();
   @Unique
   private class_4597 provider;
   @Unique
   private class_11684 renderDispatcher;
   @Shadow
   private class_276 field_53080;
   @Shadow
   @Final
   private class_9960 field_53081;
   @Shadow
   @Final
   private class_898 field_4109;
   @Unique
   private Stack<class_276> framebufferStack;
   @Unique
   private Stack<class_9925<class_276>> framebufferHandleStack;

   @Inject(
      method = {"method_3244"},
      at = {@At("TAIL")}
   )
   private void onSetWorld(class_638 world, CallbackInfo ci) {
      this.esp = (ESP)Modules.get().get(ESP.class);
      this.noRender = (NoRender)Modules.get().get(NoRender.class);
   }

   @Inject(
      method = {"method_22979"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onCheckEmpty(class_4587 matrixStack, CallbackInfo info) {
      info.cancel();
   }

   @Inject(
      method = {"method_22712"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onDrawHighlightedBlockOutline(class_4587 matrices, class_4588 vertexConsumer, double x, double y, double z, class_12074 state, int i, float f, CallbackInfo ci) {
      if (Modules.get().isActive(BlockSelection.class)) {
         ci.cancel();
      }

   }

   @ModifyArg(
      method = {"method_22710"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_761;method_74752(Lnet/minecraft/class_4184;Lnet/minecraft/class_4604;Z)V"
),
      index = 2
   )
   private boolean renderSetupTerrainModifyArg(boolean spectator) {
      return Modules.get().isActive(Freecam.class) || spectator;
   }

   @WrapWithCondition(
      method = {"method_62216"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_9976;method_62320(Lnet/minecraft/class_4597;Lnet/minecraft/class_243;Lnet/minecraft/class_12077;)V"
)}
   )
   private boolean shouldRenderPrecipitation(class_9976 instance, class_4597 vertexConsumers, class_243 pos, class_12077 weatherRenderState) {
      return !this.noRender.noWeather();
   }

   @WrapWithCondition(
      method = {"method_62216"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_9978;method_62322(Lnet/minecraft/class_12078;Lnet/minecraft/class_243;DD)V"
)}
   )
   private boolean shouldRenderWorldBorder(class_9978 instance, class_12078 state, class_243 cameraPos, double viewDistanceBlocks, double farPlaneDistance) {
      return !this.noRender.noWorldBorder();
   }

   @Inject(
      method = {"method_43788(Lnet/minecraft/class_4184;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void hasBlindnessOrDarkness(class_4184 camera, CallbackInfoReturnable<Boolean> info) {
      if (this.noRender.noBlindness() || this.noRender.noDarkness()) {
         info.setReturnValue((Object)null);
      }

   }

   @Inject(
      method = {"method_22710"},
      at = {@At("HEAD")}
   )
   private void onRenderHead(class_9922 allocator, class_9779 tickCounter, boolean renderBlockOutline, class_4184 camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, Matrix4f matrix4f2, GpuBufferSlice fog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci) {
      PostProcessShaders.beginRender();
   }

   @Inject(
      method = {"method_72916"},
      at = {@At("TAIL")}
   )
   private void onPushEntityRenders(class_4587 matrices, class_11658 worldState, class_11659 queue, CallbackInfo info) {
      if (this.renderDispatcher == null) {
         this.renderDispatcher = new class_11684(this.outlineRenderCommandQueue, MeteorClient.mc.method_1541(), new WrapperImmediateVertexConsumerProvider(() -> this.provider), MeteorClient.mc.method_72703(), NoopOutlineVertexConsumerProvider.INSTANCE, NoopImmediateVertexConsumerProvider.INSTANCE, MeteorClient.mc.field_1772);
      }

      this.draw(worldState, matrices, PostProcessShaders.CHAMS, (entity) -> Color.WHITE);
      this.draw(worldState, matrices, PostProcessShaders.ENTITY_OUTLINE, (entity) -> this.esp.getColor(entity));
   }

   @Unique
   private void draw(class_11658 worldState, class_4587 matrices, EntityShader shader, Function<class_1297, Color> colorGetter) {
      class_243 camera = worldState.field_63082.field_63078;
      boolean empty = true;

      for(class_10017 state : worldState.field_61735) {
         class_1297 entity = ((IEntityRenderState)state).meteor$getEntity();
         if (entity != null && shader.shouldDraw(entity)) {
            Color color = (Color)colorGetter.apply(entity);
            if (color != null) {
               this.outlineRenderCommandQueue.setColor(color);
               class_897<?, class_10017> renderer = this.field_4109.method_68832(state);
               class_243 offset = renderer.method_23169(state);
               matrices.method_22903();
               matrices.method_22904(state.field_53325 - camera.field_1352 + offset.field_1352, state.field_53326 - camera.field_1351 + offset.field_1351, state.field_53327 - camera.field_1350 + offset.field_1350);
               renderer.method_3936(state, matrices, this.outlineRenderCommandQueue, worldState.field_63082);
               matrices.method_22909();
               empty = false;
            }
         }
      }

      if (!empty) {
         this.meteor$pushEntityOutlineFramebuffer(shader.framebuffer);
         this.provider = shader.vertexConsumerProvider;
         this.renderDispatcher.method_73002();
         this.outlineRenderCommandQueue.method_72954();
         this.provider = null;
         this.meteor$popEntityOutlineFramebuffer();
      }
   }

   @ModifyExpressionValue(
      method = {"method_72917"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_761;method_40050(Lnet/minecraft/class_2338;)Z"
)}
   )
   boolean fillEntityRenderStatesIsRenderingReady(boolean original) {
      return this.esp.forceRender() ? true : original;
   }

   @Inject(
      method = {"method_62214"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_4618;method_23285()V"
)}
   )
   private void onRender(CallbackInfo ci) {
      PostProcessShaders.submitEntityVertices();
   }

   @Inject(
      method = {"method_3242"},
      at = {@At("HEAD")}
   )
   private void onResized(int width, int height, CallbackInfo info) {
      PostProcessShaders.onResized(width, height);
   }

   @ModifyArg(
      method = {"method_62205"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_9955;method_62168(ILnet/minecraft/class_4063;FLnet/minecraft/class_243;JF)V"
)
   )
   private int modifyColor(int original) {
      Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
      return ambience.isActive() && (Boolean)ambience.customCloudColor.get() ? ((SettingColor)ambience.cloudColor.get()).getPacked() : original;
   }

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void init$IWorldRenderer(CallbackInfo info) {
      this.framebufferStack = new ObjectArrayList();
      this.framebufferHandleStack = new ObjectArrayList();
   }

   public void meteor$pushEntityOutlineFramebuffer(class_276 framebuffer) {
      this.framebufferStack.push(this.field_53080);
      this.field_53080 = framebuffer;
      this.framebufferHandleStack.push(this.field_53081.field_53097);
      this.field_53081.field_53097 = () -> framebuffer;
   }

   public void meteor$popEntityOutlineFramebuffer() {
      this.field_53080 = (class_276)this.framebufferStack.pop();
      this.field_53081.field_53097 = (class_9925)this.framebufferHandleStack.pop();
   }
}
