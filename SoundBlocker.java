package meteordevelopment.meteorclient.systems.modules.misc;

import java.util.List;
import meteordevelopment.meteorclient.events.world.PlaySoundEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.SoundEventListSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1113;
import net.minecraft.class_3414;
import net.minecraft.class_7923;

public class SoundBlocker extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<class_3414>> sounds;

   public SoundBlocker() {
      super(Categories.Misc, "sound-blocker", "Cancels out selected sounds.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sounds = this.sgGeneral.add(((SoundEventListSetting.Builder)((SoundEventListSetting.Builder)(new SoundEventListSetting.Builder()).name("sounds")).description("Sounds to block.")).build());
   }

   @EventHandler
   private void onPlaySound(PlaySoundEvent event) {
      for(class_3414 sound : this.sounds.get()) {
         if (sound.comp_3319().equals(event.sound.method_4775())) {
            event.cancel();
            break;
         }
      }

   }

   public boolean shouldBlock(class_1113 soundInstance) {
      return this.isActive() && ((List)this.sounds.get()).contains(Setting.parseId(class_7923.field_41172, soundInstance.method_4775().method_12832()));
   }
}
