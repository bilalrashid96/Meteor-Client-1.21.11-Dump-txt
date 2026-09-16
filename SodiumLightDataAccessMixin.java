package meteordevelopment.meteorclient.mixin.sodium;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Fullbright;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.minecraft.class_1920;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {LightDataAccess.class},
   remap = false
)
public abstract class SodiumLightDataAccessMixin {
   @Unique
   private static final int FULL_LIGHT = 4095;
   @Shadow
   protected class_1920 level;
   @Shadow
   @Final
   private class_2338.class_2339 pos;
   @Unique
   private Xray xray;
   @Unique
   private Fullbright fb;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void onInit(CallbackInfo info) {
      this.xray = (Xray)Modules.get().get(Xray.class);
      this.fb = (Fullbright)Modules.get().get(Fullbright.class);
   }

   @ModifyVariable(
      method = {"compute"},
      at = @At("TAIL"),
      name = {"bl"}
   )
   private int compute_modifyBL(int light) {
      if (this.xray.isActive()) {
         class_2680 state = this.level.method_8320(this.pos);
         if (!this.xray.isBlocked(state.method_26204(), this.pos)) {
            return 4095;
         }
      }

      return light;
   }

   @ModifyVariable(
      method = {"compute"},
      at = @At("STORE"),
      name = {"sl"}
   )
   private int compute_assignSL(int sl) {
      return Math.max(this.fb.getLuminance(class_1944.field_9284), sl);
   }

   @ModifyVariable(
      method = {"compute"},
      at = @At("STORE"),
      name = {"bl"}
   )
   private int compute_assignBL(int bl) {
      return Math.max(this.fb.getLuminance(class_1944.field_9282), bl);
   }
}
