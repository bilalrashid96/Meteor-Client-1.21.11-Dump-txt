package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.WaypointsModule;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_418;

public class AutoRespawn extends Module {
   public AutoRespawn() {
      super(Categories.Player, "auto-respawn", "Automatically respawns after death.");
   }

   @EventHandler(
      priority = 100
   )
   private void onOpenScreenEvent(OpenScreenEvent event) {
      if (event.screen instanceof class_418) {
         ((WaypointsModule)Modules.get().get(WaypointsModule.class)).addDeath(this.mc.field_1724.method_73189());
         this.mc.field_1724.method_7331();
         event.cancel();
      }
   }
}
