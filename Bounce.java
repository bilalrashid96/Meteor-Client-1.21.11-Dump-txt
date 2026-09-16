package meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightMode;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightModes;
import meteordevelopment.meteorclient.systems.modules.player.Rotation;
import net.minecraft.class_1294;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_2680;
import net.minecraft.class_2708;
import net.minecraft.class_2848;
import net.minecraft.class_310;
import net.minecraft.class_3481;
import net.minecraft.class_746;
import net.minecraft.class_2848.class_2849;

public class Bounce extends ElytraFlightMode {
   boolean rubberbanded = false;
   int tickDelay;
   double prevFov;

   public Bounce() {
      super(ElytraFlightModes.Bounce);
      this.tickDelay = (Integer)this.elytraFly.restartDelay.get();
   }

   public void onTick() {
      super.onTick();
      if (this.mc.field_1690.field_1903.method_1434() && !this.mc.field_1724.method_6128() && !(Boolean)this.elytraFly.manualTakeoff.get()) {
         this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12982));
      }

      if (checkConditions(this.mc.field_1724)) {
         if (!this.rubberbanded) {
            if (this.prevFov != (double)0.0F && !(Boolean)this.elytraFly.sprint.get()) {
               this.mc.field_1690.method_42454().method_41748((double)0.0F);
            }

            if ((Boolean)this.elytraFly.autoJump.get()) {
               this.mc.field_1690.field_1903.method_23481(true);
            }

            this.mc.field_1690.field_1894.method_23481(true);
            this.mc.field_1724.method_36456(this.getYawDirection());
            if ((Boolean)this.elytraFly.lockPitch.get()) {
               this.mc.field_1724.method_36457(((Double)this.elytraFly.pitch.get()).floatValue());
            }
         }

         if (!(Boolean)this.elytraFly.sprint.get()) {
            if (this.mc.field_1724.method_6128()) {
               this.mc.field_1724.method_5728(this.mc.field_1724.method_24828());
            } else {
               this.mc.field_1724.method_5728(true);
            }
         }

         if (this.rubberbanded && (Boolean)this.elytraFly.restart.get()) {
            if (this.tickDelay > 0) {
               --this.tickDelay;
            } else {
               this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12982));
               this.rubberbanded = false;
               this.tickDelay = (Integer)this.elytraFly.restartDelay.get();
            }
         }
      }

   }

   public void onPreTick() {
      super.onPreTick();
      if (checkConditions(this.mc.field_1724) && (Boolean)this.elytraFly.sprint.get()) {
         this.mc.field_1724.method_5728(true);
      }

   }

   private void unpress() {
      this.mc.field_1690.field_1894.method_23481(false);
      if ((Boolean)this.elytraFly.autoJump.get()) {
         this.mc.field_1690.field_1903.method_23481(false);
      }

   }

   public void onPacketReceive(PacketEvent.Receive event) {
      if (event.packet instanceof class_2708) {
         this.rubberbanded = true;
         this.mc.field_1724.method_66281();
      }

   }

   public void onPacketSend(PacketEvent.Send event) {
      if (event.packet instanceof class_2848 && ((class_2848)event.packet).method_12365().equals(class_2849.field_12982) && !(Boolean)this.elytraFly.sprint.get()) {
         this.mc.field_1724.method_5728(true);
      }

   }

   public static boolean recastElytra(class_746 player) {
      if (checkConditions(player) && startGliding(player)) {
         player.field_3944.method_52787(new class_2848(player, class_2849.field_12982));
         return true;
      } else {
         return false;
      }
   }

   public static boolean checkConditions(class_746 player) {
      class_2680 blockState = player.method_55667();
      boolean isClimbing = blockState.method_26164(class_3481.field_22414) && !blockState.method_26164(class_3481.field_63247);
      return !player.method_31549().field_7479 && !player.method_5765() && !isClimbing && !player.method_5799() && !player.method_6059(class_1294.field_5902);
   }

   private static boolean startGliding(class_746 player) {
      for(class_1304 equipmentSlot : class_1304.field_54086) {
         if (class_1309.method_63624(player.method_6118(equipmentSlot), equipmentSlot)) {
            class_310 var10000 = MeteorClient.mc;
            Objects.requireNonNull(player);
            var10000.method_40000(player::method_23669);
            return true;
         }
      }

      return false;
   }

   private float getYawDirection() {
      float var10000;
      switch ((Rotation.LockMode)this.elytraFly.yawLockMode.get()) {
         case None -> var10000 = this.mc.field_1724.method_36454();
         case Smart -> var10000 = (float)Math.round((this.mc.field_1724.method_36454() + 1.0F) / 45.0F) * 45.0F;
         case Simple -> var10000 = ((Double)this.elytraFly.yaw.get()).floatValue();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public void onActivate() {
      this.prevFov = (Double)this.mc.field_1690.method_42454().method_41753();
   }

   public void onDeactivate() {
      this.unpress();
      this.rubberbanded = false;
      if (this.prevFov != (double)0.0F && !(Boolean)this.elytraFly.sprint.get()) {
         this.mc.field_1690.method_42454().method_41748(this.prevFov);
      }

   }
}
