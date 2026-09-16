package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_640;
import net.minecraft.class_742;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_742.class})
public interface AbstractClientPlayerEntityAccessor {
   @Accessor("field_3901")
   void meteor$setPlayerListEntry(class_640 var1);
}
