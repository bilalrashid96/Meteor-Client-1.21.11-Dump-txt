package meteordevelopment.meteorclient.renderer.text;

import java.io.IOException;
import java.nio.ByteBuffer;
import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_310;

public class CustomTextRenderer implements TextRenderer {
   public static final Color SHADOW_COLOR = new Color(60, 60, 60, 180);
   private final MeshBuilder mesh;
   public final FontFace fontFace;
   private final Font[] fonts;
   private Font font;
   private boolean building;
   private boolean scaleOnly;
   private double fontScale;
   private double scale;

   public CustomTextRenderer(FontFace fontFace) throws IOException {
      this.mesh = new MeshBuilder(MeteorRenderPipelines.UI_TEXT);
      this.fontScale = (double)1.0F;
      this.scale = (double)1.0F;
      this.fontFace = fontFace;
      ByteBuffer buffer = fontFace.readToDirectByteBuffer();
      this.fonts = new Font[5];

      for(int i = 0; i < this.fonts.length; ++i) {
         this.fonts[i] = new Font(buffer, (int)Math.round((double)27.0F * ((double)i * (double)0.5F + (double)1.0F)));
      }

   }

   public void setAlpha(double a) {
      this.mesh.alpha = a;
   }

   public void begin(double scale, boolean scaleOnly, boolean big) {
      if (this.building) {
         throw new RuntimeException("CustomTextRenderer.begin() called twice");
      } else {
         if (!scaleOnly) {
            this.mesh.begin();
         }

         if (big) {
            this.font = this.fonts[this.fonts.length - 1];
         } else {
            double scaleA = Math.floor(scale * (double)10.0F) / (double)10.0F;
            int scaleI;
            if (scaleA >= (double)3.0F) {
               scaleI = 5;
            } else if (scaleA >= (double)2.5F) {
               scaleI = 4;
            } else if (scaleA >= (double)2.0F) {
               scaleI = 3;
            } else if (scaleA >= (double)1.5F) {
               scaleI = 2;
            } else {
               scaleI = 1;
            }

            this.font = this.fonts[scaleI - 1];
         }

         this.building = true;
         this.scaleOnly = scaleOnly;
         this.fontScale = (double)this.font.getHeight() / (double)27.0F;
         this.scale = (double)1.0F + (scale - this.fontScale) / this.fontScale;
      }
   }

   public double getWidth(String text, int length, boolean shadow) {
      if (text.isEmpty()) {
         return (double)0.0F;
      } else {
         Font font = this.building ? this.font : this.fonts[0];
         return (font.getWidth(text, length) + (double)(shadow ? 1 : 0)) * this.scale / (double)1.5F;
      }
   }

   public double getHeight(boolean shadow) {
      Font font = this.building ? this.font : this.fonts[0];
      return (double)(font.getHeight() + 1 + (shadow ? 1 : 0)) * this.scale / (double)1.5F;
   }

   public double render(String text, double x, double y, Color color, boolean shadow) {
      boolean wasBuilding = this.building;
      if (!wasBuilding) {
         this.begin();
      }

      double width;
      if (shadow) {
         int preShadowA = SHADOW_COLOR.a;
         SHADOW_COLOR.a = (int)((double)color.a / (double)255.0F * (double)preShadowA);
         width = this.font.render(this.mesh, text, x + this.fontScale * this.scale / (double)1.5F, y + this.fontScale * this.scale / (double)1.5F, SHADOW_COLOR, this.scale / (double)1.5F);
         this.font.render(this.mesh, text, x, y, color, this.scale / (double)1.5F);
         SHADOW_COLOR.a = preShadowA;
      } else {
         width = this.font.render(this.mesh, text, x, y, color, this.scale / (double)1.5F);
      }

      if (!wasBuilding) {
         this.end();
      }

      return width;
   }

   public boolean isBuilding() {
      return this.building;
   }

   public void end() {
      if (!this.building) {
         throw new RuntimeException("CustomTextRenderer.end() called without calling begin()");
      } else {
         if (!this.scaleOnly) {
            this.mesh.end();
            MeshRenderer.begin().attachments(class_310.method_1551().method_1522()).pipeline(MeteorRenderPipelines.UI_TEXT).mesh(this.mesh).sampler("u_Texture", this.font.texture.method_71659(), this.font.texture.method_75484()).end();
         }

         this.building = false;
         this.scale = (double)1.0F;
      }
   }

   public void destroy() {
      for(Font font : this.fonts) {
         font.texture.close();
      }

   }
}
