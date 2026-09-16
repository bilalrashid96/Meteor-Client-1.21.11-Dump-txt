package meteordevelopment.meteorclient.gui.screens;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.systems.modules.render.marker.BaseMarker;

public class MarkerScreen extends WindowScreen {
   private final BaseMarker marker;
   private WContainer settingsContainer;

   public MarkerScreen(GuiTheme theme, BaseMarker marker) {
      super(theme, marker.name.get());
      this.marker = marker;
   }

   public void initWidgets() {
      if (!this.marker.settings.groups.isEmpty()) {
         this.settingsContainer = (WContainer)this.add(this.theme.verticalList()).expandX().widget();
         this.settingsContainer.add(this.theme.settings(this.marker.settings)).expandX();
      }

      WWidget widget = this.getWidget(this.theme);
      if (widget != null) {
         this.add(this.theme.horizontalSeparator()).expandX();
         Cell<WWidget> cell = this.add(widget);
         if (widget instanceof WContainer) {
            cell.expandX();
         }
      }

   }

   public void method_25393() {
      super.method_25393();
      this.marker.settings.tick(this.settingsContainer, this.theme);
   }

   public WWidget getWidget(GuiTheme theme) {
      return null;
   }
}
