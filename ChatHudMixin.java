package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.mixininterface.IChatHudLine;
import meteordevelopment.meteorclient.mixininterface.IChatHudLineVisible;
import meteordevelopment.meteorclient.mixininterface.IMessageHandler;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.BetterChat;
import net.minecraft.class_2561;
import net.minecraft.class_303;
import net.minecraft.class_310;
import net.minecraft.class_338;
import net.minecraft.class_5481;
import net.minecraft.class_7469;
import net.minecraft.class_7591;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_338.class})
public abstract class ChatHudMixin implements IChatHud {
   @Shadow
   @Final
   class_310 field_2062;
   @Shadow
   @Final
   private List<class_303.class_7590> field_2064;
   @Shadow
   @Final
   private List<class_303> field_2061;
   @Unique
   private BetterChat betterChat;
   @Unique
   private int nextId;

   @Shadow
   public abstract void method_1812(class_2561 var1);

   public void meteor$add(class_2561 message, int id) {
      this.nextId = id;
      this.method_1812(message);
      this.nextId = 0;
   }

   @Inject(
      method = {"method_1815"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/util/List;addFirst(Ljava/lang/Object;)V",
   shift = Shift.AFTER
)}
   )
   private void onAddMessageAfterNewChatHudLineVisible(class_303 message, CallbackInfo ci) {
      ((IChatHudLine)this.field_2064.getFirst()).meteor$setId(this.nextId);
   }

   @Inject(
      method = {"method_58744(Lnet/minecraft/class_303;)V"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/util/List;addFirst(Ljava/lang/Object;)V",
   shift = Shift.AFTER
)}
   )
   private void onAddMessageAfterNewChatHudLine(class_303 message, CallbackInfo ci) {
      ((IChatHudLine)this.field_2061.getFirst()).meteor$setId(this.nextId);
   }

   @ModifyExpressionValue(
      method = {"method_1815"},
      at = {@At(
   value = "NEW",
   target = "(ILnet/minecraft/class_5481;Lnet/minecraft/class_7591;Z)Lnet/minecraft/class_303$class_7590;"
)}
   )
   private class_303.class_7590 onAddMessage_modifyChatHudLineVisible(class_303.class_7590 line, @Local(ordinal = 1) int j) {
      IMessageHandler handler = (IMessageHandler)this.field_2062.method_44714();
      if (handler == null) {
         return line;
      } else {
         IChatHudLineVisible meteorLine = (IChatHudLineVisible)line;
         meteorLine.meteor$setSender(handler.meteor$getSender());
         meteorLine.meteor$setStartOfEntry(j == 0);
         return line;
      }
   }

   @ModifyExpressionValue(
      method = {"method_44811(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V"},
      at = {@At(
   value = "NEW",
   target = "(ILnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)Lnet/minecraft/class_303;"
)}
   )
   private class_303 onAddMessage_modifyChatHudLine(class_303 line) {
      IMessageHandler handler = (IMessageHandler)this.field_2062.method_44714();
      if (handler == null) {
         return line;
      } else {
         ((IChatHudLine)line).meteor$setSender(handler.meteor$getSender());
         return line;
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"method_44811(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V"},
      cancellable = true
   )
   private void onAddMessage(class_2561 message, class_7469 signatureData, class_7591 indicator, CallbackInfo ci, @Local(argsOnly = true) LocalRef<class_2561> messageRef, @Local(argsOnly = true) LocalRef<class_7591> indicatorRef) {
      ReceiveMessageEvent event = (ReceiveMessageEvent)MeteorClient.EVENT_BUS.post(ReceiveMessageEvent.get(message, indicator, this.nextId));
      if (event.isCancelled()) {
         ci.cancel();
      } else {
         this.field_2064.removeIf((msg) -> ((IChatHudLine)msg).meteor$getId() == this.nextId && this.nextId != 0);

         for(int i = this.field_2061.size() - 1; i > -1; --i) {
            if (((IChatHudLine)this.field_2061.get(i)).meteor$getId() == this.nextId && this.nextId != 0) {
               this.field_2061.remove(i);
               this.getBetterChat().removeLine(i);
            }
         }

         if (event.isModified()) {
            messageRef.set(event.getMessage());
            indicatorRef.set(event.getIndicator());
         }
      }

   }

   @ModifyExpressionValue(
      method = {"method_58744(Lnet/minecraft/class_303;)V"},
      at = {@At(
   value = "CONSTANT",
   args = {"intValue=100"}
)}
   )
   private int maxLength(int size) {
      return Modules.get() != null && this.getBetterChat().isLongerChat() ? size + this.betterChat.getExtraChatLines() : size;
   }

   @ModifyExpressionValue(
      method = {"method_1815"},
      at = {@At(
   value = "CONSTANT",
   args = {"intValue=100"}
)}
   )
   private int maxLengthVisible(int size) {
      return Modules.get() != null && this.getBetterChat().isLongerChat() ? size + this.betterChat.getExtraChatLines() : size;
   }

   @ModifyExpressionValue(
      method = {"method_1805(Lnet/minecraft/class_338$class_12233;IIZ)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3532;method_15386(F)I"
)}
   )
   private int onRender_modifyWidth(int width) {
      return this.getBetterChat().modifyChatWidth(width);
   }

   @Inject(
      method = {"method_1815"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_338;method_1819()Z"
)}
   )
   private void onBreakChatMessageLines(class_303 message, CallbackInfo ci, @Local List<class_5481> list) {
      if (Modules.get() != null) {
         this.getBetterChat().lines.addFirst(list.size());
      }
   }

   @Inject(
      method = {"method_58744(Lnet/minecraft/class_303;)V"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/util/List;removeLast()Ljava/lang/Object;"
)}
   )
   private void onRemoveMessage(class_303 message, CallbackInfo ci) {
      if (Modules.get() != null) {
         int extra = this.getBetterChat().isLongerChat() ? this.getBetterChat().getExtraChatLines() : 0;

         for(int size = this.betterChat.lines.size(); size > 100 + extra; --size) {
            this.betterChat.lines.removeLast();
         }

      }
   }

   @Inject(
      method = {"method_1808"},
      at = {@At("HEAD")}
   )
   private void onClear(boolean clearHistory, CallbackInfo ci) {
      this.getBetterChat().lines.clear();
   }

   @Inject(
      method = {"method_44813"},
      at = {@At("HEAD")}
   )
   private void onRefresh(CallbackInfo ci) {
      this.getBetterChat().lines.clear();
   }

   @Unique
   private BetterChat getBetterChat() {
      if (this.betterChat == null) {
         this.betterChat = (BetterChat)Modules.get().get(BetterChat.class);
      }

      return this.betterChat;
   }
}
