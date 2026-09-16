package meteordevelopment.meteorclient.gui.themes.meteor.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.WVerticalSeparator;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorVerticalSeparator extends WVerticalSeparator implements MeteorWidget {
   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      MeteorGuiTheme theme = this.theme();
      Color colorEdges = theme.separatorEdges.get();
      Color colorCenter = theme.separatorCenter.get();
      double s = theme.scale((double)1.0F);
      double offsetX = (double)Math.round(this.width / (double)2.0F);
      renderer.quad(this.x + offsetX, this.y, s, this.height / (double)2.0F, colorEdges, colorEdges, colorCenter, colorCenter);
      renderer.quad(this.x + offsetX, this.y + this.height / (double)2.0F, s, this.height / (double)2.0F, colorCenter, colorCenter, colorEdges, colorEdges);
   }
}
