package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1802;

public class EXPThrower extends Module {
   public EXPThrower() {
      super(Categories.Player, "exp-thrower", "Automatically throws XP bottles from your hotbar.");
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      FindItemResult exp = InvUtils.findInHotbar(class_1802.field_8287);
      if (exp.found()) {
         Rotations.rotate((double)this.mc.field_1724.method_36454(), (double)90.0F, () -> {
            if (exp.getHand() != null) {
               this.mc.field_1761.method_2919(this.mc.field_1724, exp.getHand());
            } else {
               InvUtils.swap(exp.slot(), true);
               this.mc.field_1761.method_2919(this.mc.field_1724, exp.getHand());
               InvUtils.swapBack();
            }

         });
      }
   }
}
