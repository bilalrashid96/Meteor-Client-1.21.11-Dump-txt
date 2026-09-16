package meteordevelopment.meteorclient.events.entity.player;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_1297;

public class AttackEntityEvent extends Cancellable {
   private static final AttackEntityEvent INSTANCE = new AttackEntityEvent();
   public class_1297 entity;

   public static AttackEntityEvent get(class_1297 entity) {
      INSTANCE.setCancelled(false);
      INSTANCE.entity = entity;
      return INSTANCE;
   }
}
