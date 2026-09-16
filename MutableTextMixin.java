package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IText;
import net.minecraft.class_2477;
import net.minecraft.class_5250;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({class_5250.class})
public abstract class MutableTextMixin implements IText {
   @Shadow
   private @Nullable class_2477 field_39009;

   public void meteor$invalidateCache() {
      this.field_39009 = null;
   }
}
