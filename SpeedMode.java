package meteordevelopment.meteorclient.systems.modules.movement.speed;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_310;

public class SpeedMode {
   protected final class_310 mc = class_310.method_1551();
   protected final Speed settings = (Speed)Modules.get().get(Speed.class);
   private final SpeedModes type;
   protected int stage;
   protected double distance;
   protected double speed;

   public SpeedMode(SpeedModes type) {
      this.type = type;
      this.reset();
   }

   public void onTick() {
   }

   public void onMove(PlayerMoveEvent event) {
   }

   public void onRubberband() {
      this.reset();
   }

   public void onActivate() {
   }

   public void onDeactivate() {
   }

   protected double getDefaultSpeed() {
      double defaultSpeed = 0.2873;
      if (this.mc.field_1724.method_6059(class_1294.field_5904)) {
         int amplifier = this.mc.field_1724.method_6112(class_1294.field_5904).method_5578();
         defaultSpeed *= (double)1.0F + 0.2 * (double)(amplifier + 1);
      }

      if (this.mc.field_1724.method_6059(class_1294.field_5909)) {
         int amplifier = this.mc.field_1724.method_6112(class_1294.field_5909).method_5578();
         defaultSpeed /= (double)1.0F + 0.2 * (double)(amplifier + 1);
      }

      return defaultSpeed;
   }

   protected void reset() {
      this.stage = 0;
      this.distance = (double)0.0F;
      this.speed = 0.2873;
   }

   protected double getHop(double height) {
      class_1293 jumpBoost = this.mc.field_1724.method_6059(class_1294.field_5913) ? this.mc.field_1724.method_6112(class_1294.field_5913) : null;
      if (jumpBoost != null) {
         height += (double)((float)(jumpBoost.method_5578() + 1) * 0.1F);
      }

      return height;
   }

   public String getHudString() {
      return this.type.name();
   }
}
