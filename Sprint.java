package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerInteractEntityC2SPacket;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2596;
import net.minecraft.class_2848;
import net.minecraft.class_2824.class_5907;
import net.minecraft.class_2848.class_2849;

public class Sprint extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<Mode> mode;
   private final Setting<Boolean> keepSprint;
   private final Setting<Boolean> unsprintOnHit;
   public final Setting<Boolean> unsprintInWater;
   private final Setting<Boolean> permaSprint;

   public Sprint() {
      super(Categories.Movement, "sprint", "Automatically sprints.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("sprint-mode")).description("What mode of sprinting.")).defaultValue(Sprint.Mode.Strict)).build());
      this.keepSprint = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("keep-sprint")).description("Whether to keep sprinting after attacking.")).defaultValue(false)).build());
      this.unsprintOnHit = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("unsprint-on-hit")).description("Whether to stop sprinting before attacking, to ensure you get crits and sweep attacks.")).defaultValue(false)).build());
      this.unsprintInWater = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("unsprint-in-water")).description("Whether to stop sprinting when in water.")).defaultValue(true)).visible(() -> this.mode.get() == Sprint.Mode.Rage)).build());
      this.permaSprint = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sprint-while-stationary")).description("Sprint even when not moving.")).defaultValue(false)).visible(() -> this.mode.get() == Sprint.Mode.Rage)).build());
   }

   @EventHandler(
      priority = 100
   )
   private void onTickMovement(TickEvent.Post event) {
      if (!(Boolean)this.unsprintInWater.get() || !this.mc.field_1724.method_5799()) {
         this.mc.field_1724.method_5728(this.shouldSprint());
      }
   }

   @EventHandler(
      priority = 100
   )
   private void onPacketSend(PacketEvent.Send event) {
      if ((Boolean)this.unsprintOnHit.get()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof IPlayerInteractEntityC2SPacket) {
            IPlayerInteractEntityC2SPacket packet = (IPlayerInteractEntityC2SPacket)var3;
            if (packet.meteor$getType() == class_5907.field_29172) {
               this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12985));
               this.mc.field_1724.method_5728(false);
               return;
            }
         }

      }
   }

   @EventHandler
   private void onPacketSent(PacketEvent.Sent event) {
      if ((Boolean)this.unsprintOnHit.get() && (Boolean)this.keepSprint.get()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof IPlayerInteractEntityC2SPacket) {
            IPlayerInteractEntityC2SPacket packet = (IPlayerInteractEntityC2SPacket)var3;
            if (packet.meteor$getType() == class_5907.field_29172) {
               if (this.shouldSprint() && !this.mc.field_1724.method_5624()) {
                  this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12981));
                  this.mc.field_1724.method_5728(true);
                  return;
               }

               return;
            }
         }

      }
   }

   public boolean shouldSprint() {
      if (this.mc.field_1755 != null && !(Boolean)((GUIMove)Modules.get().get(GUIMove.class)).sprint.get()) {
         return false;
      } else {
         float movement = this.mode.get() == Sprint.Mode.Rage ? Math.abs(this.mc.field_1724.field_6250) + Math.abs(this.mc.field_1724.field_6212) : this.mc.field_1724.field_6250;
         if (!((double)movement <= (this.mc.field_1724.method_5869() ? (double)1.0E-5F : 0.8)) || this.mode.get() != Sprint.Mode.Strict && (Boolean)this.permaSprint.get()) {
            boolean strictSprint = !this.mc.field_1724.method_74016() && !this.mc.field_1724.method_74025() && this.mc.field_1724.method_5765() ? this.mc.field_1724.method_5854().method_48155() && this.mc.field_1724.method_5854().method_66247() : this.mc.field_1724.method_7344().method_75882() && (!this.mc.field_1724.field_5976 || this.mc.field_1724.field_34927);
            return this.isActive() && (this.mode.get() == Sprint.Mode.Rage || strictSprint);
         } else {
            return false;
         }
      }
   }

   public boolean rageSprint() {
      return this.isActive() && this.mode.get() == Sprint.Mode.Rage;
   }

   public boolean unsprintInWater() {
      return this.isActive() && (Boolean)this.unsprintInWater.get();
   }

   public boolean stopSprinting() {
      return !this.isActive() || !(Boolean)this.keepSprint.get();
   }

   public static enum Mode {
      Strict,
      Rage;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Strict, Rage};
      }
   }
}
