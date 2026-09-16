package meteordevelopment.meteorclient.utils.render.postprocess;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import java.nio.ByteBuffer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import net.minecraft.class_11280;
import net.minecraft.class_276;
import net.minecraft.class_6367;
import org.lwjgl.glfw.GLFW;

public abstract class PostProcessShader {
   protected final RenderPipeline pipeline;
   public final class_276 framebuffer;
   private static final int UNIFORM_SIZE = (new Std140SizeCalculator()).putVec2().putFloat().get();
   private static final class_11280<UniformData> UNIFORM_STORAGE;

   protected PostProcessShader(RenderPipeline pipeline) {
      this.pipeline = pipeline;
      this.framebuffer = new class_6367(MeteorClient.NAME + " PostProcessShader " + this.getClass().getSimpleName(), MeteorClient.mc.method_22683().method_4489(), MeteorClient.mc.method_22683().method_4506(), true);
   }

   protected abstract boolean shouldDraw();

   protected void preDraw() {
   }

   protected void postDraw() {
   }

   protected abstract void setupPass(MeshRenderer var1);

   public void clearTexture() {
      if (this.shouldDraw()) {
         RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.framebuffer.method_30277(), 0);
      }

   }

   public void submitVertices(Runnable draw) {
      if (this.shouldDraw()) {
         this.preDraw();
         draw.run();
         this.postDraw();
      }
   }

   public void render() {
      if (this.shouldDraw()) {
         MeshRenderer renderer = MeshRenderer.begin().attachments(MeteorClient.mc.method_1522()).pipeline(this.pipeline).fullscreen().uniform("PostData", UNIFORM_STORAGE.method_71102(new UniformData((float)MeteorClient.mc.method_22683().method_4489(), (float)MeteorClient.mc.method_22683().method_4506(), (float)GLFW.glfwGetTime()))).sampler("u_Texture", this.framebuffer.method_71639(), RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST));
         this.setupPass(renderer);
         renderer.end();
      }
   }

   public void onResized(int width, int height) {
      if (this.framebuffer != null) {
         this.framebuffer.method_1234(width, height);
      }
   }

   public static void flipFrame() {
      UNIFORM_STORAGE.method_71100();
   }

   static {
      UNIFORM_STORAGE = new class_11280("Meteor - Post UBO", UNIFORM_SIZE, 16);
   }

   private static record UniformData(float sizeX, float sizeY, float time) implements class_11280.class_11281 {
      public void method_71104(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer).putVec2(this.sizeX, this.sizeY).putFloat(this.time);
      }
   }
}
