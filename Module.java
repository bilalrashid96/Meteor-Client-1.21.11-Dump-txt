package meteordevelopment.meteorclient.systems.modules;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_124;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import org.jetbrains.annotations.NotNull;

public abstract class Module implements ISerializable<Module>, Comparable<Module> {
   protected final class_310 mc;
   public final Category category;
   public final String name;
   public final String title;
   public final String description;
   public final String[] aliases;
   public final Color color;
   public final MeteorAddon addon;
   public final Settings settings;
   private boolean active;
   public boolean serialize;
   public boolean runInMainMenu;
   public boolean autoSubscribe;
   public final Keybind keybind;
   public boolean toggleOnBindRelease;
   public boolean chatFeedback;
   public boolean favorite;

   public Module(Category category, String name, String description, String... aliases) {
      this.settings = new Settings();
      this.serialize = true;
      this.runInMainMenu = false;
      this.autoSubscribe = true;
      this.keybind = Keybind.none();
      this.toggleOnBindRelease = false;
      this.chatFeedback = true;
      this.favorite = false;
      if (name.contains(" ")) {
         MeteorClient.LOG.warn("Module '{}' contains invalid characters in its name making it incompatible with Meteor Client commands.", name);
      }

      this.mc = class_310.method_1551();
      this.category = category;
      this.name = name;
      this.title = Utils.nameToTitle(name);
      this.description = description;
      this.aliases = aliases;
      this.color = Color.fromHsv(Utils.random((double)0.0F, (double)360.0F), 0.35, (double)1.0F);
      String classname = this.getClass().getName();

      for(MeteorAddon addon : AddonManager.ADDONS) {
         if (classname.startsWith(addon.getPackage())) {
            this.addon = addon;
            return;
         }
      }

      this.addon = null;
   }

   public Module(Category category, String name, String desc) {
      this(category, name, desc);
   }

   public WWidget getWidget(GuiTheme theme) {
      return null;
   }

   public void onActivate() {
   }

   public void onDeactivate() {
   }

   public void toggle() {
      if (!this.active) {
         this.active = true;
         Modules.get().addActive(this);
         this.settings.onActivated();
         if (this.runInMainMenu || Utils.canUpdate()) {
            if (this.autoSubscribe) {
               MeteorClient.EVENT_BUS.subscribe(this);
            }

            this.onActivate();
         }
      } else {
         if (this.runInMainMenu || Utils.canUpdate()) {
            if (this.autoSubscribe) {
               MeteorClient.EVENT_BUS.unsubscribe(this);
            }

            this.onDeactivate();
         }

         this.active = false;
         Modules.get().removeActive(this);
      }

   }

   public void enable() {
      if (!this.isActive()) {
         this.toggle();
      }

   }

   public void disable() {
      if (this.isActive()) {
         this.toggle();
      }

   }

   public void sendToggledMsg() {
      if ((Boolean)Config.get().chatFeedback.get() && this.chatFeedback) {
         ChatUtils.forceNextPrefixClass(this.getClass());
         ChatUtils.sendMsg(this.hashCode(), class_124.field_1080, "Toggled (highlight)%s(default) %s(default).", this.title, this.isActive() ? String.valueOf(class_124.field_1060) + "on" : String.valueOf(class_124.field_1061) + "off");
      }

   }

   public void info(class_2561 message) {
      ChatUtils.forceNextPrefixClass(this.getClass());
      ChatUtils.sendMsg(this.title, message);
   }

   public void info(String message, Object... args) {
      ChatUtils.forceNextPrefixClass(this.getClass());
      ChatUtils.infoPrefix(this.title, message, args);
   }

   public void warning(String message, Object... args) {
      ChatUtils.forceNextPrefixClass(this.getClass());
      ChatUtils.warningPrefix(this.title, message, args);
   }

   public void error(String message, Object... args) {
      ChatUtils.forceNextPrefixClass(this.getClass());
      ChatUtils.errorPrefix(this.title, message, args);
   }

   public boolean isActive() {
      return this.active;
   }

   public String getInfoString() {
      return null;
   }

   public class_2487 toTag() {
      if (!this.serialize) {
         return null;
      } else {
         class_2487 tag = new class_2487();
         tag.method_10582("name", this.name);
         tag.method_10566("keybind", this.keybind.toTag());
         tag.method_10556("toggleOnKeyRelease", this.toggleOnBindRelease);
         tag.method_10556("chatFeedback", this.chatFeedback);
         tag.method_10556("favorite", this.favorite);
         tag.method_10566("settings", this.settings.toTag());
         tag.method_10556("active", this.active);
         return tag;
      }
   }

   public Module fromTag(class_2487 tag) {
      this.keybind.fromTag(tag.method_68568("keybind"));
      this.toggleOnBindRelease = tag.method_68566("toggleOnKeyRelease", false);
      this.chatFeedback = !tag.method_10545("chatFeedback") || tag.method_68566("chatFeedback", false);
      this.favorite = tag.method_68566("favorite", false);
      class_2520 settingsTag = tag.method_10580("settings");
      if (settingsTag instanceof class_2487) {
         this.settings.fromTag((class_2487)settingsTag);
      }

      boolean active = tag.method_68566("active", false);
      if (active != this.isActive()) {
         this.toggle();
      }

      return this;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Module module = (Module)o;
         return Objects.equals(this.name, module.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name});
   }

   public int compareTo(@NotNull Module o) {
      return this.name.compareTo(o.name);
   }
}
