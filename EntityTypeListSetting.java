package meteordevelopment.meteorclient.settings;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import net.minecraft.class_1299;
import net.minecraft.class_1311;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class EntityTypeListSetting extends Setting<Set<class_1299<?>>> {
   public final Predicate<class_1299<?>> filter;
   private List<String> suggestions;
   private static final List<String> groups = List.of("animal", "wateranimal", "monster", "ambient", "misc");

   public EntityTypeListSetting(String name, String description, Set<class_1299<?>> defaultValue, Consumer<Set<class_1299<?>>> onChanged, Consumer<Setting<Set<class_1299<?>>>> onModuleActivated, IVisible visible, Predicate<class_1299<?>> filter) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.filter = filter;
   }

   public void resetImpl() {
      this.value = new ObjectOpenHashSet((Collection)this.defaultValue);
   }

   protected Set<class_1299<?>> parseImpl(String str) {
      String[] values = str.split(",");
      Set<class_1299<?>> entities = new ObjectOpenHashSet(values.length);

      try {
         for(String value : values) {
            class_1299<?> entity = (class_1299)parseId(class_7923.field_41177, value);
            if (entity != null) {
               entities.add(entity);
            } else {
               String lowerValue = value.trim().toLowerCase();
               if (groups.contains(lowerValue)) {
                  for(class_1299<?> entityType : class_7923.field_41177) {
                     if (this.filter == null || this.filter.test(entityType)) {
                        switch (lowerValue) {
                           case "animal":
                              if (entityType.method_5891() == class_1311.field_6294) {
                                 entities.add(entityType);
                              }
                              break;
                           case "wateranimal":
                              if (entityType.method_5891() == class_1311.field_24460 || entityType.method_5891() == class_1311.field_6300 || entityType.method_5891() == class_1311.field_30092 || entityType.method_5891() == class_1311.field_34447) {
                                 entities.add(entityType);
                              }
                              break;
                           case "monster":
                              if (entityType.method_5891() == class_1311.field_6302) {
                                 entities.add(entityType);
                              }
                              break;
                           case "ambient":
                              if (entityType.method_5891() == class_1311.field_6303) {
                                 entities.add(entityType);
                              }
                              break;
                           case "misc":
                              if (entityType.method_5891() == class_1311.field_17715) {
                                 entities.add(entityType);
                              }
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var14) {
      }

      return entities;
   }

   protected boolean isValueValid(Set<class_1299<?>> value) {
      return true;
   }

   public List<String> getSuggestions() {
      if (this.suggestions == null) {
         this.suggestions = new ArrayList(groups);

         for(class_1299<?> entityType : class_7923.field_41177) {
            if (this.filter == null || this.filter.test(entityType)) {
               this.suggestions.add(class_7923.field_41177.method_10221(entityType).toString());
            }
         }
      }

      return this.suggestions;
   }

   public class_2487 save(class_2487 tag) {
      class_2499 valueTag = new class_2499();

      for(class_1299<?> entityType : (Set)this.get()) {
         valueTag.add(class_2519.method_23256(class_7923.field_41177.method_10221(entityType).toString()));
      }

      tag.method_10566("value", valueTag);
      return tag;
   }

   public Set<class_1299<?>> load(class_2487 tag) {
      ((Set)this.get()).clear();

      for(class_2520 tagI : tag.method_68569("value")) {
         class_1299<?> type = (class_1299)class_7923.field_41177.method_63535(class_2960.method_60654((String)tagI.method_68658().orElse("")));
         if (this.filter == null || this.filter.test(type)) {
            ((Set)this.get()).add(type);
         }
      }

      return (Set)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, Set<class_1299<?>>, EntityTypeListSetting> {
      private Predicate<class_1299<?>> filter;

      public Builder() {
         super(new ObjectOpenHashSet(0));
      }

      public Builder defaultValue(class_1299<?>... defaults) {
         return (Builder)this.defaultValue(defaults != null ? new ObjectOpenHashSet(defaults) : new ObjectOpenHashSet(0));
      }

      public Builder onlyAttackable() {
         this.filter = EntityUtils::isAttackable;
         return this;
      }

      public Builder filter(Predicate<class_1299<?>> filter) {
         this.filter = filter;
         return this;
      }

      public EntityTypeListSetting build() {
         return new EntityTypeListSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible, this.filter);
      }
   }
}
