package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormatElement.Type;
import com.mojang.blaze3d.vertex.VertexFormatElement.Usage;

public abstract class MeteorVertexFormatElements {
   public static final VertexFormatElement POS2;

   private MeteorVertexFormatElements() {
   }

   private static int getNextVertexFormatElementId() {
      int id = 0;

      while(VertexFormatElement.byId(id) != null) {
         ++id;
         if (id >= 32) {
            throw new RuntimeException("Too many mods registering VertexFormatElements");
         }
      }

      return id;
   }

   static {
      POS2 = VertexFormatElement.register(getNextVertexFormatElementId(), 0, Type.FLOAT, Usage.POSITION, 2);
   }
}
