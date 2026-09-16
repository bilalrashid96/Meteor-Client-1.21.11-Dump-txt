package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1293;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1293.class})
public interface StatusEffectInstanceAccessor {
   @Accessor("field_5895")
   void meteor$setDuration(int var1);
}
