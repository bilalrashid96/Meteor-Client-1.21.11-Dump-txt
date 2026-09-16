package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.mixininterface.IEntityRenderState;
import meteordevelopment.meteorclient.utils.network.Capes;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_12079;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_972;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_972.class})
public abstract class CapeFeatureRendererMixin {
   @ModifyExpressionValue(
      method = {"method_4177(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10055;FF)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_8685;comp_1627()Lnet/minecraft/class_12079$class_12081;"
)}
   )
   private class_12079.class_12081 modifyCapeTexture(class_12079.class_12081 original, class_4587 matrices, class_11659 entityRenderCommandQueue, int i, class_10055 state, float f, float g) {
      class_1297 var9 = ((IEntityRenderState)state).meteor$getEntity();
      if (var9 instanceof class_1657 player) {
         class_2960 id = Capes.get(player);
         return (class_12079.class_12081)(id == null ? original : new class_12079.class_10726(id, id));
      } else {
         return original;
      }
   }
}
