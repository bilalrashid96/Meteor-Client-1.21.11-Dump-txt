package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.function.ToDoubleFunction;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.Hitboxes;
import net.minecraft.class_12392;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_12392.class})
public class AttackRangeComponentMixin {
   @ModifyExpressionValue(
      method = {"method_76737(Lnet/minecraft/class_1309;Ljava/util/function/ToDoubleFunction;D)Z"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_12392;comp_5262:F",
   opcode = 180
)}
   )
   private float modifyHitboxMargin(float original, class_1309 entity, ToDoubleFunction<class_243> squaredDistanceFunction, double extraHitboxMargin) {
      float v = (float)((Hitboxes)Modules.get().get(Hitboxes.class)).getEntityValue(entity);
      return original + v;
   }
}
