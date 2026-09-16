package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_10789;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import org.apache.commons.io.IOUtils;

public abstract class MeteorRenderPipelines {
   private static final List<RenderPipeline> PIPELINES = new ArrayList();
   private static final RenderPipeline.Snippet MESH_UNIFORMS;
   public static final RenderPipeline WORLD_COLORED;
   public static final RenderPipeline WORLD_COLORED_LINES;
   public static final RenderPipeline WORLD_COLORED_DEPTH;
   public static final RenderPipeline WORLD_COLORED_LINES_DEPTH;
   public static final RenderPipeline UI_COLORED;
   public static final RenderPipeline UI_COLORED_LINES;
   public static final RenderPipeline UI_TEXTURED;
   public static final RenderPipeline UI_TEXT;
   public static final RenderPipeline POST_OUTLINE;
   public static final RenderPipeline POST_IMAGE;
   public static final RenderPipeline BLUR_DOWN;
   public static final RenderPipeline BLUR_UP;
   public static final RenderPipeline BLUR_PASSTHROUGH;

   private static RenderPipeline add(RenderPipeline pipeline) {
      PIPELINES.add(pipeline);
      return pipeline;
   }

   public static void precompile() {
      GpuDevice device = RenderSystem.getDevice();
      class_3300 resources = class_310.method_1551().method_1478();

      for(RenderPipeline pipeline : PIPELINES) {
         device.precompilePipeline(pipeline, (identifier, shaderType) -> {
            class_3298 resource = (class_3298)resources.method_14486(identifier).get();

            try {
               InputStream in = resource.method_14482();

               String var5;
               try {
                  var5 = IOUtils.toString(in, StandardCharsets.UTF_8);
               } catch (Throwable var8) {
                  if (in != null) {
                     try {
                        in.close();
                     } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                     }
                  }

                  throw var8;
               }

               if (in != null) {
                  in.close();
               }

               return var5;
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
         });
      }

   }

   private MeteorRenderPipelines() {
   }

   static {
      MESH_UNIFORMS = RenderPipeline.builder(new RenderPipeline.Snippet[0]).withUniform("MeshData", class_10789.field_60031).buildSnippet();
      WORLD_COLORED = add((new ExtendedRenderPipelineBuilder(new RenderPipeline.Snippet[]{MESH_UNIFORMS})).withLocation(MeteorClient.identifier("pipeline/world_colored")).withVertexFormat(class_290.field_1576, class_5596.field_27379).withVertexShader(MeteorClient.identifier("shaders/pos_color.vert")).withFragmentShader(MeteorClient.identifier("shaders/pos_color.frag")).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).withCull(false).build());
      WORLD_COLORED_LINES = add((new ExtendedRenderPipelineBuilder(new RenderPipeline.Snippet[]{MESH_UNIFORMS})).withLineSmooth().withLocation(MeteorClient.identifier("pipeline/world_colored_lines")).withVertexFormat(class_290.field_1576, class_5596.field_29344).withVertexShader(MeteorClient.identifier("shaders/pos_color.vert")).withFragmentShader(MeteorClient.identifier("shaders/pos_color.frag")).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).withCull(false).build());
      WORLD_COLORED_DEPTH = add((new ExtendedRenderPipelineBuilder(new RenderPipeline.Snippet[]{MESH_UNIFORMS})).withLocation(MeteorClient.identifier("pipeline/world_colored_depth")).withVertexFormat(class_290.field_1576, class_5596.field_27379).withVertexShader(MeteorClient.identifier("shaders/pos_color.vert")).withFragmentShader(MeteorClient.identifier("shaders/pos_color.frag")).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).withCull(false).build());
      WORLD_COLORED_LINES_DEPTH = add((new ExtendedRenderPipelineBuilder(new RenderPipeline.Snippet[]{MESH_UNIFORMS})).withLineSmooth().withLocation(MeteorClient.identifier("pipeline/world_colored_lines_depth")).withVertexFormat(class_290.field_1576, class_5596.field_29344).withVertexShader(MeteorClient.identifier("shaders/pos_color.vert")).withFragmentShader(MeteorClient.identifier("shaders/pos_color.frag")).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).withCull(false).build());
      UI_COLORED = add((new ExtendedRenderPipelineBuilder(new RenderPipeline.Snippet[]{MESH_UNIFORMS})).withLocation(MeteorClient.identifier("pipeline/ui_colored")).withVertexFormat(MeteorVertexFormats.POS2_COLOR, class_5596.field_27379).withVertexShader(MeteorClient.identifier("shaders/pos_color.vert")).withFragmentShader(MeteorClient.identifier("shaders/pos_color.frag")).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).withCull(true).build());
      UI_COLORED_LINES = add((new ExtendedRenderPipelineBuilder(new RenderPipeline.Snippet[]{MESH_UNIFORMS})).withLocation(MeteorClient.identifier("pipeline/ui_colored_lines")).withVertexFormat(MeteorVertexFormats.POS2_COLOR, class_5596.field_29344).withVertexShader(MeteorClient.identifier("shaders/pos_color.vert")).withFragmentShader(MeteorClient.identifier("shaders/pos_color.frag")).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).withCull(true).build());
      UI_TEXTURED = add((new ExtendedRenderPipelineBuilder(new RenderPipeline.Snippet[]{MESH_UNIFORMS})).withLocation(MeteorClient.identifier("pipeline/ui_textured")).withVertexFormat(MeteorVertexFormats.POS2_TEXTURE_COLOR, class_5596.field_27379).withVertexShader(MeteorClient.identifier("shaders/pos_tex_color.vert")).withFragmentShader(MeteorClient.identifier("shaders/pos_tex_color.frag")).withSampler("u_Texture").withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).withCull(true).build());
      UI_TEXT = add((new ExtendedRenderPipelineBuilder(new RenderPipeline.Snippet[]{MESH_UNIFORMS})).withLocation(MeteorClient.identifier("pipeline/ui_text")).withVertexFormat(MeteorVertexFormats.POS2_TEXTURE_COLOR, class_5596.field_27379).withVertexShader(MeteorClient.identifier("shaders/text.vert")).withFragmentShader(MeteorClient.identifier("shaders/text.frag")).withSampler("u_Texture").withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).withCull(true).build());
      POST_OUTLINE = add((new ExtendedRenderPipelineBuilder(new RenderPipeline.Snippet[0])).withLocation(MeteorClient.identifier("pipeline/post/outline")).withVertexFormat(MeteorVertexFormats.POS2, class_5596.field_27379).withVertexShader(MeteorClient.identifier("shaders/post-process/base.vert")).withFragmentShader(MeteorClient.identifier("shaders/post-process/outline.frag")).withSampler("u_Texture").withUniform("PostData", class_10789.field_60031).withUniform("OutlineData", class_10789.field_60031).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).withCull(false).build());
      POST_IMAGE = add((new ExtendedRenderPipelineBuilder(new RenderPipeline.Snippet[]{MESH_UNIFORMS})).withLocation(MeteorClient.identifier("pipeline/post/image")).withVertexFormat(MeteorVertexFormats.POS2, class_5596.field_27379).withVertexShader(MeteorClient.identifier("shaders/post-process/base.vert")).withFragmentShader(MeteorClient.identifier("shaders/post-process/image.frag")).withSampler("u_Texture").withSampler("u_TextureI").withUniform("PostData", class_10789.field_60031).withUniform("ImageData", class_10789.field_60031).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).withCull(false).build());
      BLUR_DOWN = add((new ExtendedRenderPipelineBuilder(new RenderPipeline.Snippet[]{MESH_UNIFORMS})).withLocation(MeteorClient.identifier("pipeline/blur/down")).withVertexFormat(MeteorVertexFormats.POS2, class_5596.field_27379).withVertexShader(MeteorClient.identifier("shaders/blur.vert")).withFragmentShader(MeteorClient.identifier("shaders/blur_down.frag")).withSampler("u_Texture").withUniform("BlurData", class_10789.field_60031).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).withCull(false).build());
      BLUR_UP = add((new ExtendedRenderPipelineBuilder(new RenderPipeline.Snippet[]{MESH_UNIFORMS})).withLocation(MeteorClient.identifier("pipeline/blur/up")).withVertexFormat(MeteorVertexFormats.POS2, class_5596.field_27379).withVertexShader(MeteorClient.identifier("shaders/blur.vert")).withFragmentShader(MeteorClient.identifier("shaders/blur_up.frag")).withSampler("u_Texture").withUniform("BlurData", class_10789.field_60031).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).withCull(false).build());
      BLUR_PASSTHROUGH = add((new ExtendedRenderPipelineBuilder(new RenderPipeline.Snippet[]{MESH_UNIFORMS})).withLocation(MeteorClient.identifier("pipeline/blur/up")).withVertexFormat(MeteorVertexFormats.POS2, class_5596.field_27379).withVertexShader(MeteorClient.identifier("shaders/passthrough.vert")).withFragmentShader(MeteorClient.identifier("shaders/passthrough.frag")).withSampler("u_Texture").withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).withCull(false).build());
   }
}
