package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1163;
import net.minecraft.class_1920;
import net.minecraft.class_2338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1163.class})
public abstract class BiomeColorsMixin {
   @Inject(
      method = {"method_4961"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onGetWaterColor(class_1920 world, class_2338 pos, CallbackInfoReturnable<Integer> info) {
      Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
      if (ambience.isActive() && (Boolean)ambience.customWaterColor.get()) {
         info.setReturnValue(((SettingColor)ambience.waterColor.get()).getPacked());
      }

   }

   @Inject(
      method = {"method_4966"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onGetFoliageColor(class_1920 world, class_2338 pos, CallbackInfoReturnable<Integer> info) {
      Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
      if (ambience.isActive() && (Boolean)ambience.customFoliageColor.get()) {
         info.setReturnValue(((SettingColor)ambience.foliageColor.get()).getPacked());
      }

   }

   @Inject(
      method = {"method_4962"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onGetGrassColor(class_1920 world, class_2338 pos, CallbackInfoReturnable<Integer> info) {
      Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
      if (ambience.isActive() && (Boolean)ambience.customGrassColor.get()) {
         info.setReturnValue(((SettingColor)ambience.grassColor.get()).getPacked());
      }

   }
}
