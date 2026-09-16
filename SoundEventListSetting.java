package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_7923;

public class SoundEventListSetting extends Setting<List<class_3414>> {
   public SoundEventListSetting(String name, String description, List<class_3414> defaultValue, Consumer<List<class_3414>> onChanged, Consumer<Setting<List<class_3414>>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   public void resetImpl() {
      this.value = new ArrayList((Collection)this.defaultValue);
   }

   protected List<class_3414> parseImpl(String str) {
      String[] values = str.split(",");
      List<class_3414> sounds = new ArrayList(values.length);

      try {
         for(String value : values) {
            class_3414 sound = (class_3414)parseId(class_7923.field_41172, value);
            if (sound != null) {
               sounds.add(sound);
            }
         }
      } catch (Exception var9) {
      }

      return sounds;
   }

   protected boolean isValueValid(List<class_3414> value) {
      return true;
   }

   public Iterable<class_2960> getIdentifierSuggestions() {
      return class_7923.field_41172.method_10235();
   }

   public class_2487 save(class_2487 tag) {
      class_2499 valueTag = new class_2499();

      for(class_3414 sound : (List)this.get()) {
         class_2960 id = class_7923.field_41172.method_10221(sound);
         if (id != null) {
            valueTag.add(class_2519.method_23256(id.toString()));
         }
      }

      tag.method_10566("value", valueTag);
      return tag;
   }

   public List<class_3414> load(class_2487 tag) {
      ((List)this.get()).clear();

      for(class_2520 tagI : tag.method_68569("value")) {
         class_3414 soundEvent = (class_3414)class_7923.field_41172.method_63535(class_2960.method_60654((String)tagI.method_68658().orElse("")));
         if (soundEvent != null) {
            ((List)this.get()).add(soundEvent);
         }
      }

      return (List)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, List<class_3414>, SoundEventListSetting> {
      public Builder() {
         super(new ArrayList(0));
      }

      public Builder defaultValue(class_3414... defaults) {
         return (Builder)this.defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList());
      }

      public SoundEventListSetting build() {
         return new SoundEventListSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
