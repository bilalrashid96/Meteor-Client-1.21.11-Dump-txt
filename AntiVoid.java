package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;

public class AntiVoid extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Mode> mode;
   private boolean wasFlightEnabled;
   private boolean hasRun;

   public AntiVoid() {
      super(Categories.Movement, "anti-void", "Attempts to prevent you from falling into the void.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The method to prevent you from falling into the void.")).defaultValue(AntiVoid.Mode.Jump)).onChanged((a) -> this.onActivate())).build());
   }

   public void onActivate() {
      if (this.mode.get() == AntiVoid.Mode.Flight) {
         this.wasFlightEnabled = Modules.get().isActive(Flight.class);
      }

   }

   public void onDeactivate() {
      if (!this.wasFlightEnabled && this.mode.get() == AntiVoid.Mode.Flight && Utils.canUpdate()) {
         ((Flight)Modules.get().get(Flight.class)).disable();
      }

   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      int minY = this.mc.field_1687.method_31607();
      if (!(this.mc.field_1724.method_23318() > (double)minY) && !(this.mc.field_1724.method_23318() < (double)(minY - 15))) {
         switch (((Mode)this.mode.get()).ordinal()) {
            case 0:
               ((Flight)Modules.get().get(Flight.class)).enable();
               this.hasRun = true;
               break;
            case 1:
               this.mc.field_1724.method_6043();
         }

      } else {
         if (this.hasRun && this.mode.get() == AntiVoid.Mode.Flight) {
            ((Flight)Modules.get().get(Flight.class)).disable();
            this.hasRun = false;
         }

      }
   }

   public static enum Mode {
      Flight,
      Jump;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Flight, Jump};
      }
   }
}
