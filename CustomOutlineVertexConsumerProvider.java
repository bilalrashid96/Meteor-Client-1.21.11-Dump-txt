package meteordevelopment.meteorclient.utils.render;

import java.util.Optional;
import net.minecraft.class_1921;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_9799;

public class CustomOutlineVertexConsumerProvider implements class_4597 {
   private final class_4597.class_4598 immediate = class_4597.method_22991(new class_9799(1536));

   public class_4588 method_73477(class_1921 layer) {
      if (layer.method_24295()) {
         return new CustomVertexConsumer(this.immediate.method_73477(layer));
      } else {
         Optional<class_1921> optional = layer.method_23289();
         return (class_4588)(optional.isPresent() ? new CustomVertexConsumer(this.immediate.method_73477((class_1921)optional.get())) : NoopVertexConsumer.INSTANCE);
      }
   }

   public void draw() {
      this.immediate.method_22993();
   }

   private static record CustomVertexConsumer(class_4588 consumer) implements class_4588 {
      public class_4588 method_22912(float x, float y, float z) {
         this.consumer.method_22912(x, y, z);
         return this;
      }

      public class_4588 method_1336(int red, int green, int blue, int alpha) {
         this.consumer.method_1336(red, green, blue, alpha);
         return this;
      }

      public class_4588 method_39415(int argb) {
         this.consumer.method_39415(argb);
         return this;
      }

      public class_4588 method_22913(float u, float v) {
         this.consumer.method_22913(u, v);
         return this;
      }

      public class_4588 method_60796(int u, int v) {
         return this;
      }

      public class_4588 method_22921(int u, int v) {
         return this;
      }

      public class_4588 method_22914(float x, float y, float z) {
         return this;
      }

      public class_4588 method_75298(float width) {
         return this;
      }
   }
}
