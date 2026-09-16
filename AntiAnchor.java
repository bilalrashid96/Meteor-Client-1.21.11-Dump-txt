package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2482;

public class AntiAnchor extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> swing;

   public AntiAnchor() {
      super(Categories.Combat, "anti-anchor", "Automatically prevents Anchor Aura by placing a slab on your head.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Makes you rotate when placing.")).defaultValue(true)).build());
      this.swing = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing")).description("Swings your hand when placing.")).defaultValue(true)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.mc.field_1687.method_8320(this.mc.field_1724.method_24515().method_10086(2)).method_26204() == class_2246.field_23152 && this.mc.field_1687.method_8320(this.mc.field_1724.method_24515().method_10084()).method_26204() == class_2246.field_10124) {
         BlockUtils.place(this.mc.field_1724.method_24515().method_10069(0, 1, 0), InvUtils.findInHotbar((Predicate)((itemStack) -> class_2248.method_9503(itemStack.method_7909()) instanceof class_2482)), (Boolean)this.rotate.get(), 15, (Boolean)this.swing.get(), false, true);
      }

   }
}
