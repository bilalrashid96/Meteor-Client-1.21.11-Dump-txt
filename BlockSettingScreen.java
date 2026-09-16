package meteordevelopment.meteorclient.gui.screens.settings;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.WItemWithLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.BlockSetting;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_7923;
import org.apache.commons.lang3.Strings;

public class BlockSettingScreen extends WindowScreen {
   private final BlockSetting setting;
   private WTable table;
   private WTextBox filter;
   private String filterText = "";

   public BlockSettingScreen(GuiTheme theme, BlockSetting setting) {
      super(theme, "Select Block");
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

   private void initTable() {
      for(class_2248 block : class_7923.field_41175) {
         if ((this.setting.filter == null || this.setting.filter.test(block)) && !this.skipValue(block)) {
            WItemWithLabel item = this.theme.itemWithLabel(block.method_8389().method_7854(), Names.get(block));
            if (this.filterText.isEmpty() || Strings.CI.contains(item.getLabelText(), this.filterText)) {
               this.table.add(item);
               WButton select = (WButton)this.table.add(this.theme.button("Select")).expandCellX().right().widget();
               select.action = () -> {
                  this.setting.set(block);
                  this.method_25419();
               };
               this.table.row();
            }
         }
      }

   }

   protected boolean skipValue(class_2248 value) {
      return value == class_2246.field_10124 || class_7923.field_41175.method_10221(value).method_12832().endsWith("_wall_banner");
   }
}
