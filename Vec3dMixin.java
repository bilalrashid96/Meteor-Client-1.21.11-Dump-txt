package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IVec3d;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({class_243.class})
public abstract class Vec3dMixin implements IVec3d {
   @Shadow
   @Final
   @Mutable
   public double field_1352;
   @Shadow
   @Final
   @Mutable
   public double field_1351;
   @Shadow
   @Final
   @Mutable
   public double field_1350;

   public class_243 meteor$set(double x, double y, double z) {
      this.field_1352 = x;
      this.field_1351 = y;
      this.field_1350 = z;
      return (class_243)this;
   }

   public class_243 meteor$setXZ(double x, double z) {
      this.field_1352 = x;
      this.field_1350 = z;
      return (class_243)this;
   }

   public class_243 meteor$setY(double y) {
      this.field_1351 = y;
      return (class_243)this;
   }
}
