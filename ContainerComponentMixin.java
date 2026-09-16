package meteordevelopment.meteorclient.mixin;

import java.util.function.Consumer;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_2371;
import net.minecraft.class_2561;
import net.minecraft.class_9288;
import net.minecraft.class_9473;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_9288.class})
public abstract class ContainerComponentMixin {
   @Shadow
   @Final
   private class_2371<class_1799> field_49338;

   @Inject(
      method = {"method_57409"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAppendTooltip(class_1792.class_9635 context, Consumer<class_2561> textConsumer, class_1836 type, class_9473 components, CallbackInfo ci) {
      if (Modules.get() != null) {
         BetterTooltips tooltips = (BetterTooltips)Modules.get().get(BetterTooltips.class);
         if (tooltips.isActive()) {
            if (tooltips.previewShulkers()) {
               ci.cancel();
            } else if (tooltips.shulkerCompactTooltip()) {
               ci.cancel();
               tooltips.applyCompactShulkerTooltip(this.field_49338, textConsumer);
            }
         }

      }
   }
}
