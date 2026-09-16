package meteordevelopment.meteorclient.mixin.sodium;

import meteordevelopment.meteorclient.systems.modules.render.Xray;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.fabric.render.FluidRendererImpl;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3610;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {FluidRendererImpl.class},
   remap = false
)
public abstract class SodiumFluidRendererImplMixin {
   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRender(LevelSlice level, class_2680 blockState, class_3610 fluidState, class_2338 blockPos, class_2338 offset, TranslucentGeometryCollector collector, ChunkBuildBuffers buffers, CallbackInfo info) {
      int alpha = Xray.getAlpha(fluidState.method_15759(), blockPos);
      if (alpha == 0) {
         info.cancel();
      }

   }
}
