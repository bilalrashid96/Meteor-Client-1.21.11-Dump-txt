package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import net.minecraft.class_10151;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_10151.class})
public abstract class ShaderLoaderMixin {
   @Inject(
      method = {"method_62945(Lnet/minecraft/class_10151$class_10153;Lnet/minecraft/class_3300;Lnet/minecraft/class_3695;)V"},
      at = {@At("TAIL")}
   )
   private void meteor$reloadPipelines(CallbackInfo info) {
      MeteorRenderPipelines.precompile();
   }
}
