package meteordevelopment.meteorclient.events.packets;

import net.minecraft.class_2767;

public class PlaySoundPacketEvent {
   private static final PlaySoundPacketEvent INSTANCE = new PlaySoundPacketEvent();
   public class_2767 packet;

   public static PlaySoundPacketEvent get(class_2767 packet) {
      INSTANCE.packet = packet;
      return INSTANCE;
   }
}
