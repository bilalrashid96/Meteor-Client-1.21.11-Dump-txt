package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ClientPlayerEntityAccessor;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2696;
import net.minecraft.class_2828;

public class Flight extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgAntiKick;
   private final Setting<Mode> mode;
   private final Setting<Double> speed;
   private final Setting<Boolean> verticalSpeedMatch;
   private final Setting<Boolean> noSneak;
   private final Setting<AntiKickMode> antiKickMode;
   private final Setting<Integer> delay;
   private final Setting<Integer> offTime;
   private int delayLeft;
   private int offLeft;
   private boolean flip;
   private float lastYaw;
   private double lastPacketY;

   public Flight() {
      super(Categories.Movement, "flight", "FLYYYY! No Fall is recommended with this module.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgAntiKick = this.settings.createGroup("Anti Kick");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The mode for Flight.")).defaultValue(Flight.Mode.Abilities)).onChanged((mode) -> {
         if (this.isActive() && Utils.canUpdate()) {
            this.abilitiesOff();
         }
      })).build());
      this.speed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("speed")).description("Your speed when flying.")).defaultValue(0.1).min((double)0.0F).build());
      this.verticalSpeedMatch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("vertical-speed-match")).description("Matches your vertical speed to your horizontal speed, otherwise uses vanilla ratio.")).defaultValue(false)).build());
      this.noSneak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("no-sneak")).description("Prevents you from sneaking while flying.")).defaultValue(false)).visible(() -> this.mode.get() == Flight.Mode.Velocity)).build());
      this.antiKickMode = this.sgAntiKick.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The mode for anti kick.")).defaultValue(Flight.AntiKickMode.Packet)).build());
      this.delay = this.sgAntiKick.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("The amount of delay, in ticks, between flying down a bit and return to original position")).defaultValue(20)).min(1).sliderMax(200).build());
      this.offTime = this.sgAntiKick.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("off-time")).description("The amount of delay, in ticks, to fly down a bit to reset floating ticks.")).defaultValue(1)).min(1).sliderRange(1, 20).build());
      this.delayLeft = (Integer)this.delay.get();
      this.offLeft = (Integer)this.offTime.get();
      this.lastPacketY = Double.MAX_VALUE;
   }

   public void onActivate() {
      if (this.mode.get() == Flight.Mode.Abilities && !this.mc.field_1724.method_7325()) {
         this.mc.field_1724.method_31549().field_7479 = true;
         if (this.mc.field_1724.method_31549().field_7477) {
            return;
         }

         this.mc.field_1724.method_31549().field_7478 = true;
      }

   }

   public void onDeactivate() {
      if (this.mode.get() == Flight.Mode.Abilities && !this.mc.field_1724.method_7325()) {
         this.abilitiesOff();
      }

   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      float currentYaw = this.mc.field_1724.method_36454();
      if (this.mc.field_1724.field_6017 >= (double)3.0F && currentYaw == this.lastYaw && this.mc.field_1724.method_18798().method_1033() < 0.003) {
         this.mc.field_1724.method_36456(currentYaw + (float)(this.flip ? 1 : -1));
         this.flip = !this.flip;
      }

      this.lastYaw = currentYaw;
   }

   @EventHandler
   private void onPostTick(TickEvent.Post event) {
      if (this.delayLeft > 0) {
         --this.delayLeft;
      }

      if (this.offLeft <= 0 && this.delayLeft <= 0) {
         this.delayLeft = (Integer)this.delay.get();
         this.offLeft = (Integer)this.offTime.get();
         if (this.antiKickMode.get() == Flight.AntiKickMode.Packet) {
            ((ClientPlayerEntityAccessor)this.mc.field_1724).meteor$setTicksSinceLastPositionPacketSent(20);
         }
      } else if (this.delayLeft <= 0) {
         boolean shouldReturn = false;
         if (this.antiKickMode.get() == Flight.AntiKickMode.Normal) {
            if (this.mode.get() == Flight.Mode.Abilities) {
               this.abilitiesOff();
               shouldReturn = true;
            }
         } else if (this.antiKickMode.get() == Flight.AntiKickMode.Packet && this.offLeft == (Integer)this.offTime.get()) {
            ((ClientPlayerEntityAccessor)this.mc.field_1724).meteor$setTicksSinceLastPositionPacketSent(20);
         }

         --this.offLeft;
         if (shouldReturn) {
            return;
         }
      }

      if (this.mc.field_1724.method_36454() != this.lastYaw) {
         this.mc.field_1724.method_36456(this.lastYaw);
      }

      switch (((Mode)this.mode.get()).ordinal()) {
         case 0:
            if (this.mc.field_1724.method_7325()) {
               return;
            }

            this.mc.field_1724.method_31549().method_7248(((Double)this.speed.get()).floatValue());
            this.mc.field_1724.method_31549().field_7479 = true;
            if (this.mc.field_1724.method_31549().field_7477) {
               return;
            }

            this.mc.field_1724.method_31549().field_7478 = true;
            break;
         case 1:
            this.mc.field_1724.method_31549().field_7479 = false;
            this.mc.field_1724.method_18800((double)0.0F, (double)0.0F, (double)0.0F);
            class_243 playerVelocity = this.mc.field_1724.method_18798();
            if (this.mc.field_1690.field_1903.method_1434()) {
               playerVelocity = playerVelocity.method_1031((double)0.0F, (Double)this.speed.get() * (double)((Boolean)this.verticalSpeedMatch.get() ? 10.0F : 5.0F), (double)0.0F);
            }

            if (this.mc.field_1690.field_1832.method_1434()) {
               playerVelocity = playerVelocity.method_1023((double)0.0F, (Double)this.speed.get() * (double)((Boolean)this.verticalSpeedMatch.get() ? 10.0F : 5.0F), (double)0.0F);
            }

            this.mc.field_1724.method_18799(playerVelocity);
            if ((Boolean)this.noSneak.get()) {
               this.mc.field_1724.method_24830(false);
            }
      }

   }

   private void antiKickPacket(class_2828 packet, double currentY) {
      if (this.delayLeft <= 0 && this.lastPacketY != Double.MAX_VALUE && this.shouldFlyDown(currentY, this.lastPacketY) && EntityUtils.isOnAir(this.mc.field_1724)) {
         ((PlayerMoveC2SPacketAccessor)packet).meteor$setY(this.lastPacketY - 0.0313);
      } else {
         this.lastPacketY = currentY;
      }

   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2828 packet) {
         if (this.antiKickMode.get() == Flight.AntiKickMode.Packet) {
            double currentY = packet.method_12268(Double.MAX_VALUE);
            if (currentY != Double.MAX_VALUE) {
               this.antiKickPacket(packet, currentY);
            } else {
               class_2828 fullPacket;
               if (packet.method_36172()) {
                  fullPacket = new class_2828.class_2830(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321(), packet.method_12271(0.0F), packet.method_12270(0.0F), packet.method_12273(), this.mc.field_1724.field_5976);
               } else {
                  fullPacket = new class_2828.class_2829(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321(), packet.method_12273(), this.mc.field_1724.field_5976);
               }

               event.cancel();
               this.antiKickPacket(fullPacket, this.mc.field_1724.method_23318());
               this.mc.method_1562().method_52787(fullPacket);
            }

            return;
         }
      }

   }

   @EventHandler
   private void onReceivePacket(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2696 packet) {
         if (this.mode.get() == Flight.Mode.Abilities) {
            event.cancel();
            this.mc.field_1724.method_31549().field_7480 = packet.method_11695();
            this.mc.field_1724.method_31549().field_7477 = packet.method_11696();
            this.mc.field_1724.method_31549().method_7250(packet.method_11691());
            return;
         }
      }

   }

   private boolean shouldFlyDown(double currentY, double lastY) {
      if (currentY >= lastY) {
         return true;
      } else {
         return lastY - currentY < 0.0313;
      }
   }

   private void abilitiesOff() {
      this.mc.field_1724.method_31549().field_7479 = false;
      this.mc.field_1724.method_31549().method_7248(0.05F);
      if (!this.mc.field_1724.method_31549().field_7477) {
         this.mc.field_1724.method_31549().field_7478 = false;
      }
   }

   public float getOffGroundSpeed() {
      return this.isActive() && this.mode.get() == Flight.Mode.Velocity ? ((Double)this.speed.get()).floatValue() * (this.mc.field_1724.method_5624() ? 15.0F : 10.0F) : -1.0F;
   }

   public boolean noSneak() {
      return this.isActive() && this.mode.get() == Flight.Mode.Velocity && (Boolean)this.noSneak.get();
   }

   public static enum Mode {
      Abilities,
      Velocity;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Abilities, Velocity};
      }
   }

   public static enum AntiKickMode {
      Normal,
      Packet,
      None;

      // $FF: synthetic method
      private static AntiKickMode[] $values() {
         return new AntiKickMode[]{Normal, Packet, None};
      }
   }
}
