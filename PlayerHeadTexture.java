package meteordevelopment.meteorclient.utils.render;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.class_3298;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class PlayerHeadTexture extends Texture {
   private boolean needsRotate;

   public PlayerHeadTexture(byte[] head, boolean needsRotate) {
      super(8, 8, TextureFormat.RGBA8, FilterMode.NEAREST, FilterMode.NEAREST);
      this.upload(BufferUtils.createByteBuffer(head.length).put(head));
      this.needsRotate = needsRotate;
   }

   public PlayerHeadTexture() {
      super(8, 8, TextureFormat.RGBA8, FilterMode.NEAREST, FilterMode.NEAREST);

      try {
         InputStream inputStream = ((class_3298)MeteorClient.mc.method_1478().method_14486(MeteorClient.identifier("textures/steve.png")).get()).method_14482();

         try {
            ByteBuffer data = TextureUtil.readResource(inputStream);
            data.rewind();
            MemoryStack stack = MemoryStack.stackPush();

            try {
               IntBuffer width = stack.mallocInt(1);
               IntBuffer height = stack.mallocInt(1);
               IntBuffer comp = stack.mallocInt(1);
               ByteBuffer image = STBImage.stbi_load_from_memory(data, width, height, comp, 4);
               this.upload(image);
               STBImage.stbi_image_free(image);
            } catch (Throwable var10) {
               if (stack != null) {
                  try {
                     stack.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (stack != null) {
               stack.close();
            }

            MemoryUtil.memFree(data);
         } catch (Throwable var11) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (inputStream != null) {
            inputStream.close();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   public boolean needsRotate() {
      return this.needsRotate;
   }

   public static byte[] downloadHead(String url) throws IOException {
      InputStream in = Http.get(url).sendInputStream();

      BufferedImage skin;
      try {
         skin = ImageIO.read(in);
      } catch (Throwable var9) {
         if (in != null) {
            try {
               in.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (in != null) {
         in.close();
      }

      if (skin == null) {
         throw new IOException("Failed to decode skin image.");
      } else {
         byte[] head = new byte[256];
         int[] pixel = new int[4];
         int i = 0;

         for(int x = 8; x < 16; ++x) {
            for(int y = 8; y < 16; ++y) {
               skin.getData().getPixel(x, y, pixel);

               for(int j = 0; j < 4; ++j) {
                  head[i++] = (byte)pixel[j];
               }
            }
         }

         i = 0;

         for(int x = 40; x < 48; ++x) {
            for(int y = 8; y < 16; ++y) {
               skin.getData().getPixel(x, y, pixel);
               if (pixel[3] != 0) {
                  for(int j = 0; j < 4; ++j) {
                     head[i++] = (byte)pixel[j];
                  }
               } else {
                  i += 4;
               }
            }
         }

         return head;
      }
   }
}
