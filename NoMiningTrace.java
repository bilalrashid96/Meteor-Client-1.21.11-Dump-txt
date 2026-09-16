package meteordevelopment.meteorclient.systems.modules.player;

import java.util.Set;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_3489;

public class NoMiningTrace extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<Boolean> onlyWhenHoldingPickaxe;

   public NoMiningTrace() {
      super(Categories.Player, "no-mining-trace", "Allows you to mine blocks through entities.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("blacklisted-entities")).description("Entities you will interact with as normal.")).defaultValue().build());
      this.onlyWhenHoldingPickaxe = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-when-holding-a-pickaxe")).description("Whether or not to work only when holding a pickaxe.")).defaultValue(true)).build());
   }

   public boolean canWork(class_1297 entity) {
      if (!this.isActive()) {
         return false;
      } else {
         return (!(Boolean)this.onlyWhenHoldingPickaxe.get() || this.mc.field_1724.method_6047().method_31573(class_3489.field_42614) || this.mc.field_1724.method_6079().method_31573(class_3489.field_42614)) && (entity == null || !((Set)this.entities.get()).contains(entity.method_5864()));
      }
   }
}
