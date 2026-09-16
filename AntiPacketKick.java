package meteordevelopment.meteorclient.systems.modules.misc;

import java.util.Objects;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class AntiPacketKick extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<Boolean> catchExceptions;
   public final Setting<Boolean> logExceptions;

   public AntiPacketKick() {
      super(Categories.Misc, "anti-packet-kick", "Attempts to prevent you from being disconnected by large packets.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.catchExceptions = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("catch-exceptions")).description("Drops corrupted packets.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("log-exceptions")).description("Logs caught exceptions.")).defaultValue(true);
      Setting var10003 = this.catchExceptions;
      Objects.requireNonNull(var10003);
      this.logExceptions = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
   }

   public boolean catchExceptions() {
      return this.isActive() && (Boolean)this.catchExceptions.get();
   }
}
