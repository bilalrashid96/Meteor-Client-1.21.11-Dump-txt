package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Chams;
import net.minecraft.class_10014;
import net.minecraft.class_3532;
import net.minecraft.class_9946;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_9946.class})
public abstract class EndCrystalEntityModelMixin {
   @ModifyExpressionValue(
      method = {"method_62083(Lnet/minecraft/class_10014;)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_892;method_23155(F)F"
)}
   )
   private float setAngles$bounce(float original, class_10014 state) {
      Chams module = (Chams)Modules.get().get(Chams.class);
      if (module.isActive() && (Boolean)module.crystals.get()) {
         float g = class_3532.method_15374((double)(state.field_53328 * 0.2F)) / 2.0F + 0.5F;
         g = (g * g + g) * 0.4F * ((Double)module.crystalsBounce.get()).floatValue();
         return g - 1.4F;
      } else {
         return original;
      }
   }

   @ModifyExpressionValue(
      method = {"method_62083(Lnet/minecraft/class_10014;)V"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_10014;field_53328:F",
   ordinal = 0
)}
   )
   private float modifySpeed(float original) {
      Chams module = (Chams)Modules.get().get(Chams.class);
      return module.isActive() && (Boolean)module.crystals.get() ? original * ((Double)module.crystalsRotationSpeed.get()).floatValue() : original;
   }
}
