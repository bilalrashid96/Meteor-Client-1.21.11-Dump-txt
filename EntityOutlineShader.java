package meteordevelopment.meteorclient.utils.render.postprocess;

import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import net.minecraft.class_1297;

public class EntityOutlineShader extends EntityShader {
   private static ESP esp;

   public EntityOutlineShader() {
      super(MeteorRenderPipelines.POST_OUTLINE);
   }

   protected boolean shouldDraw() {
      if (esp == null) {
         esp = (ESP)Modules.get().get(ESP.class);
      }

      return esp.isShader();
   }

   public boolean shouldDraw(class_1297 entity) {
      if (!this.shouldDraw()) {
         return false;
      } else {
         return !esp.shouldSkip(entity);
      }
   }

   protected void setupPass(MeshRenderer renderer) {
      renderer.uniform("OutlineData", OutlineUniforms.write((Integer)esp.outlineWidth.get(), ((Double)esp.fillOpacity.get()).floatValue(), ((ShapeMode)esp.shapeMode.get()).ordinal(), ((Double)esp.glowMultiplier.get()).floatValue()));
   }
}
