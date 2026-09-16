package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AntiPacketKick;
import net.minecraft.class_2505;
import net.minecraft.class_2540;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({class_2540.class})
public abstract class PacketByteBufMixin {
   @ModifyArg(
      method = {"method_56345(Lio/netty/buffer/ByteBuf;)Lnet/minecraft/class_2487;"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_2540;method_56340(Lio/netty/buffer/ByteBuf;Lnet/minecraft/class_2505;)Lnet/minecraft/class_2520;"
)
   )
   private static class_2505 xlPackets(class_2505 sizeTracker) {
      return Modules.get().isActive(AntiPacketKick.class) ? class_2505.method_53898() : sizeTracker;
   }
}
