package meteordevelopment.meteorclient.events.entity.player;

import net.minecraft.class_1799;

public class PickItemsEvent {
   private static final PickItemsEvent INSTANCE = new PickItemsEvent();
   public class_1799 itemStack;
   public int count;

   public static PickItemsEvent get(class_1799 itemStack, int count) {
      INSTANCE.itemStack = itemStack;
      INSTANCE.count = count;
      return INSTANCE;
   }
}
