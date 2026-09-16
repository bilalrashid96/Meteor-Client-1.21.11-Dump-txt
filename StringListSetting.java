package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;

public class StringListSetting extends Setting<List<String>> {
   public final Class<? extends WTextBox.Renderer> renderer;
   public final CharFilter filter;

   public StringListSetting(String name, String description, List<String> defaultValue, Consumer<List<String>> onChanged, Consumer<Setting<List<String>>> onModuleActivated, IVisible visible, Class<? extends WTextBox.Renderer> renderer, CharFilter filter) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.renderer = renderer;
      this.filter = filter;
   }

   protected List<String> parseImpl(String str) {
      return Arrays.asList(str.split(","));
   }

   protected boolean isValueValid(List<String> value) {
      return true;
   }

   public class_2487 save(class_2487 tag) {
      class_2499 valueTag = new class_2499();

      for(int i = 0; i < ((List)this.value).size(); ++i) {
         valueTag.method_68586(i, class_2519.method_23256((String)((List)this.get()).get(i)));
      }

      tag.method_10566("value", valueTag);
      return tag;
   }

   public List<String> load(class_2487 tag) {
      ((List)this.get()).clear();

      for(class_2520 tagI : tag.method_68569("value")) {
         ((List)this.get()).add((String)tagI.method_68658().orElse(""));
      }

      return (List)this.get();
   }

   public void resetImpl() {
      this.value = new ArrayList((Collection)this.defaultValue);
   }

   public static void fillTable(GuiTheme theme, WTable table, StringListSetting setting) {
      table.clear();
      ArrayList<String> strings = new ArrayList((Collection)setting.get());
      CharFilter filter = setting.filter == null ? (text, c) -> true : setting.filter;

      for(int i = 0; i < ((List)setting.get()).size(); ++i) {
         String message = (String)((List)setting.get()).get(i);
         WTextBox textBox = (WTextBox)table.add(theme.textBox(message, filter, setting.renderer)).expandX().widget();
         textBox.action = () -> strings.set(i, textBox.get());
         textBox.actionOnUnfocused = () -> setting.set(strings);
         WMinus delete = (WMinus)table.add(theme.minus()).widget();
         delete.action = () -> {
            strings.remove(i);
            setting.set(strings);
            fillTable(theme, table, setting);
         };
         table.row();
      }

      if (!((List)setting.get()).isEmpty()) {
         table.add(theme.horizontalSeparator()).expandX();
         table.row();
      }

      WButton add = (WButton)table.add(theme.button("Add")).expandX().widget();
      add.action = () -> {
         strings.add("");
         setting.set(strings);
         fillTable(theme, table, setting);
      };
      WButton reset = (WButton)table.add(theme.button(GuiRenderer.RESET)).widget();
      reset.action = () -> {
         setting.reset();
         fillTable(theme, table, setting);
      };
      reset.tooltip = "Reset";
   }

   public static class Builder extends Setting.SettingBuilder<Builder, List<String>, StringListSetting> {
      private Class<? extends WTextBox.Renderer> renderer;
      private CharFilter filter;

      public Builder() {
         super(new ArrayList(0));
      }

      public Builder defaultValue(String... defaults) {
         return (Builder)this.defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList());
      }

      public Builder renderer(Class<? extends WTextBox.Renderer> renderer) {
         this.renderer = renderer;
         return this;
      }

      public Builder filter(CharFilter filter) {
         this.filter = filter;
         return this;
      }

      public StringListSetting build() {
         return new StringListSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible, this.renderer, this.filter);
      }
   }
}
