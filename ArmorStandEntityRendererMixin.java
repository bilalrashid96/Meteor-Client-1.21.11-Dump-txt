package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import net.minecraft.class_1299;
import net.minecraft.class_877;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_877.class})
public class ArmorStandEntityRendererMixin {
   @Unique
   private static ESP esp;

   @ModifyExpressionValue(
      method = {"method_24301(Lnet/minecraft/class_9998;ZZZ)Lnet/minecraft/class_1921;"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_9998;field_53247:Z",
   opcode = 180
)}
   )
   private boolean modifyMarkerValue(boolean original) {
      if (esp == null) {
         esp = (ESP)Modules.get().get(ESP.class);
      }

      return original && (!esp.isActive() || esp.shouldSkip(class_1299.field_6131));
   }
}
