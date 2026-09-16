package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;

public class AutoClicker extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> inScreens;
   private final Setting<Mode> leftClickMode;
   private final Setting<Integer> leftClickDelay;
   private final Setting<Mode> rightClickMode;
   private final Setting<Integer> rightClickDelay;
   private int rightClickTimer;
   private int leftClickTimer;

   public AutoClicker() {
      super(Categories.Player, "auto-clicker", "Automatically clicks.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.inScreens = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("while-in-screens")).description("Whether to click while a screen is open.")).defaultValue(true)).build());
      this.leftClickMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode-left")).description("The method of clicking for left clicks.")).defaultValue(AutoClicker.Mode.Press)).build());
      this.leftClickDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay-left")).description("The amount of delay between left clicks in ticks.")).defaultValue(2)).min(0).sliderMax(60).visible(() -> this.leftClickMode.get() == AutoClicker.Mode.Press)).build());
      this.rightClickMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode-right")).description("The method of clicking for right clicks.")).defaultValue(AutoClicker.Mode.Press)).build());
      this.rightClickDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay-right")).description("The amount of delay between right clicks in ticks.")).defaultValue(2)).min(0).sliderMax(60).visible(() -> this.rightClickMode.get() == AutoClicker.Mode.Press)).build());
   }

   public void onActivate() {
      this.rightClickTimer = 0;
      this.leftClickTimer = 0;
      this.mc.field_1690.field_1886.method_23481(false);
      this.mc.field_1690.field_1904.method_23481(false);
   }

   public void onDeactivate() {
      this.mc.field_1690.field_1886.method_23481(false);
      this.mc.field_1690.field_1904.method_23481(false);
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if ((Boolean)this.inScreens.get() || this.mc.field_1755 == null) {
         switch (((Mode)this.leftClickMode.get()).ordinal()) {
            case 0:
            default:
               break;
            case 1:
               this.mc.field_1690.field_1886.method_23481(true);
               break;
            case 2:
               ++this.leftClickTimer;
               if (this.leftClickTimer > (Integer)this.leftClickDelay.get()) {
                  Utils.leftClick();
                  this.leftClickTimer = 0;
               }
         }

         switch (((Mode)this.rightClickMode.get()).ordinal()) {
            case 0:
            default:
               break;
            case 1:
               this.mc.field_1690.field_1904.method_23481(true);
               break;
            case 2:
               ++this.rightClickTimer;
               if (this.rightClickTimer > (Integer)this.rightClickDelay.get()) {
                  Utils.rightClick();
                  this.rightClickTimer = 0;
               }
         }

      }
   }

   public static enum Mode {
      Disabled,
      Hold,
      Press;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Disabled, Hold, Press};
      }
   }
}
