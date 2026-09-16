package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.ServerSpoof;
import net.minecraft.class_1066;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1066.class})
public abstract class ServerResourcePackLoaderMixin {
   @Inject(
      method = {"method_55536"},
      at = {@At("TAIL")}
   )
   private void removeInactivePacksTail(CallbackInfo ci) {
      ((ServerSpoof)Modules.get().get(ServerSpoof.class)).silentAcceptResourcePack = false;
   }
}
