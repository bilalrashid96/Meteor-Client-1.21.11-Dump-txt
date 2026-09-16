package meteordevelopment.meteorclient.systems.modules.combat;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1802;

public class SelfWeb extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Mode> mode;
   private final Setting<Integer> range;
   private final Setting<Boolean> doubles;
   private final Setting<Boolean> turnOff;
   private final Setting<Boolean> rotate;

   public SelfWeb() {
      super(Categories.Combat, "self-web", "Automatically places webs on you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The mode to use for selfweb.")).defaultValue(SelfWeb.Mode.Normal)).build());
      this.range = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("range")).description("How far away the player has to be from you to place webs. Requires Mode to Smart.")).defaultValue(3)).min(1).sliderRange(1, 7).visible(() -> this.mode.get() == SelfWeb.Mode.Smart)).build());
      this.doubles = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("double-place")).description("Places webs in your upper hitbox as well.")).defaultValue(false)).build());
      this.turnOff = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-toggle")).description("Toggles off after placing the webs.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Forces you to rotate downwards when placing webs.")).defaultValue(true)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      switch (((Mode)this.mode.get()).ordinal()) {
         case 0:
            this.placeWeb();
            break;
         case 1:
            if (TargetUtils.getPlayerTarget((double)(Integer)this.range.get(), SortPriority.LowestDistance) != null) {
               this.placeWeb();
            }
      }

   }

   private void placeWeb() {
      FindItemResult web = InvUtils.findInHotbar(class_1802.field_8786);
      BlockUtils.place(this.mc.field_1724.method_24515(), web, (Boolean)this.rotate.get(), 0, false);
      if ((Boolean)this.doubles.get()) {
         BlockUtils.place(this.mc.field_1724.method_24515().method_10069(0, 1, 0), web, (Boolean)this.rotate.get(), 0, false);
      }

      if ((Boolean)this.turnOff.get()) {
         this.toggle();
      }

   }

   public static enum Mode {
      Normal,
      Smart;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Normal, Smart};
      }
   }
}
