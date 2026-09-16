package meteordevelopment.meteorclient.mixin.viafabricplus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({GeneralSettings.class})
public abstract class GeneralSettingsMixin {
   @ModifyExpressionValue(
      method = {"<init>"},
      at = {@At(
   value = "CONSTANT",
   args = {"intValue=2"},
   ordinal = 1
)},
      remap = false
   )
   private int modifyDefaultPosition(int original) {
      return 4;
   }
}
