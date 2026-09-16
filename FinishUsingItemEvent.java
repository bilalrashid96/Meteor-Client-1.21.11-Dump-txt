package meteordevelopment.meteorclient.events.entity.player;

import net.minecraft.class_1799;

public class FinishUsingItemEvent {
   private static final FinishUsingItemEvent INSTANCE = new FinishUsingItemEvent();
   public class_1799 itemStack;

   public static FinishUsingItemEvent get(class_1799 itemStack) {
      INSTANCE.itemStack = itemStack;
      return INSTANCE;
   }
}
