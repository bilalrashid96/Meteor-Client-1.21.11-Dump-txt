package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1657;
import net.minecraft.class_4050;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_1657.class})
public interface PlayerEntityAccessor {
   @Invoker("method_52558")
   boolean meteor$canChangeIntoPose(class_4050 var1);
}
