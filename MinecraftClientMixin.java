package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.concurrent.CompletableFuture;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.DoAttackEvent;
import meteordevelopment.meteorclient.events.entity.player.DoItemUseEvent;
import meteordevelopment.meteorclient.events.entity.player.ItemUseCrosshairTargetEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.game.ResolutionChangedEvent;
import meteordevelopment.meteorclient.events.game.ResourcePacksReloadedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.mixininterface.IMinecraftClient;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.InventoryTweaks;
import meteordevelopment.meteorclient.systems.modules.movement.GUIMove;
import meteordevelopment.meteorclient.systems.modules.player.FastUse;
import meteordevelopment.meteorclient.systems.modules.player.Multitask;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import meteordevelopment.meteorclient.systems.modules.world.HighwayBuilder;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.CPSUtils;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.network.OnlinePlayers;
import net.minecraft.class_10209;
import net.minecraft.class_1041;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_239;
import net.minecraft.class_276;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_312;
import net.minecraft.class_315;
import net.minecraft.class_437;
import net.minecraft.class_636;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.jetbrains.annotations.Nullable;
import org.meteordev.starscript.Script;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {class_310.class},
   priority = 1001
)
public abstract class MinecraftClientMixin implements IMinecraftClient {
   @Unique
   private boolean doItemUseCalled;
   @Unique
   private boolean rightClick;
   @Unique
   private long lastTime;
   @Unique
   private boolean firstFrame;
   @Shadow
   public class_638 field_1687;
   @Shadow
   @Final
   public class_312 field_1729;
   @Shadow
   @Final
   private class_1041 field_1704;
   @Shadow
   public class_437 field_1755;
   @Shadow
   @Final
   public class_315 field_1690;
   @Shadow
   public @Nullable class_636 field_1761;
   @Shadow
   private int field_1752;
   @Shadow
   public @Nullable class_746 field_1724;
   @Shadow
   @Final
   @Mutable
   private class_276 field_1689;
   @Unique
   private boolean isBreaking = false;

   @Shadow
   protected abstract void method_1583();

   @Shadow
   protected abstract void method_1590(boolean var1);

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void onInit(CallbackInfo info) {
      MeteorClient.INSTANCE.onInitializeClient();
      this.firstFrame = true;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"method_1574"}
   )
   private void onPreTick(CallbackInfo info) {
      OnlinePlayers.update();
      this.doItemUseCalled = false;
      class_10209.method_64146().method_15396("meteor-client_pre_update");
      MeteorClient.EVENT_BUS.post(TickEvent.Pre.get());
      class_10209.method_64146().method_15407();
      if (this.rightClick && !this.doItemUseCalled && this.field_1761 != null) {
         this.method_1583();
      }

      this.rightClick = false;
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"method_1574"}
   )
   private void onTick(CallbackInfo info) {
      class_10209.method_64146().method_15396("meteor-client_post_update");
      MeteorClient.EVENT_BUS.post(TickEvent.Post.get());
      class_10209.method_64146().method_15407();
   }

   @Inject(
      method = {"method_1536"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAttack(CallbackInfoReturnable<Boolean> cir) {
      CPSUtils.onAttack();
      if (((DoAttackEvent)MeteorClient.EVENT_BUS.post(DoAttackEvent.get())).isCancelled()) {
         cir.cancel();
      }

   }

   @Inject(
      method = {"method_1583"},
      at = {@At("HEAD")}
   )
   private void onDoItemUse(CallbackInfo info) {
      this.doItemUseCalled = true;
   }

   @Inject(
      method = {"method_18096(Lnet/minecraft/class_437;ZZ)V"},
      at = {@At("HEAD")}
   )
   private void onDisconnect(class_437 screen, boolean transferring, boolean stopSound, CallbackInfo info) {
      if (this.field_1687 != null) {
         MeteorClient.EVENT_BUS.post(GameLeftEvent.get());
      }

   }

   @Inject(
      method = {"method_1507"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSetScreen(class_437 screen, CallbackInfo info) {
      if (screen instanceof WidgetScreen) {
         screen.method_16014(this.field_1729.method_1603() * (double)this.field_1704.method_4495(), this.field_1729.method_1604() * (double)this.field_1704.method_4495());
      }

      OpenScreenEvent event = OpenScreenEvent.get(screen);
      MeteorClient.EVENT_BUS.post(event);
      if (event.isCancelled()) {
         info.cancel();
      }

   }

   @WrapOperation(
      method = {"method_1507"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_304;method_1437()V"
)}
   )
   private void onSetScreenKeyBindingUnpressAll(Operation<Void> op) {
      Modules modules = Modules.get();
      if (modules == null) {
         op.call(new Object[0]);
      } else {
         GUIMove guimove = (GUIMove)modules.get(GUIMove.class);
         if (guimove != null && guimove.isActive() && !guimove.skip()) {
            class_315 options = MeteorClient.mc.field_1690;

            for(class_304 kb : KeyBindingAccessor.getKeysById().values()) {
               if (kb != options.field_1894 && kb != options.field_1913 && kb != options.field_1849 && kb != options.field_1881 && (!(Boolean)guimove.sneak.get() || kb != options.field_1832) && (!(Boolean)guimove.sprint.get() || kb != options.field_1867) && (!(Boolean)guimove.jump.get() || kb != options.field_1903)) {
                  ((KeyBindingAccessor)kb).meteor$invokeReset();
               }
            }

         } else {
            op.call(new Object[0]);
         }
      }
   }

   @Inject(
      method = {"method_1583"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1799;method_45435(Lnet/minecraft/class_7699;)Z"
)}
   )
   private void onDoItemUseHand(CallbackInfo ci, @Local class_1799 itemStack) {
      FastUse fastUse = (FastUse)Modules.get().get(FastUse.class);
      if (fastUse.isActive()) {
         this.field_1752 = fastUse.getItemUseCooldown(itemStack);
      }

   }

   @Inject(
      method = {"method_1583"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1268;values()[Lnet/minecraft/class_1268;"
)},
      cancellable = true
   )
   private void onDoItemUseBeforeHands(CallbackInfo ci) {
      if (((DoItemUseEvent)MeteorClient.EVENT_BUS.post(DoItemUseEvent.get())).isCancelled()) {
         ci.cancel();
      }

   }

   @ModifyExpressionValue(
      method = {"method_1583"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_310;field_1765:Lnet/minecraft/class_239;",
   ordinal = 1
)}
   )
   private class_239 doItemUseMinecraftClientCrosshairTargetProxy(class_239 original) {
      return ((ItemUseCrosshairTargetEvent)MeteorClient.EVENT_BUS.post(ItemUseCrosshairTargetEvent.get(original))).target;
   }

   @ModifyReturnValue(
      method = {"method_36561(ZLnet/minecraft/class_310$class_8764;)Ljava/util/concurrent/CompletableFuture;"},
      at = {@At("RETURN")}
   )
   private CompletableFuture<Void> onReloadResourcesNewCompletableFuture(CompletableFuture<Void> original) {
      return original.thenRun(() -> MeteorClient.EVENT_BUS.post(ResourcePacksReloadedEvent.get()));
   }

   @ModifyArg(
      method = {"method_24288"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1041;method_24286(Ljava/lang/String;)V"
)
   )
   private String setTitle(String original) {
      if (Config.get() != null && (Boolean)Config.get().customWindowTitle.get()) {
         String customTitle = Config.get().customWindowTitleText.get();
         Script script = MeteorStarscript.compile(customTitle);
         if (script != null) {
            String title = MeteorStarscript.run(script);
            if (title != null) {
               customTitle = title;
            }
         }

         return customTitle;
      } else {
         return original;
      }
   }

   @WrapWithCondition(
      method = {"method_1508"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_636;method_2897(Lnet/minecraft/class_1657;)V"
)}
   )
   private boolean wrapStopUsing(class_636 instance, class_1657 player) {
      return this.HB$stopUsingItem();
   }

   @Unique
   private boolean HB$stopUsingItem() {
      HighwayBuilder b = (HighwayBuilder)Modules.get().get(HighwayBuilder.class);
      return !b.isActive() || !b.drawingBow;
   }

   @Inject(
      method = {"method_15993"},
      at = {@At("TAIL")}
   )
   private void onResolutionChanged(CallbackInfo info) {
      MeteorClient.EVENT_BUS.post(ResolutionChangedEvent.get());
   }

   @Inject(
      method = {"method_1523"},
      at = {@At("HEAD")}
   )
   private void onRender(CallbackInfo info) {
      long time = System.currentTimeMillis();
      if (this.firstFrame) {
         this.lastTime = time;
         this.firstFrame = false;
      }

      Utils.frameTime = (double)(time - this.lastTime) / (double)1000.0F;
      this.lastTime = time;
   }

   @ModifyExpressionValue(
      method = {"method_1583"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_636;method_2923()Z"
)}
   )
   private boolean doItemUseModifyIsBreakingBlock(boolean original) {
      return !Modules.get().isActive(Multitask.class) && original;
   }

   @ModifyExpressionValue(
      method = {"method_1590"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_6115()Z"
)}
   )
   private boolean handleBlockBreakingModifyIsUsingItem(boolean original) {
      return !Modules.get().isActive(Multitask.class) && original;
   }

   @ModifyExpressionValue(
      method = {"method_1508"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_6115()Z",
   ordinal = 0
)}
   )
   private boolean handleInputEventsModifyIsUsingItem(boolean original) {
      return !((Multitask)Modules.get().get(Multitask.class)).attackingEntities() && original;
   }

   @Inject(
      method = {"method_1508"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_6115()Z",
   ordinal = 0,
   shift = Shift.BEFORE
)}
   )
   private void handleInputEventsInjectStopUsingItem(CallbackInfo info) {
      if (((Multitask)Modules.get().get(Multitask.class)).attackingEntities() && this.field_1724.method_6115()) {
         if (!this.field_1690.field_1904.method_1434() && this.HB$stopUsingItem()) {
            this.field_1761.method_2897(this.field_1724);
         }

         while(this.field_1690.field_1904.method_1436()) {
         }
      }

   }

   @ModifyReturnValue(
      method = {"method_27022"},
      at = {@At("RETURN")}
   )
   private boolean hasOutlineModifyIsOutline(boolean original, class_1297 entity) {
      ESP esp = (ESP)Modules.get().get(ESP.class);
      if (esp == null) {
         return original;
      } else if (esp.isGlow() && !esp.shouldSkip(entity)) {
         return esp.getColor(entity) != null || original;
      } else {
         return original;
      }
   }

   @WrapWithCondition(
      method = {"method_1574"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_310;method_1508()V"
)}
   )
   private boolean wrapHandleInputEvents(class_310 instance) {
      return !((InventoryTweaks)Modules.get().get(InventoryTweaks.class)).frameInput();
   }

   @WrapWithCondition(
      method = {"method_1508"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_310;method_1590(Z)V"
)}
   )
   private boolean wrapHandleBlockBreaking(class_310 instance, boolean breaking) {
      this.isBreaking = breaking;
      return !((InventoryTweaks)Modules.get().get(InventoryTweaks.class)).frameInput();
   }

   @Inject(
      method = {"method_1574"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_310;method_1508()V",
   shift = Shift.AFTER
)}
   )
   private void afterHandleInputEvents(CallbackInfo ci) {
      if (((InventoryTweaks)Modules.get().get(InventoryTweaks.class)).frameInput()) {
         this.method_1590(this.isBreaking);
         this.isBreaking = false;
      }
   }

   public void meteor$rightClick() {
      this.rightClick = true;
   }

   public void meteor$setFramebuffer(class_276 framebuffer) {
      this.field_1689 = framebuffer;
   }
}
