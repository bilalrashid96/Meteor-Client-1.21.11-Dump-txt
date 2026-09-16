package meteordevelopment.meteorclient.gui.screens.settings;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.WItemWithLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.ItemSetting;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_7923;
import org.apache.commons.lang3.Strings;

public class ItemSettingScreen extends WindowScreen {
   private final ItemSetting setting;
   private WTable table;
   private WTextBox filter;
   private String filterText = "";

   public ItemSettingScreen(GuiTheme theme, ItemSetting setting) {
      super(theme, "Select item");
      this.setting = setting;
   }

   public void initWidgets() {
      this.filter = (WTextBox)this.add(this.theme.textBox("")).minWidth((double)400.0F).expandX().widget();
      this.filter.setFocused(true);
      this.filter.action = () -> {
         this.filterText = this.filter.get().trim();
         this.table.clear();
         this.initTable();
      };
      this.table = (WTable)this.add(this.theme.table()).expandX().widget();
      this.initTable();
   }

   public void initTable() {
      for(class_1792 item : class_7923.field_41178) {
         if ((this.setting.filter == null || this.setting.filter.test(item)) && item != class_1802.field_8162) {
            WItemWithLabel itemLabel = this.theme.itemWithLabel(item.method_7854(), Names.get(item));
            if (this.filterText.isEmpty() || Strings.CI.contains(itemLabel.getLabelText(), this.filterText)) {
               this.table.add(itemLabel);
               WButton select = (WButton)this.table.add(this.theme.button("Select")).expandCellX().right().widget();
               select.action = () -> {
                  this.setting.set(item);
                  this.method_25419();
               };
               this.table.row();
            }
         }
      }

   }
}
