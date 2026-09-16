package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_2350;
import net.minecraft.class_3965;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_3965.class})
public interface BlockHitResultAccessor {
   @Mutable
   @Accessor("field_17588")
   void meteor$setSide(class_2350 var1);
}
