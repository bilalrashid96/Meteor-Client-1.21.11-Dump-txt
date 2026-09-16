package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.events.meteor.MouseScrollEvent;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import net.minecraft.class_1041;
import net.minecraft.class_11909;
import net.minecraft.class_11910;
import net.minecraft.class_310;
import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_312.class})
public abstract class MouseMixin {
   @Shadow
   @Final
   private class_310 field_1779;

   @Shadow
   public abstract double method_68879(class_1041 var1);

   @Shadow
   public abstract double method_68883(class_1041 var1);

   @Inject(
      method = {"method_1601"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMouseButton(long window, class_11910 mouseInput, int action, CallbackInfo ci) {
      Input.setButtonState(mouseInput.comp_4801(), action != 0);
      class_11909 click = new class_11909(this.method_68879(this.field_1779.method_22683()), this.method_68883(this.field_1779.method_22683()), mouseInput);
      if (((MouseClickEvent)MeteorClient.EVENT_BUS.post(MouseClickEvent.get(click, KeyAction.get(action)))).isCancelled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_1598"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info) {
      if (((MouseScrollEvent)MeteorClient.EVENT_BUS.post(MouseScrollEvent.get(vertical))).isCancelled()) {
         info.cancel();
      }

   }
}
