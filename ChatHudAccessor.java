package meteordevelopment.meteorclient.mixin;

import java.util.List;
import net.minecraft.class_303;
import net.minecraft.class_338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_338.class})
public interface ChatHudAccessor {
   @Accessor("field_2064")
   List<class_303.class_7590> meteor$getVisibleMessages();

   @Accessor("field_2061")
   List<class_303> meteor$getMessages();
}
