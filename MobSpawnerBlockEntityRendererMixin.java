package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_11973;
import net.minecraft.class_2636;
import net.minecraft.class_827;
import net.minecraft.class_839;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_839.class})
public abstract class MobSpawnerBlockEntityRendererMixin implements class_827<class_2636, class_11973> {
   @Inject(
      method = {"method_55253"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onRenderDisplayEntity(CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noMobInSpawner()) {
         ci.cancel();
      }

   }
}
