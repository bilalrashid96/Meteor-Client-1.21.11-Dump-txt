package meteordevelopment.meteorclient.systems.modules.movement.speed.modes;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Anchor;
import meteordevelopment.meteorclient.systems.modules.movement.speed.SpeedMode;
import meteordevelopment.meteorclient.systems.modules.movement.speed.SpeedModes;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import org.joml.Vector2d;

public class Strafe extends SpeedMode {
   private long timer = 0L;

   public Strafe() {
      super(SpeedModes.Strafe);
   }

   public void onMove(PlayerMoveEvent event) {
      switch (this.stage) {
         case 0:
            if (PlayerUtils.isMoving()) {
               ++this.stage;
               this.speed = (double)1.18F * this.getDefaultSpeed() - 0.01;
            }
         case 1:
            if (PlayerUtils.isMoving() && this.mc.field_1724.method_24828()) {
               ((IVec3d)event.movement).meteor$setY(this.getHop(0.40123128));
               this.speed *= (Double)this.settings.ncpSpeed.get();
               ++this.stage;
            }
            break;
         case 2:
            this.speed = this.distance - 0.76 * (this.distance - this.getDefaultSpeed());
            ++this.stage;
            break;
         case 3:
            if (!this.mc.field_1687.method_18026(this.mc.field_1724.method_5829().method_989((double)0.0F, this.mc.field_1724.method_18798().field_1351, (double)0.0F)) || this.mc.field_1724.field_5992 && this.stage > 0) {
               this.stage = 0;
            }

            this.speed = this.distance - this.distance / (double)159.0F;
      }

      this.speed = Math.max(this.speed, this.getDefaultSpeed());
      if ((Boolean)this.settings.ncpSpeedLimit.get()) {
         if (System.currentTimeMillis() - this.timer > 2500L) {
            this.timer = System.currentTimeMillis();
         }

         this.speed = Math.min(this.speed, System.currentTimeMillis() - this.timer > 1250L ? 0.44 : 0.43);
      }

      Vector2d change = transformStrafe(this.speed);
      Anchor anchor = (Anchor)Modules.get().get(Anchor.class);
      if (anchor.isActive() && anchor.controlMovement) {
         change.set(anchor.deltaX, anchor.deltaZ);
      }

      ((IVec3d)event.movement).meteor$setXZ(change.x, change.y);
   }

   public static Vector2d transformStrafe(double speed) {
      float forward = Math.signum(MeteorClient.mc.field_1724.field_3913.method_3128().field_1342);
      float side = Math.signum(MeteorClient.mc.field_1724.field_3913.method_3128().field_1343);
      float yaw = MeteorClient.mc.field_1724.method_61415(MeteorClient.mc.method_61966().method_60637(true));
      if (forward == 0.0F && side == 0.0F) {
         return new Vector2d();
      } else {
         float strafe = 90.0F * side;
         if (forward != 0.0F) {
            strafe *= forward * 0.5F;
         }

         yaw -= strafe;
         if (forward < 0.0F) {
            yaw -= 180.0F;
         }

         double yawRadians = Math.toRadians((double)yaw);
         return new Vector2d(-Math.sin(yawRadians) * speed, Math.cos(yawRadians) * speed);
      }
   }

   public void onTick() {
      this.distance = Math.sqrt((this.mc.field_1724.method_23317() - this.mc.field_1724.field_6014) * (this.mc.field_1724.method_23317() - this.mc.field_1724.field_6014) + (this.mc.field_1724.method_23321() - this.mc.field_1724.field_5969) * (this.mc.field_1724.method_23321() - this.mc.field_1724.field_5969));
   }
}
