package meteordevelopment.meteorclient.systems.macros;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.utils.StarscriptTextBoxRenderer;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import org.meteordev.starscript.Script;

public class Macro implements ISerializable<Macro> {
   public final Settings settings = new Settings();
   private final SettingGroup sgGeneral;
   public Setting<String> name;
   public Setting<List<String>> messages;
   public Setting<Keybind> keybind;
   private final List<Script> scripts;
   private boolean dirty;

   public Macro() {
      this.sgGeneral = this.settings.getDefaultGroup();
      this.name = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name of the macro.")).build());
      this.messages = this.sgGeneral.add(((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("messages")).description("The messages for the macro to send.")).onChanged((v) -> this.dirty = true)).renderer(StarscriptTextBoxRenderer.class).build());
      this.keybind = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("keybind")).description("The bind to run the macro.")).build());
      this.scripts = new ArrayList(1);
   }

   public Macro(class_2520 tag) {
      this.sgGeneral = this.settings.getDefaultGroup();
      this.name = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name of the macro.")).build());
      this.messages = this.sgGeneral.add(((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("messages")).description("The messages for the macro to send.")).onChanged((v) -> this.dirty = true)).renderer(StarscriptTextBoxRenderer.class).build());
      this.keybind = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("keybind")).description("The bind to run the macro.")).build());
      this.scripts = new ArrayList(1);
      this.fromTag((class_2487)tag);
   }

   public boolean onAction(boolean isKey, int value, int modifiers) {
      return ((Keybind)this.keybind.get()).matches(isKey, value, modifiers) && MeteorClient.mc.field_1755 == null ? this.onAction() : false;
   }

   public boolean onAction() {
      if (this.dirty) {
         this.scripts.clear();

         for(String message : this.messages.get()) {
            Script script = MeteorStarscript.compile(message);
            if (script != null) {
               this.scripts.add(script);
            }
         }

         this.dirty = false;
      }

      for(Script script : this.scripts) {
         String message = MeteorStarscript.run(script);
         if (message != null) {
            ChatUtils.sendPlayerMsg(message, false);
         }
      }

      return true;
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("settings", this.settings.toTag());
      return tag;
   }

   public Macro fromTag(class_2487 tag) {
      if (tag.method_10545("settings")) {
         this.settings.fromTag(tag.method_68568("settings"));
      }

      return this;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Macro macro = (Macro)o;
         return Objects.equals(macro.name.get(), this.name.get());
      } else {
         return false;
      }
   }
}
