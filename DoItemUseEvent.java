package meteordevelopment.meteorclient.events.entity.player;

import meteordevelopment.meteorclient.events.Cancellable;

public class DoItemUseEvent extends Cancellable {
   private static final DoItemUseEvent INSTANCE = new DoItemUseEvent();

   public static DoItemUseEvent get() {
      return INSTANCE;
   }
}
