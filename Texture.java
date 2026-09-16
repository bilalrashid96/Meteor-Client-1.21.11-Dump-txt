package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.class_1011;
import net.minecraft.class_1044;
import net.minecraft.class_1011.class_1012;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Texture extends class_1044 {
   public Texture(int width, int height, TextureFormat format, FilterMode min, FilterMode mag) {
      this.field_56974 = RenderSystem.getDevice().createTexture("", 15, format, width, height, 1, 1);
      this.field_63613 = RenderSystem.getSamplerCache().method_75293(AddressMode.REPEAT, AddressMode.REPEAT, min, mag, false);
      this.field_60597 = RenderSystem.getDevice().createTextureView(this.field_56974);
   }

   public int getWidth() {
      return this.method_68004().getWidth(0);
   }

   public int getHeight() {
      return this.method_68004().getHeight(0);
   }

   public void upload(byte[] bytes) {
      this.upload(BufferUtils.createByteBuffer(bytes.length).put(bytes));
   }

   public void upload(ByteBuffer buffer) {
      class_1011 image = this.getImage();
      buffer.rewind();
      MemoryUtil.memCopy(MemoryUtil.memAddress(buffer), image.method_67769(), (long)buffer.remaining());
      RenderSystem.getDevice().createCommandEncoder().writeToTexture(this.field_56974, image);
      image.close();
   }

   private @NotNull class_1011 getImage() {
      class_1011.class_1012 var10000;
      switch (this.field_56974.getFormat()) {
         case RGBA8 -> var10000 = class_1012.field_4997;
         case RED8 -> var10000 = class_1012.field_4998;
         default -> throw new IllegalArgumentException();
      }

      class_1011.class_1012 imageFormat = var10000;
      return new class_1011(imageFormat, this.getWidth(), this.getHeight(), false);
   }

   public static Texture readResource(String path, boolean flipY, FilterMode filter) {
      try {
         InputStream in = Texture.class.getResourceAsStream(path);

         ByteBuffer data;
         label69: {
            Texture var11;
            try {
               if (in == null) {
                  data = null;
                  break label69;
               }

               data = TextureUtil.readResource(in).rewind();
               MemoryStack stack = MemoryStack.stackPush();

               try {
                  IntBuffer width = stack.mallocInt(1);
                  IntBuffer height = stack.mallocInt(1);
                  IntBuffer comp = stack.mallocInt(1);
                  STBImage.stbi_set_flip_vertically_on_load(flipY);
                  ByteBuffer image = STBImage.stbi_load_from_memory(data, width, height, comp, 4);
                  Texture texture = new Texture(width.get(0), height.get(0), TextureFormat.RGBA8, filter, filter);
                  texture.upload(image);
                  STBImage.stbi_image_free(image);
                  STBImage.stbi_set_flip_vertically_on_load(false);
                  var11 = texture;
               } catch (Throwable var14) {
                  if (stack != null) {
                     try {
                        stack.close();
                     } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                     }
                  }

                  throw var14;
               }

               if (stack != null) {
                  stack.close();
               }
            } catch (Throwable var15) {
               if (in != null) {
                  try {
                     in.close();
                  } catch (Throwable var12) {
                     var15.addSuppressed(var12);
                  }
               }

               throw var15;
            }

            if (in != null) {
               in.close();
            }

            return var11;
         }

         if (in != null) {
            in.close();
         }

         return data;
      } catch (IOException var16) {
         return null;
      }
   }
}
