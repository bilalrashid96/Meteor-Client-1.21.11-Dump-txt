package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Fullbright;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import net.minecraft.class_1309;
import net.minecraft.class_3695;
import net.minecraft.class_765;
import net.minecraft.class_9848;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_765.class})
public abstract class LightmapTextureManagerMixin {
   @Shadow
   @Final
   private GpuTexture field_57927;

   @Inject(
      method = {"method_3313"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3695;method_15396(Ljava/lang/String;)V",
   shift = Shift.AFTER
)},
      cancellable = true
   )
   private void update$skip(float tickProgress, CallbackInfo ci, @Local class_3695 profiler) {
      if (((Fullbright)Modules.get().get(Fullbright.class)).getGamma() || Modules.get().isActive(Xray.class)) {
         RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.field_57927, class_9848.method_61324(255, 255, 255, 255));
         profiler.method_15407();
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_42596"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getDarknessFactor(class_1309 entity, float factor, float tickProgress, CallbackInfoReturnable<Float> info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noDarkness()) {
         info.setReturnValue(0.0F);
      }

   }
}
