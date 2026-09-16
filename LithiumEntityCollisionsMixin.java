package meteordevelopment.meteorclient.mixin.lithium;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Collisions;
import net.caffeinemc.mods.lithium.common.entity.LithiumEntityCollisions;
import net.minecraft.class_238;
import net.minecraft.class_2784;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LithiumEntityCollisions.class})
public abstract class LithiumEntityCollisionsMixin {
   @Inject(
      method = {"isWithinWorldBorder"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onIsWithinWorldBorder(class_2784 border, class_238 box, CallbackInfoReturnable<Boolean> cir) {
      if (((Collisions)Modules.get().get(Collisions.class)).ignoreBorder()) {
         cir.setReturnValue(true);
      }

   }
}
