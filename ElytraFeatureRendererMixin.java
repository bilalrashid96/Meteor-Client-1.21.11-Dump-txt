package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.mixininterface.IEntityRenderState;
import meteordevelopment.meteorclient.utils.network.Capes;
import net.minecraft.class_10034;
import net.minecraft.class_11659;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2960;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_583;
import net.minecraft.class_979;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_979.class})
public abstract class ElytraFeatureRendererMixin<S extends class_10034, M extends class_583<S>> extends class_3887<S, M> {
   public ElytraFeatureRendererMixin(class_3883<S, M> context) {
      super(context);
   }

   @ModifyExpressionValue(
      method = {"method_17161(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10034;FF)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_979;method_64084(Lnet/minecraft/class_10034;)Lnet/minecraft/class_2960;"
)}
   )
   private class_2960 modifyCapeTexture(class_2960 original, class_4587 matrices, class_11659 entityRenderCommandQueue, int i, S state, float f, float g) {
      class_1297 var9 = ((IEntityRenderState)state).meteor$getEntity();
      if (var9 instanceof class_1657 player) {
         class_2960 id = Capes.get(player);
         return id == null ? original : id;
      } else {
         return original;
      }
   }
}
