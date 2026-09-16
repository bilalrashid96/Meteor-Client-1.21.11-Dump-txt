package meteordevelopment.meteorclient.systems.modules.movement.elytrafly;

import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_2848;
import net.minecraft.class_310;
import net.minecraft.class_2848.class_2849;

public class ElytraFlightMode {
   protected final class_310 mc = class_310.method_1551();
   protected final ElytraFly elytraFly = (ElytraFly)Modules.get().get(ElytraFly.class);
   private final ElytraFlightModes type;
   protected boolean lastJumpPressed;
   protected boolean incrementJumpTimer;
   protected boolean lastForwardPressed;
   protected int jumpTimer;
   protected double velX;
   protected double velY;
   protected double velZ;
   protected double ticksLeft;
   protected class_243 forward;
   protected class_243 right;
   protected double acceleration;

   public ElytraFlightMode(ElytraFlightModes type) {
      this.type = type;
   }

   public void onTick() {
      if ((Boolean)this.elytraFly.autoReplenish.get()) {
         FindItemResult fireworks = InvUtils.find(class_1802.field_8639);
         if (fireworks.found() && !fireworks.isHotbar()) {
            InvUtils.move().from(fireworks.slot()).toHotbar((Integer)this.elytraFly.replenishSlot.get() - 1);
         }
      }

      if ((Boolean)this.elytraFly.replace.get()) {
         class_1799 chestStack = this.mc.field_1724.method_6118(class_1304.field_6174);
         if (chestStack.method_7909() == class_1802.field_8833 && chestStack.method_7936() - chestStack.method_7919() <= (Integer)this.elytraFly.replaceDurability.get()) {
            FindItemResult elytra = InvUtils.find((Predicate)((stack) -> stack.method_7936() - stack.method_7919() > (Integer)this.elytraFly.replaceDurability.get() && stack.method_7909() == class_1802.field_8833));
            InvUtils.move().from(elytra.slot()).toArmor(2);
         }
      }

   }

   public void onPreTick() {
   }

   public void onPacketSend(PacketEvent.Send event) {
   }

   public void onPacketReceive(PacketEvent.Receive event) {
   }

   public void onPlayerMove() {
   }

   public void onActivate() {
      this.lastJumpPressed = false;
      this.jumpTimer = 0;
      this.ticksLeft = (double)0.0F;
      this.acceleration = (double)0.0F;
   }

   public void onDeactivate() {
   }

   public void autoTakeoff() {
      if (this.incrementJumpTimer) {
         ++this.jumpTimer;
      }

      boolean jumpPressed = this.mc.field_1690.field_1903.method_1434();
      if ((Boolean)this.elytraFly.autoTakeOff.get() && this.elytraFly.flightMode.get() != ElytraFlightModes.Pitch40 && this.elytraFly.flightMode.get() != ElytraFlightModes.Bounce || !(Boolean)this.elytraFly.manualTakeoff.get() && this.elytraFly.flightMode.get() == ElytraFlightModes.Bounce && jumpPressed) {
         if (!this.lastJumpPressed && !this.mc.field_1724.method_6128()) {
            this.jumpTimer = 0;
            this.incrementJumpTimer = true;
         }

         if (this.jumpTimer >= 8) {
            this.jumpTimer = 0;
            this.incrementJumpTimer = false;
            this.mc.field_1724.method_6100(false);
            this.mc.field_1724.method_5728(true);
            this.mc.field_1724.method_6043();
            this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12982));
         }
      }

      this.lastJumpPressed = jumpPressed;
   }

   public void handleAutopilot() {
      if (this.mc.field_1724.method_6128()) {
         if ((Boolean)this.elytraFly.autoPilot.get() && this.mc.field_1724.method_23318() > (Double)this.elytraFly.autoPilotMinimumHeight.get() && this.elytraFly.flightMode.get() != ElytraFlightModes.Bounce) {
            this.mc.field_1690.field_1894.method_23481(true);
            this.lastForwardPressed = true;
         }

         if ((Boolean)this.elytraFly.useFireworks.get()) {
            if (this.ticksLeft <= (double)0.0F) {
               this.ticksLeft = (Double)this.elytraFly.autoPilotFireworkDelay.get() * (double)20.0F;
               FindItemResult itemResult = InvUtils.findInHotbar(class_1802.field_8639);
               if (!itemResult.found()) {
                  return;
               }

               if (itemResult.isOffhand()) {
                  this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5810);
                  this.mc.field_1724.method_6104(class_1268.field_5810);
               } else {
                  InvUtils.swap(itemResult.slot(), true);
                  this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
                  this.mc.field_1724.method_6104(class_1268.field_5808);
                  InvUtils.swapBack();
               }
            }

            --this.ticksLeft;
         }

      }
   }

   public void handleHorizontalSpeed(PlayerMoveEvent event) {
      boolean a = false;
      boolean b = false;
      if (this.mc.field_1690.field_1894.method_1434()) {
         this.velX += this.forward.field_1352 * this.getSpeed() * (double)10.0F;
         this.velZ += this.forward.field_1350 * this.getSpeed() * (double)10.0F;
         a = true;
      } else if (this.mc.field_1690.field_1881.method_1434()) {
         this.velX -= this.forward.field_1352 * this.getSpeed() * (double)10.0F;
         this.velZ -= this.forward.field_1350 * this.getSpeed() * (double)10.0F;
         a = true;
      }

      if (this.mc.field_1690.field_1849.method_1434()) {
         this.velX += this.right.field_1352 * this.getSpeed() * (double)10.0F;
         this.velZ += this.right.field_1350 * this.getSpeed() * (double)10.0F;
         b = true;
      } else if (this.mc.field_1690.field_1913.method_1434()) {
         this.velX -= this.right.field_1352 * this.getSpeed() * (double)10.0F;
         this.velZ -= this.right.field_1350 * this.getSpeed() * (double)10.0F;
         b = true;
      }

      if (a && b) {
         double diagonal = (double)1.0F / Math.sqrt((double)2.0F);
         this.velX *= diagonal;
         this.velZ *= diagonal;
      }

   }

   public void handleVerticalSpeed(PlayerMoveEvent event) {
      if (this.mc.field_1690.field_1903.method_1434()) {
         this.velY += (double)0.5F * (Double)this.elytraFly.verticalSpeed.get();
      } else if (this.mc.field_1690.field_1832.method_1434()) {
         this.velY -= (double)0.5F * (Double)this.elytraFly.verticalSpeed.get();
      }

   }

   public void handleFallMultiplier() {
      if (this.velY < (double)0.0F) {
         this.velY *= (Double)this.elytraFly.fallMultiplier.get();
      } else if (this.velY > (double)0.0F) {
         this.velY = (double)0.0F;
      }

   }

   public void handleAcceleration() {
      if ((Boolean)this.elytraFly.acceleration.get()) {
         if (!PlayerUtils.isMoving()) {
            this.acceleration = (double)0.0F;
         }

         this.acceleration = Math.min(this.acceleration + (Double)this.elytraFly.accelerationMin.get() + (Double)this.elytraFly.accelerationStep.get() * 0.1, (Double)this.elytraFly.horizontalSpeed.get());
      } else {
         this.acceleration = (double)0.0F;
      }

   }

   public void zeroAcceleration() {
      this.acceleration = (double)0.0F;
   }

   protected double getSpeed() {
      return (Boolean)this.elytraFly.acceleration.get() ? this.acceleration : (Double)this.elytraFly.horizontalSpeed.get();
   }

   public String getHudString() {
      return this.type.name();
   }
}
