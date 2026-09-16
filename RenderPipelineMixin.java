package meteordevelopment.meteorclient.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import meteordevelopment.meteorclient.mixininterface.IRenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({RenderPipeline.class})
public abstract class RenderPipelineMixin implements IRenderPipeline {
   @Unique
   private boolean lineSmooth;

   public void meteor$setLineSmooth(boolean lineSmooth) {
      this.lineSmooth = lineSmooth;
   }

   public boolean meteor$getLineSmooth() {
      return this.lineSmooth;
   }
}
