package meteordevelopment.meteorclient.systems.modules.combat;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;

public class AntiAnvil extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> swing;
   private final Setting<Boolean> rotate;

   public AntiAnvil() {
      super(Categories.Combat, "anti-anvil", "Automatically prevents Auto Anvil by placing between you and the anvil.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.swing = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing")).description("Swings your hand client-side when placing.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Makes you rotate when placing.")).defaultValue(true)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      for(int i = 0; (double)i <= this.mc.field_1724.method_55754(); ++i) {
         class_2338 pos = this.mc.field_1724.method_24515().method_10069(0, i + 3, 0);
         if (this.mc.field_1687.method_8320(pos).method_26204() == class_2246.field_10535 && this.mc.field_1687.method_8320(pos.method_10074()).method_26215() && BlockUtils.place(pos.method_10074(), InvUtils.findInHotbar(class_1802.field_8281), (Boolean)this.rotate.get(), 15, (Boolean)this.swing.get(), true)) {
            break;
         }
      }

   }
}
