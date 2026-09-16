package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.utils.tooltip.MeteorTooltipData;
import net.minecraft.class_5632;
import net.minecraft.class_5684;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_5684.class})
public interface TooltipComponentMixin {
   @Inject(
      method = {"method_32663(Lnet/minecraft/class_5632;)Lnet/minecraft/class_5684;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void shortcutMeteorTooltipData(class_5632 tooltipData, CallbackInfoReturnable<class_5684> cir) {
      if (tooltipData instanceof MeteorTooltipData) {
         cir.setReturnValue((Object)null);
      }

   }
}
