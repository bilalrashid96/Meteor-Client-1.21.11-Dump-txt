package meteordevelopment.meteorclient.gui.screens.settings.base;

import java.util.Comparator;
import java.util.Map;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import org.jetbrains.annotations.Nullable;

public abstract class CollectionMapSettingScreen<K, V> extends WindowScreen {
   private final Setting<?> setting;
   protected final Map<K, V> map;
   private final Iterable<K> registry;
   private WTable table;
   private String filterText = "";

   public CollectionMapSettingScreen(GuiTheme theme, String title, Setting<?> setting, Map<K, V> map, Iterable<K> registry) {
      super(theme, title);
      this.setting = setting;
      this.map = map;
      this.registry = registry;
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
      Comparator<K> prioritizeChanged = Comparator.comparing((key) -> {
         Object patt0$temp = this.map.get(key);
         boolean var10000;
         if (patt0$temp instanceof IChangeable changeable) {
            if (changeable.isChanged()) {
               var10000 = true;
               return !var10000;
            }
         }

         var10000 = false;
         return !var10000;
      });
      Iterable<K> sorted = SortingHelper.<K>sortWithPriority(this.registry, this::includeValue, this::getValueNames, this.filterText, prioritizeChanged);
      sorted.forEach((t) -> {
         V data;
         boolean var10000;
         label17: {
            data = (V)this.map.get(t);
            if (data instanceof IChangeable changeable) {
               if (changeable.isChanged()) {
                  var10000 = true;
                  break label17;
               }
            }

            var10000 = false;
         }

         boolean isChanged = var10000;
         this.table.add(this.getValueWidget(t)).expandCellX();
         this.table.add(this.theme.label(isChanged ? "*" : " "));
         this.table.add(this.getDataWidget(t, data));
         WButton reset = (WButton)this.table.add(this.theme.button(GuiRenderer.RESET)).widget();
         reset.action = () -> this.removeValue(t);
         reset.tooltip = "Reset";
         this.table.row();
      });
   }

   protected void invalidateTable() {
      this.table.clear();
      this.initTable();
   }

   protected void removeValue(K value) {
      if (this.map.remove(value) != null) {
         this.setting.onChanged();
         this.invalidateTable();
      }

   }

   protected boolean includeValue(K value) {
      return true;
   }

   protected abstract WWidget getValueWidget(K var1);

   protected abstract WWidget getDataWidget(K var1, @Nullable V var2);

   protected abstract String[] getValueNames(K var1);
}
