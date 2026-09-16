package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.BlockHitResultAccessor;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2350;
import net.minecraft.class_2596;
import net.minecraft.class_2885;

public class BuildHeight extends Module {
   public BuildHeight() {
      super(Categories.World, "build-height", "Allows you to interact with objects at the build limit.");
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2885 p) {
         if (this.mc.field_1687 != null) {
            if (p.method_12543().method_17784().field_1351 >= (double)this.mc.field_1687.method_31605() && p.method_12543().method_17780() == class_2350.field_11036) {
               ((BlockHitResultAccessor)p.method_12543()).meteor$setSide(class_2350.field_11033);
            }

         }
      }
   }
}
