package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_11278;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_11278.class})
public interface ProjectionMatrix2Accessor {
   @Invoker("method_71094")
   Matrix4f meteor$callGetMatrix(float var1, float var2);
}
