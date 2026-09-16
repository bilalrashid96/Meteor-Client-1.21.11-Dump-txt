package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;

public class AirJump extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> maintainLevel;
   private int level;

   public AirJump() {
      super(Categories.Movement, "air-jump", "Lets you jump in the air.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.maintainLevel = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("maintain-level")).description("Maintains your current Y level when holding the jump key.")).defaultValue(false)).build());
   }

   public void onActivate() {
      this.level = this.mc.field_1724.method_24515().method_10264();
   }

   @EventHandler
   private void onKey(KeyEvent event) {
      if (!Modules.get().isActive(Freecam.class) && this.mc.field_1755 == null && !this.mc.field_1724.method_24828()) {
         if (event.action == KeyAction.Press) {
            if (this.mc.field_1690.field_1903.method_1417(event.input)) {
               this.level = this.mc.field_1724.method_24515().method_10264();
               this.mc.field_1724.method_6043();
            } else if (this.mc.field_1690.field_1832.method_1417(event.input)) {
               --this.level;
            }

         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (!Modules.get().isActive(Freecam.class) && !this.mc.field_1724.method_24828()) {
         if ((Boolean)this.maintainLevel.get() && this.mc.field_1724.method_24515().method_10264() == this.level && this.mc.field_1690.field_1903.method_1434()) {
            this.mc.field_1724.method_6043();
         }

      }
   }
}
