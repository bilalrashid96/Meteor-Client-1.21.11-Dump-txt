package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1309;
import net.minecraft.class_3611;
import net.minecraft.class_6862;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_1309.class})
public interface LivingEntityAccessor {
   @Invoker("method_6010")
   void meteor$swimUpwards(class_6862<class_3611> var1);

   @Accessor("field_6282")
   boolean meteor$isJumping();

   @Accessor("field_6228")
   int meteor$getJumpCooldown();

   @Accessor("field_6228")
   void meteor$setJumpCooldown(int var1);
}
