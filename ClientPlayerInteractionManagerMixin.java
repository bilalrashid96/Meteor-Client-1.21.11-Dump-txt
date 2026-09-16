package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.DropItemsEvent;
import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.entity.player.BlockBreakingCooldownEvent;
import meteordevelopment.meteorclient.events.entity.player.BreakBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractEntityEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractItemEvent;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.mixininterface.IClientPlayerInteractionManager;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.BreakDelay;
import meteordevelopment.meteorclient.systems.modules.player.SpeedMine;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2846;
import net.minecraft.class_3965;
import net.minecraft.class_636;
import net.minecraft.class_638;
import net.minecraft.class_7204;
import net.minecraft.class_746;
import net.minecraft.class_2846.class_2847;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_636.class})
public abstract class ClientPlayerInteractionManagerMixin implements IClientPlayerInteractionManager {
   @Shadow
   private int field_3716;

   @Shadow
   protected abstract void method_2911();

   @Shadow
   public abstract boolean method_2899(class_2338 var1);

   @Shadow
   public abstract void method_41931(class_638 var1, class_7204 var2);

   @Inject(
      method = {"method_2906"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onClickSlot(int syncId, int slotId, int button, class_1713 actionType, class_1657 player, CallbackInfo info) {
      if (actionType == class_1713.field_7795 && slotId >= 0 && slotId < player.field_7512.field_7761.size()) {
         if (((DropItemsEvent)MeteorClient.EVENT_BUS.post(DropItemsEvent.get(((class_1735)player.field_7512.field_7761.get(slotId)).method_7677()))).isCancelled()) {
            info.cancel();
         }
      } else if (slotId == -999 && ((DropItemsEvent)MeteorClient.EVENT_BUS.post(DropItemsEvent.get(player.field_7512.method_34255()))).isCancelled()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"method_2910"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAttackBlock(class_2338 blockPos, class_2350 direction, CallbackInfoReturnable<Boolean> info) {
      if (((StartBreakingBlockEvent)MeteorClient.EVENT_BUS.post(StartBreakingBlockEvent.get(blockPos, direction))).isCancelled()) {
         info.cancel();
      } else {
         SpeedMine sm = (SpeedMine)Modules.get().get(SpeedMine.class);
         class_2680 state = MeteorClient.mc.field_1687.method_8320(blockPos);
         if (!sm.instamine() || !sm.filter(state.method_26204())) {
            return;
         }

         if (state.method_26165(MeteorClient.mc.field_1724, MeteorClient.mc.field_1687, blockPos) > 0.5F) {
            this.method_2899(blockPos);
            this.method_41931(MeteorClient.mc.field_1687, (sequence) -> new class_2846(class_2847.field_12968, blockPos, direction, sequence));
            this.method_41931(MeteorClient.mc.field_1687, (sequence) -> new class_2846(class_2847.field_12973, blockPos, direction, sequence));
            info.setReturnValue(true);
         }
      }

   }

   @Inject(
      method = {"method_2896"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void interactBlock(class_746 player, class_1268 hand, class_3965 hitResult, CallbackInfoReturnable<class_1269> cir) {
      if (((InteractBlockEvent)MeteorClient.EVENT_BUS.post(InteractBlockEvent.get(player.method_6047().method_7960() ? class_1268.field_5810 : hand, hitResult))).isCancelled()) {
         cir.setReturnValue(class_1269.field_5814);
      }

   }

   @Inject(
      method = {"method_2918"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAttackEntity(class_1657 player, class_1297 target, CallbackInfo info) {
      if (((AttackEntityEvent)MeteorClient.EVENT_BUS.post(AttackEntityEvent.get(target))).isCancelled()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"method_2905"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onInteractEntity(class_1657 player, class_1297 entity, class_1268 hand, CallbackInfoReturnable<class_1269> info) {
      if (((InteractEntityEvent)MeteorClient.EVENT_BUS.post(InteractEntityEvent.get(entity, hand))).isCancelled()) {
         info.setReturnValue(class_1269.field_5814);
      }

   }

   @Inject(
      method = {"method_2915"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onDropCreativeStack(class_1799 stack, CallbackInfo info) {
      if (((DropItemsEvent)MeteorClient.EVENT_BUS.post(DropItemsEvent.get(stack))).isCancelled()) {
         info.cancel();
      }

   }

   @Redirect(
      method = {"method_2902"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/class_636;field_3716:I",
   opcode = 181,
   ordinal = 1
)
   )
   private void creativeBreakDelayChange(class_636 interactionManager, int value) {
      BlockBreakingCooldownEvent event = (BlockBreakingCooldownEvent)MeteorClient.EVENT_BUS.post(BlockBreakingCooldownEvent.get(value));
      this.field_3716 = event.cooldown;
   }

   @Redirect(
      method = {"method_2902"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/class_636;field_3716:I",
   opcode = 181,
   ordinal = 2
)
   )
   private void survivalBreakDelayChange(class_636 interactionManager, int value) {
      BlockBreakingCooldownEvent event = (BlockBreakingCooldownEvent)MeteorClient.EVENT_BUS.post(BlockBreakingCooldownEvent.get(value));
      this.field_3716 = event.cooldown;
   }

   @Redirect(
      method = {"method_2910"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/class_636;field_3716:I",
   opcode = 181
)
   )
   private void creativeBreakDelayChange2(class_636 interactionManager, int value) {
      BlockBreakingCooldownEvent event = (BlockBreakingCooldownEvent)MeteorClient.EVENT_BUS.post(BlockBreakingCooldownEvent.get(value));
      this.field_3716 = event.cooldown;
   }

   @ModifyExpressionValue(
      method = {"method_41930"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_2680;method_26165(Lnet/minecraft/class_1657;Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;)F"
)}
   )
   private float modifyBlockBreakingDelta(float original) {
      if (((BreakDelay)Modules.get().get(BreakDelay.class)).preventInstaBreak() && original >= 1.0F) {
         BlockBreakingCooldownEvent event = (BlockBreakingCooldownEvent)MeteorClient.EVENT_BUS.post(BlockBreakingCooldownEvent.get(this.field_3716));
         this.field_3716 = event.cooldown;
         return 0.0F;
      } else {
         return original;
      }
   }

   @Inject(
      method = {"method_2899"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onBreakBlock(class_2338 blockPos, CallbackInfoReturnable<Boolean> info) {
      if (((BreakBlockEvent)MeteorClient.EVENT_BUS.post(BreakBlockEvent.get(blockPos))).isCancelled()) {
         info.setReturnValue(false);
      }

   }

   @Inject(
      method = {"method_2919"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onInteractItem(class_1657 player, class_1268 hand, CallbackInfoReturnable<class_1269> info) {
      InteractItemEvent event = (InteractItemEvent)MeteorClient.EVENT_BUS.post(InteractItemEvent.get(hand));
      if (event.toReturn != null) {
         info.setReturnValue(event.toReturn);
      }

   }

   @Inject(
      method = {"method_2925"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onCancelBlockBreaking(CallbackInfo info) {
      if (BlockUtils.breaking) {
         info.cancel();
      }

   }

   public void meteor$syncSelected() {
      this.method_2911();
   }
}
