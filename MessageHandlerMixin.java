package meteordevelopment.meteorclient.mixin;

import com.mojang.authlib.GameProfile;
import java.time.Instant;
import meteordevelopment.meteorclient.mixininterface.IMessageHandler;
import net.minecraft.class_2556;
import net.minecraft.class_2561;
import net.minecraft.class_7471;
import net.minecraft.class_7594;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_7594.class})
public abstract class MessageHandlerMixin implements IMessageHandler {
   @Unique
   private GameProfile sender;

   @Inject(
      method = {"method_44943"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_338;method_44811(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V",
   shift = Shift.BEFORE
)}
   )
   private void onProcessChatMessageInternal_beforeAddMessage(class_2556.class_7602 params, class_7471 message, class_2561 decorated, GameProfile sender, boolean onlyShowSecureChat, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> info) {
      this.sender = sender;
   }

   @Inject(
      method = {"method_44943"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_338;method_44811(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V",
   shift = Shift.AFTER
)}
   )
   private void onProcessChatMessageInternal_afterAddMessage(class_2556.class_7602 params, class_7471 message, class_2561 decorated, GameProfile sender, boolean onlyShowSecureChat, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> info) {
      this.sender = null;
   }

   public GameProfile meteor$getSender() {
      return this.sender;
   }
}
