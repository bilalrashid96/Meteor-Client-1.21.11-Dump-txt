package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.CanWalkOnFluidEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.HighJump;
import meteordevelopment.meteorclient.systems.modules.movement.Sprint;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightModes;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes.Bounce;
import meteordevelopment.meteorclient.systems.modules.player.NoStatusEffects;
import meteordevelopment.meteorclient.systems.modules.player.OffhandCrash;
import meteordevelopment.meteorclient.systems.modules.render.HandView;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_1268;
import net.minecraft.class_1291;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_3610;
import net.minecraft.class_6880;
import net.minecraft.class_9334;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1309.class})
public abstract class LivingEntityMixin extends class_1297 {
   @Unique
   private boolean previousElytra = false;

   public LivingEntityMixin(class_1299<?> type, class_1937 world) {
      super(type, world);
   }

   @ModifyReturnValue(
      method = {"method_26319"},
      at = {@At("RETURN")}
   )
   private boolean onCanWalkOnFluid(boolean original, class_3610 fluidState) {
      if (this != MeteorClient.mc.field_1724) {
         return original;
      } else {
         CanWalkOnFluidEvent event = (CanWalkOnFluidEvent)MeteorClient.EVENT_BUS.post(CanWalkOnFluidEvent.get(fluidState));
         return event.walkOnFluid;
      }
   }

   @Inject(
      method = {"method_6037"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void spawnItemParticles(class_1799 stack, int count, CallbackInfo info) {
      NoRender noRender = (NoRender)Modules.get().get(NoRender.class);
      if (noRender.noEatParticles() && stack.method_57353().method_57832(class_9334.field_50075)) {
         info.cancel();
      }

   }

   @Inject(
      method = {"method_6116"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onEquipStack(class_1304 slot, class_1799 oldStack, class_1799 newStack, CallbackInfo info) {
      if (this == MeteorClient.mc.field_1724) {
         if (((OffhandCrash)Modules.get().get(OffhandCrash.class)).isAntiCrash()) {
            info.cancel();
         }

      }
   }

   @ModifyVariable(
      method = {"method_23667(Lnet/minecraft/class_1268;Z)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private class_1268 setHand(class_1268 hand) {
      if (this != MeteorClient.mc.field_1724) {
         return hand;
      } else {
         HandView handView = (HandView)Modules.get().get(HandView.class);
         if (handView.isActive()) {
            if (handView.swingMode.get() == HandView.SwingMode.None) {
               return hand;
            } else {
               return handView.swingMode.get() == HandView.SwingMode.Offhand ? class_1268.field_5810 : class_1268.field_5808;
            }
         } else {
            return hand;
         }
      }
   }

   @ModifyExpressionValue(
      method = {"method_6028"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_12126;comp_4976()I"
)}
   )
   private int getHandSwingDuration(int original) {
      if (this != MeteorClient.mc.field_1724) {
         return original;
      } else {
         return ((HandView)Modules.get().get(HandView.class)).isActive() && MeteorClient.mc.field_1690.method_31044().method_31034() ? (Integer)((HandView)Modules.get().get(HandView.class)).swingSpeed.get() : original;
      }
   }

   @ModifyReturnValue(
      method = {"method_6128"},
      at = {@At("RETURN")}
   )
   private boolean isGlidingHook(boolean original) {
      if (this != MeteorClient.mc.field_1724) {
         return original;
      } else {
         return ((ElytraFly)Modules.get().get(ElytraFly.class)).canPacketEfly() ? true : original;
      }
   }

   @Inject(
      method = {"method_6128"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void recastOnLand(CallbackInfoReturnable<Boolean> cir) {
      boolean elytra = (Boolean)cir.getReturnValue();
      ElytraFly elytraFly = (ElytraFly)Modules.get().get(ElytraFly.class);
      if (this.previousElytra && !elytra && elytraFly.isActive() && elytraFly.flightMode.get() == ElytraFlightModes.Bounce) {
         cir.setReturnValue(Bounce.recastElytra(MeteorClient.mc.field_1724));
      }

      this.previousElytra = elytra;
   }

   @ModifyReturnValue(
      method = {"method_6059"},
      at = {@At("RETURN")}
   )
   private boolean hasStatusEffect(boolean original, class_6880<class_1291> effect) {
      if (effect != null && effect.comp_349() != null) {
         return ((NoStatusEffects)Modules.get().get(NoStatusEffects.class)).shouldBlock((class_1291)effect.comp_349()) ? false : original;
      } else {
         return original;
      }
   }

   @ModifyExpressionValue(
      method = {"method_6043"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1309;method_36454()F"
)}
   )
   private float modifyGetYaw(float original) {
      if (this != MeteorClient.mc.field_1724) {
         return original;
      } else if (!((Sprint)Modules.get().get(Sprint.class)).rageSprint()) {
         return original;
      } else {
         float forward = Math.signum(MeteorClient.mc.field_1724.field_6250);
         float strafe = 90.0F * Math.signum(MeteorClient.mc.field_1724.field_6212);
         if (forward != 0.0F) {
            strafe *= forward * 0.5F;
         }

         original -= strafe;
         if (forward < 0.0F) {
            original -= 180.0F;
         }

         return original;
      }
   }

   @ModifyConstant(
      method = {"method_6043"},
      constant = {@Constant(
   floatValue = 1.0E-5F
)}
   )
   private float modifyJumpConstant(float original) {
      if (this != MeteorClient.mc.field_1724) {
         return original;
      } else {
         return !Modules.get().isActive(HighJump.class) ? original : -1.0F;
      }
   }

   @ModifyExpressionValue(
      method = {"method_6043"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1309;method_5624()Z"
)}
   )
   private boolean modifyIsSprinting(boolean original) {
      if (this != MeteorClient.mc.field_1724) {
         return original;
      } else if (!((Sprint)Modules.get().get(Sprint.class)).rageSprint()) {
         return original;
      } else {
         return original && (Math.abs(MeteorClient.mc.field_1724.field_6250) > 1.0E-5F || Math.abs(MeteorClient.mc.field_1724.field_6212) > 1.0E-5F);
      }
   }
}
