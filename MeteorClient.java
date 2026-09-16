package meteordevelopment.meteorclient;

import java.io.File;
import java.lang.invoke.MethodHandles;
import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.hud.screens.HudEditorScreen;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.meteorclient.utils.ReflectInit;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Version;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.misc.input.KeyBinds;
import meteordevelopment.meteorclient.utils.network.OnlinePlayers;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.IEventBus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_408;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class MeteorClient implements ClientModInitializer {
   public static final String MOD_ID = "meteor-client";
   public static final ModMetadata MOD_META = ((ModContainer)FabricLoader.getInstance().getModContainer("meteor-client").orElseThrow()).getMetadata();
   public static final String NAME;
   public static final Version VERSION;
   public static final String BUILD_NUMBER;
   public static MeteorClient INSTANCE;
   public static MeteorAddon ADDON;
   public static class_310 mc;
   public static final IEventBus EVENT_BUS = new EventBus();
   public static final File FOLDER = FabricLoader.getInstance().getGameDir().resolve("meteor-client").toFile();
   public static final Logger LOG;
   private boolean wasWidgetScreen;
   private boolean wasHudHiddenRoot;

   public void onInitializeClient() {
      if (INSTANCE == null) {
         INSTANCE = this;
      } else {
         mc = class_310.method_1551();
         if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            LOG.info("Force loading mixins");
            MixinEnvironment.getCurrentEnvironment().audit();
         }

         LOG.info("Initializing {}", NAME);
         if (!FOLDER.exists()) {
            FOLDER.getParentFile().mkdirs();
            FOLDER.mkdir();
            Systems.addPreLoadTask(() -> ((DiscordPresence)Modules.get().get(DiscordPresence.class)).enable());
         }

         AddonManager.init();
         AddonManager.ADDONS.forEach((addon) -> {
            try {
               EVENT_BUS.registerLambdaFactory(addon.getPackage(), (lookupInMethod, klass) -> (MethodHandles.Lookup)lookupInMethod.invoke((Object)null, klass, MethodHandles.lookup()));
            } catch (AbstractMethodError e) {
               throw new RuntimeException("Addon \"%s\" is too old and cannot be ran.".formatted(addon.name), e);
            }
         });
         ReflectInit.registerPackages();
         ReflectInit.init(PreInit.class);
         Categories.init();
         Systems.init();
         EVENT_BUS.subscribe(this);
         AddonManager.ADDONS.forEach(MeteorAddon::onInitialize);
         Modules.get().sortModules();
         Systems.load();
         ReflectInit.init(PostInit.class);
         Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            OnlinePlayers.leave();
            Systems.save();
            GuiThemes.save();
         }));
      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (mc.field_1755 == null && mc.method_18506() == null && KeyBinds.OPEN_COMMANDS.method_1436()) {
         mc.method_1507(new class_408(Config.get().prefix.get(), true));
      }

   }

   @EventHandler
   private void onKey(KeyEvent event) {
      if (event.action == KeyAction.Press && KeyBinds.OPEN_GUI.method_1417(event.input)) {
         this.toggleGui();
      }

   }

   @EventHandler
   private void onMouseClick(MouseClickEvent event) {
      if (event.action == KeyAction.Press && KeyBinds.OPEN_GUI.method_1433(event.click)) {
         this.toggleGui();
      }

   }

   private void toggleGui() {
      if (Utils.canCloseGui()) {
         mc.field_1755.method_25419();
      } else if (Utils.canOpenGui()) {
         ((Tab)Tabs.get().getFirst()).openScreen(GuiThemes.get());
      }

   }

   @EventHandler(
      priority = -200
   )
   private void onOpenScreen(OpenScreenEvent event) {
      if (event.screen instanceof WidgetScreen) {
         if (!this.wasWidgetScreen) {
            this.wasHudHiddenRoot = mc.field_1690.field_1842;
         }

         if (GuiThemes.get().hideHUD() || this.wasHudHiddenRoot) {
            mc.field_1690.field_1842 = !(event.screen instanceof HudEditorScreen);
         }
      } else {
         if (this.wasWidgetScreen) {
            mc.field_1690.field_1842 = this.wasHudHiddenRoot;
         }

         this.wasHudHiddenRoot = mc.field_1690.field_1842;
      }

      this.wasWidgetScreen = event.screen instanceof WidgetScreen;
   }

   public static class_2960 identifier(String path) {
      return class_2960.method_60655("meteor-client", path);
   }

   static {
      NAME = MOD_META.getName();
      LOG = LoggerFactory.getLogger(NAME);
      String versionString = MOD_META.getVersion().getFriendlyString();
      if (versionString.contains("-")) {
         versionString = versionString.split("-")[0];
      }

      if (versionString.equals("${version}")) {
         versionString = "0.0.0";
      }

      VERSION = new Version(versionString);
      BUILD_NUMBER = MOD_META.getCustomValue("meteor-client:build_number").getAsString();
   }
}
