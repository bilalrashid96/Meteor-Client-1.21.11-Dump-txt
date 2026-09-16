package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Sneak extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Mode> mode;

   public Sneak() {
      super(Categories.Movement, "sneak", "Sneaks for you");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Which method to sneak.")).defaultValue(Sneak.Mode.Vanilla)).build());
   }

   public boolean doPacket() {
      return this.isActive() && !this.mc.field_1724.method_31549().field_7479 && this.mode.get() == Sneak.Mode.Packet;
   }

   public boolean doVanilla() {
      return this.isActive() && !this.mc.field_1724.method_31549().field_7479 && this.mode.get() == Sneak.Mode.Vanilla;
   }

   public static enum Mode {
      Packet,
      Vanilla;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Packet, Vanilla};
      }
   }
}
