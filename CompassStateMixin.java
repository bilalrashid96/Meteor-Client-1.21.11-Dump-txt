package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import net.minecraft.class_10473;
import net.minecraft.class_11566;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_10473.class})
public abstract class CompassStateMixin {
   @ModifyExpressionValue(
      method = {"method_65649"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_11566;method_73188()F"
)}
   )
   private static float callLivingEntityGetYaw(float original) {
      return Modules.get().isActive(Freecam.class) ? MeteorClient.mc.field_1773.method_19418().method_19330() : original;
   }

   @ModifyReturnValue(
      method = {"method_65651(Lnet/minecraft/class_11566;Lnet/minecraft/class_2338;)D"},
      at = {@At("RETURN")}
   )
   private static double modifyGetAngleTo(double original, class_11566 from, class_2338 to) {
      if (Modules.get().isActive(Freecam.class)) {
         class_243 vec3d = class_243.method_24953(to);
         class_4184 camera = MeteorClient.mc.field_1773.method_19418();
         return Math.atan2(vec3d.method_10215() - camera.method_71156().field_1350, vec3d.method_10216() - camera.method_71156().field_1352) / (double)((float)Math.PI * 2F);
      } else {
         return original;
      }
   }
}
