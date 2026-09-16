package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.List;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import meteordevelopment.meteorclient.systems.modules.movement.Slippy;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import net.minecraft.class_1935;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_4970;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_2248.class})
public abstract class BlockMixin extends class_4970 implements class_1935 {
   public BlockMixin(class_4970.class_2251 settings) {
      super(settings);
   }

   @ModifyReturnValue(
      method = {"method_9499"},
      at = {@At("RETURN")}
   )
   public float getSlipperiness(float original) {
      if (Modules.get() == null) {
         return original;
      } else {
         Slippy slippy = (Slippy)Modules.get().get(Slippy.class);
         class_2248 block = (class_2248)this;
         if (slippy.isActive()) {
            if (slippy.listMode.get() == Slippy.ListMode.Whitelist) {
               if (((List)slippy.allowedBlocks.get()).contains(block)) {
                  return ((Double)slippy.friction.get()).floatValue();
               }
            } else if (!((List)slippy.ignoredBlocks.get()).contains(block)) {
               return ((Double)slippy.friction.get()).floatValue();
            }
         }

         if (block == class_2246.field_10030 && ((NoSlow)Modules.get().get(NoSlow.class)).slimeBlock()) {
            return 0.6F;
         } else {
            return original;
         }
      }
   }

   @Inject(
      method = {"method_9607"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void meteor$forceXrayFace(class_2680 state, class_2680 sideState, class_2350 side, CallbackInfoReturnable<Boolean> cir) {
      Modules modules = Modules.get();
      if (modules != null) {
         Xray xray = (Xray)modules.get(Xray.class);
         if (xray.isActive() && !xray.isBlocked(state.method_26204(), (class_2338)null)) {
            cir.setReturnValue(true);
         }

      }
   }
}
