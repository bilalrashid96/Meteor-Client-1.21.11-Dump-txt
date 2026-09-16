package meteordevelopment.meteorclient.gui.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.Settings;

public abstract class SettingsWidgetFactory {
   private static final Map<Class<?>, Function<GuiTheme, Factory>> customFactories = new HashMap();
   protected final GuiTheme theme;
   protected final Map<Class<?>, Factory> factories = new HashMap();

   public SettingsWidgetFactory(GuiTheme theme) {
      this.theme = theme;
   }

   public static void registerCustomFactory(Class<?> settingClass, Function<GuiTheme, Factory> factoryFunction) {
      customFactories.put(settingClass, factoryFunction);
   }

   public static void unregisterCustomFactory(Class<?> settingClass) {
      customFactories.remove(settingClass);
   }

   public abstract WWidget create(GuiTheme var1, Settings var2, String var3);

   protected Factory getFactory(Class<?> settingClass) {
      return customFactories.containsKey(settingClass) ? (Factory)((Function)customFactories.get(settingClass)).apply(this.theme) : (Factory)this.factories.get(settingClass);
   }

   @FunctionalInterface
   public interface Factory {
      void create(WTable var1, Setting<?> var2);
   }
}
