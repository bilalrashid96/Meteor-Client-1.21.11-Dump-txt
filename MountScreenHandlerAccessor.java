package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_12343;
import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_12343.class})
public interface MountScreenHandlerAccessor {
   @Accessor("field_64486")
   class_1309 meteor$getMount();
}
