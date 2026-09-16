package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.TridentBoost;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1835;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_1835.class})
public abstract class TridentItemMixin {
   @Inject(
      method = {"method_7840"},
      at = {@At("HEAD")}
   )
   private void onStoppedUsingHead(class_1799 stack, class_1937 world, class_1309 user, int remainingUseTicks, CallbackInfoReturnable<Boolean> info) {
      if (user == MeteorClient.mc.field_1724) {
         Utils.isReleasingTrident = true;
      }

   }

   @Inject(
      method = {"method_7840"},
      at = {@At("TAIL")}
   )
   private void onStoppedUsingTail(class_1799 stack, class_1937 world, class_1309 user, int remainingUseTicks, CallbackInfoReturnable<Boolean> info) {
      if (user == MeteorClient.mc.field_1724) {
         Utils.isReleasingTrident = false;
      }

   }

   @ModifyArgs(
      method = {"method_7840"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1657;method_5762(DDD)V"
)
   )
   private void modifyVelocity(Args args) {
      TridentBoost tridentBoost = (TridentBoost)Modules.get().get(TridentBoost.class);
      args.set(0, (Double)args.get(0) * tridentBoost.getMultiplier());
      args.set(1, (Double)args.get(1) * tridentBoost.getMultiplier());
      args.set(2, (Double)args.get(2) * tridentBoost.getMultiplier());
   }

   @ModifyExpressionValue(
      method = {"method_7836"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1657;method_5721()Z"
)}
   )
   private boolean isInWaterUse(boolean original) {
      TridentBoost tridentBoost = (TridentBoost)Modules.get().get(TridentBoost.class);
      return tridentBoost.allowOutOfWater() || original;
   }

   @ModifyExpressionValue(
      method = {"method_7840"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1657;method_5721()Z"
)}
   )
   private boolean isInWaterPostUse(boolean original) {
      TridentBoost tridentBoost = (TridentBoost)Modules.get().get(TridentBoost.class);
      return tridentBoost.allowOutOfWater() || original;
   }
}
