package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1536;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1536.class})
public interface FishingBobberEntityAccessor {
   @Accessor("field_23232")
   boolean meteor$hasCaughtFish();
}
