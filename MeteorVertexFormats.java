package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public abstract class MeteorVertexFormats {
   public static final VertexFormat POS2;
   public static final VertexFormat POS2_COLOR;
   public static final VertexFormat POS2_TEXTURE_COLOR;

   private MeteorVertexFormats() {
   }

   static {
      POS2 = VertexFormat.builder().add("Position", MeteorVertexFormatElements.POS2).build();
      POS2_COLOR = VertexFormat.builder().add("Position", MeteorVertexFormatElements.POS2).add("Color", VertexFormatElement.COLOR).build();
      POS2_TEXTURE_COLOR = VertexFormat.builder().add("Position", MeteorVertexFormatElements.POS2).add("Texture", VertexFormatElement.UV).add("Color", VertexFormatElement.COLOR).build();
   }
}
