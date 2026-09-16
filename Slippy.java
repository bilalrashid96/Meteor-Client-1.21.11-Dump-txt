package meteordevelopment.meteorclient.systems.modules.movement;

import java.util.List;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.class_2248;

public class Slippy extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<Double> friction;
   public final Setting<ListMode> listMode;
   public final Setting<List<class_2248>> ignoredBlocks;
   public final Setting<List<class_2248>> allowedBlocks;

   public Slippy() {
      super(Categories.Movement, "slippy", "Changes the base friction level of blocks.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.friction = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("friction")).description("The base friction level.")).range(0.01, 1.1).sliderRange(0.01, 1.1).defaultValue((double)1.0F).build());
      this.listMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("list-mode")).description("The mode to select blocks.")).defaultValue(Slippy.ListMode.Blacklist)).build());
      this.ignoredBlocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("ignored-blocks")).description("Decide which blocks not to slip on")).visible(() -> this.listMode.get() == Slippy.ListMode.Blacklist)).build());
      this.allowedBlocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("allowed-blocks")).description("Decide which blocks to slip on")).visible(() -> this.listMode.get() == Slippy.ListMode.Whitelist)).build());
   }

   public static enum ListMode {
      Whitelist,
      Blacklist;

      // $FF: synthetic method
      private static ListMode[] $values() {
         return new ListMode[]{Whitelist, Blacklist};
      }
   }
}
