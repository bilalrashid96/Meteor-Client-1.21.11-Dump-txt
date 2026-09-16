package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.InventoryTweaks;
import net.minecraft.class_1799;
import net.minecraft.class_9276;
import net.minecraft.class_9334;
import net.minecraft.class_9929;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_9929.class})
public class BundleTooltipSubmenuHandlerMixin {
   @ModifyExpressionValue(
      method = {"method_61976"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_5537;method_61645(Lnet/minecraft/class_1799;)I"
)}
   )
   private int uncapBundleScrolling1(int original, class_1799 item, int slotId, int selectedItemIndex) {
      return ((InventoryTweaks)Modules.get().get(InventoryTweaks.class)).uncapBundleScrolling() ? ((class_9276)item.method_58695(class_9334.field_49650, class_9276.field_49289)).method_57426() : original;
   }

   @ModifyExpressionValue(
      method = {"method_61973"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_5537;method_61645(Lnet/minecraft/class_1799;)I"
)}
   )
   private int uncapBundleScrolling2(int original, double horizontal, double vertical, int slotId, class_1799 item) {
      return ((InventoryTweaks)Modules.get().get(InventoryTweaks.class)).uncapBundleScrolling() ? ((class_9276)item.method_58695(class_9334.field_49650, class_9276.field_49289)).method_57426() : original;
   }
}
