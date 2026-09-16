package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_10529;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_10529.class})
public abstract class AbstractSignBlockEntityRendererMixin {
   @ModifyExpressionValue(
      method = {"method_65828"},
      at = {@At(
   value = "CONSTANT",
   args = {"intValue=4", "ordinal=1"}
)}
   )
   private int loopTextLengthProxy(int i) {
      return ((NoRender)Modules.get().get(NoRender.class)).noSignText() ? 0 : i;
   }
}
