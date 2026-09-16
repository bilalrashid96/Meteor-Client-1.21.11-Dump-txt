package meteordevelopment.meteorclient.events.game;

public class ResolutionChangedEvent {
   private static final ResolutionChangedEvent INSTANCE = new ResolutionChangedEvent();

   public static ResolutionChangedEvent get() {
      return INSTANCE;
   }
}
