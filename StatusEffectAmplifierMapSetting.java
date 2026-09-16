package meteordevelopment.meteorclient.settings;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.function.Consumer;
import net.minecraft.class_1291;
import net.minecraft.class_2487;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class StatusEffectAmplifierMapSetting extends Setting<Reference2IntMap<class_1291>> {
   public static final Reference2IntMap<class_1291> EMPTY_STATUS_EFFECT_MAP = createStatusEffectMap();

   public StatusEffectAmplifierMapSetting(String name, String description, Reference2IntMap<class_1291> defaultValue, Consumer<Reference2IntMap<class_1291>> onChanged, Consumer<Setting<Reference2IntMap<class_1291>>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   public void resetImpl() {
      this.value = new Reference2IntOpenHashMap(this.defaultValue);
   }

   protected Reference2IntMap<class_1291> parseImpl(String str) {
      String[] values = str.split(",");
      Reference2IntMap<class_1291> effects = new Reference2IntOpenHashMap(EMPTY_STATUS_EFFECT_MAP);

      try {
         for(String value : values) {
            String[] split = value.split(" ");
            class_1291 effect = (class_1291)parseId(class_7923.field_41174, split[0]);
            int level = Integer.parseInt(split[1]);
            effects.put(effect, level);
         }
      } catch (Exception var11) {
      }

      return effects;
   }

   protected boolean isValueValid(Reference2IntMap<class_1291> value) {
      return true;
   }

   public class_2487 save(class_2487 tag) {
      class_2487 valueTag = new class_2487();
      ObjectIterator var3 = ((Reference2IntMap)this.get()).keySet().iterator();

      while(var3.hasNext()) {
         class_1291 statusEffect = (class_1291)var3.next();
         class_2960 id = class_7923.field_41174.method_10221(statusEffect);
         if (id != null) {
            valueTag.method_10569(id.toString(), ((Reference2IntMap)this.get()).getInt(statusEffect));
         }
      }

      tag.method_10566("value", valueTag);
      return tag;
   }

   private static Reference2IntMap<class_1291> createStatusEffectMap() {
      Reference2IntMap<class_1291> map = new Reference2IntArrayMap(class_7923.field_41174.method_10235().size());
      class_7923.field_41174.forEach((potion) -> map.put(potion, 0));
      return map;
   }

   public Reference2IntMap<class_1291> load(class_2487 tag) {
      ((Reference2IntMap)this.get()).clear();
      class_2487 valueTag = tag.method_68568("value");

      for(String key : valueTag.method_10541()) {
         class_1291 statusEffect = (class_1291)class_7923.field_41174.method_63535(class_2960.method_60654(key));
         if (statusEffect != null) {
            ((Reference2IntMap)this.get()).put(statusEffect, valueTag.method_68083(key, 0));
         }
      }

      return (Reference2IntMap)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, Reference2IntMap<class_1291>, StatusEffectAmplifierMapSetting> {
      public Builder() {
         super(new Reference2IntOpenHashMap(0));
      }

      public StatusEffectAmplifierMapSetting build() {
         return new StatusEffectAmplifierMapSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
