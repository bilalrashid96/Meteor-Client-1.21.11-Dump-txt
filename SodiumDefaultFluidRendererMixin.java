package meteordevelopment.meteorclient.mixin.sodium;

import meteordevelopment.meteorclient.systems.modules.render.Xray;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.light.LightPipeline;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadViewMutable;
import net.caffeinemc.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.class_1058;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_3610;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {DefaultFluidRenderer.class},
   remap = false
)
public abstract class SodiumDefaultFluidRendererMixin {
   @Final
   @Shadow
   private int[] quadColors;
   @Unique
   private int xrayAlpha;

   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRender(LevelSlice level, class_2680 blockState, class_3610 fluidState, class_2338 blockPos, class_2338 offset, TranslucentGeometryCollector collector, ChunkModelBuilder meshBuilder, Material material, ColorProvider<class_3610> colorProvider, class_1058[] sprites, CallbackInfo ci) {
      this.xrayAlpha = Xray.getAlpha(fluidState.method_15759(), blockPos);
      if (this.xrayAlpha == 0) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"updateQuad"},
      at = {@At("TAIL")}
   )
   private void onUpdateQuad(ModelQuadViewMutable quad, LevelSlice level, class_2338 pos, LightPipeline lighter, class_2350 dir, ModelQuadFacing facing, float brightness, ColorProvider<class_3610> colorProvider, class_3610 fluidState, CallbackInfo ci) {
      if (this.xrayAlpha != -1) {
         for(int i = 0; i < 4; ++i) {
            this.quadColors[i] = this.quadColors[i] & 16777215 | this.xrayAlpha << 24;
         }
      }

   }

   @ModifyArg(
      method = {"render"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;writeQuad(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;Lnet/minecraft/class_2338;Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;Lnet/caffeinemc/mods/sodium/client/model/quad/properties/ModelQuadFacing;Z)V"
),
      index = 2
   )
   private Material modifyMaterial(Material material) {
      return this.xrayAlpha != -1 ? DefaultMaterials.TRANSLUCENT : material;
   }
}
