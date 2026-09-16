package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_11246;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_332.class})
public interface DrawContextAccessor {
   @Accessor("field_59826")
   class_11246 getState();

   @Accessor("field_44659")
   class_332.class_8214 getScissorStack();
}
