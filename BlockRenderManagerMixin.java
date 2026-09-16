package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BreakIndicators;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_1920;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_776;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_776.class})
public abstract class BlockRenderManagerMixin {
   @Inject(
      method = {"method_23071"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderDamage(class_2680 state, class_2338 pos, class_1920 world, class_4587 matrix, class_4588 vertexConsumer, CallbackInfo info) {
      if (Modules.get().isActive(BreakIndicators.class) || ((NoRender)Modules.get().get(NoRender.class)).noBlockBreakOverlay()) {
         info.cancel();
      }

   }
}
