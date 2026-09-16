package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_4970;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_4970.class})
public interface AbstractBlockAccessor {
   @Accessor("field_23159")
   boolean meteor$isCollidable();
}
