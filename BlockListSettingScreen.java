package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.Collection;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.settings.base.CollectionListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_7922;
import net.minecraft.class_7923;

public class BlockListSettingScreen extends CollectionListSettingScreen<class_2248> {
   public BlockListSettingScreen(GuiTheme theme, BlockListSetting setting) {
      super(theme, "Select Blocks", setting, (Collection)setting.get(), class_7923.field_41175);
   }

   protected boolean includeValue(class_2248 value) {
      if (class_7923.field_41175.method_10221(value).method_12832().endsWith("_wall_banner")) {
         return false;
      } else {
         Predicate<class_2248> filter = ((BlockListSetting)this.setting).filter;
         if (filter == null) {
            return value != class_2246.field_10124;
         } else {
            return filter.test(value);
         }
      }
   }

   protected WWidget getValueWidget(class_2248 value) {
      return this.theme.itemWithLabel(value.method_8389().method_7854(), Names.get(value));
   }

   protected String[] getValueNames(class_2248 value) {
      return new String[]{Names.get(value), class_7923.field_41175.method_10221(value).toString()};
   }

   protected class_2248 getAdditionalValue(class_2248 value) {
      String path = class_7923.field_41175.method_10221(value).method_12832();
      if (!path.endsWith("_banner")) {
         return null;
      } else {
         class_7922 var10000 = class_7923.field_41175;
         String var10001 = path.substring(0, path.length() - 6);
         return (class_2248)var10000.method_63535(class_2960.method_60656(var10001 + "wall_banner"));
      }
   }
}
