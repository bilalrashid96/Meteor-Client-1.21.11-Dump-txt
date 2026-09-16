package meteordevelopment.meteorclient.mixin;

import java.io.File;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.ChangePerspectiveEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.utils.misc.input.KeyBinds;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_5498;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_315.class})
public abstract class GameOptionsMixin {
   @Shadow
   @Final
   @Mutable
   public class_304[] field_1839;

   @Inject(
      method = {"<init>"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_315;field_1839:[Lnet/minecraft/class_304;",
   opcode = 181,
   shift = Shift.AFTER
)}
   )
   private void onInitAfterKeysAll(class_310 client, File optionsFile, CallbackInfo info) {
      this.field_1839 = KeyBinds.apply(this.field_1839);
   }

   @Inject(
      method = {"method_31043"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void setPerspective(class_5498 perspective, CallbackInfo info) {
      if (Modules.get() != null) {
         ChangePerspectiveEvent event = (ChangePerspectiveEvent)MeteorClient.EVENT_BUS.post(ChangePerspectiveEvent.get(perspective));
         if (event.isCancelled()) {
            info.cancel();
         }

         if (Modules.get().isActive(Freecam.class)) {
            info.cancel();
         }

      }
   }
}
