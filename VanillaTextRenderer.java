package meteordevelopment.meteorclient.renderer.text;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_9799;
import net.minecraft.class_327.class_6415;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class VanillaTextRenderer implements TextRenderer {
   public static final VanillaTextRenderer INSTANCE = new VanillaTextRenderer();
   private final class_9799 buffer = new class_9799(2048);
   private final class_4597.class_4598 immediate;
   private final class_4587 matrices;
   private final Matrix4f emptyMatrix;
   public double scale;
   public boolean scaleIndividually;
   private boolean building;
   private double alpha;

   private VanillaTextRenderer() {
      this.immediate = class_4597.method_22991(this.buffer);
      this.matrices = new class_4587();
      this.emptyMatrix = new Matrix4f();
      this.scale = (double)2.0F;
      this.alpha = (double)1.0F;
   }

   public void setAlpha(double a) {
      this.alpha = a;
   }

   public double getWidth(String text, int length, boolean shadow) {
      if (text.isEmpty()) {
         return (double)0.0F;
      } else {
         if (length != text.length()) {
            text = text.substring(0, length);
         }

         return (double)(MeteorClient.mc.field_1772.method_1727(text) + (shadow ? 1 : 0)) * this.scale;
      }
   }

   public double getHeight(boolean shadow) {
      Objects.requireNonNull(MeteorClient.mc.field_1772);
      return (double)(9 + (shadow ? 1 : 0)) * this.scale;
   }

   public void begin(double scale, boolean scaleOnly, boolean big) {
      if (this.building) {
         throw new RuntimeException("VanillaTextRenderer.begin() called twice");
      } else {
         this.scale = scale * (double)2.0F;
         this.building = true;
      }
   }

   public double render(String text, double x, double y, Color color, boolean shadow) {
      boolean wasBuilding = this.building;
      if (!wasBuilding) {
         this.begin();
      }

      x += (double)0.5F * this.scale;
      y += (double)0.5F * this.scale;
      int preA = color.a;
      color.a = (int)((double)color.a / (double)255.0F * this.alpha * (double)255.0F);
      Matrix4f matrix = this.emptyMatrix;
      if (this.scaleIndividually) {
         this.matrices.method_22903();
         this.matrices.method_22905((float)this.scale, (float)this.scale, 1.0F);
         matrix = this.matrices.method_23760().method_23761();
      }

      MeteorClient.mc.field_1772.method_27521(text, (float)(x / this.scale), (float)(y / this.scale), color.getPacked(), shadow, matrix, this.immediate, class_6415.field_33993, 0, 15728880);
      double x2 = x / this.scale + (double)MeteorClient.mc.field_1772.method_1727(text);
      if (this.scaleIndividually) {
         this.matrices.method_22909();
      }

      color.a = preA;
      if (!wasBuilding) {
         this.end();
      }

      return (x2 - (double)1.0F) * this.scale;
   }

   public boolean isBuilding() {
      return this.building;
   }

   public void end() {
      if (!this.building) {
         throw new RuntimeException("VanillaTextRenderer.end() called without calling begin()");
      } else {
         Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
         matrixStack.pushMatrix();
         if (!this.scaleIndividually) {
            matrixStack.scale((float)this.scale, (float)this.scale, 1.0F);
         }

         this.immediate.method_22993();
         matrixStack.popMatrix();
         this.scale = (double)2.0F;
         this.building = false;
      }
   }
}
