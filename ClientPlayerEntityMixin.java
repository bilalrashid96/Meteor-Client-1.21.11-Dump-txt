package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.DropItemsEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerTickMovementEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.EntityControl;
import meteordevelopment.meteorclient.systems.modules.movement.Flight;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import meteordevelopment.meteorclient.systems.modules.movement.Scaffold;
import meteordevelopment.meteorclient.systems.modules.movement.Sneak;
import meteordevelopment.meteorclient.systems.modules.movement.Sprint;
import meteordevelopment.meteorclient.systems.modules.movement.Velocity;
import meteordevelopment.meteorclient.systems.modules.player.LiquidInteract;
import meteordevelopment.meteorclient.systems.modules.player.NoMiningTrace;
import meteordevelopment.meteorclient.systems.modules.player.Portals;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import net.minecraft.class_10185;
import net.minecraft.class_1297;
import net.minecraft.class_1316;
import net.minecraft.class_239;
import net.minecraft.class_3966;
import net.minecraft.class_437;
import net.minecraft.class_638;
import net.minecraft.class_742;
import net.minecraft.class_744;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_746.class})
public abstract class ClientPlayerEntityMixin extends class_742 {
   @Shadow
   public class_744 field_3913;

   public ClientPlayerEntityMixin(class_638 world, GameProfile profile) {
      super(world, profile);
   }

   @Inject(
      method = {"method_7290"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onDropSelectedItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> info) {
      if (((DropItemsEvent)MeteorClient.EVENT_BUS.post(DropItemsEvent.get(this.method_6047()))).isCancelled()) {
         info.setReturnValue(false);
      }

   }

   @ModifyExpressionValue(
      method = {"method_60887"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_310;field_1755:Lnet/minecraft/class_437;",
   opcode = 180
)}
   )
   private class_437 modifyNauseaCurrentScreen(class_437 original) {
      return Modules.get().isActive(Portals.class) ? null : original;
   }

   @ModifyExpressionValue(
      method = {"method_67270"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_6115()Z"
)}
   )
   private boolean redirectUsingItem(boolean isUsingItem) {
      return ((NoSlow)Modules.get().get(NoSlow.class)).items() ? false : isUsingItem;
   }

   @Inject(
      method = {"method_5715"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onIsSneaking(CallbackInfoReturnable<Boolean> info) {
      if (((Scaffold)Modules.get().get(Scaffold.class)).scaffolding()) {
         info.setReturnValue(false);
      }

      if (((Flight)Modules.get().get(Flight.class)).noSneak()) {
         info.setReturnValue(false);
      }

   }

   @Inject(
      method = {"method_20303"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onShouldSlowDown(CallbackInfoReturnable<Boolean> info) {
      if (((NoSlow)Modules.get().get(NoSlow.class)).sneaking()) {
         info.setReturnValue(this.method_20448());
      }

   }

   @Inject(
      method = {"method_30673"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onPushOutOfBlocks(double x, double d, CallbackInfo info) {
      Velocity velocity = (Velocity)Modules.get().get(Velocity.class);
      if (velocity.isActive() && (Boolean)velocity.blocks.get()) {
         info.cancel();
      }

   }

   @ModifyExpressionValue(
      method = {"method_5773"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_744;field_54155:Lnet/minecraft/class_10185;",
   opcode = 180
)}
   )
   private class_10185 isSneaking(class_10185 original) {
      return !((Sneak)Modules.get().get(Sneak.class)).doPacket() && !((NoSlow)Modules.get().get(NoSlow.class)).airStrict() ? original : new class_10185(original.comp_3159(), original.comp_3160(), original.comp_3161(), original.comp_3162(), original.comp_3163(), true, original.comp_3165());
   }

   @Inject(
      method = {"method_6007"},
      at = {@At("HEAD")}
   )
   private void preTickMovement(CallbackInfo ci) {
      MeteorClient.EVENT_BUS.post(PlayerTickMovementEvent.get());
   }

   @ModifyReturnValue(
      method = {"method_3151"},
      at = {@At("RETURN")}
   )
   private float modifyMountJumpStrength(float original) {
      return ((EntityControl)Modules.get().get(EntityControl.class)).maxJump() ? 1.0F : original;
   }

   @Inject(
      method = {"method_45773"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void changeJumpingMount(CallbackInfoReturnable<class_1316> info) {
      if (((EntityControl)Modules.get().get(EntityControl.class)).cancelJump()) {
         info.setReturnValue((Object)null);
      }

   }

   @ModifyReturnValue(
      method = {"method_76763(Lnet/minecraft/class_1297;DDF)Lnet/minecraft/class_239;"},
      at = {@At("RETURN")}
   )
   private static class_239 onUpdateTargetedEntity(class_239 original, @Local class_239 hitResult) {
      if (original instanceof class_3966 ehr) {
         if (((NoMiningTrace)Modules.get().get(NoMiningTrace.class)).canWork(ehr.method_17782()) && hitResult.method_17783() == class_240.field_1332) {
            return hitResult;
         }

         class_1297 var4 = ehr.method_17782();
         if (var4 instanceof FakePlayerEntity fakePlayer) {
            if (fakePlayer.noHit) {
               return hitResult;
            }
         }
      }

      return original;
   }

   @ModifyExpressionValue(
      method = {"method_76763(Lnet/minecraft/class_1297;DDF)Lnet/minecraft/class_239;"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_5745(DFZ)Lnet/minecraft/class_239;"
)}
   )
   private static class_239 modifyRaycastResult(class_239 original, class_1297 entity, double blockInteractionRange, double entityInteractionRange, float tickProgress, @Local(ordinal = 0,argsOnly = true) double maxDistance) {
      if (!Modules.get().isActive(LiquidInteract.class)) {
         return original;
      } else {
         return original.method_17783() != class_240.field_1333 ? original : entity.method_5745(maxDistance, tickProgress, true);
      }
   }

   @ModifyExpressionValue(
      method = {"method_48300"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_744;method_20622()Z"
)}
   )
   private boolean modifyIsWalking(boolean original) {
      if (!((Sprint)Modules.get().get(Sprint.class)).rageSprint()) {
         return original;
      } else {
         float forwards = Math.abs(this.field_6250);
         float sideways = Math.abs(this.field_6212);
         return this.method_5869() ? forwards > 1.0E-5F || sideways > 1.0E-5F : (double)forwards > 0.8 || (double)sideways > 0.8;
      }
   }

   @ModifyExpressionValue(
      method = {"method_6007"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_744;method_20622()Z"
)}
   )
   private boolean modifyMovement(boolean original) {
      if (!((Sprint)Modules.get().get(Sprint.class)).rageSprint()) {
         return original;
      } else {
         return Math.abs(this.field_6212) > 1.0E-5F || Math.abs(this.field_6250) > 1.0E-5F;
      }
   }

   @WrapWithCondition(
      method = {"method_6007"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_5728(Z)V",
   ordinal = 3
)}
   )
   private boolean wrapSetSprinting(class_746 instance, boolean b) {
      Sprint s = (Sprint)Modules.get().get(Sprint.class);
      return !s.rageSprint() || s.unsprintInWater() && this.method_5799();
   }

   @Inject(
      method = {"method_3136"},
      at = {@At("HEAD")}
   )
   private void onSendMovementPacketsHead(CallbackInfo info) {
      MeteorClient.EVENT_BUS.post(SendMovementPacketsEvent.Pre.get());
   }

   @Inject(
      method = {"method_5773"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_634;method_52787(Lnet/minecraft/class_2596;)V",
   ordinal = 1
)}
   )
   private void onTickHasVehicleBeforeSendPackets(CallbackInfo info) {
      MeteorClient.EVENT_BUS.post(SendMovementPacketsEvent.Pre.get());
   }

   @Inject(
      method = {"method_3136"},
      at = {@At("TAIL")}
   )
   private void onSendMovementPacketsTail(CallbackInfo info) {
      MeteorClient.EVENT_BUS.post(SendMovementPacketsEvent.Post.get());
   }

   @Inject(
      method = {"method_5773"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_634;method_52787(Lnet/minecraft/class_2596;)V",
   ordinal = 1,
   shift = Shift.AFTER
)}
   )
   private void onTickHasVehicleAfterSendPackets(CallbackInfo info) {
      MeteorClient.EVENT_BUS.post(SendMovementPacketsEvent.Post.get());
   }
}
