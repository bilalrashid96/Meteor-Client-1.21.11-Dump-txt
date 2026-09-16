package meteordevelopment.meteorclient.systems.modules.player;

import java.util.List;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;

public class FastUse extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Mode> mode;
   private final Setting<List<class_1792>> items;
   private final Setting<Boolean> blocks;
   private final Setting<Integer> cooldown;

   public FastUse() {
      super(Categories.Player, "fast-use", "Allows you to use items at very high speeds.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Which items to fast use.")).defaultValue(FastUse.Mode.All)).build());
      this.items = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("items")).description("Which items should fast place work on in \"Some\" mode.")).visible(() -> this.mode.get() == FastUse.Mode.Some)).build());
      this.blocks = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("blocks")).description("Fast-places blocks if the mode is \"Some\" mode.")).visible(() -> this.mode.get() == FastUse.Mode.Some)).defaultValue(false)).build());
      this.cooldown = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("cooldown")).description("Fast-use cooldown in ticks.")).defaultValue(0)).min(0).sliderMax(4).build());
   }

   public int getItemUseCooldown(class_1799 itemStack) {
      return this.mode.get() != FastUse.Mode.All && !this.shouldWorkSome(itemStack) ? 4 : (Integer)this.cooldown.get();
   }

   private boolean shouldWorkSome(class_1799 itemStack) {
      return (Boolean)this.blocks.get() && itemStack.method_7909() instanceof class_1747 || ((List)this.items.get()).contains(itemStack.method_7909());
   }

   public static enum Mode {
      All,
      Some;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{All, Some};
      }
   }
}
