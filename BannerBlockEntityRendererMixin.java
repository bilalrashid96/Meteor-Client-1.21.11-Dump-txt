package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_10378;
import net.minecraft.class_1088;
import net.minecraft.class_11659;
import net.minecraft.class_11683;
import net.minecraft.class_11701;
import net.minecraft.class_11949;
import net.minecraft.class_12075;
import net.minecraft.class_12249;
import net.minecraft.class_3902;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_4730;
import net.minecraft.class_7833;
import net.minecraft.class_823;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_823.class})
public abstract class BannerBlockEntityRendererMixin {
   @Shadow
   @Final
   private class_11701 field_61779;

   @Inject(
      method = {"method_3546(Lnet/minecraft/class_11949;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void injectRender1(class_11949 bannerBlockEntityRenderState, class_4587 matrixStack, class_11659 orderedRenderCommandQueue, class_12075 arg, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).getBannerRenderMode() == NoRender.BannerRenderMode.None) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_3546(Lnet/minecraft/class_11949;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_823;method_65555(Lnet/minecraft/class_11701;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;IIFLnet/minecraft/class_10378;Lnet/minecraft/class_10377;FLnet/minecraft/class_1767;Lnet/minecraft/class_9307;Lnet/minecraft/class_11683$class_11792;I)V"
)},
      cancellable = true
   )
   private void injectRender2(class_11949 bannerBlockEntityRenderState, class_4587 matrixStack, class_11659 orderedRenderCommandQueue, class_12075 cameraRenderState, CallbackInfo ci, @Local class_10378 bannerBlockModel) {
      if (((NoRender)Modules.get().get(NoRender.class)).getBannerRenderMode() == NoRender.BannerRenderMode.Pillar) {
         renderPillar(matrixStack, orderedRenderCommandQueue, bannerBlockEntityRenderState.field_62676, bannerBlockEntityRenderState.field_62663, bannerBlockModel, this.field_61779, bannerBlockEntityRenderState.field_62677);
         ci.cancel();
      }

   }

   @Unique
   private static void renderPillar(class_4587 matrices, class_11659 entityRenderCommandQueue, int light, float rotation, class_10378 model, class_11701 spriteHolder, class_11683.class_11792 arg) {
      matrices.method_22903();
      matrices.method_46416(0.5F, 0.0F, 0.5F);
      matrices.method_22907(class_7833.field_40716.rotationDegrees(rotation));
      matrices.method_22905(0.6666667F, -0.6666667F, -0.6666667F);
      class_4730 spriteIdentifier = class_1088.field_20847;
      entityRenderCommandQueue.method_73490(model, class_3902.field_17274, matrices, spriteIdentifier.method_24146(class_12249::method_75982), light, class_4608.field_21444, -1, spriteHolder.method_73030(spriteIdentifier), 0, arg);
      matrices.method_22909();
   }
}
