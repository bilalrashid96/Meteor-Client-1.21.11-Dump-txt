package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_10034;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_4587;
import net.minecraft.class_572;
import net.minecraft.class_970;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_970.class})
public abstract class ArmorFeatureRendererMixin<S extends class_10034, M extends class_572<S>, A extends class_572<S>> {
   @Inject(
      method = {"method_17157(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10034;FF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRender(class_4587 matrixStack, class_11659 orderedRenderCommandQueue, int i, S bipedEntityRenderState, float f, float g, CallbackInfo ci) {
      if (bipedEntityRenderState instanceof class_10055 && ((NoRender)Modules.get().get(NoRender.class)).noArmor()) {
         ci.cancel();
      }

   }
}
