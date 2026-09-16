package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_12247;
import net.minecraft.class_1921;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1921.class})
public interface RenderLayerAccessor {
   @Accessor("field_64013")
   class_12247 getRenderSetup();
}
