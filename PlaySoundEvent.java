package meteordevelopment.meteorclient.events.world;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_1113;

public class PlaySoundEvent extends Cancellable {
   private static final PlaySoundEvent INSTANCE = new PlaySoundEvent();
   public class_1113 sound;

   public static PlaySoundEvent get(class_1113 sound) {
      INSTANCE.setCancelled(false);
      INSTANCE.sound = sound;
      return INSTANCE;
   }
}
