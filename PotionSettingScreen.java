package meteordevelopment.meteorclient.gui.screens.settings;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.PotionSetting;
import meteordevelopment.meteorclient.utils.misc.MyPotion;
import net.minecraft.class_1074;

public class PotionSettingScreen extends WindowScreen {
   private final PotionSetting setting;

   public PotionSettingScreen(GuiTheme theme, PotionSetting setting) {
      super(theme, "Select Potion");
      this.setting = setting;
   }

   public void initWidgets() {
      WTable table = (WTable)this.add(this.theme.table()).expandX().widget();

      for(MyPotion potion : MyPotion.values()) {
         table.add(this.theme.itemWithLabel(potion.potion, class_1074.method_4662(potion.potion.method_7909().method_7876(), new Object[0])));
         WButton select = (WButton)table.add(this.theme.button("Select")).widget();
         select.action = () -> {
            this.setting.set(potion);
            this.method_25419();
         };
         table.row();
      }

   }
}
