package meteordevelopment.meteorclient.events.entity.player;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_1268;
import net.minecraft.class_1297;

public class InteractEntityEvent extends Cancellable {
   private static final InteractEntityEvent INSTANCE = new InteractEntityEvent();
   public class_1297 entity;
   public class_1268 hand;

   public static InteractEntityEvent get(class_1297 entity, class_1268 hand) {
      INSTANCE.setCancelled(false);
      INSTANCE.entity = entity;
      INSTANCE.hand = hand;
      return INSTANCE;
   }
}
