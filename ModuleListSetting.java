package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;

public class ModuleListSetting extends Setting<List<Module>> {
   private static List<String> suggestions;

   public ModuleListSetting(String name, String description, List<Module> defaultValue, Consumer<List<Module>> onChanged, Consumer<Setting<List<Module>>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   public void resetImpl() {
      this.value = new ArrayList((Collection)this.defaultValue);
   }

   protected List<Module> parseImpl(String str) {
      String[] values = str.split(",");
      List<Module> modules = new ArrayList(values.length);

      try {
         for(String value : values) {
            Module module = Modules.get().get(value.trim());
            if (module != null) {
               modules.add(module);
            }
         }
      } catch (Exception var9) {
      }

      return modules;
   }

   protected boolean isValueValid(List<Module> value) {
      return true;
   }

   public List<String> getSuggestions() {
      if (suggestions == null) {
         suggestions = new ArrayList(Modules.get().getAll().size());

         for(Module module : Modules.get().getAll()) {
            suggestions.add(module.name);
         }
      }

      return suggestions;
   }

   public class_2487 save(class_2487 tag) {
      class_2499 modulesTag = new class_2499();

      for(Module module : (List)this.get()) {
         modulesTag.add(class_2519.method_23256(module.name));
      }

      tag.method_10566("modules", modulesTag);
      return tag;
   }

   public List<Module> load(class_2487 tag) {
      ((List)this.get()).clear();

      for(class_2520 tagI : tag.method_68569("modules")) {
         Module module = Modules.get().get((String)tagI.method_68658().orElse(""));
         if (module != null) {
            ((List)this.get()).add(module);
         }
      }

      return (List)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, List<Module>, ModuleListSetting> {
      public Builder() {
         super(new ArrayList(0));
      }

      @SafeVarargs
      public final Builder defaultValue(Class<? extends Module>... defaults) {
         List<Module> modules = new ArrayList();

         for(Class<? extends Module> klass : defaults) {
            if (Modules.get().get(klass) != null) {
               modules.add(Modules.get().get(klass));
            }
         }

         return (Builder)this.defaultValue(modules);
      }

      public ModuleListSetting build() {
         return new ModuleListSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
