package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AntiPacketKick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
   targets = {"net/minecraft/class_8039$1$1"}
)
public class PacketBundlingMixin {
   @ModifyExpressionValue(
      method = {"method_48328"},
      at = {@At(
   value = "CONSTANT",
   args = {"intValue=4096"}
)}
   )
   private int add(int value) {
      return ((AntiPacketKick)Modules.get().get(AntiPacketKick.class)).isActive() ? Integer.MAX_VALUE : value;
   }
}
