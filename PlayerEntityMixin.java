package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.DropItemsEvent;
import meteordevelopment.meteorclient.events.entity.player.ClipAtLedgeEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Flight;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import meteordevelopment.meteorclient.systems.modules.movement.Sprint;
import meteordevelopment.meteorclient.systems.modules.player.Reach;
import meteordevelopment.meteorclient.systems.modules.player.SpeedMine;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1656;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3965;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1657.class})
public abstract class PlayerEntityMixin extends class_1309 {
   @Shadow
   public abstract class_1656 method_31549();

   protected PlayerEntityMixin(class_1299<? extends class_1309> entityType, class_1937 world) {
      super(entityType, world);
   }

   @Inject(
      method = {"method_21825"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void clipAtLedge(CallbackInfoReturnable<Boolean> info) {
      if (this.method_73183().method_8608()) {
         ClipAtLedgeEvent event = (ClipAtLedgeEvent)MeteorClient.EVENT_BUS.post(ClipAtLedgeEvent.get());
         if (event.isSet()) {
            info.setReturnValue(event.isClip());
         }

      }
   }

   @Inject(
      method = {"method_7328"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onDropItem(class_1799 stack, boolean retainOwnership, CallbackInfoReturnable<class_1542> cir) {
      if (this.method_73183().method_8608() && !stack.method_7960() && ((DropItemsEvent)MeteorClient.EVENT_BUS.post(DropItemsEvent.get(stack))).isCancelled()) {
         cir.setReturnValue((Object)null);
      }

   }

   @Inject(
      method = {"method_7325"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onIsSpectator(CallbackInfoReturnable<Boolean> info) {
      if (MeteorClient.mc.method_1562() == null) {
         info.setReturnValue(false);
      }

   }

   @Inject(
      method = {"method_68878"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onIsCreative(CallbackInfoReturnable<Boolean> info) {
      if (MeteorClient.mc.method_1562() == null) {
         info.setReturnValue(false);
      }

   }

   @ModifyReturnValue(
      method = {"method_7351"},
      at = {@At("RETURN")}
   )
   public float onGetBlockBreakingSpeed(float breakSpeed, class_2680 block) {
      if (!this.method_73183().method_8608()) {
         return breakSpeed;
      } else {
         SpeedMine speedMine = (SpeedMine)Modules.get().get(SpeedMine.class);
         if (speedMine.isActive() && speedMine.mode.get() == SpeedMine.Mode.Normal && speedMine.filter(block.method_26204())) {
            float breakSpeedMod = (float)((double)breakSpeed * (Double)speedMine.modifier.get());
            class_239 var6 = MeteorClient.mc.field_1765;
            if (var6 instanceof class_3965) {
               class_3965 bhr = (class_3965)var6;
               class_2338 pos = bhr.method_17777();
               return !((Double)speedMine.modifier.get() < (double)1.0F) && BlockUtils.canInstaBreak(pos, breakSpeed) != BlockUtils.canInstaBreak(pos, breakSpeedMod) ? 0.9F / BlockUtils.calcBlockBreakingDelta2(pos, 1.0F) : breakSpeedMod;
            } else {
               return breakSpeed;
            }
         } else {
            return breakSpeed;
         }
      }
   }

   @ModifyReturnValue(
      method = {"method_6029"},
      at = {@At("RETURN")}
   )
   private float onGetMovementSpeed(float original) {
      if (!this.method_73183().method_8608()) {
         return original;
      } else if (!((NoSlow)Modules.get().get(NoSlow.class)).slowness()) {
         return original;
      } else {
         float walkSpeed = this.method_31549().method_7253();
         if (original < walkSpeed) {
            return this.method_5624() ? (float)((double)walkSpeed * 1.300000011920929) : walkSpeed;
         } else {
            return original;
         }
      }
   }

   @Inject(
      method = {"method_49484"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetOffGroundSpeed(CallbackInfoReturnable<Float> info) {
      if (this.method_73183().method_8608()) {
         float speed = ((Flight)Modules.get().get(Flight.class)).getOffGroundSpeed();
         if (speed != -1.0F) {
            info.setReturnValue(speed);
         }

      }
   }

   @WrapWithCondition(
      method = {"method_75122"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1657;method_18799(Lnet/minecraft/class_243;)V"
)}
   )
   private boolean keepSprint$setVelocity(class_1657 instance, class_243 vec3d) {
      return ((Sprint)Modules.get().get(Sprint.class)).stopSprinting();
   }

   @WrapWithCondition(
      method = {"method_75122"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1657;method_5728(Z)V"
)}
   )
   private boolean keepSprint$setSprinting(class_1657 instance, boolean b) {
      return ((Sprint)Modules.get().get(Sprint.class)).stopSprinting();
   }

   @ModifyReturnValue(
      method = {"method_55754"},
      at = {@At("RETURN")}
   )
   private double modifyBlockInteractionRange(double original) {
      return Math.max((double)0.0F, original + ((Reach)Modules.get().get(Reach.class)).blockReach());
   }

   @ModifyReturnValue(
      method = {"method_55755"},
      at = {@At("RETURN")}
   )
   private double modifyEntityInteractionRange(double original) {
      return Math.max((double)0.0F, original + ((Reach)Modules.get().get(Reach.class)).entityReach());
   }
}
