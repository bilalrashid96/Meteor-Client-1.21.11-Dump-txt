package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.misc.FakeClientPlayer;
import net.minecraft.class_640;
import net.minecraft.class_742;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_742.class})
public abstract class AbstractClientPlayerEntityMixin {
   @Inject(
      method = {"method_3123"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetPlayerListEntry(CallbackInfoReturnable<class_640> info) {
      if (MeteorClient.mc.method_1562() == null) {
         info.setReturnValue(FakeClientPlayer.getPlayerListEntry());
      }

   }
}
