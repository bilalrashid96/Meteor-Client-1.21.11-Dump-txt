package meteordevelopment.meteorclient.mixin.sodium;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {AbstractBlockRenderContext.class},
   remap = false
)
public abstract class SodiumBlockOcclusionCacheMixin {
   @Shadow
   protected class_2680 state;
   @Shadow
   protected class_2338 pos;
   @Shadow
   protected LevelSlice slice;
   @Unique
   private Xray xray;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void onInit(CallbackInfo info) {
      this.xray = (Xray)Modules.get().get(Xray.class);
   }

   @Inject(
      method = {"shouldDrawSide"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void meteor$forceXrayFace(class_2350 facing, CallbackInfoReturnable<Boolean> cir) {
      if (this.xray != null && this.xray.isActive() && !this.xray.isBlocked(this.state.method_26204(), (class_2338)null)) {
         cir.setReturnValue(true);
      }

   }

   @ModifyReturnValue(
      method = {"shouldDrawSide"},
      at = {@At("RETURN")}
   )
   private boolean shouldDrawSide(boolean original, class_2350 facing) {
      return !this.xray.isActive() ? original : this.xray.modifyDrawSide(this.state, this.slice, this.pos, facing, original);
   }
}
