package meteordevelopment.meteorclient.events.entity.player;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_1268;
import net.minecraft.class_3965;

public class InteractBlockEvent extends Cancellable {
   private static final InteractBlockEvent INSTANCE = new InteractBlockEvent();
   public class_1268 hand;
   public class_3965 result;

   public static InteractBlockEvent get(class_1268 hand, class_3965 result) {
      INSTANCE.setCancelled(false);
      INSTANCE.hand = hand;
      INSTANCE.result = result;
      return INSTANCE;
   }
}
