package meteordevelopment.meteorclient.systems.profiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2487;
import org.jetbrains.annotations.NotNull;

public class Profiles extends System<Profiles> implements Iterable<Profile> {
   public static final File FOLDER;
   private List<Profile> profiles = new ArrayList();

   public Profiles() {
      super("profiles");
   }

   public static Profiles get() {
      return (Profiles)Systems.get(Profiles.class);
   }

   public void add(Profile profile) {
      if (!this.profiles.contains(profile)) {
         this.profiles.add(profile);
      }

      profile.save();
      this.save();
   }

   public void remove(Profile profile) {
      if (this.profiles.remove(profile)) {
         profile.delete();
      }

      this.save();
   }

   public Profile get(String name) {
      for(Profile profile : this) {
         if (((String)profile.name.get()).equalsIgnoreCase(name)) {
            return profile;
         }
      }

      return null;
   }

   public List<Profile> getAll() {
      return this.profiles;
   }

   public File getFile() {
      return new File(FOLDER, "profiles.nbt");
   }

   @EventHandler
   private void onGameJoined(GameJoinedEvent event) {
      for(Profile profile : this) {
         if (((List)profile.loadOnJoin.get()).contains(Utils.getWorldName())) {
            profile.load();
         }
      }

   }

   public boolean isEmpty() {
      return this.profiles.isEmpty();
   }

   public @NotNull Iterator<Profile> iterator() {
      return this.profiles.iterator();
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("profiles", NbtUtils.listToTag(this.profiles));
      return tag;
   }

   public Profiles fromTag(class_2487 tag) {
      this.profiles = NbtUtils.<Profile>listFromTag(tag.method_68569("profiles"), Profile::new);

      for(File file : FOLDER.listFiles()) {
         if (file.isDirectory() && this.get(file.getName()) == null) {
            Profile p = new Profile();
            p.name.set(file.getName());
            boolean add = false;

            for(File f : file.listFiles()) {
               if (f.getName().equals("hud.nbt")) {
                  add = true;
                  p.hud.set(true);
               } else if (f.getName().equals("macros.nbt")) {
                  add = true;
                  p.macros.set(true);
               } else if (f.getName().equals("modules.nbt")) {
                  add = true;
                  p.modules.set(true);
               } else if (f.getName().endsWith(".nbt")) {
                  add = true;
                  p.waypoints.set(true);
               }
            }

            if (add) {
               this.add(p);
            }
         }
      }

      return this;
   }

   static {
      FOLDER = new File(MeteorClient.FOLDER, "profiles");
   }
}
