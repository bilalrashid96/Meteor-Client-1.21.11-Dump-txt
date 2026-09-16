package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_5225;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_5225.class})
public interface TextHandlerAccessor {
   @Accessor("field_24216")
   class_5225.class_5231 meteor$getWidthRetriever();
}
