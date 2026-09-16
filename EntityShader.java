package meteordevelopment.meteorclient.utils.render.postprocess;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.IWorldRenderer;
import meteordevelopment.meteorclient.utils.render.CustomOutlineVertexConsumerProvider;
import net.minecraft.class_1297;

public abstract class EntityShader extends PostProcessShader {
   public final CustomOutlineVertexConsumerProvider vertexConsumerProvider = new CustomOutlineVertexConsumerProvider();

   protected EntityShader(RenderPipeline pipeline) {
      super(pipeline);
   }

   public abstract boolean shouldDraw(class_1297 var1);

   protected void preDraw() {
      ((IWorldRenderer)MeteorClient.mc.field_1769).meteor$pushEntityOutlineFramebuffer(this.framebuffer);
   }

   protected void postDraw() {
      ((IWorldRenderer)MeteorClient.mc.field_1769).meteor$popEntityOutlineFramebuffer();
   }

   public void submitVertices() {
      CustomOutlineVertexConsumerProvider var10001 = this.vertexConsumerProvider;
      Objects.requireNonNull(var10001);
      this.submitVertices(var10001::draw);
   }
}
