package meteordevelopment.meteorclient.systems.profiles;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.macros.Macros;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import org.apache.commons.io.FileUtils;

public class Profile implements ISerializable<Profile> {
   public final Settings settings = new Settings();
   private final SettingGroup sgGeneral;
   private final SettingGroup sgSave;
   public Setting<String> name;
   public Setting<List<String>> loadOnJoin;
   public Setting<Boolean> hud;
   public Setting<Boolean> macros;
   public Setting<Boolean> modules;
   public Setting<Boolean> waypoints;

   public Profile() {
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgSave = this.settings.createGroup("Save");
      this.name = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name of the profile.")).filter(Utils::nameFilter).build());
      this.loadOnJoin = this.sgGeneral.add(((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("load-on-join")).description("Which servers to set this profile as active when joining.")).filter(Utils::ipFilter).build());
      this.hud = this.sgSave.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hud")).description("Whether the profile should save hud.")).defaultValue(false)).build());
      this.macros = this.sgSave.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("macros")).description("Whether the profile should save macros.")).defaultValue(false)).build());
      this.modules = this.sgSave.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("modules")).description("Whether the profile should save modules.")).defaultValue(false)).build());
      this.waypoints = this.sgSave.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("waypoints")).description("Whether the profile should save waypoints.")).defaultValue(false)).build());
   }

   public Profile(class_2520 tag) {
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgSave = this.settings.createGroup("Save");
      this.name = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name of the profile.")).filter(Utils::nameFilter).build());
      this.loadOnJoin = this.sgGeneral.add(((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("load-on-join")).description("Which servers to set this profile as active when joining.")).filter(Utils::ipFilter).build());
      this.hud = this.sgSave.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hud")).description("Whether the profile should save hud.")).defaultValue(false)).build());
      this.macros = this.sgSave.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("macros")).description("Whether the profile should save macros.")).defaultValue(false)).build());
      this.modules = this.sgSave.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("modules")).description("Whether the profile should save modules.")).defaultValue(false)).build());
      this.waypoints = this.sgSave.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("waypoints")).description("Whether the profile should save waypoints.")).defaultValue(false)).build());
      this.fromTag((class_2487)tag);
   }

   public void load() {
      File folder = this.getSafeFile();
      if (folder != null) {
         if ((Boolean)this.hud.get()) {
            Hud.get().load(folder);
         }

         if ((Boolean)this.macros.get()) {
            Macros.get().load(folder);
         }

         if ((Boolean)this.modules.get()) {
            Modules.get().load(folder);
         }

         if ((Boolean)this.waypoints.get()) {
            Waypoints.get().load(folder);
         }

      }
   }

   public void save() {
      File folder = this.getSafeFile();
      if (folder != null) {
         if ((Boolean)this.hud.get()) {
            Hud.get().save(folder);
         }

         if ((Boolean)this.macros.get()) {
            Macros.get().save(folder);
         }

         if ((Boolean)this.modules.get()) {
            Modules.get().save(folder);
         }

         if ((Boolean)this.waypoints.get()) {
            Waypoints.get().save(folder);
         }

      }
   }

   public void delete() {
      try {
         File folder = this.getSafeFile();
         if (folder != null) {
            FileUtils.deleteDirectory(folder);
         }
      } catch (IOException e) {
         MeteorClient.LOG.error("Error deleting profile {}", this.name.get(), e);
      }

   }

   public File getFile() {
      return new File(Profiles.FOLDER, this.name.get());
   }

   public File getSafeFile() {
      try {
         File folder = this.getFile().getCanonicalFile();
         File profilesFolder = Profiles.FOLDER.getCanonicalFile();
         return !((String)this.name.get()).isEmpty() && profilesFolder.equals(folder.getParentFile()) ? folder : null;
      } catch (IOException e) {
         MeteorClient.LOG.error("Error resolving profile {}", this.name.get(), e);
         return null;
      }
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("settings", this.settings.toTag());
      return tag;
   }

   public Profile fromTag(class_2487 tag) {
      if (tag.method_10545("settings")) {
         this.settings.fromTag(tag.method_68568("settings"));
      }

      return this;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Profile profile = (Profile)o;
         return Objects.equals(profile.name.get(), this.name.get());
      } else {
         return false;
      }
   }
}
