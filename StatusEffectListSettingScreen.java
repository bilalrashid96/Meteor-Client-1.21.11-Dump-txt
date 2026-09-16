package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.settings.base.CollectionListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.class_1291;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_7923;
import net.minecraft.class_9334;

public class StatusEffectListSettingScreen extends CollectionListSettingScreen<class_1291> {
   public StatusEffectListSettingScreen(GuiTheme theme, Setting<List<class_1291>> setting) {
      super(theme, "Select Effects", setting, (Collection)setting.get(), class_7923.field_41174);
   }

   protected WWidget getValueWidget(class_1291 value) {
      return this.theme.itemWithLabel(this.getPotionStack(value), Names.get(value));
   }

   protected String[] getValueNames(class_1291 value) {
      return new String[]{Names.get(value), class_7923.field_41174.method_10221(value).toString()};
   }

   private class_1799 getPotionStack(class_1291 effect) {
      class_1799 potion = class_1802.field_8574.method_7854();
      potion.method_57379(class_9334.field_49651, new class_1844(((class_1844)potion.method_58694(class_9334.field_49651)).comp_2378(), Optional.of(effect.method_5556()), ((class_1844)potion.method_58694(class_9334.field_49651)).comp_2380(), Optional.empty()));
      return potion;
   }
}
