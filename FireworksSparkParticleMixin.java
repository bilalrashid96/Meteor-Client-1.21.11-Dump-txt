package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.class_677;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_677.class_681.class})
public abstract class FireworksSparkParticleMixin {
   @Inject(
      method = {"method_3030"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_677$class_680;method_3027(Z)V"
)},
      cancellable = true
   )
   private void onAddExplosion(double x, double y, double z, double velocityX, double velocityY, double velocityZ, IntList colors, IntList targetColors, boolean trail, boolean flicker, CallbackInfo info, @Local class_677.class_680 explosion) {
      if (explosion == null) {
         info.cancel();
      }

   }
}
