package meteordevelopment.meteorclient.systems.modules.misc.swarm;

import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_156;

public class Swarm extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<Mode> mode;
   private final Setting<String> ipAddress;
   private final Setting<Integer> serverPort;
   public SwarmHost host;
   public SwarmWorker worker;

   public Swarm() {
      super(Categories.Misc, "swarm", "Allows you to control multiple instances of Meteor from one central host.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("What type of client to run.")).defaultValue(Swarm.Mode.Host)).build());
      this.ipAddress = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("ip")).description("The IP address of the host server.")).defaultValue("localhost")).visible(() -> this.mode.get() == Swarm.Mode.Worker)).build());
      this.serverPort = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("port")).description("The port used for connections.")).defaultValue(6969)).range(1, 65535).noSlider().build());
   }

   public WWidget getWidget(GuiTheme theme) {
      WVerticalList list = theme.verticalList();
      WHorizontalList b = (WHorizontalList)list.add(theme.horizontalList()).expandX().widget();
      WButton start = (WButton)b.add(theme.button("Start")).expandX().widget();
      start.action = () -> {
         if (this.isActive()) {
            this.close();
            if (this.mode.get() == Swarm.Mode.Host) {
               this.host = new SwarmHost((Integer)this.serverPort.get());
            } else {
               this.worker = new SwarmWorker(this.ipAddress.get(), (Integer)this.serverPort.get());
            }

         }
      };
      WButton stop = (WButton)b.add(theme.button("Stop")).expandX().widget();
      stop.action = this::close;
      WButton guide = (WButton)list.add(theme.button("Guide")).expandX().widget();
      guide.action = () -> class_156.method_668().method_670("https://github.com/MeteorDevelopment/meteor-client/wiki/Swarm-Guide");
      return list;
   }

   public String getInfoString() {
      return ((Mode)this.mode.get()).name();
   }

   public void onActivate() {
      this.close();
   }

   public void onDeactivate() {
      this.close();
   }

   public void close() {
      try {
         if (this.host != null) {
            this.host.disconnect();
            this.host = null;
         }

         if (this.worker != null) {
            this.worker.disconnect();
            this.worker = null;
         }
      } catch (Exception var2) {
      }

   }

   @EventHandler
   private void onGameLeft(GameLeftEvent event) {
      this.toggle();
   }

   @EventHandler
   private void onGameJoin(GameJoinedEvent event) {
      this.toggle();
   }

   public void toggle() {
      this.close();
      super.toggle();
   }

   public boolean isHost() {
      return this.mode.get() == Swarm.Mode.Host && this.host != null && !this.host.isInterrupted();
   }

   public boolean isWorker() {
      return this.mode.get() == Swarm.Mode.Worker && this.worker != null && !this.worker.isInterrupted();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.isWorker()) {
         this.worker.tick();
      }

   }

   public static enum Mode {
      Host,
      Worker;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Host, Worker};
      }
   }
}
