package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_758;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({class_758.class})
public abstract class FogRendererMixin {
   @ModifyVariable(
      method = {"method_71110(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private Vector4f modifyFogDistance(Vector4f original) {
      if (Modules.get() == null) {
         return original;
      } else {
         Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
         return ambience.isActive() && (Boolean)ambience.customFogColor.get() ? ((SettingColor)ambience.fogColor.get()).getVec4f() : original;
      }
   }

   @ModifyExpressionValue(
      method = {"method_71109"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_758;field_54018:Z"
)}
   )
   private boolean modifyFogEnabled(boolean original) {
      if (Modules.get() == null) {
         return original;
      } else {
         return original && !((NoRender)Modules.get().get(NoRender.class)).noFog();
      }
   }
}
