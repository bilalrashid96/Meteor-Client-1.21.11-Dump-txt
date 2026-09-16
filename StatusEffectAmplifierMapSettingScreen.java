package meteordevelopment.meteorclient.gui.screens.settings;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WIntEdit;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.class_1291;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_9334;
import org.apache.commons.lang3.Strings;

public class StatusEffectAmplifierMapSettingScreen extends WindowScreen {
   private final Setting<Reference2IntMap<class_1291>> setting;
   private WTable table;
   private String filterText = "";

   public StatusEffectAmplifierMapSettingScreen(GuiTheme theme, Setting<Reference2IntMap<class_1291>> setting) {
      super(theme, "Modify Amplifiers");
      this.setting = setting;
   }

   public void initWidgets() {
      WTextBox filter = (WTextBox)this.add(this.theme.textBox("")).minWidth((double)400.0F).expandX().widget();
      filter.setFocused(true);
      filter.action = () -> {
         this.filterText = filter.get().trim();
         this.table.clear();
         this.initTable();
      };
      this.table = (WTable)this.add(this.theme.table()).expandX().widget();
      this.initTable();
   }

   private void initTable() {
      List<class_1291> statusEffects = new ArrayList(((Reference2IntMap)this.setting.get()).keySet());
      statusEffects.sort(Comparator.comparing(Names::get));

      for(class_1291 statusEffect : statusEffects) {
         String name = Names.get(statusEffect);
         if (Strings.CI.contains(name, this.filterText)) {
            this.table.add(this.theme.itemWithLabel(this.getPotionStack(statusEffect), name)).expandCellX();
            WIntEdit level = this.theme.intEdit(((Reference2IntMap)this.setting.get()).getInt(statusEffect), 0, Integer.MAX_VALUE, true);
            level.action = () -> {
               ((Reference2IntMap)this.setting.get()).put(statusEffect, level.get());
               this.setting.onChanged();
            };
            this.table.add(level).minWidth((double)50.0F);
            this.table.row();
         }
      }

   }

   private class_1799 getPotionStack(class_1291 effect) {
      class_1799 potion = class_1802.field_8574.method_7854();
      potion.method_57379(class_9334.field_49651, new class_1844(((class_1844)potion.method_58694(class_9334.field_49651)).comp_2378(), Optional.of(effect.method_5556()), ((class_1844)potion.method_58694(class_9334.field_49651)).comp_2380(), Optional.empty()));
      return potion;
   }
}
