package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.Collection;
import java.util.List;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.settings.base.CollectionListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class ModuleListSettingScreen extends CollectionListSettingScreen<Module> {
   public ModuleListSettingScreen(GuiTheme theme, Setting<List<Module>> setting) {
      super(theme, "Select Modules", setting, (Collection)setting.get(), Modules.get().getAll());
   }

   protected WWidget getValueWidget(Module value) {
      return this.theme.label(value.title);
   }

   protected String[] getValueNames(Module value) {
      String[] names = new String[value.aliases.length + 1];
      System.arraycopy(value.aliases, 0, names, 1, value.aliases.length);
      names[0] = value.title;
      return names;
   }
}
