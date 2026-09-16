package meteordevelopment.meteorclient.gui;

import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;

public abstract class WindowScreen extends WidgetScreen {
   protected final WWindow window;

   public WindowScreen(GuiTheme theme, WWidget icon, String title) {
      super(theme, title);
      this.window = (WWindow)super.add(theme.window(icon, title)).center().widget();
      this.window.view.scrollOnlyWhenMouseOver = false;
   }

   public WindowScreen(GuiTheme theme, String title) {
      this(theme, (WWidget)null, title);
   }

   public <W extends WWidget> Cell<W> add(W widget) {
      return this.window.<W>add(widget);
   }

   public void clear() {
      this.window.clear();
   }
}
