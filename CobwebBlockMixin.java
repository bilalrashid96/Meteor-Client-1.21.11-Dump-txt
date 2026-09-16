package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import net.minecraft.class_1297;
import net.minecraft.class_2560;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_2560.class})
public abstract class CobwebBlockMixin {
   @Inject(
      method = {"method_9548", "onEntityCollision(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityCollisionHandler;)V", "method_9548(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_1297;Lnet/minecraft/class_10774;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   @Dynamic("Explicit 1.21.9 Support")
   private void onEntityCollision(CallbackInfo ci, @Local(argsOnly = true) class_1297 entity) {
      if (entity == MeteorClient.mc.field_1724 && ((NoSlow)Modules.get().get(NoSlow.class)).cobweb()) {
         ci.cancel();
      }

   }
}
