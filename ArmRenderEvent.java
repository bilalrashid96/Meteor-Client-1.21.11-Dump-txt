package meteordevelopment.meteorclient.events.render;

import net.minecraft.class_1268;
import net.minecraft.class_4587;

public class ArmRenderEvent {
   public static ArmRenderEvent INSTANCE = new ArmRenderEvent();
   public class_4587 matrix;
   public class_1268 hand;

   public static ArmRenderEvent get(class_1268 hand, class_4587 matrices) {
      INSTANCE.matrix = matrices;
      INSTANCE.hand = hand;
      return INSTANCE;
   }
}
