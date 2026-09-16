package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Jesus;
import net.minecraft.class_1297;
import net.minecraft.class_5635;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_5635.class})
public abstract class PowderSnowBlockMixin {
   @ModifyReturnValue(
      method = {"method_32355"},
      at = {@At("RETURN")}
   )
   private static boolean onCanWalkOnPowderSnow(boolean original, class_1297 entity) {
      return entity == MeteorClient.mc.field_1724 && ((Jesus)Modules.get().get(Jesus.class)).canWalkOnPowderSnow() ? true : original;
   }
}
