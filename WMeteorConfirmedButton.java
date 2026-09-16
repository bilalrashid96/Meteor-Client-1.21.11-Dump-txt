package meteordevelopment.meteorclient.gui.themes.meteor.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WConfirmedButton;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorConfirmedButton extends WConfirmedButton implements MeteorWidget {
   public WMeteorConfirmedButton(String text, String confirmText, GuiTexture texture) {
      super(text, confirmText, texture);
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      MeteorGuiTheme theme = this.theme();
      double pad = this.pad();
      Color outline = theme.outlineColor.get(this.pressed, this.mouseOver);
      Color fg = (Color)(this.pressedOnce ? theme.backgroundColor.get(this.pressed, this.mouseOver) : (Color)theme.textColor.get());
      Color bg = (Color)(this.pressedOnce ? (Color)theme.textColor.get() : theme.backgroundColor.get(this.pressed, this.mouseOver));
      this.renderBackground(renderer, this, outline, bg);
      String text = this.getText();
      if (text != null) {
         renderer.text(text, this.x + this.width / (double)2.0F - this.textWidth / (double)2.0F, this.y + pad, fg, false);
      } else {
         double ts = theme.textHeight();
         renderer.quad(this.x + this.width / (double)2.0F - ts / (double)2.0F, this.y + pad, ts, ts, this.texture, fg);
      }

   }
}
