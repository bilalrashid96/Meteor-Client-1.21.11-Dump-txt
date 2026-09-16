package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.MixinPlugin;
import meteordevelopment.meteorclient.events.render.GetFovEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.render.RenderAfterWorldEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.mixininterface.IGameRenderer;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.render.Zoom;
import meteordevelopment.meteorclient.systems.modules.world.HighwayBuilder;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.CustomBannerGuiElementRenderer;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import net.minecraft.class_10209;
import net.minecraft.class_11228;
import net.minecraft.class_11239;
import net.minecraft.class_11246;
import net.minecraft.class_1297;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4184;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_4599;
import net.minecraft.class_757;
import net.minecraft.class_758;
import net.minecraft.class_9779;
import net.minecraft.class_758.class_4596;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_757.class})
public abstract class GameRendererMixin implements IGameRenderer {
   @Shadow
   @Final
   private class_310 field_4015;
   @Shadow
   @Final
   private class_4184 field_18765;
   @Unique
   private Renderer3D renderer;
   @Unique
   private Renderer3D depthRenderer;
   @Unique
   private final class_4587 matrices = new class_4587();
   @Shadow
   @Final
   private class_4599 field_20948;
   @Shadow
   @Final
   private class_11228 field_59965;
   @Shadow
   @Final
   private class_758 field_60793;
   @Shadow
   @Final
   class_11246 field_59966;
   @Unique
   private boolean freecamSet = false;

   @Shadow
   public abstract void method_3190(float var1);

   @Shadow
   public abstract void method_3203();

   @Shadow
   protected abstract void method_3186(class_4587 var1, float var2);

   @Shadow
   protected abstract void method_3198(class_4587 var1, float var2);

   @ModifyArg(
      method = {"<init>"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_11228;<init>(Lnet/minecraft/class_11246;Lnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_11659;Lnet/minecraft/class_11684;Ljava/util/List;)V"
)
   )
   private List<class_11239<?>> meteor$addSpecialRenderers(List<class_11239<?>> list) {
      list = new ArrayList(list);
      list.add(new CustomBannerGuiElementRenderer(this.field_20948.method_23000(), this.field_4015.method_72703()));
      return List.of((class_11239[])list.toArray(new class_11239[0]));
   }

   @Inject(
      method = {"method_3188"},
      at = {@At(
   value = "INVOKE_STRING",
   target = "Lnet/minecraft/class_3695;method_15405(Ljava/lang/String;)V",
   args = {"ldc=hand"}
)}
   )
   private void onRenderWorld(class_9779 tickCounter, CallbackInfo ci, @Local(ordinal = 0) Matrix4f projection, @Local(ordinal = 1) Matrix4f position, @Local(ordinal = 0) float tickDelta, @Local class_4587 matrixStack) {
      if (Utils.canUpdate()) {
         class_10209.method_64146().method_15396("meteor-client_render");
         if (this.renderer == null) {
            this.renderer = new Renderer3D(MeteorRenderPipelines.WORLD_COLORED_LINES, MeteorRenderPipelines.WORLD_COLORED);
         }

         if (this.depthRenderer == null) {
            this.depthRenderer = new Renderer3D(MeteorRenderPipelines.WORLD_COLORED_LINES_DEPTH, MeteorRenderPipelines.WORLD_COLORED_DEPTH);
         }

         Render3DEvent event = Render3DEvent.get(matrixStack, this.renderer, this.depthRenderer, tickDelta, this.field_18765.method_71156().field_1352, this.field_18765.method_71156().field_1351, this.field_18765.method_71156().field_1350);
         RenderSystem.getModelViewStack().pushMatrix().mul(position);
         this.matrices.method_22903();
         this.method_3198(this.matrices, this.field_18765.method_55437());
         if ((Boolean)this.field_4015.field_1690.method_42448().method_41753()) {
            this.method_3186(this.matrices, this.field_18765.method_55437());
         }

         Matrix4f inverseBob = (new Matrix4f(this.matrices.method_23760().method_23761())).invert();
         RenderSystem.getModelViewStack().mul(inverseBob);
         this.matrices.method_22909();
         Matrix4f correctedPosition = MixinPlugin.isIrisPresent && RenderUtils.isShaderPackInUse() ? (new Matrix4f(position)).mul(inverseBob) : position;
         RenderUtils.updateScreenCenter(projection, correctedPosition);
         NametagUtils.onRender(position);
         this.renderer.begin();
         this.depthRenderer.begin();
         MeteorClient.EVENT_BUS.post(event);
         this.renderer.render(matrixStack);
         this.depthRenderer.render(matrixStack);
         RenderSystem.getModelViewStack().popMatrix();
         class_10209.method_64146().method_15407();
      }
   }

   @Inject(
      method = {"method_3188"},
      at = {@At("TAIL")}
   )
   private void onRenderWorldTail(CallbackInfo info) {
      MeteorClient.EVENT_BUS.post(RenderAfterWorldEvent.get());
   }

   @Inject(
      method = {"method_3192"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_11228;method_70890(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
   shift = Shift.AFTER
)}
   )
   private void onRenderGui(class_9779 tickCounter, boolean tick, CallbackInfo info) {
      class_437 var5 = this.field_4015.field_1755;
      if (var5 instanceof WidgetScreen widgetScreen) {
         this.field_59966.method_70926();
         int mouseX = (int)this.field_4015.field_1729.method_68879(this.field_4015.method_22683());
         int mouseY = (int)this.field_4015.field_1729.method_68883(this.field_4015.method_22683());
         class_332 context = new class_332(this.field_4015, this.field_59966, mouseX, mouseY);
         widgetScreen.renderCustom(context, mouseX, mouseY, tickCounter.method_60636());
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.field_4015.method_1522().method_30278(), (double)1.0F);
         this.meteor$flushGuiState();
      }

   }

   @Inject(
      method = {"method_3189"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onShowFloatingItem(class_1799 floatingItem, CallbackInfo info) {
      if (floatingItem.method_7909() == class_1802.field_8288 && ((NoRender)Modules.get().get(NoRender.class)).noTotemAnimation()) {
         info.cancel();
      }

   }

   @ModifyExpressionValue(
      method = {"method_3188"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/lang/Math;max(FF)F",
   ordinal = 0
)}
   )
   private float applyCameraTransformationsMathHelperLerpProxy(float original) {
      return ((NoRender)Modules.get().get(NoRender.class)).noNausea() ? 0.0F : original;
   }

   @ModifyReturnValue(
      method = {"method_3196"},
      at = {@At("RETURN")}
   )
   private float modifyFov(float original) {
      return ((GetFovEvent)MeteorClient.EVENT_BUS.post(GetFovEvent.get(original))).fov;
   }

   @Inject(
      method = {"method_3190"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void updateTargetedEntityInvoke(float tickDelta, CallbackInfo info) {
      Freecam freecam = (Freecam)Modules.get().get(Freecam.class);
      boolean highwayBuilder = Modules.get().isActive(HighwayBuilder.class);
      if ((freecam.isActive() || highwayBuilder) && this.field_4015.method_1560() != null && !this.freecamSet) {
         info.cancel();
         class_1297 cameraE = this.field_4015.method_1560();
         double x = cameraE.method_23317();
         double y = cameraE.method_23318();
         double z = cameraE.method_23321();
         double lastX = cameraE.field_6014;
         double lastY = cameraE.field_6036;
         double lastZ = cameraE.field_5969;
         float yaw = cameraE.method_36454();
         float pitch = cameraE.method_36455();
         float lastYaw = cameraE.field_5982;
         float lastPitch = cameraE.field_6004;
         if (highwayBuilder) {
            cameraE.method_36456(this.field_18765.method_19330());
            cameraE.method_36457(this.field_18765.method_19329());
         } else {
            ((IVec3d)cameraE.method_73189()).meteor$set(freecam.pos.x, freecam.pos.y - (double)cameraE.method_18381(cameraE.method_18376()), freecam.pos.z);
            cameraE.field_6014 = freecam.prevPos.x;
            cameraE.field_6036 = freecam.prevPos.y - (double)cameraE.method_18381(cameraE.method_18376());
            cameraE.field_5969 = freecam.prevPos.z;
            cameraE.method_36456(freecam.yaw);
            cameraE.method_36457(freecam.pitch);
            cameraE.field_5982 = freecam.lastYaw;
            cameraE.field_6004 = freecam.lastPitch;
         }

         this.freecamSet = true;
         this.method_3190(tickDelta);
         this.freecamSet = false;
         ((IVec3d)cameraE.method_73189()).meteor$set(x, y, z);
         cameraE.field_6014 = lastX;
         cameraE.field_6036 = lastY;
         cameraE.field_5969 = lastZ;
         cameraE.method_36456(yaw);
         cameraE.method_36457(pitch);
         cameraE.field_5982 = lastYaw;
         cameraE.field_6004 = lastPitch;
      }

   }

   @Inject(
      method = {"method_3172"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderHand(float tickProgress, boolean sleeping, Matrix4f positionMatrix, CallbackInfo ci) {
      if (!((Freecam)Modules.get().get(Freecam.class)).renderHands() || !((Zoom)Modules.get().get(Zoom.class)).renderHands()) {
         ci.cancel();
      }

   }

   public void meteor$flushGuiState() {
      this.field_59965.method_70890(this.field_60793.method_71109(class_4596.field_60101));
      this.field_59965.method_70879();
   }
}
