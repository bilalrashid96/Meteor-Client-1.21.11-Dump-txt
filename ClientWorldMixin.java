package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_1297;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_638;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_638.class})
public abstract class ClientWorldMixin {
   @Shadow
   public abstract @Nullable class_1297 method_8469(int var1);

   @Inject(
      method = {"method_53875"},
      at = {@At("TAIL")}
   )
   private void onAddEntity(class_1297 entity, CallbackInfo info) {
      if (entity != null) {
         MeteorClient.EVENT_BUS.post(EntityAddedEvent.get(entity));
      }

   }

   @Inject(
      method = {"method_2945"},
      at = {@At("HEAD")}
   )
   private void onRemoveEntity(int entityId, class_1297.class_5529 removalReason, CallbackInfo info) {
      if (this.method_8469(entityId) != null) {
         MeteorClient.EVENT_BUS.post(EntityRemovedEvent.get(this.method_8469(entityId)));
      }

   }

   @ModifyArgs(
      method = {"method_2941"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_638;method_2943(IIIILnet/minecraft/class_5819;Lnet/minecraft/class_2248;Lnet/minecraft/class_2338$class_2339;)V"
)
   )
   private void doRandomBlockDisplayTicks(Args args) {
      if (((NoRender)Modules.get().get(NoRender.class)).noBarrierInvis()) {
         args.set(5, class_2246.field_10499);
      }

   }

   @Inject(
      method = {"method_31595"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAddBlockBreakParticles(class_2338 blockPos, class_2680 state, CallbackInfo info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noBlockBreakParticles()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"method_74254"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAddBlockBreakingParticles(class_2338 blockPos, class_2350 direction, CallbackInfo info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noBlockBreakParticles()) {
         info.cancel();
      }

   }
}
