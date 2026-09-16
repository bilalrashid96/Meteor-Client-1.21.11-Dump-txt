package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import net.minecraft.class_10712;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_10712.class})
public abstract class TooltipDisplayComponentMixin {
   @ModifyExpressionValue(
      method = {"method_67214"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_10712;comp_3600:Z"
)}
   )
   private boolean modifyHideTooltip(boolean original) {
      return original && !(Boolean)((BetterTooltips)Modules.get().get(BetterTooltips.class)).tooltip.get();
   }

   @ModifyExpressionValue(
      method = {"method_67214"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/util/SequencedSet;contains(Ljava/lang/Object;)Z"
)}
   )
   private boolean modifyHiddenComponents(boolean original) {
      return original && !(Boolean)((BetterTooltips)Modules.get().get(BetterTooltips.class)).additional.get();
   }
}
