package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import java.nio.ByteBuffer;
import net.minecraft.class_11280;
import org.joml.Matrix4f;

public class MeshUniforms {
   public static final int SIZE = (new Std140SizeCalculator()).putMat4f().putMat4f().get();
   private static final Data DATA = new Data();
   private static final class_11280<Data> STORAGE;

   public static void flipFrame() {
      STORAGE.method_71100();
   }

   public static GpuBufferSlice write(Matrix4f proj, Matrix4f modelView) {
      DATA.proj = proj;
      DATA.modelView = modelView;
      return STORAGE.method_71102(DATA);
   }

   static {
      STORAGE = new class_11280("Meteor - Mesh UBO", SIZE, 16);
   }

   private static final class Data implements class_11280.class_11281 {
      private Matrix4f proj;
      private Matrix4f modelView;

      public void method_71104(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer).putMat4f(this.proj).putMat4f(this.modelView);
      }

      public boolean equals(Object o) {
         return false;
      }
   }
}
