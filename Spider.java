package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_243;

public class Spider extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> speed;

   public Spider() {
      super(Categories.Movement, "spider", "Allows you to climb walls like a spider.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.speed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("climb-speed")).description("The speed you go up blocks.")).defaultValue(0.2).min((double)0.0F).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.field_1724.field_5976) {
         class_243 velocity = this.mc.field_1724.method_18798();
         if (!(velocity.field_1351 >= 0.2)) {
            this.mc.field_1724.method_18800(velocity.field_1352, (Double)this.speed.get(), velocity.field_1350);
         }
      }
   }
}
