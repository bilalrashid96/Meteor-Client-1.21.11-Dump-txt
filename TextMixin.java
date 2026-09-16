package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IText;
import net.minecraft.class_2561;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({class_2561.class})
public interface TextMixin extends IText {
   default void meteor$invalidateCache() {
   }
}
