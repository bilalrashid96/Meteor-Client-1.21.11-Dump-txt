package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.ApplyTransformationEvent;
import net.minecraft.class_4587;
import net.minecraft.class_804;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_804.class})
public abstract class TransformationMixin {
   @Inject(
      method = {"method_23075"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onApply(boolean leftHanded, class_4587.class_4665 entry, CallbackInfo info) {
      ApplyTransformationEvent event = (ApplyTransformationEvent)MeteorClient.EVENT_BUS.post(ApplyTransformationEvent.get((class_804)this, leftHanded));
      if (event.isCancelled()) {
         info.cancel();
      }

   }
}
