package meteordevelopment.meteorclient.events.entity.player;

import net.minecraft.class_239;

public class ItemUseCrosshairTargetEvent {
   private static final ItemUseCrosshairTargetEvent INSTANCE = new ItemUseCrosshairTargetEvent();
   public class_239 target;

   public static ItemUseCrosshairTargetEvent get(class_239 target) {
      INSTANCE.target = target;
      return INSTANCE;
   }
}
