package meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightMode;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightModes;

public class Pitch40 extends ElytraFlightMode {
   private boolean pitchingDown = true;
   private float pitch;

   public Pitch40() {
      super(ElytraFlightModes.Pitch40);
   }

   public void onActivate() {
      if (this.mc.field_1724.method_23318() < (Double)this.elytraFly.pitch40upperBounds.get()) {
         this.elytraFly.error("Player must be above upper bounds!", new Object[0]);
         this.elytraFly.toggle();
      } else if (this.mc.field_1724.method_23318() - (double)40.0F < (Double)this.elytraFly.pitch40lowerBounds.get()) {
         this.elytraFly.error("Player must be at least 40 blocks above the lower bounds!", new Object[0]);
         this.elytraFly.toggle();
      }

      this.pitch = 37.72F;
   }

   private float randPitch(float pitch, float bound) {
      return (float)((double)pitch + (double)bound * (Math.random() - (double)0.5F));
   }

   public void onTick() {
      super.onTick();
      if (this.pitchingDown && this.mc.field_1724.method_23318() <= (Double)this.elytraFly.pitch40lowerBounds.get()) {
         this.pitchingDown = false;
      } else if (!this.pitchingDown && this.mc.field_1724.method_23318() >= (Double)this.elytraFly.pitch40upperBounds.get()) {
         this.pitchingDown = true;
      }

      if (!this.pitchingDown) {
         this.pitch -= this.randPitch(((Double)this.elytraFly.pitch40rotationSpeedUp.get()).floatValue(), 1.0F);
         if (this.pitch < -54.77F) {
            this.pitch = -54.77F;
            this.pitchingDown = true;
         }
      } else if (this.pitch < 37.72F) {
         this.pitch += this.randPitch(((Double)this.elytraFly.pitch40rotationSpeedDown.get()).floatValue(), 0.5F);
      }

      this.mc.field_1724.method_36457(this.pitch);
   }

   public void autoTakeoff() {
   }

   public void handleHorizontalSpeed(PlayerMoveEvent event) {
      this.velX = event.movement.field_1352;
      this.velZ = event.movement.field_1350;
   }

   public void handleVerticalSpeed(PlayerMoveEvent event) {
   }

   public void handleFallMultiplier() {
   }

   public void handleAutopilot() {
   }
}
