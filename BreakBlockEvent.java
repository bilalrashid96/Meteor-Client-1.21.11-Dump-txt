package meteordevelopment.meteorclient.events.entity.player;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_2338;

public class BreakBlockEvent extends Cancellable {
   private static final BreakBlockEvent INSTANCE = new BreakBlockEvent();
   public class_2338 blockPos;

   public static BreakBlockEvent get(class_2338 blockPos) {
      INSTANCE.setCancelled(false);
      INSTANCE.blockPos = blockPos;
      return INSTANCE;
   }
}
