package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.class_2248;
import net.minecraft.class_2487;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class BlockSetting extends Setting<class_2248> {
   public final Predicate<class_2248> filter;

   public BlockSetting(String name, String description, class_2248 defaultValue, Consumer<class_2248> onChanged, Consumer<Setting<class_2248>> onModuleActivated, IVisible visible, Predicate<class_2248> filter) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.filter = filter;
   }

   protected class_2248 parseImpl(String str) {
      return (class_2248)parseId(class_7923.field_41175, str);
   }

   protected boolean isValueValid(class_2248 value) {
      return this.filter == null || this.filter.test(value);
   }

   public Iterable<class_2960> getIdentifierSuggestions() {
      return class_7923.field_41175.method_10235();
   }

   protected class_2487 save(class_2487 tag) {
      tag.method_10582("value", class_7923.field_41175.method_10221((class_2248)this.get()).toString());
      return tag;
   }

   protected class_2248 load(class_2487 tag) {
      this.value = (class_2248)class_7923.field_41175.method_63535(class_2960.method_60654(tag.method_68564("value", "")));
      if (this.filter != null && !this.filter.test(this.value)) {
         for(class_2248 block : class_7923.field_41175) {
            if (this.filter.test(block)) {
               this.value = block;
               break;
            }
         }
      }

      return (class_2248)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, class_2248, BlockSetting> {
      private Predicate<class_2248> filter;

      public Builder() {
         super((Object)null);
      }

      public Builder filter(Predicate<class_2248> filter) {
         this.filter = filter;
         return this;
      }

      public BlockSetting build() {
         return new BlockSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible, this.filter);
      }
   }
}
