package meteordevelopment.meteorclient.events.entity;

import net.minecraft.class_1297;
import net.minecraft.class_243;

public class EntityMoveEvent {
   private static final EntityMoveEvent INSTANCE = new EntityMoveEvent();
   public class_1297 entity;
   public class_243 movement;

   public static EntityMoveEvent get(class_1297 entity, class_243 movement) {
      INSTANCE.entity = entity;
      INSTANCE.movement = movement;
      return INSTANCE;
   }
}
