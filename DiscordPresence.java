package meteordevelopment.meteorclient.systems.modules.misc;

import java.util.ArrayList;
import java.util.List;
import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.discordipc.RichPresence;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.utils.StarscriptTextBoxRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_156;
import net.minecraft.class_3545;
import net.minecraft.class_3928;
import net.minecraft.class_404;
import net.minecraft.class_412;
import net.minecraft.class_4189;
import net.minecraft.class_420;
import net.minecraft.class_422;
import net.minecraft.class_426;
import net.minecraft.class_429;
import net.minecraft.class_440;
import net.minecraft.class_442;
import net.minecraft.class_443;
import net.minecraft.class_445;
import net.minecraft.class_446;
import net.minecraft.class_458;
import net.minecraft.class_4905;
import net.minecraft.class_500;
import net.minecraft.class_5235;
import net.minecraft.class_524;
import net.minecraft.class_525;
import net.minecraft.class_526;
import net.minecraft.class_5375;
import org.meteordev.starscript.Script;

public class DiscordPresence extends Module {
   private final SettingGroup sgLine1;
   private final SettingGroup sgLine2;
   private final Setting<List<String>> line1Strings;
   private final Setting<Integer> line1UpdateDelay;
   private final Setting<SelectMode> line1SelectMode;
   private final Setting<List<String>> line2Strings;
   private final Setting<Integer> line2UpdateDelay;
   private final Setting<SelectMode> line2SelectMode;
   private static final RichPresence rpc = new RichPresence();
   private SmallImage currentSmallImage;
   private int ticks;
   private boolean forceUpdate;
   private boolean lastWasInMainMenu;
   private final List<Script> line1Scripts;
   private int line1Ticks;
   private int line1I;
   private final List<Script> line2Scripts;
   private int line2Ticks;
   private int line2I;
   public static final List<class_3545<String, String>> customStates = new ArrayList();

   public DiscordPresence() {
      super(Categories.Misc, "discord-presence", "Displays Meteor as your presence on Discord.");
      this.sgLine1 = this.settings.createGroup("Line 1");
      this.sgLine2 = this.settings.createGroup("Line 2");
      this.line1Strings = this.sgLine1.add(((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("line-1-messages")).description("Messages used for the first line.")).defaultValue("{player}", "{server}").onChanged((strings) -> this.recompileLine1())).renderer(StarscriptTextBoxRenderer.class).build());
      this.line1UpdateDelay = this.sgLine1.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("line-1-update-delay")).description("How fast to update the first line in ticks.")).defaultValue(200)).min(10).sliderRange(10, 200).build());
      this.line1SelectMode = this.sgLine1.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("line-1-select-mode")).description("How to select messages for the first line.")).defaultValue(DiscordPresence.SelectMode.Sequential)).build());
      this.line2Strings = this.sgLine2.add(((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("line-2-messages")).description("Messages used for the second line.")).defaultValue("Meteor on Crack!", "{round(server.tps, 1)} TPS", "Playing on {server.difficulty} difficulty.", "{server.player_count} Players online").onChanged((strings) -> this.recompileLine2())).renderer(StarscriptTextBoxRenderer.class).build());
      this.line2UpdateDelay = this.sgLine2.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("line-2-update-delay")).description("How fast to update the second line in ticks.")).defaultValue(60)).min(10).sliderRange(10, 200).build());
      this.line2SelectMode = this.sgLine2.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("line-2-select-mode")).description("How to select messages for the second line.")).defaultValue(DiscordPresence.SelectMode.Sequential)).build());
      this.line1Scripts = new ArrayList();
      this.line2Scripts = new ArrayList();
      this.runInMainMenu = true;
   }

   public static void registerCustomState(String packageName, String state) {
      for(class_3545<String, String> pair : customStates) {
         if (((String)pair.method_15442()).equals(packageName)) {
            pair.method_34965(state);
            return;
         }
      }

      customStates.add(new class_3545(packageName, state));
   }

   public static void unregisterCustomState(String packageName) {
      customStates.removeIf((pair) -> ((String)pair.method_15442()).equals(packageName));
   }

   public void onActivate() {
      DiscordIPC.start(835240968533049424L, (Runnable)null);
      rpc.setStart(System.currentTimeMillis() / 1000L);
      String largeText = "%s %s".formatted(MeteorClient.NAME, MeteorClient.VERSION);
      if (!MeteorClient.BUILD_NUMBER.isEmpty()) {
         largeText = largeText + " Build: " + MeteorClient.BUILD_NUMBER;
      }

      rpc.setLargeImage("meteor_client", largeText);
      this.currentSmallImage = DiscordPresence.SmallImage.Snail;
      this.recompileLine1();
      this.recompileLine2();
      this.ticks = 0;
      this.line1Ticks = 0;
      this.line2Ticks = 0;
      this.lastWasInMainMenu = false;
      this.line1I = 0;
      this.line2I = 0;
   }

   public void onDeactivate() {
      DiscordIPC.stop();
   }

   private void recompile(List<String> messages, List<Script> scripts) {
      scripts.clear();

      for(String message : messages) {
         Script script = MeteorStarscript.compile(message);
         if (script != null) {
            scripts.add(script);
         }
      }

      this.forceUpdate = true;
   }

   private void recompileLine1() {
      this.recompile(this.line1Strings.get(), this.line1Scripts);
   }

   private void recompileLine2() {
      this.recompile(this.line2Strings.get(), this.line2Scripts);
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      boolean update = false;
      if (this.ticks < 200 && !this.forceUpdate) {
         ++this.ticks;
      } else {
         this.currentSmallImage = this.currentSmallImage.next();
         this.currentSmallImage.apply();
         update = true;
         this.ticks = 0;
      }

      if (Utils.canUpdate()) {
         if (this.line1Ticks < (Integer)this.line1UpdateDelay.get() && !this.forceUpdate) {
            ++this.line1Ticks;
         } else {
            if (!this.line1Scripts.isEmpty()) {
               int i = Utils.random(0, this.line1Scripts.size());
               if (this.line1SelectMode.get() == DiscordPresence.SelectMode.Sequential) {
                  if (this.line1I >= this.line1Scripts.size()) {
                     this.line1I = 0;
                  }

                  i = this.line1I++;
               }

               String message = MeteorStarscript.run((Script)this.line1Scripts.get(i));
               if (message != null) {
                  rpc.setDetails(message);
               }
            }

            update = true;
            this.line1Ticks = 0;
         }

         if (this.line2Ticks < (Integer)this.line2UpdateDelay.get() && !this.forceUpdate) {
            ++this.line2Ticks;
         } else {
            if (!this.line2Scripts.isEmpty()) {
               int i = Utils.random(0, this.line2Scripts.size());
               if (this.line2SelectMode.get() == DiscordPresence.SelectMode.Sequential) {
                  if (this.line2I >= this.line2Scripts.size()) {
                     this.line2I = 0;
                  }

                  i = this.line2I++;
               }

               String message = MeteorStarscript.run((Script)this.line2Scripts.get(i));
               if (message != null) {
                  rpc.setState(message);
               }
            }

            update = true;
            this.line2Ticks = 0;
         }
      } else if (!this.lastWasInMainMenu) {
         String var10001 = MeteorClient.NAME;
         rpc.setDetails(var10001 + " " + String.valueOf(MeteorClient.BUILD_NUMBER.isEmpty() ? MeteorClient.VERSION : String.valueOf(MeteorClient.VERSION) + " " + MeteorClient.BUILD_NUMBER));
         if (this.mc.field_1755 instanceof class_442) {
            rpc.setState("Looking at title screen");
         } else if (this.mc.field_1755 instanceof class_526) {
            rpc.setState("Selecting world");
         } else if (!(this.mc.field_1755 instanceof class_525) && !(this.mc.field_1755 instanceof class_5235)) {
            if (this.mc.field_1755 instanceof class_524) {
               rpc.setState("Editing world");
            } else if (this.mc.field_1755 instanceof class_3928) {
               rpc.setState("Loading world");
            } else if (this.mc.field_1755 instanceof class_500) {
               rpc.setState("Selecting server");
            } else if (this.mc.field_1755 instanceof class_422) {
               rpc.setState("Adding server");
            } else if (!(this.mc.field_1755 instanceof class_412) && !(this.mc.field_1755 instanceof class_420)) {
               if (this.mc.field_1755 instanceof WidgetScreen) {
                  rpc.setState("Browsing Meteor's GUI");
               } else if (!(this.mc.field_1755 instanceof class_429) && !(this.mc.field_1755 instanceof class_440) && !(this.mc.field_1755 instanceof class_443) && !(this.mc.field_1755 instanceof class_446) && !(this.mc.field_1755 instanceof class_458) && !(this.mc.field_1755 instanceof class_426) && !(this.mc.field_1755 instanceof class_404) && !(this.mc.field_1755 instanceof class_5375) && !(this.mc.field_1755 instanceof class_4189)) {
                  if (this.mc.field_1755 instanceof class_445) {
                     rpc.setState("Reading credits");
                  } else if (this.mc.field_1755 instanceof class_4905) {
                     rpc.setState("Browsing Realms");
                  } else {
                     boolean setState = false;
                     if (this.mc.field_1755 != null) {
                        String className = this.mc.field_1755.getClass().getName();

                        for(class_3545<String, String> pair : customStates) {
                           if (className.startsWith((String)pair.method_15442())) {
                              rpc.setState((String)pair.method_15441());
                              setState = true;
                              break;
                           }
                        }
                     }

                     if (!setState) {
                        rpc.setState("In main menu");
                     }
                  }
               } else {
                  rpc.setState("Changing options");
               }
            } else {
               rpc.setState("Connecting to server");
            }
         } else {
            rpc.setState("Creating world");
         }

         update = true;
      }

      if (update) {
         DiscordIPC.setActivity(rpc);
      }

      this.forceUpdate = false;
      this.lastWasInMainMenu = !Utils.canUpdate();
   }

   @EventHandler
   private void onOpenScreen(OpenScreenEvent event) {
      if (!Utils.canUpdate()) {
         this.lastWasInMainMenu = false;
      }

   }

   public WWidget getWidget(GuiTheme theme) {
      WButton help = theme.button("Open documentation.");
      help.action = () -> class_156.method_668().method_670("https://github.com/MeteorDevelopment/meteor-client/wiki/Starscript");
      return help;
   }

   static {
      registerCustomState("com.terraformersmc.modmenu.gui", "Browsing mods");
      registerCustomState("me.jellysquid.mods.sodium.client", "Changing options");
   }

   public static enum SelectMode {
      Random,
      Sequential;

      // $FF: synthetic method
      private static SelectMode[] $values() {
         return new SelectMode[]{Random, Sequential};
      }
   }

   private static enum SmallImage {
      MineGame("minegame", "MineGame159"),
      Snail("seasnail", "seasnail8169");

      private final String key;
      private final String text;

      private SmallImage(String key, String text) {
         this.key = key;
         this.text = text;
      }

      void apply() {
         DiscordPresence.rpc.setSmallImage(this.key, this.text);
      }

      SmallImage next() {
         return this == MineGame ? Snail : MineGame;
      }

      // $FF: synthetic method
      private static SmallImage[] $values() {
         return new SmallImage[]{MineGame, Snail};
      }
   }
}
