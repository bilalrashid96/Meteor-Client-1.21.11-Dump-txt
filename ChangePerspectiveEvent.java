package meteordevelopment.meteorclient.events.game;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_5498;

public class ChangePerspectiveEvent extends Cancellable {
   private static final ChangePerspectiveEvent INSTANCE = new ChangePerspectiveEvent();
   public class_5498 perspective;

   public static ChangePerspectiveEvent get(class_5498 perspective) {
      INSTANCE.setCancelled(false);
      INSTANCE.perspective = perspective;
      return INSTANCE;
   }
}
