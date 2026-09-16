package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.Collection;
import java.util.List;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.settings.base.CollectionListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.class_1074;
import net.minecraft.class_3414;
import net.minecraft.class_7923;

public class SoundEventListSettingScreen extends CollectionListSettingScreen<class_3414> {
   public SoundEventListSettingScreen(GuiTheme theme, Setting<List<class_3414>> setting) {
      super(theme, "Select Sounds", setting, (Collection)setting.get(), class_7923.field_41172);
   }

   protected WWidget getValueWidget(class_3414 value) {
      return this.theme.label(value.comp_3319().method_12832());
   }

   protected String[] getValueNames(class_3414 value) {
      return new String[]{value.comp_3319().toString(), class_1074.method_4662("subtitles." + value.comp_3319().method_12832(), new Object[0])};
   }
}
