package meteordevelopment.meteorclient.systems.modules.world;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.Settings;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.process.ICustomGoalProcess;
import baritone.api.process.IMineProcess;
import java.util.List;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1893;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2404;
import net.minecraft.class_2561;
import net.minecraft.class_2661;
import net.minecraft.class_3489;

public class InfinityMiner extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgWhenFull;
   public final Setting<List<class_2248>> targetBlocks;
   public final Setting<List<class_1792>> targetItems;
   public final Setting<List<class_2248>> repairBlocks;
   public final Setting<Double> startRepairing;
   public final Setting<Double> startMining;
   public final Setting<Boolean> walkHome;
   public final Setting<Boolean> logOut;
   private final IBaritone baritone;
   private final Settings baritoneSettings;
   private final class_2338.class_2339 homePos;
   private boolean prevMineScanDroppedItems;
   private boolean repairing;

   public InfinityMiner() {
      super(Categories.World, "infinity-miner", "Allows you to essentially mine forever by mining repair blocks when the durability gets low. Needs a mending pickaxe.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgWhenFull = this.settings.createGroup("When Full");
      this.targetBlocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("target-blocks")).description("The target blocks to mine.")).defaultValue(class_2246.field_10442, class_2246.field_29029).filter(this::filterBlocks).build());
      this.targetItems = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("target-items")).description("The target items to collect.")).defaultValue(class_1802.field_8477).build());
      this.repairBlocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("repair-blocks")).description("The repair blocks to mine.")).defaultValue(class_2246.field_10418, class_2246.field_10080, class_2246.field_10213).filter(this::filterBlocks).build());
      this.startRepairing = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("repair-threshold")).description("The durability percentage at which to start repairing.")).defaultValue((double)20.0F).range((double)1.0F, (double)99.0F).sliderRange((double)1.0F, (double)99.0F).build());
      this.startMining = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("mine-threshold")).description("The durability percentage at which to start mining.")).defaultValue((double)70.0F).range((double)1.0F, (double)99.0F).sliderRange((double)1.0F, (double)99.0F).build());
      this.walkHome = this.sgWhenFull.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("walk-home")).description("Will walk 'home' when your inventory is full.")).defaultValue(false)).build());
      this.logOut = this.sgWhenFull.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("log-out")).description("Logs out when your inventory is full. Will walk home FIRST if walk home is enabled.")).defaultValue(false)).build());
      this.baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
      this.baritoneSettings = BaritoneAPI.getSettings();
      this.homePos = new class_2338.class_2339();
   }

   public void onActivate() {
      this.prevMineScanDroppedItems = (Boolean)this.baritoneSettings.mineScanDroppedItems.value;
      this.baritoneSettings.mineScanDroppedItems.value = true;
      this.homePos.method_10101(this.mc.field_1724.method_24515());
      this.repairing = false;
   }

   public void onDeactivate() {
      this.baritone.getPathingBehavior().cancelEverything();
      this.baritoneSettings.mineScanDroppedItems.value = this.prevMineScanDroppedItems;
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.isFull()) {
         if ((Boolean)this.walkHome.get()) {
            if (this.isBaritoneNotWalking()) {
               this.info("Walking home.", new Object[0]);
               this.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(this.homePos));
            } else if (this.mc.field_1724.method_24515().equals(this.homePos) && (Boolean)this.logOut.get()) {
               this.logOut();
            }
         } else if ((Boolean)this.logOut.get()) {
            this.logOut();
         } else {
            this.info("Inventory full, stopping process.", new Object[0]);
            this.toggle();
         }

      } else if (!this.findPickaxe()) {
         this.error("Could not find a usable mending pickaxe.", new Object[0]);
         this.toggle();
      } else if (!this.checkThresholds()) {
         this.error("Start mining value can't be lower than start repairing value.", new Object[0]);
         this.toggle();
      } else {
         if (this.repairing) {
            if (!this.needsRepair()) {
               this.warning("Finished repairing, going back to mining.", new Object[0]);
               this.repairing = false;
               this.baritoneSettings.mineScanDroppedItems.value = true;
               this.mineTargetBlocks();
               return;
            }

            if (this.isBaritoneNotMining()) {
               this.mineRepairBlocks();
            }
         } else {
            if (this.needsRepair()) {
               this.warning("Pickaxe needs repair, beginning repair process", new Object[0]);
               this.repairing = true;
               this.baritoneSettings.mineScanDroppedItems.value = false;
               this.mineRepairBlocks();
               return;
            }

            if (this.isBaritoneNotMining()) {
               this.mineTargetBlocks();
            }
         }

      }
   }

   private boolean needsRepair() {
      class_1799 itemStack = this.mc.field_1724.method_6047();
      double toolPercentage = (double)((float)(itemStack.method_7936() - itemStack.method_7919()) * 100.0F / (float)itemStack.method_7936());
      return !(toolPercentage > (Double)this.startMining.get()) && (!(toolPercentage > (Double)this.startRepairing.get()) || this.repairing);
   }

   private boolean findPickaxe() {
      Predicate<class_1799> pickaxePredicate = (stack) -> stack.method_31573(class_3489.field_42614) && Utils.hasEnchantment(stack, class_1893.field_9101) && !Utils.hasEnchantment(stack, class_1893.field_9099);
      FindItemResult bestPick = InvUtils.findInHotbar(pickaxePredicate);
      if (bestPick.isOffhand()) {
         InvUtils.shiftClick().fromOffhand().toHotbar(this.mc.field_1724.method_31548().method_67532());
      } else if (bestPick.isHotbar()) {
         InvUtils.swap(bestPick.slot(), false);
      }

      return InvUtils.testInMainHand(pickaxePredicate);
   }

   private boolean checkThresholds() {
      return (Double)this.startRepairing.get() < (Double)this.startMining.get();
   }

   private void mineTargetBlocks() {
      class_2248[] array = new class_2248[((List)this.targetBlocks.get()).size()];
      this.baritone.getPathingBehavior().cancelEverything();
      this.baritone.getMineProcess().mine((class_2248[])((List)this.targetBlocks.get()).toArray(array));
   }

   private void mineRepairBlocks() {
      class_2248[] array = new class_2248[((List)this.repairBlocks.get()).size()];
      this.baritone.getPathingBehavior().cancelEverything();
      this.baritone.getMineProcess().mine((class_2248[])((List)this.repairBlocks.get()).toArray(array));
   }

   private void logOut() {
      this.toggle();
      this.mc.field_1724.field_3944.method_52787(new class_2661(class_2561.method_43470("[Infinity Miner] Inventory is full.")));
   }

   private boolean isBaritoneNotMining() {
      return !(this.baritone.getPathingControlManager().mostRecentInControl().orElse((Object)null) instanceof IMineProcess);
   }

   private boolean isBaritoneNotWalking() {
      return !(this.baritone.getPathingControlManager().mostRecentInControl().orElse((Object)null) instanceof ICustomGoalProcess);
   }

   private boolean filterBlocks(class_2248 block) {
      return block != class_2246.field_10124 && block.method_9564().method_26214(this.mc.field_1687, (class_2338)null) != -1.0F && !(block instanceof class_2404);
   }

   private boolean isFull() {
      for(int i = 0; i <= 35; ++i) {
         class_1799 itemStack = this.mc.field_1724.method_31548().method_5438(i);
         if (itemStack.method_7960()) {
            return false;
         }

         for(class_1792 item : this.targetItems.get()) {
            if (itemStack.method_7909() == item && itemStack.method_7947() < itemStack.method_7914()) {
               return false;
            }
         }
      }

      return true;
   }
}
