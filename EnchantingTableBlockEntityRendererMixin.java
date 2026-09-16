package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_1058;
import net.minecraft.class_11659;
import net.minecraft.class_11683;
import net.minecraft.class_1921;
import net.minecraft.class_3879;
import net.minecraft.class_4587;
import net.minecraft.class_828;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_828.class})
public abstract class EnchantingTableBlockEntityRendererMixin {
   @WrapWithCondition(
      method = {"method_3571(Lnet/minecraft/class_11964;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_11659;method_73490(Lnet/minecraft/class_3879;Ljava/lang/Object;Lnet/minecraft/class_4587;Lnet/minecraft/class_1921;IIILnet/minecraft/class_1058;ILnet/minecraft/class_11683$class_11792;)V"
)}
   )
   private <S> boolean onRenderBookModelRenderProxy(class_11659 instance, class_3879<? super S> model, S state, class_4587 matrixStack, class_1921 renderLayer, int i, int j, int k, class_1058 sprite, int l, class_11683.class_11792 crumblingOverlayCommand) {
      return !((NoRender)Modules.get().get(NoRender.class)).noEnchTableBook();
   }
}
