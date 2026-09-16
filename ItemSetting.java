package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.class_1792;
import net.minecraft.class_2487;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class ItemSetting extends Setting<class_1792> {
   public final Predicate<class_1792> filter;

   public ItemSetting(String name, String description, class_1792 defaultValue, Consumer<class_1792> onChanged, Consumer<Setting<class_1792>> onModuleActivated, IVisible visible, Predicate<class_1792> filter) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.filter = filter;
   }

   protected class_1792 parseImpl(String str) {
      return (class_1792)parseId(class_7923.field_41178, str);
   }

   protected boolean isValueValid(class_1792 value) {
      return this.filter == null || this.filter.test(value);
   }

   public Iterable<class_2960> getIdentifierSuggestions() {
      return class_7923.field_41178.method_10235();
   }

   public class_2487 save(class_2487 tag) {
      tag.method_10582("value", class_7923.field_41178.method_10221((class_1792)this.get()).toString());
      return tag;
   }

   public class_1792 load(class_2487 tag) {
      this.value = (class_1792)class_7923.field_41178.method_63535(class_2960.method_60654(tag.method_68564("value", "")));
      if (this.filter != null && !this.filter.test(this.value)) {
         for(class_1792 item : class_7923.field_41178) {
            if (this.filter.test(item)) {
               this.value = item;
               break;
            }
         }
      }

      return (class_1792)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, class_1792, ItemSetting> {
      private Predicate<class_1792> filter;

      public Builder() {
         super((Object)null);
      }

      public Builder filter(Predicate<class_1792> filter) {
         this.filter = filter;
         return this;
      }

      public ItemSetting build() {
         return new ItemSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible, this.filter);
      }
   }
}
