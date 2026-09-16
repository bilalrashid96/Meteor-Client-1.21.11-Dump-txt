package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.class_2248;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class BlockListSetting extends Setting<List<class_2248>> {
   public final Predicate<class_2248> filter;

   public BlockListSetting(String name, String description, List<class_2248> defaultValue, Consumer<List<class_2248>> onChanged, Consumer<Setting<List<class_2248>>> onModuleActivated, Predicate<class_2248> filter, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.filter = filter;
   }

   public void resetImpl() {
      this.value = new ArrayList((Collection)this.defaultValue);
   }

   protected List<class_2248> parseImpl(String str) {
      String[] values = str.split(",");
      List<class_2248> blocks = new ArrayList(values.length);

      try {
         for(String value : values) {
            class_2248 block = (class_2248)parseId(class_7923.field_41175, value);
            if (block != null && (this.filter == null || this.filter.test(block))) {
               blocks.add(block);
            }
         }
      } catch (Exception var9) {
      }

      return blocks;
   }

   protected boolean isValueValid(List<class_2248> value) {
      return true;
   }

   public Iterable<class_2960> getIdentifierSuggestions() {
      return class_7923.field_41175.method_10235();
   }

   protected class_2487 save(class_2487 tag) {
      class_2499 valueTag = new class_2499();

      for(class_2248 block : (List)this.get()) {
         valueTag.add(class_2519.method_23256(class_7923.field_41175.method_10221(block).toString()));
      }

      tag.method_10566("value", valueTag);
      return tag;
   }

   protected List<class_2248> load(class_2487 tag) {
      ((List)this.get()).clear();

      for(class_2520 tagI : tag.method_68569("value")) {
         class_2248 block = (class_2248)class_7923.field_41175.method_63535(class_2960.method_60654((String)tagI.method_68658().orElse("")));
         if (this.filter == null || this.filter.test(block)) {
            ((List)this.get()).add(block);
         }
      }

      return (List)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, List<class_2248>, BlockListSetting> {
      private Predicate<class_2248> filter;

      public Builder() {
         super(new ArrayList(0));
      }

      public Builder defaultValue(class_2248... defaults) {
         return (Builder)this.defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList());
      }

      public Builder filter(Predicate<class_2248> filter) {
         this.filter = filter;
         return this;
      }

      public BlockListSetting build() {
         return new BlockListSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.filter, this.visible);
      }
   }
}
