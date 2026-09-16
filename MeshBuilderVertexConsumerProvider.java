package meteordevelopment.meteorclient.utils.render;

import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_1921;
import net.minecraft.class_4588;

public class MeshBuilderVertexConsumerProvider implements IVertexConsumerProvider {
   private final MeshBuilderVertexConsumer vertexConsumer;

   public MeshBuilderVertexConsumerProvider(MeshBuilder mesh) {
      this.vertexConsumer = new MeshBuilderVertexConsumer(mesh);
   }

   public class_4588 method_73477(class_1921 layer) {
      return new W(this.vertexConsumer);
   }

   public void setColor(Color color) {
      this.vertexConsumer.fixedColor(color.r, color.g, color.b, color.a);
   }

   public void setOffset(int offsetX, int offsetY, int offsetZ) {
      this.vertexConsumer.setOffset(offsetX, offsetY, offsetZ);
   }

   private static record W(MeshBuilderVertexConsumer d) implements class_4588 {
      public class_4588 method_22912(float x, float y, float z) {
         this.d.method_22912(x, y, z);
         return this;
      }

      public class_4588 method_1336(int r, int g, int b, int a) {
         return this;
      }

      public class_4588 method_39415(int c) {
         return this;
      }

      public class_4588 method_22913(float u, float v) {
         return this;
      }

      public class_4588 method_60796(int u, int v) {
         return this;
      }

      public class_4588 method_22921(int u, int v) {
         return this;
      }

      public class_4588 method_22914(float x, float y, float z) {
         return this;
      }

      public class_4588 method_75298(float w) {
         return this;
      }
   }

   public static class MeshBuilderVertexConsumer implements class_4588 {
      private final MeshBuilder mesh;
      private int offsetX;
      private int offsetY;
      private int offsetZ;
      private final double[] xs = new double[4];
      private final double[] ys = new double[4];
      private final double[] zs = new double[4];
      private final Color color = new Color();
      private int i;

      public MeshBuilderVertexConsumer(MeshBuilder mesh) {
         this.mesh = mesh;
      }

      public void setOffset(int offsetX, int offsetY, int offsetZ) {
         this.offsetX = offsetX;
         this.offsetY = offsetY;
         this.offsetZ = offsetZ;
      }

      public class_4588 method_22912(float x, float y, float z) {
         this.xs[this.i] = (double)this.offsetX + (double)x;
         this.ys[this.i] = (double)this.offsetY + (double)y;
         this.zs[this.i] = (double)this.offsetZ + (double)z;
         if (++this.i >= 4) {
            this.mesh.ensureQuadCapacity();
            this.mesh.quad(this.mesh.vec3(this.xs[0], this.ys[0], this.zs[0]).color(this.color).next(), this.mesh.vec3(this.xs[1], this.ys[1], this.zs[1]).color(this.color).next(), this.mesh.vec3(this.xs[2], this.ys[2], this.zs[2]).color(this.color).next(), this.mesh.vec3(this.xs[3], this.ys[3], this.zs[3]).color(this.color).next());
            this.i = 0;
         }

         return this;
      }

      public class_4588 method_1336(int red, int green, int blue, int alpha) {
         return this;
      }

      public class_4588 method_39415(int argb) {
         return this;
      }

      public class_4588 method_22913(float u, float v) {
         return this;
      }

      public class_4588 method_60796(int u, int v) {
         return this;
      }

      public class_4588 method_22921(int u, int v) {
         return this;
      }

      public class_4588 method_22914(float x, float y, float z) {
         return null;
      }

      public class_4588 method_75298(float width) {
         return this;
      }

      public void fixedColor(int red, int green, int blue, int alpha) {
         this.color.set(red, green, blue, alpha);
      }
   }
}
