package meteordevelopment.meteorclient.utils.render;

import net.minecraft.class_1921;
import net.minecraft.class_4588;
import net.minecraft.class_4618;

public class NoopOutlineVertexConsumerProvider extends class_4618 {
   public static final NoopOutlineVertexConsumerProvider INSTANCE = new NoopOutlineVertexConsumerProvider();

   private NoopOutlineVertexConsumerProvider() {
   }

   public class_4588 method_73477(class_1921 layer) {
      return NoopVertexConsumer.INSTANCE;
   }

   public void method_23285() {
   }
}
