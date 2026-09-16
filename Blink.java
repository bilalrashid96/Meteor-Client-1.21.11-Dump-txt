package meteordevelopment.meteorclient.systems.modules.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_634;
import org.joml.Vector3d;

public class Blink extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> renderOriginal;
   private final Setting<Integer> delay;
   private final Setting<Keybind> cancelBlink;
   private final List<class_2828> packets;
   private FakePlayerEntity model;
   private final Vector3d start;
   private boolean cancelled;
   private boolean sending;
   private int timer;

   public Blink() {
      super(Categories.Movement, "blink", "Allows you to essentially teleport while suspending motion updates.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.renderOriginal = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-original")).description("Renders your player model at the original position.")).defaultValue(true)).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("pulse-delay")).description("After the duration in ticks has elapsed, send all packets and start blinking again. 0 to disable.")).defaultValue(0)).min(0).sliderMax(60).build());
      this.cancelBlink = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("cancel-blink")).description("Cancels sending packets and sends you back to your original position.")).defaultValue(Keybind.none())).action(() -> {
         this.cancelled = true;
         this.disable();
      }).build());
      this.packets = new ArrayList();
      this.start = new Vector3d();
      this.timer = 0;
      this.runInMainMenu = true;
   }

   public void onActivate() {
      if (Utils.canUpdate()) {
         if ((Boolean)this.renderOriginal.get()) {
            this.model = new FakePlayerEntity(this.mc.field_1724, this.mc.field_1724.method_7334().name(), 20.0F, true);
            this.model.doNotPush = true;
            this.model.hideWhenInsideCamera = true;
            this.model.noHit = true;
            this.model.spawn();
         }

         Utils.set(this.start, this.mc.field_1724.method_73189());
      }
   }

   public void onDeactivate() {
      if (Utils.canUpdate()) {
         this.dumpPackets(!this.cancelled);
         if (this.cancelled) {
            this.mc.field_1724.method_23327(this.start.x, this.start.y, this.start.z);
            this.mc.field_1724.method_18799(class_243.field_1353);
         }

         this.cancelled = false;
      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (Utils.canUpdate()) {
         ++this.timer;
         if ((Integer)this.delay.get() != 0 && (Integer)this.delay.get() <= this.timer) {
            this.onDeactivate();
            this.onActivate();
         }

      }
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      if (Utils.canUpdate()) {
         if (!this.sending) {
            class_2596 var3 = event.packet;
            if (var3 instanceof class_2828) {
               class_2828 p = (class_2828)var3;
               event.cancel();
               class_2828 prev = this.packets.isEmpty() ? null : (class_2828)this.packets.getLast();
               if (prev == null || p.method_12273() != prev.method_12273() || p.method_12271(-1.0F) != prev.method_12271(-1.0F) || p.method_12270(-1.0F) != prev.method_12270(-1.0F) || p.method_12269((double)-1.0F) != prev.method_12269((double)-1.0F) || p.method_12268((double)-1.0F) != prev.method_12268((double)-1.0F) || p.method_12274((double)-1.0F) != prev.method_12274((double)-1.0F)) {
                  synchronized(this.packets) {
                     this.packets.add(p);
                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onJoinGame(GameJoinedEvent event) {
      this.warning("Blink is currently enabled; you won't be able to interact with anything properly until you disable it!", new Object[0]);
   }

   @EventHandler
   private void onLeaveGame(GameLeftEvent event) {
      this.onDeactivate();
   }

   public String getInfoString() {
      return String.format("%.1f", (float)this.timer / 20.0F);
   }

   private void dumpPackets(boolean send) {
      this.sending = true;
      synchronized(this.packets) {
         if (send) {
            List var10000 = this.packets;
            class_634 var10001 = this.mc.field_1724.field_3944;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::method_52787);
         }

         this.packets.clear();
      }

      this.sending = false;
      if (this.model != null) {
         this.model.despawn();
         this.model = null;
      }

      this.timer = 0;
   }
}
