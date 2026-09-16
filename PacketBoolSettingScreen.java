package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.settings.base.CollectionListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.PacketListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.network.PacketUtils;
import net.minecraft.class_2596;

public class PacketBoolSettingScreen extends CollectionListSettingScreen<Class<? extends class_2596<?>>> {
   public PacketBoolSettingScreen(GuiTheme theme, Setting<Set<Class<? extends class_2596<?>>>> setting) {
      super(theme, "Select Packets", setting, (Collection)setting.get(), PacketUtils.PACKETS);
   }

   protected boolean includeValue(Class<? extends class_2596<?>> value) {
      Predicate<Class<? extends class_2596<?>>> filter = ((PacketListSetting)this.setting).filter;
      return filter == null ? true : filter.test(value);
   }

   protected WWidget getValueWidget(Class<? extends class_2596<?>> value) {
      return this.theme.label(PacketUtils.getName(value));
   }

   protected String[] getValueNames(Class<? extends class_2596<?>> value) {
      return new String[]{PacketUtils.getName(value), value.getSimpleName()};
   }
}
