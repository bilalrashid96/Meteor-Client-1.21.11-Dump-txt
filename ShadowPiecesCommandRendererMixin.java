package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_11690;
import net.minecraft.class_11788;
import net.minecraft.class_4597;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_11690.class})
public abstract class ShadowPiecesCommandRendererMixin {
   @Inject(
      method = {"method_73015"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void meteor$onRender(class_11788 queue, class_4597.class_4598 vertexConsumers, CallbackInfo info) {
      if (queue.method_73505().isEmpty()) {
         info.cancel();
      }

   }
}
