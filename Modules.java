package meteordevelopment.meteorclient.systems.modules;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.meteor.ActiveModulesChangedEvent;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.ModuleBindChangedEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.pathing.BaritoneUtils;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.combat.AnchorAura;
import meteordevelopment.meteorclient.systems.modules.combat.AntiAnvil;
import meteordevelopment.meteorclient.systems.modules.combat.AntiBed;
import meteordevelopment.meteorclient.systems.modules.combat.ArrowDodge;
import meteordevelopment.meteorclient.systems.modules.combat.AttributeSwap;
import meteordevelopment.meteorclient.systems.modules.combat.AutoAnvil;
import meteordevelopment.meteorclient.systems.modules.combat.AutoArmor;
import meteordevelopment.meteorclient.systems.modules.combat.AutoCity;
import meteordevelopment.meteorclient.systems.modules.combat.AutoEXP;
import meteordevelopment.meteorclient.systems.modules.combat.AutoLog;
import meteordevelopment.meteorclient.systems.modules.combat.AutoTotem;
import meteordevelopment.meteorclient.systems.modules.combat.AutoTrap;
import meteordevelopment.meteorclient.systems.modules.combat.AutoWeapon;
import meteordevelopment.meteorclient.systems.modules.combat.AutoWeb;
import meteordevelopment.meteorclient.systems.modules.combat.BedAura;
import meteordevelopment.meteorclient.systems.modules.combat.BowAimbot;
import meteordevelopment.meteorclient.systems.modules.combat.BowSpam;
import meteordevelopment.meteorclient.systems.modules.combat.Burrow;
import meteordevelopment.meteorclient.systems.modules.combat.Criticals;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.systems.modules.combat.Hitboxes;
import meteordevelopment.meteorclient.systems.modules.combat.HoleFiller;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.systems.modules.combat.Offhand;
import meteordevelopment.meteorclient.systems.modules.combat.Quiver;
import meteordevelopment.meteorclient.systems.modules.combat.SelfAnvil;
import meteordevelopment.meteorclient.systems.modules.combat.SelfTrap;
import meteordevelopment.meteorclient.systems.modules.combat.SelfWeb;
import meteordevelopment.meteorclient.systems.modules.combat.Surround;
import meteordevelopment.meteorclient.systems.modules.misc.AntiPacketKick;
import meteordevelopment.meteorclient.systems.modules.misc.AutoReconnect;
import meteordevelopment.meteorclient.systems.modules.misc.BetterBeacons;
import meteordevelopment.meteorclient.systems.modules.misc.BetterChat;
import meteordevelopment.meteorclient.systems.modules.misc.BookBot;
import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
import meteordevelopment.meteorclient.systems.modules.misc.InventoryTweaks;
import meteordevelopment.meteorclient.systems.modules.misc.MessageAura;
import meteordevelopment.meteorclient.systems.modules.misc.Notebot;
import meteordevelopment.meteorclient.systems.modules.misc.Notifier;
import meteordevelopment.meteorclient.systems.modules.misc.PacketCanceller;
import meteordevelopment.meteorclient.systems.modules.misc.PacketLogger;
import meteordevelopment.meteorclient.systems.modules.misc.ServerSpoof;
import meteordevelopment.meteorclient.systems.modules.misc.SoundBlocker;
import meteordevelopment.meteorclient.systems.modules.misc.Spam;
import meteordevelopment.meteorclient.systems.modules.misc.swarm.Swarm;
import meteordevelopment.meteorclient.systems.modules.movement.AirJump;
import meteordevelopment.meteorclient.systems.modules.movement.Anchor;
import meteordevelopment.meteorclient.systems.modules.movement.AntiVoid;
import meteordevelopment.meteorclient.systems.modules.movement.AutoJump;
import meteordevelopment.meteorclient.systems.modules.movement.AutoWalk;
import meteordevelopment.meteorclient.systems.modules.movement.AutoWasp;
import meteordevelopment.meteorclient.systems.modules.movement.Blink;
import meteordevelopment.meteorclient.systems.modules.movement.ClickTP;
import meteordevelopment.meteorclient.systems.modules.movement.ElytraBoost;
import meteordevelopment.meteorclient.systems.modules.movement.EntityControl;
import meteordevelopment.meteorclient.systems.modules.movement.FastClimb;
import meteordevelopment.meteorclient.systems.modules.movement.Flight;
import meteordevelopment.meteorclient.systems.modules.movement.GUIMove;
import meteordevelopment.meteorclient.systems.modules.movement.HighJump;
import meteordevelopment.meteorclient.systems.modules.movement.Jesus;
import meteordevelopment.meteorclient.systems.modules.movement.LongJump;
import meteordevelopment.meteorclient.systems.modules.movement.NoFall;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import meteordevelopment.meteorclient.systems.modules.movement.Parkour;
import meteordevelopment.meteorclient.systems.modules.movement.ReverseStep;
import meteordevelopment.meteorclient.systems.modules.movement.SafeWalk;
import meteordevelopment.meteorclient.systems.modules.movement.Scaffold;
import meteordevelopment.meteorclient.systems.modules.movement.Slippy;
import meteordevelopment.meteorclient.systems.modules.movement.Sneak;
import meteordevelopment.meteorclient.systems.modules.movement.Spider;
import meteordevelopment.meteorclient.systems.modules.movement.Sprint;
import meteordevelopment.meteorclient.systems.modules.movement.Step;
import meteordevelopment.meteorclient.systems.modules.movement.TridentBoost;
import meteordevelopment.meteorclient.systems.modules.movement.Velocity;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;
import meteordevelopment.meteorclient.systems.modules.movement.speed.Speed;
import meteordevelopment.meteorclient.systems.modules.player.AirPlace;
import meteordevelopment.meteorclient.systems.modules.player.AntiAFK;
import meteordevelopment.meteorclient.systems.modules.player.AntiHunger;
import meteordevelopment.meteorclient.systems.modules.player.AutoClicker;
import meteordevelopment.meteorclient.systems.modules.player.AutoEat;
import meteordevelopment.meteorclient.systems.modules.player.AutoFish;
import meteordevelopment.meteorclient.systems.modules.player.AutoGap;
import meteordevelopment.meteorclient.systems.modules.player.AutoMend;
import meteordevelopment.meteorclient.systems.modules.player.AutoReplenish;
import meteordevelopment.meteorclient.systems.modules.player.AutoRespawn;
import meteordevelopment.meteorclient.systems.modules.player.AutoTool;
import meteordevelopment.meteorclient.systems.modules.player.BreakDelay;
import meteordevelopment.meteorclient.systems.modules.player.ChestSwap;
import meteordevelopment.meteorclient.systems.modules.player.EXPThrower;
import meteordevelopment.meteorclient.systems.modules.player.FakePlayer;
import meteordevelopment.meteorclient.systems.modules.player.FastUse;
import meteordevelopment.meteorclient.systems.modules.player.GhostHand;
import meteordevelopment.meteorclient.systems.modules.player.InstantRebreak;
import meteordevelopment.meteorclient.systems.modules.player.LiquidInteract;
import meteordevelopment.meteorclient.systems.modules.player.MiddleClickExtra;
import meteordevelopment.meteorclient.systems.modules.player.Multitask;
import meteordevelopment.meteorclient.systems.modules.player.NameProtect;
import meteordevelopment.meteorclient.systems.modules.player.NoInteract;
import meteordevelopment.meteorclient.systems.modules.player.NoMiningTrace;
import meteordevelopment.meteorclient.systems.modules.player.NoRotate;
import meteordevelopment.meteorclient.systems.modules.player.NoStatusEffects;
import meteordevelopment.meteorclient.systems.modules.player.OffhandCrash;
import meteordevelopment.meteorclient.systems.modules.player.Portals;
import meteordevelopment.meteorclient.systems.modules.player.PotionSaver;
import meteordevelopment.meteorclient.systems.modules.player.Reach;
import meteordevelopment.meteorclient.systems.modules.player.Rotation;
import meteordevelopment.meteorclient.systems.modules.player.SpeedMine;
import meteordevelopment.meteorclient.systems.modules.render.BetterTab;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import meteordevelopment.meteorclient.systems.modules.render.BlockSelection;
import meteordevelopment.meteorclient.systems.modules.render.Blur;
import meteordevelopment.meteorclient.systems.modules.render.BossStack;
import meteordevelopment.meteorclient.systems.modules.render.Breadcrumbs;
import meteordevelopment.meteorclient.systems.modules.render.BreakIndicators;
import meteordevelopment.meteorclient.systems.modules.render.CameraTweaks;
import meteordevelopment.meteorclient.systems.modules.render.Chams;
import meteordevelopment.meteorclient.systems.modules.render.CityESP;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import meteordevelopment.meteorclient.systems.modules.render.EntityOwner;
import meteordevelopment.meteorclient.systems.modules.render.FreeLook;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.Fullbright;
import meteordevelopment.meteorclient.systems.modules.render.HandView;
import meteordevelopment.meteorclient.systems.modules.render.HoleESP;
import meteordevelopment.meteorclient.systems.modules.render.ItemHighlight;
import meteordevelopment.meteorclient.systems.modules.render.ItemPhysics;
import meteordevelopment.meteorclient.systems.modules.render.LightOverlay;
import meteordevelopment.meteorclient.systems.modules.render.LogoutSpots;
import meteordevelopment.meteorclient.systems.modules.render.Nametags;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.render.PopChams;
import meteordevelopment.meteorclient.systems.modules.render.StorageESP;
import meteordevelopment.meteorclient.systems.modules.render.TimeChanger;
import meteordevelopment.meteorclient.systems.modules.render.Tracers;
import meteordevelopment.meteorclient.systems.modules.render.Trail;
import meteordevelopment.meteorclient.systems.modules.render.Trajectories;
import meteordevelopment.meteorclient.systems.modules.render.TunnelESP;
import meteordevelopment.meteorclient.systems.modules.render.VoidESP;
import meteordevelopment.meteorclient.systems.modules.render.WallHack;
import meteordevelopment.meteorclient.systems.modules.render.WaypointsModule;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import meteordevelopment.meteorclient.systems.modules.render.Zoom;
import meteordevelopment.meteorclient.systems.modules.render.blockesp.BlockESP;
import meteordevelopment.meteorclient.systems.modules.render.marker.Marker;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import meteordevelopment.meteorclient.systems.modules.world.AutoBreed;
import meteordevelopment.meteorclient.systems.modules.world.AutoBrewer;
import meteordevelopment.meteorclient.systems.modules.world.AutoMount;
import meteordevelopment.meteorclient.systems.modules.world.AutoNametag;
import meteordevelopment.meteorclient.systems.modules.world.AutoShearer;
import meteordevelopment.meteorclient.systems.modules.world.AutoSign;
import meteordevelopment.meteorclient.systems.modules.world.AutoSmelter;
import meteordevelopment.meteorclient.systems.modules.world.BuildHeight;
import meteordevelopment.meteorclient.systems.modules.world.Collisions;
import meteordevelopment.meteorclient.systems.modules.world.EChestFarmer;
import meteordevelopment.meteorclient.systems.modules.world.EndermanLook;
import meteordevelopment.meteorclient.systems.modules.world.Excavator;
import meteordevelopment.meteorclient.systems.modules.world.Flamethrower;
import meteordevelopment.meteorclient.systems.modules.world.HighwayBuilder;
import meteordevelopment.meteorclient.systems.modules.world.InfinityMiner;
import meteordevelopment.meteorclient.systems.modules.world.LiquidFiller;
import meteordevelopment.meteorclient.systems.modules.world.MountBypass;
import meteordevelopment.meteorclient.systems.modules.world.NoGhostBlocks;
import meteordevelopment.meteorclient.systems.modules.world.Nuker;
import meteordevelopment.meteorclient.systems.modules.world.PacketMine;
import meteordevelopment.meteorclient.systems.modules.world.SpawnProofer;
import meteordevelopment.meteorclient.systems.modules.world.StashFinder;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.systems.modules.world.VeinMiner;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.ValueComparableMap;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2520;
import net.minecraft.class_3545;
import org.jetbrains.annotations.Nullable;

public class Modules extends System<Modules> {
   private static final List<Category> CATEGORIES = new ArrayList();
   private final Map<Class<? extends Module>, Module> moduleInstances = new Reference2ReferenceOpenHashMap();
   private final Map<Category, List<Module>> groups = new Reference2ReferenceOpenHashMap();
   private final List<Module> active = new ArrayList();
   private Module moduleToBind;
   private boolean awaitingKeyRelease = false;

   public Modules() {
      super("modules");
   }

   public static Modules get() {
      return (Modules)Systems.get(Modules.class);
   }

   public void init() {
      this.initCombat();
      this.initPlayer();
      this.initMovement();
      this.initRender();
      this.initWorld();
      this.initMisc();
   }

   public void load(File folder) {
      for(Module module : this.getAll()) {
         for(SettingGroup group : module.settings) {
            for(Setting<?> setting : group) {
               setting.reset();
            }
         }
      }

      super.load(folder);
   }

   public void sortModules() {
      for(List<Module> modules : this.groups.values()) {
         modules.sort(Comparator.comparing((o) -> o.title));
      }

   }

   public static void registerCategory(Category category) {
      if (!Categories.REGISTERING) {
         throw new RuntimeException("Modules.registerCategory - Cannot register category outside of onRegisterCategories callback.");
      } else {
         CATEGORIES.add(category);
      }
   }

   public static Iterable<Category> loopCategories() {
      return CATEGORIES;
   }

   public <T extends Module> @Nullable T get(Class<T> klass) {
      return (T)(this.moduleInstances.get(klass));
   }

   public <T extends Module> Optional<T> getOptional(Class<T> klass) {
      return Optional.ofNullable(this.get(klass));
   }

   public @Nullable Module get(String name) {
      for(Module module : this.moduleInstances.values()) {
         if (module.name.equalsIgnoreCase(name)) {
            return module;
         }
      }

      return null;
   }

   public boolean isActive(Class<? extends Module> klass) {
      Module module = this.get(klass);
      return module != null && module.isActive();
   }

   public List<Module> getGroup(Category category) {
      return (List)this.groups.computeIfAbsent(category, (category1) -> new ArrayList());
   }

   public Collection<Module> getAll() {
      return this.moduleInstances.values();
   }

   public int getCount() {
      return this.moduleInstances.size();
   }

   public List<Module> getActive() {
      return this.active;
   }

   public List<class_3545<Module, String>> searchTitles(String text) {
      Map<class_3545<Module, String>, Integer> modules = new HashMap();

      for(Module module : this.moduleInstances.values()) {
         String title = module.title;
         int score = Utils.searchLevenshteinDefault(title, text, false);
         if ((Boolean)Config.get().moduleAliases.get()) {
            for(String alias : module.aliases) {
               int aliasScore = Utils.searchLevenshteinDefault(alias, text, false);
               if (aliasScore < score) {
                  title = module.title + " (" + alias + ")";
                  score = aliasScore;
               }
            }
         }

         modules.put(new class_3545(module, title), score);
      }

      List<class_3545<Module, String>> l = new ArrayList(modules.keySet());
      Objects.requireNonNull(modules);
      l.sort(Comparator.comparingInt(modules::get));
      return l;
   }

   public Set<Module> searchSettingTitles(String text) {
      Map<Module, Integer> modules = new ValueComparableMap<Module, Integer>(Comparator.naturalOrder());

      for(Module module : this.moduleInstances.values()) {
         int lowest = Integer.MAX_VALUE;

         for(SettingGroup sg : module.settings) {
            for(Setting<?> setting : sg) {
               int score = Utils.searchLevenshteinDefault(setting.title, text, false);
               if (score < lowest) {
                  lowest = score;
               }
            }
         }

         modules.put(module, (Integer)modules.getOrDefault(module, 0) + lowest);
      }

      return modules.keySet();
   }

   void addActive(Module module) {
      synchronized(this.active) {
         if (!this.active.contains(module)) {
            this.active.add(module);
            MeteorClient.EVENT_BUS.post(ActiveModulesChangedEvent.get());
         }

      }
   }

   void removeActive(Module module) {
      synchronized(this.active) {
         if (this.active.remove(module)) {
            MeteorClient.EVENT_BUS.post(ActiveModulesChangedEvent.get());
         }

      }
   }

   public void setModuleToBind(Module moduleToBind) {
      this.moduleToBind = moduleToBind;
   }

   public void awaitKeyRelease() {
      this.awaitingKeyRelease = true;
   }

   public boolean isBinding() {
      return this.moduleToBind != null;
   }

   @EventHandler(
      priority = 200
   )
   private void onKeyBinding(KeyEvent event) {
      if (event.action == KeyAction.Release && this.onBinding(true, event.key(), event.modifiers())) {
         event.cancel();
      }

   }

   @EventHandler(
      priority = 200
   )
   private void onButtonBinding(MouseClickEvent event) {
      if (event.action == KeyAction.Release && this.onBinding(false, event.button(), 0)) {
         event.cancel();
      }

   }

   private boolean onBinding(boolean isKey, int value, int modifiers) {
      if (!this.isBinding()) {
         return false;
      } else if (!this.awaitingKeyRelease) {
         if (this.moduleToBind.keybind.canBindTo(isKey, value, modifiers)) {
            this.moduleToBind.keybind.set(isKey, value, modifiers);
            this.moduleToBind.info("Bound to (highlight)%s(default).", this.moduleToBind.keybind);
         } else {
            if (value != 256) {
               return false;
            }

            this.moduleToBind.keybind.set(Keybind.none());
            this.moduleToBind.info("Removed bind.");
         }

         MeteorClient.EVENT_BUS.post(ModuleBindChangedEvent.get(this.moduleToBind));
         this.moduleToBind = null;
         return true;
      } else if (isKey && (value == 257 || value == 335)) {
         this.awaitingKeyRelease = false;
         return false;
      } else {
         return false;
      }
   }

   @EventHandler(
      priority = 100
   )
   private void onKey(KeyEvent event) {
      if (event.action != KeyAction.Repeat) {
         this.onAction(true, event.key(), event.modifiers(), event.action == KeyAction.Press);
      }
   }

   @EventHandler(
      priority = 100
   )
   private void onMouseClick(MouseClickEvent event) {
      if (event.action != KeyAction.Repeat) {
         this.onAction(false, event.button(), 0, event.action == KeyAction.Press);
      }
   }

   private void onAction(boolean isKey, int value, int modifiers, boolean isPress) {
      if (MeteorClient.mc.field_1755 == null && !Input.isKeyPressed(292)) {
         for(Module module : this.moduleInstances.values()) {
            if (module.keybind.matches(isKey, value, modifiers) && (isPress || module.toggleOnBindRelease && module.isActive())) {
               module.toggle();
               module.sendToggledMsg();
            }
         }

      }
   }

   @EventHandler(
      priority = 201
   )
   private void onOpenScreen(OpenScreenEvent event) {
      if (Utils.canUpdate()) {
         for(Module module : this.moduleInstances.values()) {
            if (module.toggleOnBindRelease && module.isActive()) {
               module.toggle();
               module.sendToggledMsg();
            }
         }

      }
   }

   @EventHandler
   private void onGameJoined(GameJoinedEvent event) {
      synchronized(this.active) {
         for(Module module : this.getAll()) {
            if (module.isActive() && !module.runInMainMenu) {
               MeteorClient.EVENT_BUS.subscribe(module);
               module.onActivate();
            }
         }

      }
   }

   @EventHandler
   private void onGameLeft(GameLeftEvent event) {
      synchronized(this.active) {
         for(Module module : this.getAll()) {
            if (module.isActive() && !module.runInMainMenu) {
               MeteorClient.EVENT_BUS.unsubscribe(module);
               module.onDeactivate();
            }
         }

      }
   }

   public void disableAll() {
      synchronized(this.active) {
         for(Module module : this.getAll()) {
            module.disable();
         }

      }
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      class_2499 modulesTag = new class_2499();

      for(Module module : this.getAll()) {
         class_2487 moduleTag = module.toTag();
         if (moduleTag != null) {
            modulesTag.add(moduleTag);
         }
      }

      tag.method_10566("modules", modulesTag);
      return tag;
   }

   public Modules fromTag(class_2487 tag) {
      this.disableAll();

      for(class_2520 moduleTagI : tag.method_68569("modules")) {
         class_2487 moduleTag = (class_2487)moduleTagI;
         Module module = this.get(moduleTag.method_68564("name", ""));
         if (module != null) {
            module.fromTag(moduleTag);
         }
      }

      return this;
   }

   public void add(Module module) {
      if (!CATEGORIES.contains(module.category)) {
         throw new RuntimeException("Modules.addModule - Module's category was not registered.");
      } else {
         AtomicReference<Module> removedModule = new AtomicReference();
         if (this.moduleInstances.values().removeIf((module1) -> {
            if (module1.name.equals(module.name)) {
               removedModule.set(module1);
               module1.settings.unregisterColorSettings();
               return true;
            } else {
               return false;
            }
         })) {
            this.getGroup(((Module)removedModule.get()).category).remove(removedModule.get());
         }

         this.moduleInstances.put(module.getClass(), module);
         this.getGroup(module.category).add(module);
         module.settings.registerColorSettings(module);
      }
   }

   private void initCombat() {
      this.add(new AnchorAura());
      this.add(new AntiAnvil());
      this.add(new AntiBed());
      this.add(new ArrowDodge());
      this.add(new AttributeSwap());
      this.add(new AutoAnvil());
      this.add(new AutoArmor());
      this.add(new AutoCity());
      this.add(new AutoEXP());
      this.add(new AutoLog());
      this.add(new AutoTotem());
      this.add(new AutoTrap());
      this.add(new AutoWeapon());
      this.add(new AutoWeb());
      this.add(new BedAura());
      this.add(new BowAimbot());
      this.add(new BowSpam());
      this.add(new Burrow());
      this.add(new Criticals());
      this.add(new CrystalAura());
      this.add(new Hitboxes());
      this.add(new HoleFiller());
      this.add(new KillAura());
      this.add(new Offhand());
      this.add(new Quiver());
      this.add(new SelfAnvil());
      this.add(new SelfTrap());
      this.add(new SelfWeb());
      this.add(new Surround());
   }

   private void initPlayer() {
      this.add(new AirPlace());
      this.add(new AntiAFK());
      this.add(new AntiHunger());
      this.add(new AutoEat());
      this.add(new AutoClicker());
      this.add(new AutoFish());
      this.add(new AutoGap());
      this.add(new AutoMend());
      this.add(new AutoReplenish());
      this.add(new AutoRespawn());
      this.add(new AutoTool());
      this.add(new BreakDelay());
      this.add(new ChestSwap());
      this.add(new EXPThrower());
      this.add(new FakePlayer());
      this.add(new FastUse());
      this.add(new GhostHand());
      this.add(new InstantRebreak());
      this.add(new LiquidInteract());
      this.add(new MiddleClickExtra());
      this.add(new Multitask());
      this.add(new NameProtect());
      this.add(new NoInteract());
      this.add(new NoMiningTrace());
      this.add(new NoRotate());
      this.add(new NoStatusEffects());
      this.add(new OffhandCrash());
      this.add(new Portals());
      this.add(new PotionSaver());
      this.add(new Reach());
      this.add(new Rotation());
      this.add(new SpeedMine());
   }

   private void initMovement() {
      this.add(new AirJump());
      this.add(new Anchor());
      this.add(new AntiVoid());
      this.add(new AutoJump());
      this.add(new AutoWalk());
      this.add(new AutoWasp());
      this.add(new Blink());
      this.add(new ClickTP());
      this.add(new ElytraBoost());
      this.add(new ElytraFly());
      this.add(new EntityControl());
      this.add(new FastClimb());
      this.add(new Flight());
      this.add(new GUIMove());
      this.add(new HighJump());
      this.add(new Jesus());
      this.add(new LongJump());
      this.add(new NoFall());
      this.add(new NoSlow());
      this.add(new Parkour());
      this.add(new ReverseStep());
      this.add(new SafeWalk());
      this.add(new Scaffold());
      this.add(new Slippy());
      this.add(new Sneak());
      this.add(new Speed());
      this.add(new Spider());
      this.add(new Sprint());
      this.add(new Step());
      this.add(new TridentBoost());
      this.add(new Velocity());
   }

   private void initRender() {
      this.add(new BetterTab());
      this.add(new BetterTooltips());
      this.add(new BlockESP());
      this.add(new BlockSelection());
      this.add(new Blur());
      this.add(new BossStack());
      this.add(new Breadcrumbs());
      this.add(new BreakIndicators());
      this.add(new CameraTweaks());
      this.add(new Chams());
      this.add(new CityESP());
      this.add(new EntityOwner());
      this.add(new ESP());
      this.add(new Freecam());
      this.add(new FreeLook());
      this.add(new Fullbright());
      this.add(new HandView());
      this.add(new HoleESP());
      this.add(new ItemPhysics());
      this.add(new ItemHighlight());
      this.add(new LightOverlay());
      this.add(new LogoutSpots());
      this.add(new Marker());
      this.add(new Nametags());
      this.add(new NoRender());
      this.add(new PopChams());
      this.add(new StorageESP());
      this.add(new TimeChanger());
      this.add(new Tracers());
      this.add(new Trail());
      this.add(new Trajectories());
      this.add(new TunnelESP());
      this.add(new VoidESP());
      this.add(new WallHack());
      this.add(new WaypointsModule());
      this.add(new Xray());
      this.add(new Zoom());
   }

   private void initWorld() {
      this.add(new Ambience());
      this.add(new AutoBreed());
      this.add(new AutoBrewer());
      this.add(new AutoMount());
      this.add(new AutoNametag());
      this.add(new AutoShearer());
      this.add(new AutoSign());
      this.add(new AutoSmelter());
      this.add(new BuildHeight());
      this.add(new Collisions());
      this.add(new EChestFarmer());
      this.add(new EndermanLook());
      this.add(new Flamethrower());
      this.add(new HighwayBuilder());
      this.add(new LiquidFiller());
      this.add(new MountBypass());
      this.add(new NoGhostBlocks());
      this.add(new Nuker());
      this.add(new PacketMine());
      this.add(new StashFinder());
      this.add(new SpawnProofer());
      this.add(new Timer());
      this.add(new VeinMiner());
      if (BaritoneUtils.IS_AVAILABLE) {
         this.add(new Excavator());
         this.add(new InfinityMiner());
      }

   }

   private void initMisc() {
      this.add(new AntiPacketKick());
      this.add(new AutoReconnect());
      this.add(new BetterBeacons());
      this.add(new BetterChat());
      this.add(new BookBot());
      this.add(new DiscordPresence());
      this.add(new InventoryTweaks());
      this.add(new MessageAura());
      this.add(new Notebot());
      this.add(new Notifier());
      this.add(new PacketCanceller());
      this.add(new PacketLogger());
      this.add(new ServerSpoof());
      this.add(new SoundBlocker());
      this.add(new Spam());
      this.add(new Swarm());
   }
}
