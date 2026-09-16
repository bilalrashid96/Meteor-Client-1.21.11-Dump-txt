package meteordevelopment.meteorclient.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import meteordevelopment.meteorclient.mixininterface.IGpuDevice;
import meteordevelopment.meteorclient.mixininterface.IRenderPipeline;
import net.minecraft.class_10860;
import net.minecraft.class_10865;
import org.lwjgl.opengl.GL11C;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_10860.class})
public abstract class GlCommandEncoderMixin {
   @Shadow
   @Final
   private class_10865 field_57844;

   @Inject(
      method = {"createRenderPass(Ljava/util/function/Supplier;Lcom/mojang/blaze3d/textures/GpuTextureView;Ljava/util/OptionalInt;Lcom/mojang/blaze3d/textures/GpuTextureView;Ljava/util/OptionalDouble;)Lcom/mojang/blaze3d/systems/RenderPass;"},
      at = {@At("RETURN")}
   )
   private void createRenderPass$iGpuDevice(CallbackInfoReturnable<RenderPass> info) {
      ((IGpuDevice)this.field_57844).meteor$onCreateRenderPass((RenderPass)info.getReturnValue());
   }

   @Inject(
      method = {"method_68356"},
      at = {@At(
   value = "INVOKE",
   target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_polygonMode(II)V"
)}
   )
   private void setPipelineAndApplyState$lineSmooth(RenderPipeline pipeline, CallbackInfo info) {
      if (((IRenderPipeline)pipeline).meteor$getLineSmooth()) {
         GL11C.glEnable(2848);
         GL11C.glLineWidth(1.0F);
      } else {
         GL11C.glDisable(2848);
      }

   }
}
