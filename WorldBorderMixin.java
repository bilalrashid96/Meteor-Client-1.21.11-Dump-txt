package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Collisions;
import net.minecraft.class_2784;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_2784.class})
public abstract class WorldBorderMixin {
   @Inject(
      method = {"method_39459"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void canCollide(CallbackInfoReturnable<Boolean> info) {
      if (((Collisions)Modules.get().get(Collisions.class)).ignoreBorder()) {
         info.setReturnValue(false);
      }

   }

   @Inject(
      method = {"method_11952(Lnet/minecraft/class_2338;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void contains(CallbackInfoReturnable<Boolean> info) {
      if (((Collisions)Modules.get().get(Collisions.class)).ignoreBorder()) {
         info.setReturnValue(true);
      }

   }
}
