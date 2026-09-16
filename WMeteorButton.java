package meteordevelopment.meteorclient.gui.themes.meteor.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;

public class WMeteorButton extends WButton implements MeteorWidget {
   public WMeteorButton(String text, GuiTexture texture) {
      super(text, texture);
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      MeteorGuiTheme theme = this.theme();
      double pad = this.pad();
      this.renderBackground(renderer, this, this.pressed, this.mouseOver);
      if (this.text != null) {
         renderer.text(this.text, this.x + this.width / (double)2.0F - this.textWidth / (double)2.0F, this.y + pad, theme.textColor.get(), false);
      } else {
         double ts = theme.textHeight();
         renderer.quad(this.x + this.width / (double)2.0F - ts / (double)2.0F, this.y + pad, ts, ts, this.texture, theme.textColor.get());
      }

   }
}
