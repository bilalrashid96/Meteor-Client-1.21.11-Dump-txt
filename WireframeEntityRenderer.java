package meteordevelopment.meteorclient.utils.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.SequencedMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.mixin.RenderLayerAccessor;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_10017;
import net.minecraft.class_11661;
import net.minecraft.class_11684;
import net.minecraft.class_12246;
import net.minecraft.class_1297;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_897;
import net.minecraft.class_9799;

public class WireframeEntityRenderer {
   private static final class_4587 matrices = new class_4587();
   private static Renderer3D renderer;
   private static final class_11661 renderCommandQueue = new class_11661();
   private static final class_11684 renderDispatcher;
   private static Color sideColor;
   private static Color lineColor;
   private static ShapeMode shapeMode;
   private static double offsetX;
   private static double offsetY;
   private static double offsetZ;

   private WireframeEntityRenderer() {
   }

   public static void render(Render3DEvent event, class_1297 entity, double scale, Color sideColor, Color lineColor, ShapeMode shapeMode) {
      WireframeEntityRenderer.renderer = event.renderer;
      WireframeEntityRenderer.sideColor = sideColor;
      WireframeEntityRenderer.lineColor = lineColor;
      WireframeEntityRenderer.shapeMode = shapeMode;
      float tickDelta = MeteorClient.mc.field_1687.method_54719().method_54754() ? 1.0F : event.tickDelta;
      offsetX = class_3532.method_16436((double)tickDelta, entity.field_6038, entity.method_23317());
      offsetY = class_3532.method_16436((double)tickDelta, entity.field_5971, entity.method_23318());
      offsetZ = class_3532.method_16436((double)tickDelta, entity.field_5989, entity.method_23321());
      class_897<class_1297, class_10017> renderer = MeteorClient.mc.method_1561().method_3953(entity);
      class_10017 state = renderer.method_62425(entity, tickDelta);
      class_243 entityOffset = renderer.method_23169(state);
      offsetX += entityOffset.field_1352;
      offsetY += entityOffset.field_1351;
      offsetZ += entityOffset.field_1350;
      matrices.method_22903();
      matrices.method_22905((float)scale, (float)scale, (float)scale);
      renderer.method_3936(state, matrices, renderCommandQueue, MeteorClient.mc.field_1773.method_72912().field_63082);
      matrices.method_22909();
      renderDispatcher.method_73002();
      renderCommandQueue.method_72954();
   }

   static {
      renderDispatcher = new class_11684(renderCommandQueue, MeteorClient.mc.method_1541(), WireframeEntityRenderer.MyVertexConsumerProvider.INSTANCE, MeteorClient.mc.method_72703(), NoopOutlineVertexConsumerProvider.INSTANCE, NoopImmediateVertexConsumerProvider.INSTANCE, MeteorClient.mc.field_1772);
   }

   private static class MyVertexConsumerProvider extends class_4597.class_4598 {
      public static final MyVertexConsumerProvider INSTANCE = new MyVertexConsumerProvider();
      private final Object2ObjectOpenHashMap<class_1921, MyVertexConsumer> buffers = new Object2ObjectOpenHashMap();

      protected MyVertexConsumerProvider() {
         super((class_9799)null, (SequencedMap)null);
      }

      public class_4588 method_73477(class_1921 layer) {
         if (((RenderLayerAccessor)layer).getRenderSetup().field_63989 == class_12246.field_63983) {
            return NoopVertexConsumer.INSTANCE;
         } else {
            MyVertexConsumer vertexConsumer = (MyVertexConsumer)this.buffers.get(layer);
            if (vertexConsumer == null) {
               vertexConsumer = new MyVertexConsumer();
               this.buffers.put(layer, vertexConsumer);
            }

            return vertexConsumer;
         }
      }

      public void method_22993() {
         throw new RuntimeException();
      }

      public void method_22994(class_1921 layer) {
         throw new RuntimeException();
      }
   }

   private static class MyVertexConsumer implements class_4588 {
      private final float[] xs = new float[4];
      private final float[] ys = new float[4];
      private final float[] zs = new float[4];
      private int i = 0;

      public class_4588 method_22912(float x, float y, float z) {
         this.xs[this.i] = x;
         this.ys[this.i] = y;
         this.zs[this.i] = z;
         ++this.i;
         if (this.i == 4) {
            WireframeEntityRenderer.renderer.side(WireframeEntityRenderer.offsetX + (double)this.xs[0], WireframeEntityRenderer.offsetY + (double)this.ys[0], WireframeEntityRenderer.offsetZ + (double)this.zs[0], WireframeEntityRenderer.offsetX + (double)this.xs[1], WireframeEntityRenderer.offsetY + (double)this.ys[1], WireframeEntityRenderer.offsetZ + (double)this.zs[1], WireframeEntityRenderer.offsetX + (double)this.xs[2], WireframeEntityRenderer.offsetY + (double)this.ys[2], WireframeEntityRenderer.offsetZ + (double)this.zs[2], WireframeEntityRenderer.offsetX + (double)this.xs[3], WireframeEntityRenderer.offsetY + (double)this.ys[3], WireframeEntityRenderer.offsetZ + (double)this.zs[3], WireframeEntityRenderer.sideColor, WireframeEntityRenderer.lineColor, WireframeEntityRenderer.shapeMode);
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
         return this;
      }

      public class_4588 method_75298(float width) {
         return this;
      }
   }
}
