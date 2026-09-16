package meteordevelopment.meteorclient.events.entity.player;

import net.minecraft.class_1313;
import net.minecraft.class_243;

public class PlayerMoveEvent {
   private static final PlayerMoveEvent INSTANCE = new PlayerMoveEvent();
   public class_1313 type;
   public class_243 movement;

   public static PlayerMoveEvent get(class_1313 type, class_243 movement) {
      INSTANCE.type = type;
      INSTANCE.movement = movement;
      return INSTANCE;
   }
}
