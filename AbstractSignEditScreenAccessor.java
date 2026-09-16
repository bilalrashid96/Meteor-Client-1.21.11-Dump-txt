package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_2625;
import net.minecraft.class_7743;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_7743.class})
public interface AbstractSignEditScreenAccessor {
   @Accessor("field_40424")
   class_2625 meteor$getSign();
}
