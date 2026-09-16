package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_322;
import net.minecraft.class_324;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({class_324.class})
public abstract class BlockColorsMixin {
   @ModifyArg(
      method = {"method_1689"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_324;method_1690(Lnet/minecraft/class_322;[Lnet/minecraft/class_2248;)V",
   ordinal = 3
),
      index = 0
   )
   private static class_322 modifySpruceLeavesColor(class_322 provider) {
      return (state, world, pos, tintIndex) -> getModifiedColor(-10380959);
   }

   @ModifyArg(
      method = {"method_1689"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_324;method_1690(Lnet/minecraft/class_322;[Lnet/minecraft/class_2248;)V",
   ordinal = 4
),
      index = 0
   )
   private static class_322 modifyBirchLeavesColor(class_322 provider) {
      return (state, world, pos, tintIndex) -> getModifiedColor(-8345771);
   }

   @Unique
   private static int getModifiedColor(int original) {
      if (Modules.get() == null) {
         return original;
      } else {
         Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
         return ambience.isActive() && (Boolean)ambience.customFoliageColor.get() ? ((SettingColor)ambience.foliageColor.get()).getPacked() : original;
      }
   }
}
