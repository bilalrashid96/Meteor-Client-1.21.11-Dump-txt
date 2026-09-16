package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.EntityMoveEvent;
import meteordevelopment.meteorclient.events.entity.player.JumpVelocityMultiplierEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.ICamera;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.Hitboxes;
import meteordevelopment.meteorclient.systems.modules.movement.Flight;
import meteordevelopment.meteorclient.systems.modules.movement.Jesus;
import meteordevelopment.meteorclient.systems.modules.movement.NoFall;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import meteordevelopment.meteorclient.systems.modules.movement.Velocity;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import meteordevelopment.meteorclient.systems.modules.render.FreeLook;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.world.HighwayBuilder;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import net.minecraft.class_1297;
import net.minecraft.class_1313;
import net.minecraft.class_1657;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_243;
import net.minecraft.class_4050;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_1297.class})
public abstract class EntityMixin {
   @ModifyExpressionValue(
      method = {"method_5692"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3610;method_15758(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;)Lnet/minecraft/class_243;"
)}
   )
   private class_243 updateMovementInFluidFluidStateGetVelocity(class_243 vec) {
      if (this != MeteorClient.mc.field_1724) {
         return vec;
      } else {
         Velocity velocity = (Velocity)Modules.get().get(Velocity.class);
         if (velocity.isActive() && (Boolean)velocity.liquids.get()) {
            vec = vec.method_18805(velocity.getHorizontal(velocity.liquidsHorizontal), velocity.getVertical(velocity.liquidsVertical), velocity.getHorizontal(velocity.liquidsHorizontal));
         }

         return vec;
      }
   }

   @Inject(
      method = {"method_5799"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isTouchingWater(CallbackInfoReturnable<Boolean> info) {
      if (this == MeteorClient.mc.field_1724) {
         if (((Flight)Modules.get().get(Flight.class)).isActive()) {
            info.setReturnValue(false);
         }

         if (((NoSlow)Modules.get().get(NoSlow.class)).fluidDrag()) {
            info.setReturnValue(false);
         }

      }
   }

   @Inject(
      method = {"method_5771"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isInLava(CallbackInfoReturnable<Boolean> info) {
      if (this == MeteorClient.mc.field_1724) {
         if (((Flight)Modules.get().get(Flight.class)).isActive()) {
            info.setReturnValue(false);
         }

         if (((NoSlow)Modules.get().get(NoSlow.class)).fluidDrag()) {
            info.setReturnValue(false);
         }

      }
   }

   @Inject(
      method = {"method_5700"},
      at = {@At("HEAD")}
   )
   private void onBubbleColumnSurfaceCollision(CallbackInfo info) {
      if (this == MeteorClient.mc.field_1724) {
         Jesus jesus = (Jesus)Modules.get().get(Jesus.class);
         if (jesus.isActive()) {
            jesus.isInBubbleColumn = true;
         }

      }
   }

   @Inject(
      method = {"method_5764"},
      at = {@At("HEAD")}
   )
   private void onBubbleColumnCollision(CallbackInfo info) {
      if (this == MeteorClient.mc.field_1724) {
         Jesus jesus = (Jesus)Modules.get().get(Jesus.class);
         if (jesus.isActive()) {
            jesus.isInBubbleColumn = true;
         }

      }
   }

   @ModifyExpressionValue(
      method = {"method_5790"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_5869()Z"
)}
   )
   private boolean isSubmergedInWater(boolean submerged) {
      if (this != MeteorClient.mc.field_1724) {
         return submerged;
      } else if (((NoSlow)Modules.get().get(NoSlow.class)).fluidDrag()) {
         return false;
      } else {
         return ((Flight)Modules.get().get(Flight.class)).isActive() ? false : submerged;
      }
   }

   @ModifyArgs(
      method = {"method_5697(Lnet/minecraft/class_1297;)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_5762(DDD)V"
)
   )
   private void onPushAwayFrom(Args args, class_1297 entity) {
      Velocity velocity = (Velocity)Modules.get().get(Velocity.class);
      if (this == MeteorClient.mc.field_1724 && velocity.isActive() && (Boolean)velocity.entityPush.get()) {
         double multiplier = (Double)velocity.entityPushAmount.get();
         args.set(0, (Double)args.get(0) * multiplier);
         args.set(2, (Double)args.get(2) * multiplier);
      } else if (entity instanceof FakePlayerEntity) {
         FakePlayerEntity player = (FakePlayerEntity)entity;
         if (player.doNotPush) {
            args.set(0, (double)0.0F);
            args.set(2, (double)0.0F);
         }
      }

   }

   @ModifyReturnValue(
      method = {"method_23313"},
      at = {@At("RETURN")}
   )
   private float onGetJumpVelocityMultiplier(float original) {
      if (this == MeteorClient.mc.field_1724) {
         JumpVelocityMultiplierEvent event = (JumpVelocityMultiplierEvent)MeteorClient.EVENT_BUS.post(JumpVelocityMultiplierEvent.get());
         return original * event.multiplier;
      } else {
         return original;
      }
   }

   @Inject(
      method = {"method_5784"},
      at = {@At("HEAD")}
   )
   private void onMove(class_1313 type, class_243 movement, CallbackInfo info) {
      if (this == MeteorClient.mc.field_1724) {
         MeteorClient.EVENT_BUS.post(PlayerMoveEvent.get(type, movement));
      } else {
         MeteorClient.EVENT_BUS.post(EntityMoveEvent.get((class_1297)this, movement));
      }

   }

   @ModifyExpressionValue(
      method = {"method_23326"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_2680;method_26204()Lnet/minecraft/class_2248;"
)}
   )
   private class_2248 modifyVelocityMultiplierBlock(class_2248 original) {
      if (this != MeteorClient.mc.field_1724) {
         return original;
      } else if (original == class_2246.field_10114 && ((NoSlow)Modules.get().get(NoSlow.class)).soulSand()) {
         return class_2246.field_10340;
      } else {
         return original == class_2246.field_21211 && ((NoSlow)Modules.get().get(NoSlow.class)).honeyBlock() ? class_2246.field_10340 : original;
      }
   }

   @ModifyReturnValue(
      method = {"method_5756(Lnet/minecraft/class_1657;)Z"},
      at = {@At("RETURN")}
   )
   private boolean isInvisibleToCanceller(boolean original) {
      if (!Utils.canUpdate()) {
         return original;
      } else {
         ESP esp = (ESP)Modules.get().get(ESP.class);
         return !((NoRender)Modules.get().get(NoRender.class)).noInvisibility() && (!esp.isActive() || esp.shouldSkip((class_1297)this)) ? original : false;
      }
   }

   @Inject(
      method = {"method_5851"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isGlowing(CallbackInfoReturnable<Boolean> info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noGlowing()) {
         info.setReturnValue(false);
      }

   }

   @Inject(
      method = {"method_5871"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetTargetingMargin(CallbackInfoReturnable<Float> info) {
      double v = ((Hitboxes)Modules.get().get(Hitboxes.class)).getEntityValue((class_1297)this);
      if (v != (double)0.0F) {
         info.setReturnValue((float)v);
      }

   }

   @Inject(
      method = {"method_5756"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onIsInvisibleTo(class_1657 player, CallbackInfoReturnable<Boolean> info) {
      if (player == null) {
         info.setReturnValue(false);
      }

   }

   @Inject(
      method = {"method_18376"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getPoseHook(CallbackInfoReturnable<class_4050> info) {
      if (this == MeteorClient.mc.field_1724) {
         if (((ElytraFly)Modules.get().get(ElytraFly.class)).canPacketEfly()) {
            info.setReturnValue(class_4050.field_18077);
         }

      }
   }

   @ModifyReturnValue(
      method = {"method_18376"},
      at = {@At("RETURN")}
   )
   private class_4050 modifyGetPose(class_4050 original) {
      if (this != MeteorClient.mc.field_1724) {
         return original;
      } else {
         return original == class_4050.field_18081 && !MeteorClient.mc.field_1724.method_5715() && ((PlayerEntityAccessor)MeteorClient.mc.field_1724).meteor$canChangeIntoPose(class_4050.field_18076) ? class_4050.field_18076 : original;
      }
   }

   @ModifyReturnValue(
      method = {"method_21750"},
      at = {@At("RETURN")}
   )
   private boolean cancelBounce(boolean original) {
      return ((NoFall)Modules.get().get(NoFall.class)).cancelBounce() || original;
   }

   @Inject(
      method = {"method_5872"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void updateChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
      if (this == MeteorClient.mc.field_1724) {
         Freecam freecam = (Freecam)Modules.get().get(Freecam.class);
         FreeLook freeLook = (FreeLook)Modules.get().get(FreeLook.class);
         if (freecam.isActive()) {
            freecam.changeLookDirection(cursorDeltaX * 0.15, cursorDeltaY * 0.15);
            ci.cancel();
         } else if (Modules.get().isActive(HighwayBuilder.class)) {
            class_4184 camera = MeteorClient.mc.field_1773.method_19418();
            ((ICamera)camera).meteor$setRot((double)camera.method_19330() + cursorDeltaX * 0.15, (double)camera.method_19329() + cursorDeltaY * 0.15);
            ci.cancel();
         } else if (freeLook.cameraMode()) {
            freeLook.cameraYaw += (float)(cursorDeltaX / (double)((Double)freeLook.sensitivity.get()).floatValue());
            freeLook.cameraPitch += (float)(cursorDeltaY / (double)((Double)freeLook.sensitivity.get()).floatValue());
            if (Math.abs(freeLook.cameraPitch) > 90.0F) {
               freeLook.cameraPitch = freeLook.cameraPitch > 0.0F ? 90.0F : -90.0F;
            }

            ci.cancel();
         }

      }
   }
}
