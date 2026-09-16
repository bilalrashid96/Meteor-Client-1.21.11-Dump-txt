package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1297.class})
public interface EntityAccessor {
   @Accessor("field_5957")
   void meteor$setInWater(boolean var1);
}
