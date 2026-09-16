package meteordevelopment.meteorclient.utils.render.postprocess;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import java.nio.ByteBuffer;
import net.minecraft.class_11280;

public class OutlineUniforms {
   private static final int UNIFORM_SIZE = (new Std140SizeCalculator()).putInt().putFloat().putInt().putFloat().get();
   private static final class_11280<Data> STORAGE;

   public static void flipFrame() {
      STORAGE.method_71100();
   }

   public static GpuBufferSlice write(int width, float fillOpacity, int shapeMode, float glowMultiplier) {
      return STORAGE.method_71102(new Data(width, fillOpacity, shapeMode, glowMultiplier));
   }

   static {
      STORAGE = new class_11280("Meteor - Outline UBO", UNIFORM_SIZE, 16);
   }

   private static record Data(int width, float fillOpacity, int shapeMode, float glowMultiplier) implements class_11280.class_11281 {
      public void method_71104(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer).putInt(this.width).putFloat(this.fillOpacity).putInt(this.shapeMode).putFloat(this.glowMultiplier);
      }
   }
}
