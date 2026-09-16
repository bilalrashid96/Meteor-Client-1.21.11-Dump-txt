package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.FinishUsingItemEvent;
import meteordevelopment.meteorclient.events.entity.player.StoppedUsingItemEvent;
import meteordevelopment.meteorclient.events.game.ItemStackTooltipEvent;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1799.class})
public abstract class ItemStackMixin {
   @ModifyReturnValue(
      method = {"method_7950"},
      at = {@At("RETURN")}
   )
   private List<class_2561> onGetTooltip(List<class_2561> original) {
      if (Utils.canUpdate()) {
         ItemStackTooltipEvent event = (ItemStackTooltipEvent)MeteorClient.EVENT_BUS.post(new ItemStackTooltipEvent((class_1799)this, original));
         return event.list();
      } else {
         return original;
      }
   }

   @Inject(
      method = {"method_7910"},
      at = {@At("HEAD")}
   )
   private void onFinishUsing(class_1937 world, class_1309 user, CallbackInfoReturnable<class_1799> info) {
      if (user == MeteorClient.mc.field_1724) {
         MeteorClient.EVENT_BUS.post(FinishUsingItemEvent.get((class_1799)this));
      }

   }

   @Inject(
      method = {"method_7930"},
      at = {@At("HEAD")}
   )
   private void onStoppedUsing(class_1937 world, class_1309 user, int remainingUseTicks, CallbackInfo info) {
      if (user == MeteorClient.mc.field_1724) {
         MeteorClient.EVENT_BUS.post(StoppedUsingItemEvent.get((class_1799)this));
      }

   }
}
