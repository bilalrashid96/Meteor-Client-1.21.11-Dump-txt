package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Rotation extends Module {
   private final SettingGroup sgYaw;
   private final SettingGroup sgPitch;
   private final Setting<LockMode> yawLockMode;
   private final Setting<Double> yawAngle;
   private final Setting<LockMode> pitchLockMode;
   private final Setting<Double> pitchAngle;

   public Rotation() {
      super(Categories.Player, "rotation", "Changes/locks your yaw and pitch.");
      this.sgYaw = this.settings.createGroup("Yaw");
      this.sgPitch = this.settings.createGroup("Pitch");
      this.yawLockMode = this.sgYaw.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("yaw-lock-mode")).description("The way in which your yaw is locked.")).defaultValue(Rotation.LockMode.Simple)).build());
      this.yawAngle = this.sgYaw.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("yaw-angle")).description("Yaw angle in degrees.")).defaultValue((double)0.0F).sliderMax((double)360.0F).max((double)360.0F).visible(() -> this.yawLockMode.get() == Rotation.LockMode.Simple)).build());
      this.pitchLockMode = this.sgPitch.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("pitch-lock-mode")).description("The way in which your pitch is locked.")).defaultValue(Rotation.LockMode.Simple)).build());
      this.pitchAngle = this.sgPitch.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("pitch-angle")).description("Pitch angle in degrees.")).defaultValue((double)0.0F).range((double)-90.0F, (double)90.0F).sliderRange((double)-90.0F, (double)90.0F).visible(() -> this.pitchLockMode.get() == Rotation.LockMode.Simple)).build());
   }

   public void onActivate() {
      this.onTick((TickEvent.Post)null);
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      switch (((LockMode)this.yawLockMode.get()).ordinal()) {
         case 0 -> this.setYawAngle(this.getSmartYawDirection());
         case 1 -> this.setYawAngle(((Double)this.yawAngle.get()).floatValue());
      }

      switch (((LockMode)this.pitchLockMode.get()).ordinal()) {
         case 0 -> this.mc.field_1724.method_36457(this.getSmartPitchDirection());
         case 1 -> this.mc.field_1724.method_36457(((Double)this.pitchAngle.get()).floatValue());
      }

   }

   private float getSmartYawDirection() {
      return (float)Math.round((this.mc.field_1724.method_36454() + 1.0F) / 45.0F) * 45.0F;
   }

   private float getSmartPitchDirection() {
      return (float)Math.round((this.mc.field_1724.method_36455() + 1.0F) / 30.0F) * 30.0F;
   }

   private void setYawAngle(float yawAngle) {
      this.mc.field_1724.method_36456(yawAngle);
      this.mc.field_1724.field_6241 = yawAngle;
      this.mc.field_1724.field_6283 = yawAngle;
   }

   public static enum LockMode {
      Smart,
      Simple,
      None;

      // $FF: synthetic method
      private static LockMode[] $values() {
         return new LockMode[]{Smart, Simple, None};
      }
   }
}
