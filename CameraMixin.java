package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import meteordevelopment.meteorclient.mixininterface.ICamera;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.CameraTweaks;
import meteordevelopment.meteorclient.systems.modules.render.FreeLook;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.world.HighwayBuilder;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_5636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_4184.class})
public abstract class CameraMixin implements ICamera {
   @Shadow
   private boolean field_18719;
   @Shadow
   private float field_18718;
   @Shadow
   private float field_18717;

   @Shadow
   protected abstract void method_19325(float var1, float var2);

   @Inject(
      method = {"method_19334"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getSubmergedFluidState(CallbackInfoReturnable<class_5636> ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noLiquidOverlay()) {
         ci.setReturnValue(class_5636.field_27888);
      }

   }

   @ModifyVariable(
      method = {"method_19318"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private float modifyClipToSpace(float d) {
      if (((Freecam)Modules.get().get(Freecam.class)).isActive()) {
         return 0.0F;
      } else {
         CameraTweaks cameraTweaks = (CameraTweaks)Modules.get().get(CameraTweaks.class);
         return cameraTweaks.isActive() ? (float)cameraTweaks.distance : d;
      }
   }

   @Inject(
      method = {"method_19318"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onClipToSpace(float desiredCameraDistance, CallbackInfoReturnable<Float> info) {
      if (((CameraTweaks)Modules.get().get(CameraTweaks.class)).clip()) {
         info.setReturnValue(desiredCameraDistance);
      }

   }

   @Inject(
      method = {"method_19321"},
      at = {@At("TAIL")}
   )
   private void onUpdateTail(class_1937 area, class_1297 focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo ci) {
      if (Modules.get().isActive(Freecam.class)) {
         this.field_18719 = true;
      }

   }

   @ModifyArgs(
      method = {"method_19321"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_4184;method_19327(DDD)V"
)
   )
   private void onUpdateSetPosArgs(Args args, @Local(argsOnly = true) float tickDelta) {
      Freecam freecam = (Freecam)Modules.get().get(Freecam.class);
      if (freecam.isActive()) {
         args.set(0, freecam.getX(tickDelta));
         args.set(1, freecam.getY(tickDelta));
         args.set(2, freecam.getZ(tickDelta));
      }

   }

   @ModifyArgs(
      method = {"method_19321"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_4184;method_19325(FF)V"
)
   )
   private void onUpdateSetRotationArgs(Args args, @Local(argsOnly = true) float tickDelta) {
      Freecam freecam = (Freecam)Modules.get().get(Freecam.class);
      FreeLook freeLook = (FreeLook)Modules.get().get(FreeLook.class);
      if (freecam.isActive()) {
         args.set(0, (float)freecam.getYaw(tickDelta));
         args.set(1, (float)freecam.getPitch(tickDelta));
      } else if (Modules.get().isActive(HighwayBuilder.class)) {
         args.set(0, this.field_18718);
         args.set(1, this.field_18717);
      } else if (freeLook.isActive()) {
         args.set(0, freeLook.cameraYaw);
         args.set(1, freeLook.cameraPitch);
      }

   }

   public void meteor$setRot(double yaw, double pitch) {
      this.method_19325((float)yaw, (float)class_3532.method_15350(pitch, (double)-90.0F, (double)90.0F));
   }
}
