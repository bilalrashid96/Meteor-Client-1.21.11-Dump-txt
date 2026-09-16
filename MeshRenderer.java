package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.class_5595;
import java.util.HashMap;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_12137;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_3545;
import net.minecraft.class_4587;
import net.minecraft.class_9848;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class MeshRenderer {
   private static final MeshRenderer INSTANCE = new MeshRenderer();
   private static boolean taken;
   private GpuTextureView colorAttachment;
   private GpuTextureView depthAttachment;
   private Color clearColor;
   private RenderPipeline pipeline;
   private @Nullable MeshBuilder mesh;
   private @Nullable GpuBuffer vertexBuffer;
   private @Nullable GpuBuffer indexBuffer;
   private Matrix4f matrix;
   private final HashMap<String, GpuBufferSlice> uniforms = new HashMap();
   private final HashMap<String, class_3545<GpuTextureView, class_12137>> samplers = new HashMap();

   private MeshRenderer() {
   }

   public static MeshRenderer begin() {
      if (taken) {
         throw new IllegalStateException("Previous instance of MeshRenderer was not ended");
      } else {
         taken = true;
         return INSTANCE;
      }
   }

   public MeshRenderer attachments(GpuTextureView color, GpuTextureView depth) {
      this.colorAttachment = color;
      this.depthAttachment = depth;
      return this;
   }

   public MeshRenderer attachments(class_276 framebuffer) {
      this.colorAttachment = framebuffer.method_71639();
      this.depthAttachment = framebuffer.method_71640();
      return this;
   }

   public MeshRenderer clearColor(Color color) {
      this.clearColor = color;
      return this;
   }

   public MeshRenderer pipeline(RenderPipeline pipeline) {
      this.pipeline = pipeline;
      return this;
   }

   public MeshRenderer mesh(GpuBuffer vertices, GpuBuffer indices) {
      this.vertexBuffer = vertices;
      this.indexBuffer = indices;
      return this;
   }

   public MeshRenderer mesh(MeshBuilder mesh) {
      this.mesh = mesh;
      return this;
   }

   public MeshRenderer mesh(MeshBuilder mesh, Matrix4f matrix) {
      this.mesh = mesh;
      return this.transform(matrix);
   }

   public MeshRenderer mesh(MeshBuilder mesh, class_4587 matrices) {
      this.mesh = mesh;
      return this.transform(matrices);
   }

   public MeshRenderer transform(Matrix4f matrix) {
      this.matrix = matrix;
      return this;
   }

   public MeshRenderer transform(class_4587 matrices) {
      this.matrix = matrices.method_23760().method_23761();
      return this;
   }

   public MeshRenderer fullscreen() {
      return this.mesh(FullScreenRenderer.vbo, FullScreenRenderer.ibo);
   }

   public MeshRenderer uniform(String name, GpuBufferSlice slice) {
      this.uniforms.put(name, slice);
      return this;
   }

   public MeshRenderer sampler(String name, GpuTextureView view, class_12137 sampler) {
      if (name != null && view != null && sampler != null) {
         this.samplers.put(name, new class_3545(view, sampler));
      }

      return this;
   }

   public void end() {
      if (this.mesh != null && this.mesh.isBuilding()) {
         this.mesh.end();
      }

      int indexCount = this.mesh != null ? this.mesh.getIndicesCount() : (int)(this.indexBuffer != null ? this.indexBuffer.size() / 4L : -1L);
      if (indexCount > 0) {
         if (Utils.rendering3D || this.matrix != null) {
            RenderSystem.getModelViewStack().pushMatrix();
         }

         if (this.matrix != null) {
            RenderSystem.getModelViewStack().mul(this.matrix);
         }

         if (Utils.rendering3D) {
            applyCameraPos();
         }

         GpuBuffer vertexBuffer = this.mesh != null ? this.mesh.getVertexBuffer() : this.vertexBuffer;
         GpuBuffer indexBuffer = this.mesh != null ? this.mesh.getIndexBuffer() : this.indexBuffer;
         OptionalInt clearColor = this.clearColor != null ? OptionalInt.of(class_9848.method_61324(this.clearColor.a, this.clearColor.r, this.clearColor.g, this.clearColor.b)) : OptionalInt.empty();
         GpuBufferSlice meshData = MeshUniforms.write(RenderUtils.projection, RenderSystem.getModelViewStack());
         RenderPass pass = this.depthAttachment != null && this.pipeline.wantsDepthTexture() ? RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Meteor MeshRenderer", this.colorAttachment, clearColor, this.depthAttachment, OptionalDouble.empty()) : RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Meteor MeshRenderer", this.colorAttachment, clearColor);
         pass.setPipeline(this.pipeline);
         pass.setUniform("MeshData", meshData);

         for(String name : this.uniforms.keySet()) {
            pass.setUniform(name, (GpuBufferSlice)this.uniforms.get(name));
         }

         for(String name : this.samplers.keySet()) {
            pass.bindTexture(name, (GpuTextureView)((class_3545)this.samplers.get(name)).method_15442(), (class_12137)((class_3545)this.samplers.get(name)).method_15441());
         }

         pass.setVertexBuffer(0, vertexBuffer);
         pass.setIndexBuffer(indexBuffer, class_5595.field_27373);
         pass.drawIndexed(0, 0, indexCount, 1);
         pass.close();
         if (Utils.rendering3D || this.matrix != null) {
            RenderSystem.getModelViewStack().popMatrix();
         }
      }

      this.colorAttachment = null;
      this.depthAttachment = null;
      this.clearColor = null;
      this.pipeline = null;
      this.mesh = null;
      this.vertexBuffer = null;
      this.indexBuffer = null;
      this.matrix = null;
      this.uniforms.clear();
      this.samplers.clear();
      taken = false;
   }

   private static void applyCameraPos() {
      class_243 cameraPos = MeteorClient.mc.field_1773.method_19418().method_71156();
      RenderSystem.getModelViewStack().translate(0.0F, (float)(-cameraPos.field_1351), 0.0F);
   }
}
