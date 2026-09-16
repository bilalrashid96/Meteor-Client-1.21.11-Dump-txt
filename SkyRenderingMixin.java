package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import net.minecraft.class_12076;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_9975;
import net.minecraft.class_2874.class_12326;
import org.joml.Vector4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_9975.class})
public class SkyRenderingMixin {
   @Inject(
      method = {"method_74926"},
      at = {@At("TAIL")}
   )
   private void updateRenderState(class_638 world, float tickProgress, class_4184 camera, class_12076 state, CallbackInfo ci) {
      Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
      if (ambience.isActive()) {
         if ((Boolean)ambience.endSky.get()) {
            state.field_64464 = class_12326.field_64387;
         }

         if ((Boolean)ambience.customSkyColor.get()) {
            state.field_63097 = ambience.skyColor().getPacked();
         }

      }
   }

   @ModifyArg(
      method = {"method_62312"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_11282;method_71106(Lorg/joml/Matrix4fc;Lorg/joml/Vector4fc;Lorg/joml/Vector3fc;Lorg/joml/Matrix4fc;)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"
)
   )
   private Vector4fc modifyEndSkyColor(Vector4fc original) {
      Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
      return (Vector4fc)(ambience.isActive() && (Boolean)ambience.endSky.get() && (Boolean)ambience.customSkyColor.get() ? ambience.skyColor().getVec4f() : original);
   }
}
