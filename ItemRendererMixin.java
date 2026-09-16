package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_10444;
import net.minecraft.class_918;
import net.minecraft.class_10444.class_10445;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({class_918.class})
public abstract class ItemRendererMixin {
   @ModifyVariable(
      method = {"method_62476(Lnet/minecraft/class_811;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;II[ILjava/util/List;Lnet/minecraft/class_1921;Lnet/minecraft/class_10444$class_10445;)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private static class_10444.class_10445 modifyEnchant(class_10444.class_10445 glint) {
      return ((NoRender)Modules.get().get(NoRender.class)).noEnchantGlint() ? class_10445.field_55341 : glint;
   }
}
