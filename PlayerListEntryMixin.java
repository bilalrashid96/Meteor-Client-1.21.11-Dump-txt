package meteordevelopment.meteorclient.mixin;

import com.mojang.authlib.GameProfile;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.NameProtect;
import net.minecraft.class_1068;
import net.minecraft.class_310;
import net.minecraft.class_640;
import net.minecraft.class_8685;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_640.class})
public abstract class PlayerListEntryMixin {
   @Shadow
   public abstract GameProfile method_2966();

   @Inject(
      method = {"method_52810"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetTexture(CallbackInfoReturnable<class_8685> info) {
      if (this.method_2966().name().equals(class_310.method_1551().method_1548().method_1676()) && ((NameProtect)Modules.get().get(NameProtect.class)).skinProtect()) {
         info.setReturnValue(class_1068.method_52854(this.method_2966()));
      }

   }
}
