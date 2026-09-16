package meteordevelopment.meteorclient.events.meteor;

public class CustomFontChangedEvent {
   private static final CustomFontChangedEvent INSTANCE = new CustomFontChangedEvent();

   public static CustomFontChangedEvent get() {
      return INSTANCE;
   }
}
