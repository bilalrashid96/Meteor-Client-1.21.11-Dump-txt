package meteordevelopment.meteorclient.gui.themes.meteor.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.WHorizontalSeparator;

public class WMeteorHorizontalSeparator extends WHorizontalSeparator implements MeteorWidget {
   public WMeteorHorizontalSeparator(String text) {
      super(text);
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (this.text == null) {
         this.renderWithoutText(renderer);
      } else {
         this.renderWithText(renderer);
      }

   }

   private void renderWithoutText(GuiRenderer renderer) {
      MeteorGuiTheme theme = this.theme();
      double s = theme.scale((double)1.0F);
      double w = this.width / (double)2.0F;
      renderer.quad(this.x, this.y + s, w, s, theme.separatorEdges.get(), theme.separatorCenter.get());
      renderer.quad(this.x + w, this.y + s, w, s, theme.separatorCenter.get(), theme.separatorEdges.get());
   }

   private void renderWithText(GuiRenderer renderer) {
      MeteorGuiTheme theme = this.theme();
      double s = theme.scale((double)2.0F);
      double h = theme.scale((double)1.0F);
      double textStart = (double)Math.round(this.width / (double)2.0F - this.textWidth / (double)2.0F - s);
      double textEnd = s + textStart + this.textWidth + s;
      double offsetY = (double)Math.round(this.height / (double)2.0F);
      renderer.quad(this.x, this.y + offsetY, textStart, h, theme.separatorEdges.get(), theme.separatorCenter.get());
      renderer.text(this.text, this.x + textStart + s, this.y, theme.separatorText.get(), false);
      renderer.quad(this.x + textEnd, this.y + offsetY, this.width - textEnd, h, theme.separatorCenter.get(), theme.separatorEdges.get());
   }
}
