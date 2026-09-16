package meteordevelopment.meteorclient.gui.themes.meteor;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.BaseWidget;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;

public interface MeteorWidget extends BaseWidget {
   default MeteorGuiTheme theme() {
      return (MeteorGuiTheme)this.getTheme();
   }

   default void renderBackground(GuiRenderer renderer, WWidget widget, Color outlineColor, Color backgroundColor) {
      MeteorGuiTheme theme = this.theme();
      double s = theme.scale((double)2.0F);
      renderer.quad(widget.x + s, widget.y + s, widget.width - s * (double)2.0F, widget.height - s * (double)2.0F, backgroundColor);
      renderer.quad(widget.x, widget.y, widget.width, s, outlineColor);
      renderer.quad(widget.x, widget.y + widget.height - s, widget.width, s, outlineColor);
      renderer.quad(widget.x, widget.y + s, s, widget.height - s * (double)2.0F, outlineColor);
      renderer.quad(widget.x + widget.width - s, widget.y + s, s, widget.height - s * (double)2.0F, outlineColor);
   }

   default void renderBackground(GuiRenderer renderer, WWidget widget, boolean pressed, boolean mouseOver) {
      MeteorGuiTheme theme = this.theme();
      this.renderBackground(renderer, widget, theme.outlineColor.get(pressed, mouseOver), theme.backgroundColor.get(pressed, mouseOver));
   }
}
