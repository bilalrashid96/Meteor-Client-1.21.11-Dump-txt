package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.ChunkOcclusionEvent;
import net.minecraft.class_2338;
import net.minecraft.class_852;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_852.class})
public abstract class ChunkOcclusionDataBuilderMixin {
   @Inject(
      method = {"method_3682"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMarkClosed(class_2338 pos, CallbackInfo info) {
      ChunkOcclusionEvent event = (ChunkOcclusionEvent)MeteorClient.EVENT_BUS.post(ChunkOcclusionEvent.get());
      if (event.isCancelled()) {
         info.cancel();
      }

   }
}
