package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import meteordevelopment.meteorclient.mixininterface.IRenderPipeline;

public class ExtendedRenderPipelineBuilder extends RenderPipeline.Builder {
   private boolean lineSmooth;

   public ExtendedRenderPipelineBuilder(RenderPipeline.Snippet... snippets) {
      for(RenderPipeline.Snippet snippet : snippets) {
         this.withSnippet(snippet);
      }

   }

   public ExtendedRenderPipelineBuilder withLineSmooth() {
      this.lineSmooth = true;
      return this;
   }

   public RenderPipeline build() {
      RenderPipeline pipeline = super.build();
      ((IRenderPipeline)pipeline).meteor$setLineSmooth(this.lineSmooth);
      return pipeline;
   }
}
