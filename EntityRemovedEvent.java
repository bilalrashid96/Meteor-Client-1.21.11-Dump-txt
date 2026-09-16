package meteordevelopment.meteorclient.events.entity;

import net.minecraft.class_1297;

public class EntityRemovedEvent {
   private static final EntityRemovedEvent INSTANCE = new EntityRemovedEvent();
   public class_1297 entity;

   public static EntityRemovedEvent get(class_1297 entity) {
      INSTANCE.entity = entity;
      return INSTANCE;
   }
}
