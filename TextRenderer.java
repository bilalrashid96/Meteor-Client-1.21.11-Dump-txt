package meteordevelopment.meteorclient.renderer.text;

import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.render.color.Color;

public interface TextRenderer {
   static TextRenderer get() {
      return (TextRenderer)((Boolean)Config.get().customFont.get() ? Fonts.RENDERER : VanillaTextRenderer.INSTANCE);
   }

   void setAlpha(double var1);

   void begin(double var1, boolean var3, boolean var4);

   default void begin(double scale) {
      this.begin(scale, false, false);
   }

   default void begin() {
      this.begin((double)1.0F, false, false);
   }

   default void beginBig() {
      this.begin((double)1.0F, false, true);
   }

   double getWidth(String var1, int var2, boolean var3);

   default double getWidth(String text, boolean shadow) {
      return this.getWidth(text, text.length(), shadow);
   }

   default double getWidth(String text) {
      return this.getWidth(text, text.length(), false);
   }

   double getHeight(boolean var1);

   default double getHeight() {
      return this.getHeight(false);
   }

   double render(String var1, double var2, double var4, Color var6, boolean var7);

   default double render(String text, double x, double y, Color color) {
      return this.render(text, x, y, color, false);
   }

   boolean isBuilding();

   void end();
}
