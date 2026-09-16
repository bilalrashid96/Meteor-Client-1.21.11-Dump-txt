package meteordevelopment.meteorclient.settings;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.lang.reflect.AccessFlag;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_1887;
import net.minecraft.class_1893;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5321;
import net.minecraft.class_7924;

public class EnchantmentListSetting extends Setting<Set<class_5321<class_1887>>> {
   public EnchantmentListSetting(String name, String description, Set<class_5321<class_1887>> defaultValue, Consumer<Set<class_5321<class_1887>>> onChanged, Consumer<Setting<Set<class_5321<class_1887>>>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   public void resetImpl() {
      this.value = new ObjectOpenHashSet((Collection)this.defaultValue);
   }

   protected Set<class_5321<class_1887>> parseImpl(String str) {
      String[] values = str.split(",");
      Set<class_5321<class_1887>> enchs = new ObjectOpenHashSet(values.length);

      for(String value : values) {
         String name = value.trim();
         class_2960 id;
         if (name.contains(":")) {
            id = class_2960.method_60654(name);
         } else {
            id = class_2960.method_60656(name);
         }

         enchs.add(class_5321.method_29179(class_7924.field_41265, id));
      }

      return enchs;
   }

   protected boolean isValueValid(Set<class_5321<class_1887>> value) {
      return true;
   }

   public Iterable<class_2960> getIdentifierSuggestions() {
      return (Iterable)Optional.ofNullable(class_310.method_1551().method_1562()).flatMap((networkHandler) -> networkHandler.method_29091().method_46759(class_7924.field_41265)).map(class_2378::method_10235).orElse(Set.of());
   }

   public class_2487 save(class_2487 tag) {
      class_2499 valueTag = new class_2499();

      for(class_5321<class_1887> ench : (Set)this.get()) {
         valueTag.add(class_2519.method_23256(ench.method_29177().toString()));
      }

      tag.method_10566("value", valueTag);
      return tag;
   }

   public Set<class_5321<class_1887>> load(class_2487 tag) {
      ((Set)this.get()).clear();

      for(class_2520 tagI : tag.method_68569("value")) {
         ((Set)this.get()).add(class_5321.method_29179(class_7924.field_41265, class_2960.method_60654((String)tagI.method_68658().orElse(""))));
      }

      return (Set)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, Set<class_5321<class_1887>>, EnchantmentListSetting> {
      private static final Set<class_5321<class_1887>> VANILLA_DEFAULTS;

      public Builder() {
         super(new ObjectOpenHashSet());
      }

      public Builder vanillaDefaults() {
         return (Builder)this.defaultValue(VANILLA_DEFAULTS);
      }

      @SafeVarargs
      public final Builder defaultValue(class_5321<class_1887>... defaults) {
         return (Builder)this.defaultValue(defaults != null ? new ObjectOpenHashSet(defaults) : new ObjectOpenHashSet());
      }

      public EnchantmentListSetting build() {
         return new EnchantmentListSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }

      static {
         Stream var10000 = Arrays.stream(class_1893.class.getDeclaredFields()).filter((field) -> field.accessFlags().containsAll(List.of(AccessFlag.PUBLIC, AccessFlag.STATIC, AccessFlag.FINAL))).filter((field) -> field.getType() == class_5321.class).map((field) -> {
            try {
               return field.get((Object)null);
            } catch (IllegalAccessException var2) {
               return null;
            }
         }).filter(Objects::nonNull);
         Objects.requireNonNull(class_5321.class);
         VANILLA_DEFAULTS = (Set)var10000.map(class_5321.class::cast).filter((registryKey) -> registryKey.method_58273() == class_7924.field_41265).collect(Collectors.toSet());
      }
   }
}
