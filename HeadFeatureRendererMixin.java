package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_10042;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_3882;
import net.minecraft.class_4587;
import net.minecraft.class_583;
import net.minecraft.class_976;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_976.class})
public abstract class HeadFeatureRendererMixin<S extends class_10042, M extends class_583<S> & class_3882> {
   @Inject(
      method = {"method_17159(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10042;FF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRender(class_4587 matrixStack, class_11659 orderedRenderCommandQueue, int i, S livingEntityRenderState, float f, float g, CallbackInfo ci) {
      if (livingEntityRenderState instanceof class_10055 && ((NoRender)Modules.get().get(NoRender.class)).noArmor()) {
         ci.cancel();
      }

   }
}
