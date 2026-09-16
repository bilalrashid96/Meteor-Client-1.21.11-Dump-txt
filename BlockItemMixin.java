package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.PlaceBlockEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.NoGhostBlocks;
import net.minecraft.class_1747;
import net.minecraft.class_1750;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1747.class})
public abstract class BlockItemMixin {
   @Shadow
   protected abstract class_2680 method_7707(class_1750 var1);

   @Inject(
      method = {"method_7708(Lnet/minecraft/class_1750;Lnet/minecraft/class_2680;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onPlace(class_1750 context, class_2680 state, CallbackInfoReturnable<Boolean> info) {
      if (context.method_8045().method_8608()) {
         if (((PlaceBlockEvent)MeteorClient.EVENT_BUS.post(PlaceBlockEvent.get(context.method_8037(), state.method_26204()))).isCancelled()) {
            info.setReturnValue(true);
         }

      }
   }

   @ModifyVariable(
      method = {"method_7712(Lnet/minecraft/class_1750;)Lnet/minecraft/class_1269;"},
      ordinal = 1,
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_2680;method_27852(Lnet/minecraft/class_2248;)Z"
)
   )
   private class_2680 modifyState(class_2680 state, class_1750 context) {
      NoGhostBlocks noGhostBlocks = (NoGhostBlocks)Modules.get().get(NoGhostBlocks.class);
      return noGhostBlocks.isActive() && (Boolean)noGhostBlocks.placing.get() ? this.method_7707(context) : state;
   }
}
