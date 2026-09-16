package meteordevelopment.meteorclient.systems.hud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import meteordevelopment.meteorclient.utils.Utils;

public class HudElementInfo<T extends HudElement> {
   public final HudGroup group;
   public final String name;
   public final String title;
   public final String description;
   public final Supplier<T> factory;
   public final List<HudElementInfo<T>.Preset> presets;

   public HudElementInfo(HudGroup group, String name, String title, String description, Supplier<T> factory) {
      this.group = group;
      this.name = name;
      this.title = title;
      this.description = description;
      this.factory = factory;
      this.presets = new ArrayList();
   }

   public HudElementInfo(HudGroup group, String name, String description, Supplier<T> factory) {
      this(group, name, Utils.nameToTitle(name), description, factory);
   }

   public HudElementInfo<T>.Preset addPreset(String title, Consumer<T> callback) {
      HudElementInfo<T>.Preset preset = new Preset(this, title, callback);
      this.presets.add(preset);
      this.presets.sort(Comparator.comparing((p) -> p.title));
      return preset;
   }

   public boolean hasPresets() {
      return !this.presets.isEmpty();
   }

   public HudElement create() {
      return (HudElement)this.factory.get();
   }

   public class Preset {
      public final HudElementInfo<?> info;
      public final String title;
      public final Consumer<T> callback;

      public Preset(HudElementInfo<?> info, String title, Consumer<T> callback) {
         this.info = info;
         this.title = title;
         this.callback = callback;
      }
   }
}
