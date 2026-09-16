package meteordevelopment.meteorclient.events.entity;

import net.minecraft.class_1297;

public class EntityDestroyEvent {
   private static final EntityDestroyEvent INSTANCE = new EntityDestroyEvent();
   public class_1297 entity;

   public static EntityDestroyEvent get(class_1297 entity) {
      INSTANCE.entity = entity;
      return INSTANCE;
   }
}
