package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4076;
import net.minecraft.class_862;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_862.class})
public abstract class ChunkBorderDebugRendererMixin {
   @Shadow
   @Final
   private class_310 field_4516;

   @ModifyExpressionValue(
      method = {"method_23109"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_4076;method_18682(Lnet/minecraft/class_2338;)Lnet/minecraft/class_4076;"
)}
   )
   private class_4076 render$getChunkPos(class_4076 original) {
      Freecam freecam = (Freecam)Modules.get().get(Freecam.class);
      if (!freecam.isActive()) {
         return original;
      } else {
         float delta = this.field_4516.method_61966().method_60637(true);
         return class_4076.method_18676(class_4076.method_18675(class_3532.method_15357(freecam.getX(delta))), class_4076.method_18675(class_3532.method_15357(freecam.getY(delta))), class_4076.method_18675(class_3532.method_15357(freecam.getZ(delta))));
      }
   }
}
