package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.List;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_10090;
import net.minecraft.class_11659;
import net.minecraft.class_20;
import net.minecraft.class_330;
import net.minecraft.class_4587;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_330.class})
public abstract class MapRendererMixin {
   @ModifyExpressionValue(
      method = {"method_1773"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_10090;field_53639:Ljava/util/List;"
)}
   )
   private List<class_20> getIconsProxy(List<class_20> original) {
      return ((NoRender)Modules.get().get(NoRender.class)).noMapMarkers() ? List.of() : original;
   }

   @Inject(
      method = {"method_1773"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onDraw(class_10090 state, class_4587 matrices, class_11659 queue, boolean skipRenderingDecorations, int light, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noMapContents()) {
         ci.cancel();
      }

   }
}
