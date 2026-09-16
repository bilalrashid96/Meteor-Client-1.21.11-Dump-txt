package meteordevelopment.meteorclient.events.entity;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_1799;

public class DropItemsEvent extends Cancellable {
   private static final DropItemsEvent INSTANCE = new DropItemsEvent();
   public class_1799 itemStack;

   public static DropItemsEvent get(class_1799 itemStack) {
      INSTANCE.setCancelled(false);
      INSTANCE.itemStack = itemStack;
      return INSTANCE;
   }
}
