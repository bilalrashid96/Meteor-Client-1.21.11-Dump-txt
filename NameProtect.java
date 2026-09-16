package meteordevelopment.meteorclient.systems.modules.player;

import java.util.Objects;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class NameProtect extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> nameProtect;
   private final Setting<String> name;
   private final Setting<Boolean> skinProtect;
   private String username;

   public NameProtect() {
      super(Categories.Player, "name-protect", "Hide player names and skins.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.nameProtect = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("name-protect")).description("Hides your name client-side.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      StringSetting.Builder var10002 = (StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("Name to be replaced with.")).defaultValue("seasnail");
      Setting var10003 = this.nameProtect;
      Objects.requireNonNull(var10003);
      this.name = var10001.add(((StringSetting.Builder)var10002.visible(var10003::get)).build());
      this.skinProtect = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("skin-protect")).description("Make players become Steves.")).defaultValue(true)).build());
      this.username = "If you see this, something is wrong.";
   }

   public void onActivate() {
      this.username = this.mc.method_1548().method_1676();
   }

   public String replaceName(String string) {
      return string != null && this.isActive() ? string.replace(this.username, (CharSequence)this.name.get()) : string;
   }

   public String getName(String original) {
      return !((String)this.name.get()).isEmpty() && this.isActive() ? (String)this.name.get() : original;
   }

   public boolean skinProtect() {
      return this.isActive() && (Boolean)this.skinProtect.get();
   }
}
