package meteordevelopment.meteorclient.events.entity;

import net.minecraft.class_1297;

public class EntityAddedEvent {
   private static final EntityAddedEvent INSTANCE = new EntityAddedEvent();
   public class_1297 entity;

   public static EntityAddedEvent get(class_1297 entity) {
      INSTANCE.entity = entity;
      return INSTANCE;
   }
}
