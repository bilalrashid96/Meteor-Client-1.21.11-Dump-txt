package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.ISlot;
import net.minecraft.class_1735;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
   targets = {"net/minecraft/class_481$class_484"}
)
public abstract class CreativeSlotMixin implements ISlot {
   @Shadow
   @Final
   class_1735 field_2898;

   public int meteor$getId() {
      return this.field_2898.field_7874;
   }

   public int meteor$getIndex() {
      return this.field_2898.method_34266();
   }
}
