package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.ISlot;
import net.minecraft.class_1735;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({class_1735.class})
public abstract class SlotMixin implements ISlot {
   @Shadow
   public int field_7874;
   @Shadow
   @Final
   private int field_7875;

   public int meteor$getId() {
      return this.field_7874;
   }

   public int meteor$getIndex() {
      return this.field_7875;
   }
}
