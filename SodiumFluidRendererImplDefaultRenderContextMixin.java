package meteordevelopment.meteorclient.mixin.sodium;

import java.util.Arrays;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.class_2338;
import net.minecraft.class_3486;
import net.minecraft.class_3610;
import net.minecraft.class_3611;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   targets = {"net/caffeinemc/mods/sodium/fabric/render/FluidRendererImpl$DefaultRenderContext"},
   remap = false
)
public abstract class SodiumFluidRendererImplDefaultRenderContextMixin {
   @Unique
   private Ambience ambience;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void onInit(CallbackInfo info) {
      this.ambience = (Ambience)Modules.get().get(Ambience.class);
   }

   @Inject(
      method = {"getColorProvider"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetColorProvider(class_3611 fluid, CallbackInfoReturnable<ColorProvider<class_3610>> info) {
      if (this.ambience.isActive() && (Boolean)this.ambience.customLavaColor.get() && fluid.method_15785().method_15767(class_3486.field_15518)) {
         info.setReturnValue(this::lavaColorProvider);
      }

   }

   @Unique
   private void lavaColorProvider(LevelSlice slice, class_2338 pos, class_2338.class_2339 scratchPos, class_3610 state, ModelQuadView quad, int[] output, boolean smooth) {
      Color c = this.ambience.lavaColor.get();
      Arrays.fill(output, ColorABGR.pack(c.r, c.g, c.b, c.a));
   }
}
