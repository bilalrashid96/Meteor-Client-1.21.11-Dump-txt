package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_11701;
import net.minecraft.class_824;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_824.class})
public interface BlockEntityRenderManagerAccessor {
   @Accessor("field_61783")
   class_11701 getSpriteHolder();
}
