package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.Dir;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_310;
import net.minecraft.class_4587;

public class Renderer3D {
   public final MeshBuilder lines;
   public final MeshBuilder triangles;
   private final RenderPipeline linesPipeline;
   private final RenderPipeline trianglesPipeline;

   public Renderer3D(RenderPipeline lines, RenderPipeline triangles) {
      this.lines = new MeshBuilder(lines);
      this.triangles = new MeshBuilder(triangles);
      this.linesPipeline = lines;
      this.trianglesPipeline = triangles;
   }

   public void begin() {
      this.lines.begin();
      this.triangles.begin();
   }

   public void render(class_4587 matrices) {
      MeshRenderer.begin().attachments(class_310.method_1551().method_1522()).pipeline(this.linesPipeline).mesh(this.lines, matrices).end();
      MeshRenderer.begin().attachments(class_310.method_1551().method_1522()).pipeline(this.trianglesPipeline).mesh(this.triangles, matrices).end();
   }

   public void line(double x1, double y1, double z1, double x2, double y2, double z2, Color color1, Color color2) {
      this.lines.ensureLineCapacity();
      this.lines.line(this.lines.vec3(x1, y1, z1).color(color1).next(), this.lines.vec3(x2, y2, z2).color(color2).next());
   }

   public void line(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
      this.line(x1, y1, z1, x2, y2, z2, color, color);
   }

   public void boxLines(double x1, double y1, double z1, double x2, double y2, double z2, Color color, int excludeDir) {
      this.lines.ensureCapacity(8, 24);
      int blb = this.lines.vec3(x1, y1, z1).color(color).next();
      int blf = this.lines.vec3(x1, y1, z2).color(color).next();
      int brb = this.lines.vec3(x2, y1, z1).color(color).next();
      int brf = this.lines.vec3(x2, y1, z2).color(color).next();
      int tlb = this.lines.vec3(x1, y2, z1).color(color).next();
      int tlf = this.lines.vec3(x1, y2, z2).color(color).next();
      int trb = this.lines.vec3(x2, y2, z1).color(color).next();
      int trf = this.lines.vec3(x2, y2, z2).color(color).next();
      if (excludeDir == 0) {
         this.lines.line(blb, tlb);
         this.lines.line(blf, tlf);
         this.lines.line(brb, trb);
         this.lines.line(brf, trf);
         this.lines.line(blb, blf);
         this.lines.line(brb, brf);
         this.lines.line(blb, brb);
         this.lines.line(blf, brf);
         this.lines.line(tlb, tlf);
         this.lines.line(trb, trf);
         this.lines.line(tlb, trb);
         this.lines.line(tlf, trf);
      } else {
         if (Dir.isNot(excludeDir, (byte)32) && Dir.isNot(excludeDir, (byte)8)) {
            this.lines.line(blb, tlb);
         }

         if (Dir.isNot(excludeDir, (byte)32) && Dir.isNot(excludeDir, (byte)16)) {
            this.lines.line(blf, tlf);
         }

         if (Dir.isNot(excludeDir, (byte)64) && Dir.isNot(excludeDir, (byte)8)) {
            this.lines.line(brb, trb);
         }

         if (Dir.isNot(excludeDir, (byte)64) && Dir.isNot(excludeDir, (byte)16)) {
            this.lines.line(brf, trf);
         }

         if (Dir.isNot(excludeDir, (byte)32) && Dir.isNot(excludeDir, (byte)4)) {
            this.lines.line(blb, blf);
         }

         if (Dir.isNot(excludeDir, (byte)64) && Dir.isNot(excludeDir, (byte)4)) {
            this.lines.line(brb, brf);
         }

         if (Dir.isNot(excludeDir, (byte)8) && Dir.isNot(excludeDir, (byte)4)) {
            this.lines.line(blb, brb);
         }

         if (Dir.isNot(excludeDir, (byte)16) && Dir.isNot(excludeDir, (byte)4)) {
            this.lines.line(blf, brf);
         }

         if (Dir.isNot(excludeDir, (byte)32) && Dir.isNot(excludeDir, (byte)2)) {
            this.lines.line(tlb, tlf);
         }

         if (Dir.isNot(excludeDir, (byte)64) && Dir.isNot(excludeDir, (byte)2)) {
            this.lines.line(trb, trf);
         }

         if (Dir.isNot(excludeDir, (byte)8) && Dir.isNot(excludeDir, (byte)2)) {
            this.lines.line(tlb, trb);
         }

         if (Dir.isNot(excludeDir, (byte)16) && Dir.isNot(excludeDir, (byte)2)) {
            this.lines.line(tlf, trf);
         }
      }

   }

   public void blockLines(int x, int y, int z, Color color, int excludeDir) {
      this.boxLines((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1), color, excludeDir);
   }

   public void quad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color topLeft, Color topRight, Color bottomRight, Color bottomLeft) {
      this.triangles.ensureQuadCapacity();
      this.triangles.quad(this.triangles.vec3(x1, y1, z1).color(bottomLeft).next(), this.triangles.vec3(x2, y2, z2).color(topLeft).next(), this.triangles.vec3(x3, y3, z3).color(topRight).next(), this.triangles.vec3(x4, y4, z4).color(bottomRight).next());
   }

   public void quad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color color) {
      this.quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, color, color, color, color);
   }

   public void quadVertical(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
      this.quad(x1, y1, z1, x1, y2, z1, x2, y2, z2, x2, y1, z2, color);
   }

   public void quadHorizontal(double x1, double y, double z1, double x2, double z2, Color color) {
      this.quad(x1, y, z1, x1, y, z2, x2, y, z2, x2, y, z1, color);
   }

   public void gradientQuadVertical(double x1, double y1, double z1, double x2, double y2, double z2, Color topColor, Color bottomColor) {
      this.quad(x1, y1, z1, x1, y2, z1, x2, y2, z2, x2, y1, z2, topColor, topColor, bottomColor, bottomColor);
   }

   public void side(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color sideColor, Color lineColor, ShapeMode mode) {
      if (mode.lines()) {
         this.lines.ensureCapacity(4, 8);
         int i1 = this.lines.vec3(x1, y1, z1).color(lineColor).next();
         int i2 = this.lines.vec3(x2, y2, z2).color(lineColor).next();
         int i3 = this.lines.vec3(x3, y3, z3).color(lineColor).next();
         int i4 = this.lines.vec3(x4, y4, z4).color(lineColor).next();
         this.lines.line(i1, i2);
         this.lines.line(i2, i3);
         this.lines.line(i3, i4);
         this.lines.line(i4, i1);
      }

      if (mode.sides()) {
         this.quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, sideColor);
      }

   }

   public void sideVertical(double x1, double y1, double z1, double x2, double y2, double z2, Color sideColor, Color lineColor, ShapeMode mode) {
      this.side(x1, y1, z1, x1, y2, z1, x2, y2, z2, x2, y1, z2, sideColor, lineColor, mode);
   }

   public void sideHorizontal(double x1, double y, double z1, double x2, double z2, Color sideColor, Color lineColor, ShapeMode mode) {
      this.side(x1, y, z1, x1, y, z2, x2, y, z2, x2, y, z1, sideColor, lineColor, mode);
   }

   public void boxSides(double x1, double y1, double z1, double x2, double y2, double z2, Color color, int excludeDir) {
      this.triangles.ensureCapacity(8, 36);
      int blb = this.triangles.vec3(x1, y1, z1).color(color).next();
      int blf = this.triangles.vec3(x1, y1, z2).color(color).next();
      int brb = this.triangles.vec3(x2, y1, z1).color(color).next();
      int brf = this.triangles.vec3(x2, y1, z2).color(color).next();
      int tlb = this.triangles.vec3(x1, y2, z1).color(color).next();
      int tlf = this.triangles.vec3(x1, y2, z2).color(color).next();
      int trb = this.triangles.vec3(x2, y2, z1).color(color).next();
      int trf = this.triangles.vec3(x2, y2, z2).color(color).next();
      if (excludeDir == 0) {
         this.triangles.quad(blb, blf, tlf, tlb);
         this.triangles.quad(brb, trb, trf, brf);
         this.triangles.quad(blb, tlb, trb, brb);
         this.triangles.quad(blf, brf, trf, tlf);
         this.triangles.quad(blb, brb, brf, blf);
         this.triangles.quad(tlb, tlf, trf, trb);
      } else {
         if (Dir.isNot(excludeDir, (byte)32)) {
            this.triangles.quad(blb, blf, tlf, tlb);
         }

         if (Dir.isNot(excludeDir, (byte)64)) {
            this.triangles.quad(brb, trb, trf, brf);
         }

         if (Dir.isNot(excludeDir, (byte)8)) {
            this.triangles.quad(blb, tlb, trb, brb);
         }

         if (Dir.isNot(excludeDir, (byte)16)) {
            this.triangles.quad(blf, brf, trf, tlf);
         }

         if (Dir.isNot(excludeDir, (byte)4)) {
            this.triangles.quad(blb, brb, brf, blf);
         }

         if (Dir.isNot(excludeDir, (byte)2)) {
            this.triangles.quad(tlb, tlf, trf, trb);
         }
      }

   }

   public void blockSides(int x, int y, int z, Color color, int excludeDir) {
      this.boxSides((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1), color, excludeDir);
   }

   public void box(double x1, double y1, double z1, double x2, double y2, double z2, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
      if (mode.lines()) {
         this.boxLines(x1, y1, z1, x2, y2, z2, lineColor, excludeDir);
      }

      if (mode.sides()) {
         this.boxSides(x1, y1, z1, x2, y2, z2, sideColor, excludeDir);
      }

   }

   public void box(class_2338 pos, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
      if (mode.lines()) {
         this.boxLines((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), lineColor, excludeDir);
      }

      if (mode.sides()) {
         this.boxSides((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), sideColor, excludeDir);
      }

   }

   public void box(class_238 box, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
      if (mode.lines()) {
         this.boxLines(box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1324, lineColor, excludeDir);
      }

      if (mode.sides()) {
         this.boxSides(box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1324, sideColor, excludeDir);
      }

   }
}
