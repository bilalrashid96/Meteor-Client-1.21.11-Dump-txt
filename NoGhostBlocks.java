package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.events.entity.player.BreakBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.PlaceBlockEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2680;

public class NoGhostBlocks extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> breaking;
   public final Setting<Boolean> placing;

   public NoGhostBlocks() {
      super(Categories.World, "no-ghost-blocks", "Attempts to prevent ghost blocks arising.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.breaking = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("breaking")).description("Whether to apply for block breaking actions.")).defaultValue(true)).build());
      this.placing = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("placing")).description("Whether to apply for block placement actions.")).defaultValue(true)).build());
   }

   @EventHandler
   private void onBreakBlock(BreakBlockEvent event) {
      if (!this.mc.method_1542() && (Boolean)this.breaking.get()) {
         event.cancel();
         class_2680 blockState = this.mc.field_1687.method_8320(event.blockPos);
         blockState.method_26204().method_9576(this.mc.field_1687, event.blockPos, blockState, this.mc.field_1724);
      }
   }

   @EventHandler
   private void onPlaceBlock(PlaceBlockEvent event) {
      if ((Boolean)this.placing.get()) {
         event.cancel();
      }
   }
}
