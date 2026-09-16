package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2490;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_2490.class})
public abstract class SlimeBlockMixin {
   @Inject(
      method = {"method_9591"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSteppedOn(class_1937 world, class_2338 pos, class_2680 state, class_1297 entity, CallbackInfo info) {
      if (((NoSlow)Modules.get().get(NoSlow.class)).slimeBlock() && entity == MeteorClient.mc.field_1724) {
         info.cancel();
      }

   }
}
