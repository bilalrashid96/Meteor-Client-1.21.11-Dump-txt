package meteordevelopment.meteorclient.mixin;

import java.util.Map;
import net.minecraft.class_2338;
import net.minecraft.class_2586;
import net.minecraft.class_2791;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2791.class})
public interface ChunkAccessor {
   @Accessor("field_34543")
   Map<class_2338, class_2586> getBlockEntities();
}
