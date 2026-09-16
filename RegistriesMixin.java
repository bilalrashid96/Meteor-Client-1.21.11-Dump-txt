package meteordevelopment.meteorclient.mixin;

import java.util.function.Supplier;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_7923.class})
public abstract class RegistriesMixin {
   @Redirect(
      method = {"method_47478(Lnet/minecraft/class_5321;Lnet/minecraft/class_2385;Lnet/minecraft/class_7923$class_6889;)Lnet/minecraft/class_2385;"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_2966;method_36235(Ljava/util/function/Supplier;)V"
)
   )
   private static void ignoreBootstrap(Supplier<String> callerGetter) {
   }
}
