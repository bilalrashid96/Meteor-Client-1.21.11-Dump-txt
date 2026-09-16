package meteordevelopment.meteorclient.settings;

import java.util.List;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_2487;

public class ColorSetting extends Setting<SettingColor> {
   private static final List<String> SUGGESTIONS = List.of("0 0 0 255", "225 25 25 255", "25 225 25 255", "25 25 225 255", "255 255 255 255");

   public ColorSetting(String name, String description, SettingColor defaultValue, Consumer<SettingColor> onChanged, Consumer<Setting<SettingColor>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   protected SettingColor parseImpl(String str) {
      try {
         String[] strs = str.split(" ");
         return new SettingColor(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), Integer.parseInt(strs[3]));
      } catch (NumberFormatException | IndexOutOfBoundsException var3) {
         return null;
      }
   }

   public void resetImpl() {
      if (this.value == null) {
         this.value = new SettingColor(this.defaultValue);
      } else {
         ((SettingColor)this.value).set(this.defaultValue);
      }

   }

   protected boolean isValueValid(SettingColor value) {
      value.validate();
      return true;
   }

   public List<String> getSuggestions() {
      return SUGGESTIONS;
   }

   protected class_2487 save(class_2487 tag) {
      tag.method_10566("value", ((SettingColor)this.get()).toTag());
      return tag;
   }

   public SettingColor load(class_2487 tag) {
      ((SettingColor)this.get()).fromTag(tag.method_68568("value"));
      return (SettingColor)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, SettingColor, ColorSetting> {
      public Builder() {
         super(new SettingColor());
      }

      public ColorSetting build() {
         return new ColorSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }

      public Builder defaultValue(SettingColor defaultValue) {
         ((SettingColor)this.defaultValue).set((Color)defaultValue);
         return this;
      }

      public Builder defaultValue(Color defaultValue) {
         ((SettingColor)this.defaultValue).set(defaultValue);
         return this;
      }
   }
}
