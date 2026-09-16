package meteordevelopment.meteorclient.events.world;

public class AmbientOcclusionEvent {
   private static final AmbientOcclusionEvent INSTANCE = new AmbientOcclusionEvent();
   public float lightLevel = -1.0F;

   public static AmbientOcclusionEvent get() {
      INSTANCE.lightLevel = -1.0F;
      return INSTANCE;
   }
}
