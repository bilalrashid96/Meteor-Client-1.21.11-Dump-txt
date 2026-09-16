package meteordevelopment.meteorclient.systems.modules.player;

import java.util.List;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StatusEffectListSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.class_1291;
import net.minecraft.class_1294;

public class PotionSaver extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<class_1291>> effects;
   public final Setting<Boolean> onlyWhenStationary;

   public PotionSaver() {
      super(Categories.Player, "potion-saver", "Stops potion effects ticking when you stand still.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.effects = this.sgGeneral.add(((StatusEffectListSetting.Builder)((StatusEffectListSetting.Builder)(new StatusEffectListSetting.Builder()).name("effects")).description("The effects to preserve.")).defaultValue((class_1291)class_1294.field_5910.comp_349(), (class_1291)class_1294.field_5898.comp_349(), (class_1291)class_1294.field_5907.comp_349(), (class_1291)class_1294.field_5918.comp_349(), (class_1291)class_1294.field_5904.comp_349(), (class_1291)class_1294.field_5917.comp_349(), (class_1291)class_1294.field_5924.comp_349(), (class_1291)class_1294.field_5923.comp_349(), (class_1291)class_1294.field_5922.comp_349(), (class_1291)class_1294.field_5926.comp_349(), (class_1291)class_1294.field_5906.comp_349(), (class_1291)class_1294.field_5900.comp_349(), (class_1291)class_1294.field_5927.comp_349(), (class_1291)class_1294.field_18980.comp_349()).build());
      this.onlyWhenStationary = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-when-stationary")).description("Only freezes effects when you aren't moving.")).defaultValue(false)).build());
   }

   public boolean shouldFreeze(class_1291 effect) {
      return this.isActive() && (!(Boolean)this.onlyWhenStationary.get() || !PlayerUtils.isMoving()) && !this.mc.field_1724.method_6026().isEmpty() && ((List)this.effects.get()).contains(effect);
   }
}
