package meteordevelopment.meteorclient.utils.render.postprocess;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.Set;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.ResourcePacksReloadedEvent;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Chams;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_11280;
import net.minecraft.class_1297;
import net.minecraft.class_3298;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class ChamsShader extends EntityShader {
   private static final String[] FILE_FORMATS = new String[]{"png", "jpg"};
   private static Texture IMAGE_TEX;
   private static Chams chams;
   private static final int UNIFORM_SIZE = (new Std140SizeCalculator()).putVec4().get();
   private static final class_11280<UniformData> UNIFORM_STORAGE;

   public ChamsShader() {
      super(MeteorRenderPipelines.POST_IMAGE);
      MeteorClient.EVENT_BUS.subscribe(ChamsShader.class);
   }

   @PostInit
   public static void load() {
      try {
         ByteBuffer data = null;

         for(String fileFormat : FILE_FORMATS) {
            Optional<class_3298> optional = MeteorClient.mc.method_1478().method_14486(MeteorClient.identifier("textures/chams." + fileFormat));
            if (!optional.isEmpty() && ((class_3298)optional.get()).method_14482() != null) {
               data = TextureUtil.readResource(((class_3298)optional.get()).method_14482());
               break;
            }
         }

         if (data == null) {
            return;
         }

         data.rewind();
         MemoryStack stack = MemoryStack.stackPush();

         try {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer image = STBImage.stbi_load_from_memory(data, width, height, comp, 4);
            IMAGE_TEX = new Texture(width.get(0), height.get(0), TextureFormat.RGBA8, FilterMode.NEAREST, FilterMode.NEAREST);
            IMAGE_TEX.upload(image);
            STBImage.stbi_image_free(image);
            STBImage.stbi_set_flip_vertically_on_load(false);
         } catch (Throwable var7) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (stack != null) {
            stack.close();
         }
      } catch (IOException e) {
         MeteorClient.LOG.error("Error loading the chams shader", e);
      }

   }

   @EventHandler
   private static void onResourcePacksReloaded(ResourcePacksReloadedEvent event) {
      load();
   }

   protected void setupPass(MeshRenderer renderer) {
      Color color = chams.shaderColor.get();
      renderer.uniform("ImageData", UNIFORM_STORAGE.method_71102(new UniformData((float)color.r / 255.0F, (float)color.g / 255.0F, (float)color.b / 255.0F, (float)color.a / 255.0F)));
      if (chams.isShader() && chams.shader.get() == Chams.Shader.Image && IMAGE_TEX != null) {
         renderer.sampler("u_TextureI", IMAGE_TEX.method_71659(), IMAGE_TEX.method_75484());
      }

   }

   protected boolean shouldDraw() {
      if (chams == null) {
         chams = (Chams)Modules.get().get(Chams.class);
      }

      return chams.isShader();
   }

   public boolean shouldDraw(class_1297 entity) {
      if (!this.shouldDraw()) {
         return false;
      } else {
         return ((Set)chams.entities.get()).contains(entity.method_5864()) && (entity != MeteorClient.mc.field_1724 || !(Boolean)chams.ignoreSelfDepth.get());
      }
   }

   public static void flipFrame() {
      UNIFORM_STORAGE.method_71100();
   }

   static {
      UNIFORM_STORAGE = new class_11280("Meteor - Image UBO", UNIFORM_SIZE, 16);
   }

   private static record UniformData(float r, float g, float b, float a) implements class_11280.class_11281 {
      public void method_71104(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer).putVec4(this.r, this.g, this.b, this.a);
      }
   }
}
