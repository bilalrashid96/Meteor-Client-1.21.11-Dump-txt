package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2520;
import org.jetbrains.annotations.NotNull;

public class SettingGroup implements ISerializable<SettingGroup>, Iterable<Setting<?>> {
   public final String name;
   public boolean sectionExpanded;
   final List<Setting<?>> settings = new ArrayList(1);

   SettingGroup(String name, boolean sectionExpanded) {
      this.name = name;
      this.sectionExpanded = sectionExpanded;
   }

   public Setting<?> get(String name) {
      for(Setting<?> setting : this) {
         if (setting.name.equals(name)) {
            return setting;
         }
      }

      return null;
   }

   public <T extends Setting<?>> T add(T setting) {
      this.settings.add(setting);
      return setting;
   }

   public Setting<?> getByIndex(int index) {
      return (Setting)this.settings.get(index);
   }

   public boolean wasChanged() {
      for(Setting<?> setting : this.settings) {
         if (setting.wasChanged()) {
            return true;
         }
      }

      return false;
   }

   public @NotNull Iterator<Setting<?>> iterator() {
      return this.settings.iterator();
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("name", this.name);
      tag.method_10556("sectionExpanded", this.sectionExpanded);
      class_2499 settingsTag = new class_2499();

      for(Setting<?> setting : this) {
         if (setting.wasChanged()) {
            settingsTag.add(setting.toTag());
         }
      }

      if (!settingsTag.isEmpty()) {
         tag.method_10566("settings", settingsTag);
      }

      return tag;
   }

   public SettingGroup fromTag(class_2487 tag) {
      this.sectionExpanded = tag.method_68566("sectionExpanded", false);

      for(class_2520 t : tag.method_68569("settings")) {
         class_2487 settingTag = (class_2487)t;
         Setting<?> setting = this.get(settingTag.method_68564("name", ""));
         if (setting != null) {
            setting.fromTag(settingTag);
         }
      }

      return this;
   }
}
