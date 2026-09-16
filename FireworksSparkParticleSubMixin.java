package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_11944;
import net.minecraft.class_4184;
import net.minecraft.class_677;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_677.class_680.class, class_677.class_678.class})
public abstract class FireworksSparkParticleSubMixin {
   @Inject(
      method = {"method_3074"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void buildExplosionGeometry(class_11944 arg, class_4184 camera, float f, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noFireworkExplosions()) {
         ci.cancel();
      }

   }
}
