package meteordevelopment.meteorclient.utils.render;

import java.util.SequencedMap;
import net.minecraft.class_1921;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_9799;

public class NoopImmediateVertexConsumerProvider extends class_4597.class_4598 {
   public static final NoopImmediateVertexConsumerProvider INSTANCE = new NoopImmediateVertexConsumerProvider();

   private NoopImmediateVertexConsumerProvider() {
      super((class_9799)null, (SequencedMap)null);
   }

   public class_4588 method_73477(class_1921 layer) {
      return NoopVertexConsumer.INSTANCE;
   }

   public void method_22993() {
   }

   public void method_22994(class_1921 layer) {
   }
}
