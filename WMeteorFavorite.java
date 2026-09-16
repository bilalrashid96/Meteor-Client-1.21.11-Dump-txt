package meteordevelopment.meteorclient.gui.themes.meteor.widgets.pressable;

import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WFavorite;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorFavorite extends WFavorite implements MeteorWidget {
   public WMeteorFavorite(boolean checked) {
      super(checked);
   }

   protected Color getColor() {
      return this.theme().favoriteColor.get();
   }
}
