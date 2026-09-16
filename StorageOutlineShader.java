package meteordevelopment.meteorclient.utils.render.postprocess;

import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.StorageESP;

public class StorageOutlineShader extends PostProcessShader {
   private static StorageESP storageESP;

   public StorageOutlineShader() {
      super(MeteorRenderPipelines.POST_OUTLINE);
   }

   protected boolean shouldDraw() {
      if (storageESP == null) {
         storageESP = (StorageESP)Modules.get().get(StorageESP.class);
      }

      return storageESP.isShader();
   }

   protected void setupPass(MeshRenderer renderer) {
      renderer.uniform("OutlineData", OutlineUniforms.write((Integer)storageESP.outlineWidth.get(), (float)(Integer)storageESP.fillOpacity.get() / 255.0F, ((ShapeMode)storageESP.shapeMode.get()).ordinal(), ((Double)storageESP.glowMultiplier.get()).floatValue()));
   }
}
