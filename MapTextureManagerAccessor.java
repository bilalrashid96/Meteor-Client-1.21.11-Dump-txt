package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_10093;
import net.minecraft.class_22;
import net.minecraft.class_9209;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_10093.class})
public interface MapTextureManagerAccessor {
   @Invoker("method_62625")
   class_10093.class_331 meteor$invokeGetMapTexture(class_9209 var1, class_22 var2);
}
