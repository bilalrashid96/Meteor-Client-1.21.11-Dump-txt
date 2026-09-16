package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_2487;

public class EnumSetting<T extends Enum<?>> extends Setting<T> {
   private final T[] values;
   private final List<String> suggestions;

   public EnumSetting(String name, String description, T defaultValue, Consumer<T> onChanged, Consumer<Setting<T>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.values = (T[])(defaultValue.getDeclaringClass().getEnumConstants());
      this.suggestions = new ArrayList(this.values.length);

      for(T value : this.values) {
         this.suggestions.add(value.toString());
      }

   }

   protected T parseImpl(String str) {
      for(T possibleValue : this.values) {
         if (str.equalsIgnoreCase(possibleValue.toString())) {
            return possibleValue;
         }
      }

      return null;
   }

   protected boolean isValueValid(T value) {
      return true;
   }

   public List<String> getSuggestions() {
      return this.suggestions;
   }

   public class_2487 save(class_2487 tag) {
      tag.method_10582("value", ((Enum)this.get()).toString());
      return tag;
   }

   public T load(class_2487 tag) {
      this.parse(tag.method_68564("value", ""));
      return (T)(this.get());
   }

   public static class Builder<T extends Enum<?>> extends Setting.SettingBuilder<Builder<T>, T, EnumSetting<T>> {
      public Builder() {
         super((Object)null);
      }

      public EnumSetting<T> build() {
         return new EnumSetting<T>(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
