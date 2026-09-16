package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Chams;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_10014;
import net.minecraft.class_1058;
import net.minecraft.class_11659;
import net.minecraft.class_11683;
import net.minecraft.class_12075;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_3879;
import net.minecraft.class_4587;
import net.minecraft.class_892;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_892.class})
public abstract class EndCrystalEntityRendererMixin {
   @Unique
   private Chams chams;
   @Shadow
   @Final
   @Mutable
   private static class_1921 field_21736;
   @Shadow
   @Final
   private static class_2960 field_4663;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void onInit(CallbackInfo info) {
      this.chams = (Chams)Modules.get().get(Chams.class);
   }

   @Inject(
      method = {"method_3908(Lnet/minecraft/class_10014;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"},
      at = {@At("HEAD")}
   )
   private void render$renderLayer(class_10014 endCrystalEntityRenderState, class_4587 matrixStack, class_11659 orderedRenderCommandQueue, class_12075 arg, CallbackInfo ci) {
      field_21736 = class_12249.method_76000(this.chams.isActive() && (Boolean)this.chams.crystals.get() && !(Boolean)this.chams.crystalsTexture.get() ? Chams.BLANK : field_4663);
   }

   @Inject(
      method = {"method_3908(Lnet/minecraft/class_10014;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_4587;method_22905(FFF)V"
)}
   )
   private void render$scale(class_10014 endCrystalEntityRenderState, class_4587 matrixStack, class_11659 orderedRenderCommandQueue, class_12075 arg, CallbackInfo ci) {
      if (this.chams.isActive() && (Boolean)this.chams.crystals.get()) {
         float v = ((Double)this.chams.crystalsScale.get()).floatValue();
         matrixStack.method_22905(v, v, v);
      }
   }

   @WrapWithCondition(
      method = {"method_3908(Lnet/minecraft/class_10014;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_11659;method_73489(Lnet/minecraft/class_3879;Ljava/lang/Object;Lnet/minecraft/class_4587;Lnet/minecraft/class_1921;IIILnet/minecraft/class_11683$class_11792;)V"
)}
   )
   private <S> boolean render$color(class_11659 instance, class_3879<? super S> model, S state, class_4587 matrixStack, class_1921 renderLayer, int light, int uv, int outlineColor, class_11683.class_11792 crumblingOverlayCommand) {
      if (this.chams.isActive() && (Boolean)this.chams.crystals.get()) {
         instance.method_73490(model, state, matrixStack, field_21736, light, uv, ((SettingColor)this.chams.crystalsColor.get()).getPacked(), (class_1058)null, outlineColor, (class_11683.class_11792)null);
         return false;
      } else {
         return true;
      }
   }
}
