package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_327;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_327.class})
public abstract class TextRendererMixin {
   @ModifyExpressionValue(
      method = {"method_72731"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_2583;method_10987()Z"
)}
   )
   private boolean onRenderObfuscatedStyle(boolean original) {
      if (Modules.get() != null && Modules.get().get(NoRender.class) != null) {
         return !((NoRender)Modules.get().get(NoRender.class)).noObfuscation() && original;
      } else {
         return original;
      }
   }
}
