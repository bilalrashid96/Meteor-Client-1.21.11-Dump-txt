package meteordevelopment.meteorclient.events.entity.player;

import net.minecraft.class_1268;
import net.minecraft.class_1269;

public class InteractItemEvent {
   private static final InteractItemEvent INSTANCE = new InteractItemEvent();
   public class_1268 hand;
   public class_1269 toReturn;

   public static InteractItemEvent get(class_1268 hand) {
      INSTANCE.hand = hand;
      INSTANCE.toReturn = null;
      return INSTANCE;
   }
}
