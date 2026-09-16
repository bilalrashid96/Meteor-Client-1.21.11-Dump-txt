package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.Hitboxes;
import net.minecraft.class_1297;
import net.minecraft.class_1675;
import net.minecraft.class_238;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_1675.class})
public class ProjectileUtilMixin {
   @ModifyExpressionValue(
      method = {"method_75215(Lnet/minecraft/class_1937;Lnet/minecraft/class_1297;Lnet/minecraft/class_243;Lnet/minecraft/class_243;Lnet/minecraft/class_238;Ljava/util/function/Predicate;FLnet/minecraft/class_3959$class_3960;Z)Ljava/util/Collection;"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_5829()Lnet/minecraft/class_238;"
)}
   )
   private static class_238 modifyHitboxMargin(class_238 original, @Local(ordinal = 1) class_1297 entity2) {
      double v = ((Hitboxes)Modules.get().get(Hitboxes.class)).getEntityValue(entity2);
      return v == (double)0.0F ? original : original.method_1014(v);
   }
}
