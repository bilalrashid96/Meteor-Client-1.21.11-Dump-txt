package meteordevelopment.meteorclient.events.world;

import net.minecraft.class_639;
import net.minecraft.class_642;

public class ServerConnectBeginEvent {
   private static final ServerConnectBeginEvent INSTANCE = new ServerConnectBeginEvent();
   public class_639 address;
   public class_642 info;

   public static ServerConnectBeginEvent get(class_639 address, class_642 info) {
      INSTANCE.address = address;
      INSTANCE.info = info;
      return INSTANCE;
   }
}
