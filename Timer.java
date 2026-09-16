package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Timer extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> multiplier;
   public static final double OFF = (double)1.0F;
   private double override;

   public Timer() {
      super(Categories.World, "timer", "Changes the speed of everything in your game.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.multiplier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("multiplier")).description("The timer multiplier amount.")).defaultValue((double)1.0F).min(0.1).sliderMin(0.1).build());
      this.override = (double)1.0F;
   }

   public double getMultiplier() {
      return this.override != (double)1.0F ? this.override : (this.isActive() ? (Double)this.multiplier.get() : (double)1.0F);
   }

   public void setOverride(double override) {
      this.override = override;
   }
}
