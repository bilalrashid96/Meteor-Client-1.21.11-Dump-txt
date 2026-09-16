package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_2394;
import net.minecraft.class_2396;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class ParticleTypeListSetting extends Setting<List<class_2396<?>>> {
   public ParticleTypeListSetting(String name, String description, List<class_2396<?>> defaultValue, Consumer<List<class_2396<?>>> onChanged, Consumer<Setting<List<class_2396<?>>>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   public void resetImpl() {
      this.value = new ArrayList((Collection)this.defaultValue);
   }

   protected List<class_2396<?>> parseImpl(String str) {
      String[] values = str.split(",");
      List<class_2396<?>> particleTypes = new ArrayList(values.length);

      try {
         for(String value : values) {
            class_2396<?> particleType = (class_2396)parseId(class_7923.field_41180, value);
            if (particleType instanceof class_2394) {
               particleTypes.add(particleType);
            }
         }
      } catch (Exception var9) {
      }

      return particleTypes;
   }

   protected boolean isValueValid(List<class_2396<?>> value) {
      return true;
   }

   public Iterable<class_2960> getIdentifierSuggestions() {
      return class_7923.field_41180.method_10235();
   }

   public class_2487 save(class_2487 tag) {
      class_2499 valueTag = new class_2499();

      for(class_2396<?> particleType : (List)this.get()) {
         class_2960 id = class_7923.field_41180.method_10221(particleType);
         if (id != null) {
            valueTag.add(class_2519.method_23256(id.toString()));
         }
      }

      tag.method_10566("value", valueTag);
      return tag;
   }

   public List<class_2396<?>> load(class_2487 tag) {
      ((List)this.get()).clear();

      for(class_2520 tagI : tag.method_68569("value")) {
         class_2396<?> particleType = (class_2396)class_7923.field_41180.method_63535(class_2960.method_60654((String)tagI.method_68658().orElse("")));
         if (particleType != null) {
            ((List)this.get()).add(particleType);
         }
      }

      return (List)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, List<class_2396<?>>, ParticleTypeListSetting> {
      public Builder() {
         super(new ArrayList(0));
      }

      public Builder defaultValue(class_2396<?>... defaults) {
         return (Builder)this.defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList());
      }

      public ParticleTypeListSetting build() {
         return new ParticleTypeListSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
