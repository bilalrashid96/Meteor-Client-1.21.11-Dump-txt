package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.BetterChat;
import net.minecraft.class_3544;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({class_3544.class})
public abstract class StringHelperMixin {
   @ModifyArg(
      method = {"method_43681"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3544;method_34963(Ljava/lang/String;IZ)Ljava/lang/String;"
),
      index = 1
   )
   private static int injected(int maxLength) {
      return ((BetterChat)Modules.get().get(BetterChat.class)).isInfiniteChatBox() ? Integer.MAX_VALUE : maxLength;
   }
}
