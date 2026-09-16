package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_2244;
import net.minecraft.class_2338;

public class ReverseStep extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> fallSpeed;
   private final Setting<Double> fallDistance;
   private final Setting<Boolean> vehicles;

   public ReverseStep() {
      super(Categories.Movement, "reverse-step", "Allows you to fall down blocks at a greater speed.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.fallSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fall-speed")).description("How fast to fall in blocks per second.")).defaultValue((double)3.0F).min((double)0.0F).build());
      this.fallDistance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fall-distance")).description("The maximum fall distance this setting will activate at.")).defaultValue((double)3.0F).min((double)0.0F).build());
      this.vehicles = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("vehicles")).description("Whether or not reverse step should affect vehicles.")).defaultValue(false)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      class_1297 vehicle = this.mc.field_1724.method_5854();
      if (vehicle != null && (Boolean)this.vehicles.get()) {
         if (this.canSnap(vehicle)) {
            ((IVec3d)vehicle.method_18798()).meteor$setY(-(Double)this.fallSpeed.get());
         }
      } else {
         if (this.mc.field_1724.method_21754() || this.mc.field_1724.field_6250 == 0.0F && this.mc.field_1724.field_6212 == 0.0F) {
            return;
         }

         if (!this.isOnBed() && this.canSnap(this.mc.field_1724)) {
            ((IVec3d)this.mc.field_1724.method_18798()).meteor$setY(-(Double)this.fallSpeed.get());
         }
      }

   }

   private boolean canSnap(class_1297 entity) {
      if (entity.method_24828() && !entity.method_5869() && !entity.method_5771() && !this.mc.field_1690.field_1903.method_1434() && !entity.field_5960) {
         return !this.mc.field_1687.method_18026(entity.method_5829().method_989((double)0.0F, (double)((float)(-((Double)this.fallDistance.get() + 0.01))), (double)0.0F));
      } else {
         return false;
      }
   }

   private boolean isOnBed() {
      class_2338.class_2339 blockPos = this.mc.field_1724.method_24515().method_25503();
      if (this.check(blockPos, 0, 0)) {
         return true;
      } else {
         double xa = this.mc.field_1724.method_23317() - (double)blockPos.method_10263();
         double za = this.mc.field_1724.method_23321() - (double)blockPos.method_10260();
         if (xa >= (double)0.0F && xa <= 0.3 && this.check(blockPos, -1, 0)) {
            return true;
         } else if (xa >= 0.7 && this.check(blockPos, 1, 0)) {
            return true;
         } else if (za >= (double)0.0F && za <= 0.3 && this.check(blockPos, 0, -1)) {
            return true;
         } else if (za >= 0.7 && this.check(blockPos, 0, 1)) {
            return true;
         } else if (xa >= (double)0.0F && xa <= 0.3 && za >= (double)0.0F && za <= 0.3 && this.check(blockPos, -1, -1)) {
            return true;
         } else if (xa >= (double)0.0F && xa <= 0.3 && za >= 0.7 && this.check(blockPos, -1, 1)) {
            return true;
         } else if (xa >= 0.7 && za >= (double)0.0F && za <= 0.3 && this.check(blockPos, 1, -1)) {
            return true;
         } else {
            return xa >= 0.7 && za >= 0.7 && this.check(blockPos, 1, 1);
         }
      }
   }

   private boolean check(class_2338.class_2339 blockPos, int x, int z) {
      blockPos.method_10100(x, 0, z);
      boolean is = this.mc.field_1687.method_8320(blockPos).method_26204() instanceof class_2244;
      blockPos.method_10100(-x, 0, -z);
      return is;
   }
}
