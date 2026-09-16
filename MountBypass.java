package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerInteractEntityC2SPacket;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1492;
import net.minecraft.class_2596;
import net.minecraft.class_2824.class_5907;

public class MountBypass extends Module {
   private boolean dontCancel;

   public MountBypass() {
      super(Categories.World, "mount-bypass", "Allows you to bypass the IllegalStacks plugin and put chests on entities.");
   }

   @EventHandler
   public void onSendPacket(PacketEvent.Send event) {
      if (this.dontCancel) {
         this.dontCancel = false;
      } else {
         class_2596 var3 = event.packet;
         if (var3 instanceof IPlayerInteractEntityC2SPacket) {
            IPlayerInteractEntityC2SPacket packet = (IPlayerInteractEntityC2SPacket)var3;
            if (packet.meteor$getType() == class_5907.field_29173 && packet.meteor$getEntity() instanceof class_1492) {
               event.cancel();
            }
         }

      }
   }
}
