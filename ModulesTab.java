package meteordevelopment.meteorclient.gui.tabs.builtin;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import net.minecraft.class_437;

public class ModulesTab extends Tab {
   public ModulesTab() {
      super("Modules");
   }

   public TabScreen createScreen(GuiTheme theme) {
      return theme.modulesScreen();
   }

   public boolean isScreen(class_437 screen) {
      return GuiThemes.get().isModulesScreen(screen);
   }
}
