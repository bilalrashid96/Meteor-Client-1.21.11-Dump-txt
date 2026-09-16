package meteordevelopment.meteorclient.gui.tabs.builtin;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.pathing.PathManagers;
import net.minecraft.class_437;

public class PathManagerTab extends Tab {
   public PathManagerTab() {
      super(PathManagers.get().getName());
   }

   public TabScreen createScreen(GuiTheme theme) {
      return new PathManagerScreen(theme, this);
   }

   public boolean isScreen(class_437 screen) {
      return screen instanceof PathManagerScreen;
   }

   private static class PathManagerScreen extends WindowTabScreen {
      public PathManagerScreen(GuiTheme theme, Tab tab) {
         super(theme, tab);
         PathManagers.get().getSettings().get().onActivated();
      }

      public void initWidgets() {
         WTextBox filter = (WTextBox)this.add(this.theme.textBox("")).minWidth((double)400.0F).expandX().widget();
         filter.setFocused(true);
         filter.action = () -> {
            this.clear();
            this.add(filter);
            this.add(this.theme.settings(PathManagers.get().getSettings().get(), filter.get().trim())).expandX();
         };
         this.add(this.theme.settings(PathManagers.get().getSettings().get(), filter.get().trim())).expandX();
      }

      protected void onClosed() {
         PathManagers.get().getSettings().save();
      }
   }
}
