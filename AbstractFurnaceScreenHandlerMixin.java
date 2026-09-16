package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IAbstractFurnaceScreenHandler;
import net.minecraft.class_1720;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({class_1720.class})
public abstract class AbstractFurnaceScreenHandlerMixin implements IAbstractFurnaceScreenHandler {
   @Shadow
   protected abstract boolean method_7640(class_1799 var1);

   public boolean meteor$isItemSmeltable(class_1799 itemStack) {
      return this.method_7640(itemStack);
   }
}
