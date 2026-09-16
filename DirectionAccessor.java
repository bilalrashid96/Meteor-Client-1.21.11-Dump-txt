package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_2350;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2350.class})
public interface DirectionAccessor {
   @Accessor("field_11041")
   static class_2350[] meteor$getHorizontal() {
      throw new AssertionError();
   }
}
