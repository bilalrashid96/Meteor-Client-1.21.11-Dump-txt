package meteordevelopment.meteorclient.settings;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_2487;

public class BoolSetting extends Setting<Boolean> {
   private static final List<String> SUGGESTIONS = List.of("true", "false", "toggle");

   private BoolSetting(String name, String description, Boolean defaultValue, Consumer<Boolean> onChanged, Consumer<Setting<Boolean>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   protected Boolean parseImpl(String str) {
      if (!str.equalsIgnoreCase("true") && !str.equalsIgnoreCase("1")) {
         if (!str.equalsIgnoreCase("false") && !str.equalsIgnoreCase("0")) {
            return str.equalsIgnoreCase("toggle") ? !(Boolean)this.get() : null;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   protected boolean isValueValid(Boolean value) {
      return true;
   }

   public List<String> getSuggestions() {
      return SUGGESTIONS;
   }

   public class_2487 save(class_2487 tag) {
      tag.method_10556("value", (Boolean)this.get());
      return tag;
   }

   public Boolean load(class_2487 tag) {
      this.set(tag.method_68566("value", false));
      return (Boolean)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, Boolean, BoolSetting> {
      public Builder() {
         super(false);
      }

      public BoolSetting build() {
         return new BoolSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
