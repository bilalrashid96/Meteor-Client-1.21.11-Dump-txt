package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_4588;
import net.minecraft.class_919;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_919.class})
public abstract class LightningEntityRendererMixin {
   @Inject(
      method = {"method_23183"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onSetLightningVertex(Matrix4f matrix4f, class_4588 vertexConsumer, float f, float g, int i, float h, float j, float k, float l, float m, float n, float o, boolean bl, boolean bl2, boolean bl3, boolean bl4, CallbackInfo ci) {
      Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
      if (ambience.isActive() && (Boolean)ambience.changeLightningColor.get()) {
         Color color = ambience.lightningColor.get();
         vertexConsumer.method_22918(matrix4f, f + (bl ? o : -o), (float)(i * 16), g + (bl2 ? o : -o)).method_22915((float)color.r / 255.0F, (float)color.g / 255.0F, (float)color.b / 255.0F, 0.3F);
         vertexConsumer.method_22918(matrix4f, h + (bl ? n : -n), (float)((i + 1) * 16), j + (bl2 ? n : -n)).method_22915((float)color.r / 255.0F, (float)color.g / 255.0F, (float)color.b / 255.0F, 0.3F);
         vertexConsumer.method_22918(matrix4f, h + (bl3 ? n : -n), (float)((i + 1) * 16), j + (bl4 ? n : -n)).method_22915((float)color.r / 255.0F, (float)color.g / 255.0F, (float)color.b / 255.0F, 0.3F);
         vertexConsumer.method_22918(matrix4f, f + (bl3 ? o : -o), (float)(i * 16), g + (bl4 ? o : -o)).method_22915((float)color.r / 255.0F, (float)color.g / 255.0F, (float)color.b / 255.0F, 0.3F);
         ci.cancel();
      }

   }
}
