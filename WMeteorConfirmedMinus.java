package meteordevelopment.meteorclient.gui.themes.meteor.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WConfirmedMinus;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorConfirmedMinus extends WConfirmedMinus implements MeteorWidget {
   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      MeteorGuiTheme theme = this.theme();
      double pad = this.pad();
      double s = theme.scale((double)3.0F);
      Color outline = theme.outlineColor.get(this.pressed, this.mouseOver);
      Color fg = (Color)(this.pressedOnce ? theme.backgroundColor.get(this.pressed, this.mouseOver) : (Color)this.theme().minusColor.get());
      Color bg = (Color)(this.pressedOnce ? (Color)this.theme().minusColor.get() : theme.backgroundColor.get(this.pressed, this.mouseOver));
      this.renderBackground(renderer, this, outline, bg);
      renderer.quad(this.x + pad, this.y + this.height / (double)2.0F - s / (double)2.0F, this.width - pad * (double)2.0F, s, fg);
   }
}
