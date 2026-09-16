package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import net.minecraft.class_11515;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3610;
import net.minecraft.class_4696;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_4696.class})
public class BlockRenderLayersMixin {
   @Inject(
      method = {"method_23679"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onGetBlockLayer(class_2680 state, CallbackInfoReturnable<class_11515> cir) {
      if (Modules.get() != null) {
         int alpha = Xray.getAlpha(state, (class_2338)null);
         if (alpha > 0 && alpha < 255) {
            cir.setReturnValue(class_11515.field_60926);
         }

      }
   }

   @Inject(
      method = {"method_23680"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onGetFluidLayer(class_3610 state, CallbackInfoReturnable<class_11515> cir) {
      if (Modules.get() != null) {
         int alpha = Xray.getAlpha(state.method_15759(), (class_2338)null);
         if (alpha > 0 && alpha < 255) {
            cir.setReturnValue(class_11515.field_60926);
         } else {
            Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
            int a = (ambience.lavaColor.get()).a;
            if (ambience.isActive() && (Boolean)ambience.customLavaColor.get() && a > 0 && a < 255) {
               cir.setReturnValue(class_11515.field_60926);
            }
         }

      }
   }
}
