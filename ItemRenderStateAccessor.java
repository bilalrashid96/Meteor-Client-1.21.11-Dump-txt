package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_10444;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_10444.class})
public interface ItemRenderStateAccessor {
   @Accessor("field_55339")
   int meteor$getLayerCount();

   @Accessor("field_55340")
   class_10444.class_10446[] meteor$getLayers();
}
