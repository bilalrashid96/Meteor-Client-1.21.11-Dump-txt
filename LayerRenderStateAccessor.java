package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_10444;
import net.minecraft.class_804;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_10444.class_10446.class})
public interface LayerRenderStateAccessor {
   @Accessor("field_56967")
   class_804 meteor$getTransform();
}
