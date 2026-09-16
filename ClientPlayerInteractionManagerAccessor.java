package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_2338;
import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_636.class})
public interface ClientPlayerInteractionManagerAccessor {
   @Accessor("field_3715")
   float meteor$getBreakingProgress();

   @Accessor("field_3715")
   void meteor$setCurrentBreakingProgress(float var1);

   @Accessor("field_3714")
   class_2338 meteor$getCurrentBreakingBlockPos();
}
