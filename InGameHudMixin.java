package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.mixininterface.IGameRenderer;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.BetterChat;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_10209;
import net.minecraft.class_1297;
import net.minecraft.class_266;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_329.class})
public abstract class InGameHudMixin {
   @Shadow
   public abstract void method_1747();

   @Inject(
      method = {"method_1753"},
      at = {@At("TAIL")}
   )
   private void onRender(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
      ((IGameRenderer)MeteorClient.mc.field_1773).meteor$flushGuiState();
      context.method_71048();
      class_10209.method_64146().method_15396("meteor-client_render_2d");
      Utils.unscaledProjection();
      MeteorClient.EVENT_BUS.post(Render2DEvent.get(context, context.method_51421(), context.method_51421(), tickCounter.method_60637(true)));
      context.method_71048();
      Utils.scaledProjection();
      class_10209.method_64146().method_15407();
   }

   @Inject(
      method = {"method_1765"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderStatusEffectOverlay(CallbackInfo info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noPotionIcons()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"method_1746"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderPortalOverlay(class_332 context, float nauseaStrength, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noPortalOverlay()) {
         ci.cancel();
      }

   }

   @ModifyArgs(
      method = {"method_55798"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_329;method_31977(Lnet/minecraft/class_332;Lnet/minecraft/class_2960;F)V",
   ordinal = 0
)
   )
   private void onRenderPumpkinOverlay(Args args) {
      if (((NoRender)Modules.get().get(NoRender.class)).noPumpkinOverlay()) {
         args.set(2, 0.0F);
      }

   }

   @ModifyArgs(
      method = {"method_55798"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_329;method_31977(Lnet/minecraft/class_332;Lnet/minecraft/class_2960;F)V",
   ordinal = 1
)
   )
   private void onRenderPowderedSnowOverlay(Args args) {
      if (((NoRender)Modules.get().get(NoRender.class)).noPowderedSnowOverlay()) {
         args.set(2, 0.0F);
      }

   }

   @Inject(
      method = {"method_1735"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderVignetteOverlay(class_332 context, class_1297 entity, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noVignette()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_1757(Lnet/minecraft/class_332;Lnet/minecraft/class_266;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderScoreboardSidebar(class_332 context, class_266 objective, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noScoreboard()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_55803(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderScoreboardSidebar(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noScoreboard()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_32598"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderSpyglassOverlay(class_332 context, float scale, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noSpyglassOverlay()) {
         ci.cancel();
      }

   }

   @ModifyExpressionValue(
      method = {"method_1736"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_5498;method_31034()Z"
)}
   )
   private boolean alwaysRenderCrosshairInFreecam(boolean firstPerson) {
      return Modules.get().isActive(Freecam.class) || firstPerson;
   }

   @Inject(
      method = {"method_1736"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderCrosshair(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noCrosshair()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_55801"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderTitle(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noTitle()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_1749"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderHeldItemTooltip(class_332 context, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noHeldItemName()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_1747"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_338;method_1808(Z)V"
)},
      cancellable = true
   )
   private void onClear(CallbackInfo info) {
      if (((BetterChat)Modules.get().get(BetterChat.class)).keepHistory()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"method_61980"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderNausea(class_332 context, float distortionStrength, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noNausea()) {
         ci.cancel();
      }

   }
}
