package meteordevelopment.meteorclient.systems.modules.player;

import java.util.List;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StatusEffectListSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.class_1291;
import net.minecraft.class_1294;

public class NoStatusEffects extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<class_1291>> blockedEffects;

   public NoStatusEffects() {
      super(Categories.Player, "no-status-effects", "Blocks specified status effects.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.blockedEffects = this.sgGeneral.add(((StatusEffectListSetting.Builder)((StatusEffectListSetting.Builder)(new StatusEffectListSetting.Builder()).name("blocked-effects")).description("Effects to block.")).defaultValue((class_1291)class_1294.field_5902.comp_349(), (class_1291)class_1294.field_5913.comp_349(), (class_1291)class_1294.field_5906.comp_349(), (class_1291)class_1294.field_5900.comp_349()).build());
   }

   public boolean shouldBlock(class_1291 effect) {
      return this.isActive() && ((List)this.blockedEffects.get()).contains(effect);
   }
}
