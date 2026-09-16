package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_2828;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2828.class})
public interface PlayerMoveC2SPacketAccessor {
   @Mutable
   @Accessor("field_12886")
   void meteor$setY(double var1);

   @Mutable
   @Accessor("field_29179")
   void meteor$setOnGround(boolean var1);
}
