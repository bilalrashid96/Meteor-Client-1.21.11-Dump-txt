package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_6368;
import net.minecraft.class_639;
import net.minecraft.class_6394;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@NullMarked
@Mixin({class_6394.class})
public interface BlockListCheckerMixin {
   @Inject(
      method = {"method_37097"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onCreate(CallbackInfoReturnable<class_6394> cir) {
      cir.setReturnValue(new class_6394() {
         public boolean method_37098(class_6368 address) {
            return true;
         }

         public boolean method_37099(class_639 address) {
            return true;
         }
      });
   }
}
