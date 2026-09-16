package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.textures.GpuTextureView;
import meteordevelopment.meteorclient.gui.renderer.packer.TextureRegion;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_12137;
import net.minecraft.class_310;

public class Renderer2D {
   public static Renderer2D COLOR;
   public static Renderer2D TEXTURE;
   private final boolean textured;
   public final MeshBuilder triangles;
   public final MeshBuilder lines;

   public Renderer2D(boolean textured) {
      this.textured = textured;
      this.triangles = new MeshBuilder(textured ? MeteorRenderPipelines.UI_TEXTURED : MeteorRenderPipelines.UI_COLORED);
      this.lines = new MeshBuilder(MeteorRenderPipelines.UI_COLORED_LINES);
   }

   @PreInit
   public static void init() {
      COLOR = new Renderer2D(false);
      TEXTURE = new Renderer2D(true);
   }

   public void setAlpha(double alpha) {
      this.triangles.alpha = alpha;
   }

   public void begin() {
      this.triangles.begin();
      this.lines.begin();
   }

   public void end() {
      this.triangles.end();
      this.lines.end();
   }

   public void render() {
      this.render((String)null, (GpuTextureView)null, (class_12137)null);
   }

   public void render(GpuTextureView textureView, class_12137 sampler) {
      if (!this.textured) {
         throw new IllegalStateException("Tried to render with a texture with a non-textured Renderer2D");
      } else {
         this.render("u_Texture", textureView, sampler);
      }
   }

   public void render(String samplerName, GpuTextureView samplerView, class_12137 sampler) {
      if (this.lines.isBuilding()) {
         this.lines.end();
      }

      if (this.triangles.isBuilding()) {
         this.triangles.end();
      }

      MeshRenderer.begin().attachments(class_310.method_1551().method_1522()).pipeline(MeteorRenderPipelines.UI_COLORED_LINES).mesh(this.lines).end();
      MeshRenderer.begin().attachments(class_310.method_1551().method_1522()).pipeline(this.textured ? MeteorRenderPipelines.UI_TEXTURED : MeteorRenderPipelines.UI_COLORED).mesh(this.triangles).sampler(samplerName, samplerView, sampler).end();
   }

   public void triangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
      this.triangles.ensureTriCapacity();
      this.triangles.triangle(this.triangles.vec2(x1, y1).color(color).next(), this.triangles.vec2(x2, y2).color(color).next(), this.triangles.vec2(x3, y3).color(color).next());
   }

   public void line(double x1, double y1, double x2, double y2, Color color) {
      this.lines.ensureLineCapacity();
      this.lines.line(this.lines.vec2(x1, y1).color(color).next(), this.lines.vec2(x2, y2).color(color).next());
   }

   public void boxLines(double x, double y, double width, double height, Color color) {
      this.lines.ensureCapacity(4, 8);
      int i1 = this.lines.vec2(x, y).color(color).next();
      int i2 = this.lines.vec2(x, y + height).color(color).next();
      int i3 = this.lines.vec2(x + width, y + height).color(color).next();
      int i4 = this.lines.vec2(x + width, y).color(color).next();
      this.lines.line(i1, i2);
      this.lines.line(i2, i3);
      this.lines.line(i3, i4);
      this.lines.line(i4, i1);
   }

   public void quad(double x, double y, double width, double height, Color cTopLeft, Color cTopRight, Color cBottomRight, Color cBottomLeft) {
      this.triangles.ensureQuadCapacity();
      this.triangles.quad(this.triangles.vec2(x, y).color(cTopLeft).next(), this.triangles.vec2(x, y + height).color(cBottomLeft).next(), this.triangles.vec2(x + width, y + height).color(cBottomRight).next(), this.triangles.vec2(x + width, y).color(cTopRight).next());
   }

   public void quad(double x, double y, double width, double height, Color color) {
      this.quad(x, y, width, height, color, color, color, color);
   }

   public void texQuad(double x, double y, double width, double height, Color color) {
      this.triangles.ensureQuadCapacity();
      this.triangles.quad(this.triangles.vec2(x, y).vec2((double)0.0F, (double)0.0F).color(color).next(), this.triangles.vec2(x, y + height).vec2((double)0.0F, (double)1.0F).color(color).next(), this.triangles.vec2(x + width, y + height).vec2((double)1.0F, (double)1.0F).color(color).next(), this.triangles.vec2(x + width, y).vec2((double)1.0F, (double)0.0F).color(color).next());
   }

   public void texQuad(double x, double y, double width, double height, TextureRegion texture, Color color) {
      this.triangles.ensureQuadCapacity();
      this.triangles.quad(this.triangles.vec2(x, y).vec2(texture.x1, texture.y1).color(color).next(), this.triangles.vec2(x, y + height).vec2(texture.x1, texture.y2).color(color).next(), this.triangles.vec2(x + width, y + height).vec2(texture.x2, texture.y2).color(color).next(), this.triangles.vec2(x + width, y).vec2(texture.x2, texture.y1).color(color).next());
   }

   public void texQuad(double x, double y, double width, double height, double rotation, double texX1, double texY1, double texX2, double texY2, Color color) {
      this.triangles.ensureQuadCapacity();
      double rad = Math.toRadians(rotation);
      double cos = Math.cos(rad);
      double sin = Math.sin(rad);
      double oX = x + width / (double)2.0F;
      double oY = y + height / (double)2.0F;
      double _x1 = (x - oX) * cos - (y - oY) * sin + oX;
      double _y1 = (y - oY) * cos + (x - oX) * sin + oY;
      int i1 = this.triangles.vec2(_x1, _y1).vec2(texX1, texY1).color(color).next();
      double _x2 = (x - oX) * cos - (y + height - oY) * sin + oX;
      double _y2 = (y + height - oY) * cos + (x - oX) * sin + oY;
      int i2 = this.triangles.vec2(_x2, _y2).vec2(texX1, texY2).color(color).next();
      double _x3 = (x + width - oX) * cos - (y + height - oY) * sin + oX;
      double _y3 = (y + height - oY) * cos + (x + width - oX) * sin + oY;
      int i3 = this.triangles.vec2(_x3, _y3).vec2(texX2, texY2).color(color).next();
      double _x4 = (x + width - oX) * cos - (y - oY) * sin + oX;
      double _y4 = (y - oY) * cos + (x + width - oX) * sin + oY;
      int i4 = this.triangles.vec2(_x4, _y4).vec2(texX2, texY1).color(color).next();
      this.triangles.quad(i1, i2, i3, i4);
   }

   public void texQuad(double x, double y, double width, double height, double rotation, TextureRegion region, Color color) {
      this.texQuad(x, y, width, height, rotation, region.x1, region.y1, region.x2, region.y2, color);
   }
}
