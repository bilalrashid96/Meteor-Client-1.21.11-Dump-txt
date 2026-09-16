package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_243;
import net.minecraft.class_2743;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2743.class})
public interface EntityVelocityUpdateS2CPacketAccessor {
   @Mutable
   @Accessor("field_61887")
   void meteor$setVelocity(class_243 var1);
}
