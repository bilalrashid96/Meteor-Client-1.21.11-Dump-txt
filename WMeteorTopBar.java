package meteordevelopment.meteorclient.gui.themes.meteor.widgets;

import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.WTopBar;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorTopBar extends WTopBar implements MeteorWidget {
   protected Color getButtonColor(boolean pressed, boolean hovered) {
      return this.theme().backgroundColor.get(pressed, hovered);
   }

   protected Color getNameColor() {
      return this.theme().textColor.get();
   }
}
