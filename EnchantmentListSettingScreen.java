package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.Collection;
import java.util.Set;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.settings.base.DynamicRegistryListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.class_1887;
import net.minecraft.class_5321;
import net.minecraft.class_7924;

public class EnchantmentListSettingScreen extends DynamicRegistryListSettingScreen<class_1887> {
   public EnchantmentListSettingScreen(GuiTheme theme, Setting<Set<class_5321<class_1887>>> setting) {
      super(theme, "Select Enchantments", setting, (Collection)setting.get(), class_7924.field_41265);
   }

   protected WWidget getValueWidget(class_5321<class_1887> value) {
      return this.theme.label(Names.get(value));
   }

   protected String[] getValueNames(class_5321<class_1887> value) {
      return new String[]{Names.get(value), value.method_29177().toString()};
   }
}
