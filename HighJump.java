package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.entity.player.JumpVelocityMultiplierEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class HighJump extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> multiplier;

   public HighJump() {
      super(Categories.Movement, "high-jump", "Makes you jump higher than normal.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.multiplier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("jump-multiplier")).description("Jump height multiplier.")).defaultValue((double)1.0F).min((double)0.0F).build());
   }

   @EventHandler
   private void onJumpVelocityMultiplier(JumpVelocityMultiplierEvent event) {
      event.multiplier = (float)((double)event.multiplier * (Double)this.multiplier.get());
   }
}
