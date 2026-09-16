package meteordevelopment.meteorclient.events.world;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_2394;

public class ParticleEvent extends Cancellable {
   private static final ParticleEvent INSTANCE = new ParticleEvent();
   public class_2394 particle;

   public static ParticleEvent get(class_2394 particle) {
      INSTANCE.setCancelled(false);
      INSTANCE.particle = particle;
      return INSTANCE;
   }
}
