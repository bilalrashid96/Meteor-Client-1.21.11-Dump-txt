package meteordevelopment.meteorclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.MeshUniforms;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.InventoryTweaks;
import meteordevelopment.meteorclient.utils.render.postprocess.ChamsShader;
import meteordevelopment.meteorclient.utils.render.postprocess.OutlineUniforms;
import meteordevelopment.meteorclient.utils.render.postprocess.PostProcessShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderSystem.class})
public abstract class RenderSystemMixin {
   @Inject(
      method = {"flipFrame"},
      at = {@At("TAIL")}
   )
   private static void meteor$flipFrame(CallbackInfo info) {
      MeshUniforms.flipFrame();
      PostProcessShader.flipFrame();
      ChamsShader.flipFrame();
      OutlineUniforms.flipFrame();
      if (Modules.get() != null && MeteorClient.mc.field_1724 != null) {
         if (((InventoryTweaks)Modules.get().get(InventoryTweaks.class)).frameInput()) {
            ((MinecraftClientAccessor)MeteorClient.mc).meteor$handleInputEvents();
         }

      }
   }
}
