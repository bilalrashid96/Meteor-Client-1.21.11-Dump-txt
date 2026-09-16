package meteordevelopment.meteorclient.gui.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

public abstract class WFavorite extends WPressable {
   public boolean checked;

   public WFavorite(boolean checked) {
      this.checked = checked;
   }

   protected void onCalculateSize() {
      double pad = this.pad();
      double s = this.theme.textHeight();
      this.width = pad + s + pad;
      this.height = pad + s + pad;
   }

   protected void onPressed(int button) {
      this.checked = !this.checked;
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      double pad = this.pad();
      double s = this.theme.textHeight();
      renderer.quad(this.x + pad, this.y + pad, s, s, this.checked ? GuiRenderer.FAVORITE_YES : GuiRenderer.FAVORITE_NO, this.getColor());
   }

   protected abstract Color getColor();
}
