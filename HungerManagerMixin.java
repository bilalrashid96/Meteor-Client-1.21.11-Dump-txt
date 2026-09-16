package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import net.minecraft.class_1702;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_1702.class})
public class HungerManagerMixin {
   @ModifyExpressionValue(
      method = {"method_75882()Z"},
      at = {@At(
   value = "CONSTANT",
   args = {"floatValue=6.0f"}
)}
   )
   private float onHunger(float constant) {
      return ((NoSlow)Modules.get().get(NoSlow.class)).hunger() ? -1.0F : constant;
   }
}
