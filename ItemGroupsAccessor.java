package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1761;
import net.minecraft.class_5321;
import net.minecraft.class_7706;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_7706.class})
public interface ItemGroupsAccessor {
   @Accessor("field_40206")
   static class_5321<class_1761> meteor$getInventory() {
      throw new AssertionError();
   }
}
