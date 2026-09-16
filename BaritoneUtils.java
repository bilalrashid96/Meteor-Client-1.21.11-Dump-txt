package meteordevelopment.meteorclient.pathing;

import baritone.api.BaritoneAPI;

public class BaritoneUtils {
   public static boolean IS_AVAILABLE = false;

   private BaritoneUtils() {
   }

   public static String getPrefix() {
      return IS_AVAILABLE ? (String)BaritoneAPI.getSettings().prefix.value : "";
   }
}
