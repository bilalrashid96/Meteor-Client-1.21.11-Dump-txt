package meteordevelopment.meteorclient.utils.network;

public class OnlinePlayers {
   private static long lastPingTime;

   private OnlinePlayers() {
   }

   public static void update() {
      long time = System.currentTimeMillis();
      if (time - lastPingTime > 300000L) {
         MeteorExecutor.execute(() -> Http.post("https://meteorclient.com/api/online/ping").ignoreExceptions().send());
         lastPingTime = time;
      }

   }

   public static void leave() {
      MeteorExecutor.execute(() -> Http.post("https://meteorclient.com/api/online/leave").ignoreExceptions().send());
   }
}
