package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import meteordevelopment.meteorclient.utils.PreInit;

public class FullScreenRenderer {
   public static GpuBuffer vbo;
   public static GpuBuffer ibo;
   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public static MeshBuilder mesh;

   private FullScreenRenderer() {
   }

   @PreInit
   public static void init() {
      mesh = new MeshBuilder(MeteorVertexFormats.POS2, class_5596.field_27379, 4, 6);
      mesh.begin();
      mesh.quad(mesh.vec2((double)-1.0F, (double)-1.0F).next(), mesh.vec2((double)-1.0F, (double)1.0F).next(), mesh.vec2((double)1.0F, (double)1.0F).next(), mesh.vec2((double)1.0F, (double)-1.0F).next());
      mesh.end();
      vbo = mesh.getVertexBuffer();
      ibo = mesh.getIndexBuffer();
   }
}
