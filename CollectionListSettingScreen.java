package meteordevelopment.meteorclient.gui.screens.settings.base;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.config.Config;

public abstract class CollectionListSettingScreen<T> extends WindowScreen {
   protected final Setting<?> setting;
   protected final Collection<T> collection;
   private final Iterable<T> registry;
   private WTable table;
   private String filterText = "";

   public CollectionListSettingScreen(GuiTheme theme, String title, Setting<?> setting, Collection<T> collection, Iterable<T> registry) {
      super(theme, title);
      this.registry = registry;
      this.setting = setting;
      this.collection = collection;
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
      WTable left = this.abc(this.registry, true, (t) -> {
         this.addValue(t);
         T v = (T)this.getAdditionalValue(t);
         if (v != null) {
            this.addValue(v);
         }

      });
      if ((Boolean)Config.get().syncListSettingWidths.get() || !left.cells.isEmpty()) {
         this.table.add(this.theme.verticalSeparator()).expandWidgetY();
      }

      WTable right = this.abc(this.collection, false, (t) -> {
         this.removeValue(t);
         T v = (T)this.getAdditionalValue(t);
         if (v != null) {
            this.removeValue(v);
         }

      });
      this.postWidgets(left, right);
   }

   private WTable abc(Iterable<T> iterable, boolean isLeft, Consumer<T> buttonAction) {
      Cell<WTable> cell = this.table.<WTable>add(this.theme.table()).top();
      if ((Boolean)Config.get().syncListSettingWidths.get()) {
         cell.group("sync-width");
      }

      WTable table = cell.widget();
      Predicate<T> predicate = isLeft ? (value) -> this.includeValue(value) && !this.collection.contains(value) : this::includeValue;
      Iterable<T> sorted = SortingHelper.<T>sort(iterable, predicate, this::getValueNames, this.filterText);
      sorted.forEach((t) -> {
         table.add(this.getValueWidget(t));
         WPressable button = (WPressable)table.add((WWidget)(isLeft ? this.theme.plus() : this.theme.minus())).expandCellX().right().widget();
         button.action = () -> buttonAction.accept(t);
         table.row();
      });
      if (!table.cells.isEmpty()) {
         cell.expandX();
      }

      return table;
   }

   protected void invalidateTable() {
      this.table.clear();
      this.initTable();
   }

   protected void addValue(T value) {
      if (!this.collection.contains(value)) {
         this.collection.add(value);
         this.setting.onChanged();
         this.invalidateTable();
      }

   }

   protected void removeValue(T value) {
      if (this.collection.remove(value)) {
         this.setting.onChanged();
         this.invalidateTable();
      }

   }

   protected void postWidgets(WTable left, WTable right) {
   }

   protected boolean includeValue(T value) {
      return true;
   }

   protected abstract WWidget getValueWidget(T var1);

   protected abstract String[] getValueNames(T var1);

   protected T getAdditionalValue(T value) {
      return null;
   }
}
