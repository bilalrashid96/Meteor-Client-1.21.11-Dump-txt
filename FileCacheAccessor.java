package meteordevelopment.meteorclient.mixin;

import java.nio.file.Path;
import net.minecraft.class_1071;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1071.class_8687.class})
public interface FileCacheAccessor {
   @Accessor("field_45640")
   Path meteor$getDirectory();
}
