package meteordevelopment.meteorclient.gui.tabs;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;

public abstract class WindowTabScreen extends TabScreen {
   protected final WWindow window;

   public WindowTabScreen(GuiTheme theme, Tab tab) {
      super(theme, tab);
      this.window = (WWindow)super.add(theme.window(tab.name)).center().widget();
   }

   public <W extends WWidget> Cell<W> add(W widget) {
      return this.window.<W>add(widget);
   }

   public void clear() {
      this.window.clear();
   }
}
