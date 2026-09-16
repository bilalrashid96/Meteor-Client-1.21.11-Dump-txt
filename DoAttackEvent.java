package meteordevelopment.meteorclient.events.entity.player;

import meteordevelopment.meteorclient.events.Cancellable;

public class DoAttackEvent extends Cancellable {
   private static final DoAttackEvent INSTANCE = new DoAttackEvent();

   public static DoAttackEvent get() {
      return INSTANCE;
   }
}
