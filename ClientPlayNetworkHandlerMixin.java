package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.events.entity.EntityDestroyEvent;
import meteordevelopment.meteorclient.events.entity.player.PickItemsEvent;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.events.packets.ContainerSlotUpdateEvent;
import meteordevelopment.meteorclient.events.packets.InventoryEvent;
import meteordevelopment.meteorclient.events.packets.PlaySoundPacketEvent;
import meteordevelopment.meteorclient.events.world.ChunkDataEvent;
import meteordevelopment.meteorclient.mixininterface.IExplosionS2CPacket;
import meteordevelopment.meteorclient.pathing.BaritoneUtils;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Velocity;
import meteordevelopment.meteorclient.systems.modules.player.NoRotate;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_243;
import net.minecraft.class_2535;
import net.minecraft.class_2604;
import net.minecraft.class_2649;
import net.minecraft.class_2653;
import net.minecraft.class_2664;
import net.minecraft.class_2672;
import net.minecraft.class_2678;
import net.minecraft.class_2708;
import net.minecraft.class_2716;
import net.minecraft.class_2767;
import net.minecraft.class_2775;
import net.minecraft.class_2818;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_8588;
import net.minecraft.class_8673;
import net.minecraft.class_8675;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_634.class})
public abstract class ClientPlayNetworkHandlerMixin extends class_8673 {
   @Shadow
   private class_638 field_3699;

   protected ClientPlayNetworkHandlerMixin(class_310 client, class_2535 connection, class_8675 connectionState) {
      super(client, connection, connectionState);
   }

   @Inject(
      method = {"method_11112"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onEntitySpawn(class_2604 packet, CallbackInfo info) {
      if (packet != null && packet.method_11169() != null && ((NoRender)Modules.get().get(NoRender.class)).noEntity(packet.method_11169()) && ((NoRender)Modules.get().get(NoRender.class)).getDropSpawnPacket()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"method_11120"},
      at = {@At("HEAD")}
   )
   private void onGameJoinHead(class_2678 packet, CallbackInfo info, @Share("worldNotNull") LocalBooleanRef worldNotNull) {
      worldNotNull.set(this.field_3699 != null);
   }

   @Inject(
      method = {"method_11120"},
      at = {@At("TAIL")}
   )
   private void onGameJoinTail(class_2678 packet, CallbackInfo info, @Share("worldNotNull") LocalBooleanRef worldNotNull) {
      if (worldNotNull.get()) {
         MeteorClient.EVENT_BUS.post(GameLeftEvent.get());
      }

      MeteorClient.EVENT_BUS.post(GameJoinedEvent.get());
   }

   @Inject(
      method = {"method_52798"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_2600;method_11074(Lnet/minecraft/class_2596;Lnet/minecraft/class_2547;Lnet/minecraft/class_11980;)V",
   shift = Shift.AFTER
)}
   )
   private void onEnterReconfiguration(class_8588 packet, CallbackInfo info) {
      MeteorClient.EVENT_BUS.post(GameLeftEvent.get());
   }

   @Inject(
      method = {"method_11146"},
      at = {@At("HEAD")}
   )
   private void onPlaySound(class_2767 packet, CallbackInfo info) {
      MeteorClient.EVENT_BUS.post(PlaySoundPacketEvent.get(packet));
   }

   @Inject(
      method = {"method_11128"},
      at = {@At("TAIL")}
   )
   private void onChunkData(class_2672 packet, CallbackInfo info) {
      class_2818 chunk = this.field_45588.field_1687.method_8497(packet.method_11523(), packet.method_11524());
      MeteorClient.EVENT_BUS.post(new ChunkDataEvent(chunk));
   }

   @Inject(
      method = {"method_11109"},
      at = {@At("TAIL")}
   )
   private void onContainerSlotUpdate(class_2653 packet, CallbackInfo info) {
      MeteorClient.EVENT_BUS.post(ContainerSlotUpdateEvent.get(packet));
   }

   @Inject(
      method = {"method_11153"},
      at = {@At("TAIL")}
   )
   private void onInventory(class_2649 packet, CallbackInfo info) {
      MeteorClient.EVENT_BUS.post(InventoryEvent.get(packet));
   }

   @Inject(
      method = {"method_11095"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_2716;method_36548()Lit/unimi/dsi/fastutil/ints/IntList;"
)}
   )
   private void onEntitiesDestroy(class_2716 packet, CallbackInfo ci) {
      IntListIterator var3 = packet.method_36548().iterator();

      while(var3.hasNext()) {
         int id = (Integer)var3.next();
         MeteorClient.EVENT_BUS.post(EntityDestroyEvent.get(this.field_45588.field_1687.method_8469(id)));
      }

   }

   @Inject(
      method = {"method_11124"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_2600;method_11074(Lnet/minecraft/class_2596;Lnet/minecraft/class_2547;Lnet/minecraft/class_11980;)V",
   shift = Shift.AFTER
)}
   )
   private void onExplosionVelocity(class_2664 packet, CallbackInfo ci) {
      Velocity velocity = (Velocity)Modules.get().get(Velocity.class);
      if ((Boolean)velocity.explosions.get()) {
         IExplosionS2CPacket explosionPacket = (IExplosionS2CPacket)packet;
         explosionPacket.meteor$setVelocityX((float)(((class_243)packet.comp_2884().orElse(class_243.field_1353)).field_1352 * velocity.getHorizontal(velocity.explosionsHorizontal)));
         explosionPacket.meteor$setVelocityY((float)(((class_243)packet.comp_2884().orElse(class_243.field_1353)).field_1351 * velocity.getVertical(velocity.explosionsVertical)));
         explosionPacket.meteor$setVelocityZ((float)(((class_243)packet.comp_2884().orElse(class_243.field_1353)).field_1350 * velocity.getHorizontal(velocity.explosionsHorizontal)));
      }
   }

   @Inject(
      method = {"method_11150"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_638;method_8469(I)Lnet/minecraft/class_1297;",
   ordinal = 0
)}
   )
   private void onItemPickupAnimation(class_2775 packet, CallbackInfo info) {
      class_1297 itemEntity = this.field_45588.field_1687.method_8469(packet.method_11915());
      class_1297 entity = this.field_45588.field_1687.method_8469(packet.method_11912());
      if (itemEntity instanceof class_1542 && entity == this.field_45588.field_1724) {
         MeteorClient.EVENT_BUS.post(PickItemsEvent.get(((class_1542)itemEntity).method_6983(), packet.method_11913()));
      }

   }

   @Inject(
      method = {"method_11157"},
      at = {@At("HEAD")}
   )
   private void onPlayerPositionLookHead(class_2708 packet, CallbackInfo ci, @Share("noRotateYaw") LocalFloatRef yawRef, @Share("noRotatePitch") LocalFloatRef pitchRef) {
      NoRotate noRotate = (NoRotate)Modules.get().get(NoRotate.class);
      if (noRotate.isActive() && this.field_45588.field_1724 != null) {
         yawRef.set(this.field_45588.field_1724.method_36454());
         pitchRef.set(this.field_45588.field_1724.method_36455());
      }
   }

   @Inject(
      method = {"method_11157"},
      at = {@At("RETURN")}
   )
   private void onPlayerPositionLookReturn(class_2708 packet, CallbackInfo ci, @Share("noRotateYaw") LocalFloatRef yawRef, @Share("noRotatePitch") LocalFloatRef pitchRef) {
      NoRotate noRotate = (NoRotate)Modules.get().get(NoRotate.class);
      if (noRotate.isActive() && this.field_45588.field_1724 != null) {
         float savedYaw = yawRef.get();
         float savedPitch = pitchRef.get();
         this.field_45588.field_1724.method_36456(savedYaw + 1.0E-6F);
         this.field_45588.field_1724.method_36457(savedPitch + 1.0E-6F);
         this.field_45588.field_1724.field_6241 = savedYaw;
         this.field_45588.field_1724.field_6283 = savedYaw;
      }
   }

   @Inject(
      method = {"method_45729"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSendChatMessage(String message, CallbackInfo ci, @Local(argsOnly = true) LocalRef<String> messageRef) {
      if (message.startsWith(Config.get().prefix.get()) || BaritoneUtils.IS_AVAILABLE && message.startsWith(BaritoneUtils.getPrefix())) {
         if (message.startsWith(Config.get().prefix.get())) {
            try {
               Commands.dispatch(message.substring(((String)Config.get().prefix.get()).length()));
            } catch (CommandSyntaxException e) {
               ChatUtils.error(e.getMessage());
            }

            this.field_45588.field_1705.method_1743().method_1803(message);
            ci.cancel();
         }

      } else {
         SendMessageEvent event = (SendMessageEvent)MeteorClient.EVENT_BUS.post(SendMessageEvent.get(message));
         if (!event.isCancelled()) {
            messageRef.set(event.message);
         } else {
            ci.cancel();
         }

      }
   }
}
