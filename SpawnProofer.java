package meteordevelopment.meteorclient.systems.modules.world;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2231;
import net.minecraft.class_2241;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2269;
import net.minecraft.class_2312;
import net.minecraft.class_2338;
import net.minecraft.class_2401;
import net.minecraft.class_243;
import net.minecraft.class_2482;
import net.minecraft.class_2538;
import net.minecraft.class_2577;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_8923;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class SpawnProofer extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Integer> placeDelay;
   private final Setting<Double> placeRange;
   private final Setting<Double> wallsRange;
   private final Setting<Integer> blocksPerTick;
   private final Setting<Integer> lightLevel;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Mode> mode;
   private final Setting<Boolean> rotate;
   private final Pool<class_2338.class_2339> spawnPool;
   private final List<class_2338.class_2339> spawns;
   private int timer;

   public SpawnProofer() {
      super(Categories.World, "spawn-proofer", "Automatically spawnproofs unlit areas.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.placeDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("The tick delay between placing blocks.")).defaultValue(1)).range(0, 10).build());
      this.placeRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("How far away from the player you can place a block.")).defaultValue((double)4.5F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.wallsRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("walls-range")).description("How far away from the player you can place a block behind walls.")).defaultValue((double)4.5F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.blocksPerTick = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("blocks-per-tick")).description("How many blocks to place in one tick.")).defaultValue(1)).min(1).build());
      this.lightLevel = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("light-level")).description("Light levels to spawn proof. Old spawning light: 7.")).defaultValue(0)).min(0).sliderMax(15).build());
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks")).description("Block to use for spawn proofing.")).defaultValue(class_2246.field_10336, class_2246.field_10494, class_2246.field_10454).filter(this::filterBlocks).build());
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Which spawn types should be spawn proofed.")).defaultValue(SpawnProofer.Mode.Both)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates towards the blocks being placed.")).defaultValue(true)).build());
      this.spawnPool = new Pool<class_2338.class_2339>(class_2338.class_2339::new);
      this.spawns = new ArrayList();
   }

   @EventHandler
   private void onTickPre(TickEvent.Pre event) {
      if (this.timer >= (Integer)this.placeDelay.get()) {
         boolean foundBlock = InvUtils.testInHotbar((Predicate)((itemStack) -> ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()))));
         if (!foundBlock) {
            this.error("Found none of the chosen blocks in hotbar.", new Object[0]);
            this.toggle();
         } else {
            this.spawnPool.freeAll(this.spawns);
            this.spawns.clear();
            BlockIterator.register((int)Math.ceil((Double)this.placeRange.get()), (int)Math.ceil((Double)this.placeRange.get()), (blockPos, blockState) -> {
               BlockUtils.MobSpawn spawn = BlockUtils.isValidMobSpawn(blockPos, blockState, (Integer)this.lightLevel.get());
               if (spawn == BlockUtils.MobSpawn.Always && (this.mode.get() == SpawnProofer.Mode.Always || this.mode.get() == SpawnProofer.Mode.Both) || spawn == BlockUtils.MobSpawn.Potential && (this.mode.get() == SpawnProofer.Mode.Potential || this.mode.get() == SpawnProofer.Mode.Both)) {
                  if (!BlockUtils.canPlace(blockPos)) {
                     return;
                  }

                  if (this.isOutOfRange(blockPos)) {
                     return;
                  }

                  this.spawns.add(((class_2338.class_2339)this.spawnPool.get()).method_10101(blockPos));
               }

            });
         }
      }
   }

   @EventHandler
   private void onTickPost(TickEvent.Post event) {
      if (this.timer++ >= (Integer)this.placeDelay.get()) {
         if (!this.spawns.isEmpty()) {
            FindItemResult block = InvUtils.findInHotbar((Predicate)((itemStack) -> ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()))));
            if (!block.found()) {
               this.error("Found none of the chosen blocks in hotbar.", new Object[0]);
               this.toggle();
            } else {
               int placedCount = 0;
               if (this.isLightSource(class_2248.method_9503(this.mc.field_1724.method_31548().method_5438(block.slot()).method_7909()))) {
                  this.spawns.sort(Comparator.comparingInt((blockPosx) -> this.mc.field_1687.method_22339(blockPosx)));
                  placedCount = (Integer)this.blocksPerTick.get() - 1;
               }

               for(class_2338 blockPos : this.spawns) {
                  if (placedCount < (Integer)this.blocksPerTick.get() && BlockUtils.place(blockPos, block, (Boolean)this.rotate.get(), -50, false)) {
                     ++placedCount;
                  }
               }

               this.timer = 0;
            }
         }
      }
   }

   private boolean isOutOfRange(class_2338 blockPos) {
      class_243 pos = blockPos.method_46558();
      if (!PlayerUtils.isWithin(pos, (Double)this.placeRange.get())) {
         return true;
      } else {
         class_3959 raycastContext = new class_3959(this.mc.field_1724.method_33571(), pos, class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
         class_3965 result = this.mc.field_1687.method_17742(raycastContext);
         if (result != null && result.method_17777().equals(blockPos)) {
            return false;
         } else {
            return !PlayerUtils.isWithin(pos, (Double)this.wallsRange.get());
         }
      }
   }

   private boolean filterBlocks(class_2248 block) {
      return this.isNonOpaqueBlock(block) || this.isLightSource(block);
   }

   private boolean isNonOpaqueBlock(class_2248 block) {
      return block instanceof class_2269 || block instanceof class_2482 || block instanceof class_2231 || block instanceof class_8923 || block instanceof class_2538 || block instanceof class_2577 || block instanceof class_2401 || block instanceof class_2312 || block instanceof class_2241;
   }

   private boolean isLightSource(class_2248 block) {
      return block.method_9564().method_26213() > 0;
   }

   public static enum Mode {
      Always,
      Potential,
      Both;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Always, Potential, Both};
      }
   }
}
