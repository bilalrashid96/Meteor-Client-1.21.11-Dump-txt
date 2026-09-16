package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.ElytraBoost;
import net.minecraft.class_1671;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1671.class})
public abstract class FireworkRocketEntityMixin {
   @Shadow
   private int field_7613;
   @Shadow
   private int field_7612;

   @Inject(
      method = {"method_5773"},
      at = {@At("TAIL")}
   )
   private void onTick(CallbackInfo info) {
      class_1671 firework = (class_1671)this;
      if (((ElytraBoost)Modules.get().get(ElytraBoost.class)).isFirework(firework) && this.field_7613 > this.field_7612) {
         firework.method_31472();
      }

   }

   @Inject(
      method = {"method_7454"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onEntityHit(class_3966 entityHitResult, CallbackInfo info) {
      class_1671 firework = (class_1671)this;
      if (((ElytraBoost)Modules.get().get(ElytraBoost.class)).isFirework(firework)) {
         firework.method_31472();
         info.cancel();
      }

   }

   @Inject(
      method = {"method_24920"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onBlockHit(class_3965 blockHitResult, CallbackInfo info) {
      class_1671 firework = (class_1671)this;
      if (((ElytraBoost)Modules.get().get(ElytraBoost.class)).isFirework(firework)) {
         firework.method_31472();
         info.cancel();
      }

   }
}
