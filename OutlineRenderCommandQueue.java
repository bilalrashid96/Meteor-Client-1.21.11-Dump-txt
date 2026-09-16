package meteordevelopment.meteorclient.utils;

import java.util.List;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_10017;
import net.minecraft.class_10444;
import net.minecraft.class_1058;
import net.minecraft.class_1087;
import net.minecraft.class_11659;
import net.minecraft.class_11661;
import net.minecraft.class_11683;
import net.minecraft.class_11788;
import net.minecraft.class_11791;
import net.minecraft.class_12075;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_327;
import net.minecraft.class_3879;
import net.minecraft.class_4587;
import net.minecraft.class_5481;
import net.minecraft.class_630;
import net.minecraft.class_777;
import net.minecraft.class_811;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class OutlineRenderCommandQueue extends class_11661 {
   private int color;
   private int[] tints;

   public void setColor(Color color) {
      this.color = color.getPacked();
   }

   public class_11788 method_73531(int i) {
      return (class_11788)this.field_62244.computeIfAbsent(i, (order) -> new OutlineBatchingRenderCommandQueue(this));
   }

   private class OutlineBatchingRenderCommandQueue extends class_11788 {
      public OutlineBatchingRenderCommandQueue(class_11661 orderedQueueImpl) {
         super(orderedQueueImpl);
      }

      public void method_73479(class_4587 matrices, float shadowRadius, List<class_10017.class_11680> shadowPieces) {
      }

      public void method_73482(class_4587 matrices, @Nullable class_243 nameLabelPos, int y, class_2561 label, boolean notSneaking, int light, double squaredDistanceToCamera, class_12075 cameraState) {
      }

      public void method_73478(class_4587 matrices, float x, float y, class_5481 text, boolean dropShadow, class_327.class_6415 layerType, int light, int color, int backgroundColor, int outlineColor) {
      }

      public void method_73488(class_4587 matrices, class_10017 renderState, Quaternionf rotation) {
      }

      public void method_73486(class_4587 matrices, class_10017.class_10018 leashData) {
      }

      public <S> void method_73490(class_3879<? super S> model, S state, class_4587 matrices, class_1921 renderLayer, int light, int overlay, int tintedColor, @Nullable class_1058 sprite, int outlineColor, class_11683.@Nullable class_11792 crumblingOverlay) {
         super.method_73490(model, state, matrices, renderLayer, light, overlay, OutlineRenderCommandQueue.this.color, sprite, 0, crumblingOverlay);
      }

      public void method_73494(class_630 part, class_4587 matrices, class_1921 renderLayer, int light, int overlay, @Nullable class_1058 sprite, boolean sheeted, boolean hasGlint, int tintedColor, class_11683.@Nullable class_11792 crumblingOverlay, int i) {
         super.method_73494(part, matrices, renderLayer, light, overlay, sprite, sheeted, hasGlint, OutlineRenderCommandQueue.this.color, crumblingOverlay, i);
      }

      public void method_73481(class_4587 matrices, class_2680 state, int light, int overlay, int outlineColor) {
      }

      public void method_73485(class_4587 matrices, class_11791 state) {
      }

      public void method_73484(class_4587 matrices, class_1921 renderLayer, class_1087 model, float r, float g, float b, int light, int overlay, int outlineColor) {
         r = (float)Color.toRGBAR(OutlineRenderCommandQueue.this.color) / 255.0F;
         g = (float)Color.toRGBAG(OutlineRenderCommandQueue.this.color) / 255.0F;
         b = (float)Color.toRGBAB(OutlineRenderCommandQueue.this.color) / 255.0F;
         super.method_73484(matrices, renderLayer, model, r, g, b, light, overlay, outlineColor);
      }

      public void method_73480(class_4587 matrices, class_811 displayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<class_777> quads, class_1921 renderLayer, class_10444.class_10445 glintType) {
         if (OutlineRenderCommandQueue.this.tints == null || OutlineRenderCommandQueue.this.tints[0] != OutlineRenderCommandQueue.this.color) {
            OutlineRenderCommandQueue.this.tints = new int[]{OutlineRenderCommandQueue.this.color, OutlineRenderCommandQueue.this.color, OutlineRenderCommandQueue.this.color, OutlineRenderCommandQueue.this.color};
         }

         super.method_73480(matrices, displayContext, light, overlay, outlineColors, OutlineRenderCommandQueue.this.tints, quads, renderLayer, glintType);
      }

      public void method_73483(class_4587 matrices, class_1921 renderLayer, class_11659.class_11660 customRenderer) {
      }

      public void method_74315(class_11659.class_11947 customRenderer) {
      }
   }
}
