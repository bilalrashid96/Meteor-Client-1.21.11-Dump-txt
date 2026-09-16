package meteordevelopment.meteorclient.events.entity.player;

public class JumpVelocityMultiplierEvent {
   private static final JumpVelocityMultiplierEvent INSTANCE = new JumpVelocityMultiplierEvent();
   public float multiplier = 1.0F;

   public static JumpVelocityMultiplierEvent get() {
      INSTANCE.multiplier = 1.0F;
      return INSTANCE;
   }
}
