package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.NameProtect;
import net.minecraft.class_5223;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({class_5223.class})
public abstract class TextVisitFactoryMixin {
   @ModifyArg(
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_5223;method_27473(Ljava/lang/String;ILnet/minecraft/class_2583;Lnet/minecraft/class_2583;Lnet/minecraft/class_5224;)Z",
   ordinal = 0
),
      method = {"method_27472(Ljava/lang/String;ILnet/minecraft/class_2583;Lnet/minecraft/class_5224;)Z"},
      index = 0
   )
   private static String adjustText(String text) {
      return Modules.get() != null ? ((NameProtect)Modules.get().get(NameProtect.class)).replaceName(text) : text;
   }
}
