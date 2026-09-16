package meteordevelopment.meteorclient.events.game;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_437;

public class OpenScreenEvent extends Cancellable {
   private static final OpenScreenEvent INSTANCE = new OpenScreenEvent();
   public class_437 screen;

   public static OpenScreenEvent get(class_437 screen) {
      INSTANCE.setCancelled(false);
      INSTANCE.screen = screen;
      return INSTANCE;
   }
}
