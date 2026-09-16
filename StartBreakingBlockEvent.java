package meteordevelopment.meteorclient.events.entity.player;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_2338;
import net.minecraft.class_2350;

public class StartBreakingBlockEvent extends Cancellable {
   private static final StartBreakingBlockEvent INSTANCE = new StartBreakingBlockEvent();
   public class_2338 blockPos;
   public class_2350 direction;

   public static StartBreakingBlockEvent get(class_2338 blockPos, class_2350 direction) {
      INSTANCE.setCancelled(false);
      INSTANCE.blockPos = blockPos;
      INSTANCE.direction = direction;
      return INSTANCE;
   }
}
