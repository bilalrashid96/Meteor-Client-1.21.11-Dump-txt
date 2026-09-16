package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.List;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import net.minecraft.class_10889;
import net.minecraft.class_1920;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_778;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_778.class})
public abstract class BlockModelRendererMixin {
   @Unique
   private final ThreadLocal<Integer> alphas = new ThreadLocal();

   @Inject(
      method = {"method_3361", "method_3373"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderSmooth(class_1920 world, List<class_10889> parts, class_2680 state, class_2338 pos, class_4587 matrices, class_4588 vertexConsumer, boolean cull, int overlay, CallbackInfo ci) {
      int alpha = Xray.getAlpha(state, pos);
      if (alpha == 0) {
         ci.cancel();
      } else {
         this.alphas.set(alpha);
      }

   }

   @ModifyArgs(
      method = {"method_23073"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_4588;method_22920(Lnet/minecraft/class_4587$class_4665;Lnet/minecraft/class_777;[FFFFF[II)V"
)
   )
   private void modifyXrayAlpha(Args args) {
      int alpha = (Integer)this.alphas.get();
      args.set(6, alpha == -1 ? (Float)args.get(6) : (float)alpha / 255.0F);
   }

   @ModifyReturnValue(
      method = {"method_68826"},
      at = {@At("RETURN")}
   )
   private static boolean modifyShouldDrawFace(boolean original, class_1920 world, class_2680 state, boolean cull, class_2350 side, class_2338 pos) {
      Xray xray = (Xray)Modules.get().get(Xray.class);
      return xray.isActive() ? xray.modifyDrawSide(state, world, pos.method_10093(side.method_10153()), side, original) : original;
   }
}
