package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.RenderBlockEntityEvent;
import net.minecraft.class_11659;
import net.minecraft.class_11954;
import net.minecraft.class_12075;
import net.minecraft.class_4587;
import net.minecraft.class_824;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_824.class})
public abstract class BlockEntityRenderManagerMixin {
   @Inject(
      method = {"method_3555"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private <S extends class_11954> void onRenderEntity(S renderState, class_4587 matrices, class_11659 queue, class_12075 arg, CallbackInfo ci) {
      RenderBlockEntityEvent event = (RenderBlockEntityEvent)MeteorClient.EVENT_BUS.post(RenderBlockEntityEvent.get(renderState));
      if (event.isCancelled()) {
         ci.cancel();
      }

   }
}
