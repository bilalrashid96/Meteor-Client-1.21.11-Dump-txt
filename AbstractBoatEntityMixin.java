package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.EntityControl;
import net.minecraft.class_10255;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_10255.class})
public abstract class AbstractBoatEntityMixin {
   @ModifyExpressionValue(
      method = {"method_64482"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_10255;field_54444:Z",
   opcode = 180
)}
   )
   private boolean modifyPressingLeft(boolean original) {
      return Modules.get().isActive(EntityControl.class) && (Boolean)((EntityControl)Modules.get().get(EntityControl.class)).lockYaw.get() ? false : original;
   }

   @ModifyExpressionValue(
      method = {"method_64482"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_10255;field_54428:Z",
   opcode = 180
)}
   )
   private boolean modifyPressingRight(boolean original) {
      return Modules.get().isActive(EntityControl.class) && (Boolean)((EntityControl)Modules.get().get(EntityControl.class)).lockYaw.get() ? false : original;
   }
}
