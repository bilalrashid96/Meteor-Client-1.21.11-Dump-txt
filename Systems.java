package meteordevelopment.meteorclient.systems;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.systems.accounts.Accounts;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.macros.Macros;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.profiles.Profiles;
import meteordevelopment.meteorclient.systems.proxies.Proxies;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.orbit.EventHandler;

public class Systems {
   private static final Map<Class<? extends System>, System<?>> systems = new Reference2ReferenceOpenHashMap();
   private static final List<Runnable> preLoadTasks = new ArrayList(1);

   public static void addPreLoadTask(Runnable task) {
      preLoadTasks.add(task);
   }

   public static void init() {
      add(new Modules());
      Config config = new Config();
      System<?> configSystem = add(config);
      configSystem.init();
      configSystem.load();
      config.settings.registerColorSettings((Module)null);
      add(new Macros());
      add(new Friends());
      add(new Accounts());
      add(new Waypoints());
      add(new Profiles());
      add(new Proxies());
      add(new Hud());
      MeteorClient.EVENT_BUS.subscribe(Systems.class);
   }

   public static System<?> add(System<?> system) {
      systems.put(system.getClass(), system);
      MeteorClient.EVENT_BUS.subscribe(system);
      system.init();
      return system;
   }

   @EventHandler
   private static void onGameLeft(GameLeftEvent event) {
      save();
   }

   public static void save(File folder) {
      long start = java.lang.System.currentTimeMillis();
      MeteorClient.LOG.info("Saving");

      for(System<?> system : systems.values()) {
         system.save(folder);
      }

      MeteorClient.LOG.info("Saved in {} milliseconds.", java.lang.System.currentTimeMillis() - start);
   }

   public static void save() {
      save((File)null);
   }

   public static void load(File folder) {
      long start = java.lang.System.currentTimeMillis();
      MeteorClient.LOG.info("Loading");

      for(Runnable task : preLoadTasks) {
         task.run();
      }

      for(System<?> system : systems.values()) {
         system.load(folder);
      }

      MeteorClient.LOG.info("Loaded in {} milliseconds", java.lang.System.currentTimeMillis() - start);
   }

   public static void load() {
      load((File)null);
   }

   public static <T extends System<?>> T get(Class<T> klass) {
      return (T)(systems.get(klass));
   }
}
