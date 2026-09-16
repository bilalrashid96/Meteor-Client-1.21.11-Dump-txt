package meteordevelopment.meteorclient.events.packets;

import net.minecraft.class_2653;

public class ContainerSlotUpdateEvent {
   private static final ContainerSlotUpdateEvent INSTANCE = new ContainerSlotUpdateEvent();
   public class_2653 packet;

   public static ContainerSlotUpdateEvent get(class_2653 packet) {
      INSTANCE.packet = packet;
      return INSTANCE;
   }
}
