package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Objects;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1893;
import net.minecraft.class_9274;

public class AutoEXP extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Mode> mode;
   private final Setting<Boolean> replenish;
   private final Setting<Boolean> onlyGround;
   private final Setting<Integer> slot;
   private final Setting<Integer> minThreshold;
   private final Setting<Integer> maxThreshold;
   private int repairingI;

   public AutoEXP() {
      super(Categories.Combat, "auto-exp", "Automatically repairs your armor and tools in pvp.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Which items to repair.")).defaultValue(AutoEXP.Mode.Both)).build());
      this.replenish = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("replenish")).description("Automatically replenishes exp into a selected hotbar slot.")).defaultValue(true)).build());
      this.onlyGround = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Only throw when the player is on the ground.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      IntSetting.Builder var10002 = (IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("exp-slot")).description("The slot to replenish exp into.");
      Setting var10003 = this.replenish;
      Objects.requireNonNull(var10003);
      this.slot = var10001.add(((IntSetting.Builder)((IntSetting.Builder)var10002.visible(var10003::get)).defaultValue(6)).range(1, 9).sliderRange(1, 9).build());
      this.minThreshold = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("min-threshold")).description("The minimum durability percentage that an item needs to fall to, to be repaired.")).defaultValue(30)).range(1, 100).sliderRange(1, 100).build());
      this.maxThreshold = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-threshold")).description("The maximum durability percentage to repair items to.")).defaultValue(80)).range(1, 100).sliderRange(1, 100).build());
   }

   public void onActivate() {
      this.repairingI = -1;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (!(Boolean)this.onlyGround.get() || this.mc.field_1724.method_24828()) {
         if (this.repairingI == -1) {
            if (this.mode.get() != AutoEXP.Mode.Hands) {
               for(class_1304 slot : class_9274.field_49224) {
                  class_1799 stack = this.mc.field_1724.method_6118(slot);
                  if (this.needsRepair(stack, (double)(Integer)this.minThreshold.get())) {
                     this.repairingI = 36 + slot.method_5927();
                     break;
                  }
               }
            }

            if (this.mode.get() != AutoEXP.Mode.Armor && this.repairingI == -1) {
               for(class_1268 hand : class_1268.values()) {
                  if (this.needsRepair(this.mc.field_1724.method_5998(hand), (double)(Integer)this.minThreshold.get())) {
                     this.repairingI = hand == class_1268.field_5808 ? this.mc.field_1724.method_31548().method_67532() : 40;
                     break;
                  }
               }
            }
         }

         if (this.repairingI != -1) {
            if (!this.needsRepair(this.mc.field_1724.method_31548().method_5438(this.repairingI), (double)(Integer)this.maxThreshold.get())) {
               this.repairingI = -1;
               return;
            }

            FindItemResult exp = InvUtils.find(class_1802.field_8287);
            if (exp.found()) {
               if (!exp.isHotbar() && !exp.isOffhand()) {
                  if (!(Boolean)this.replenish.get()) {
                     return;
                  }

                  InvUtils.move().from(exp.slot()).toHotbar((Integer)this.slot.get() - 1);
               }

               Rotations.rotate((double)this.mc.field_1724.method_36454(), (double)90.0F, () -> {
                  if (exp.getHand() != null) {
                     this.mc.field_1761.method_2919(this.mc.field_1724, exp.getHand());
                  } else {
                     InvUtils.swap(exp.slot(), true);
                     this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
                     InvUtils.swapBack();
                  }

               });
            }
         }

      }
   }

   private boolean needsRepair(class_1799 itemStack, double threshold) {
      if (!itemStack.method_7960() && Utils.hasEnchantments(itemStack, class_1893.field_9101)) {
         return (double)(itemStack.method_7936() - itemStack.method_7919()) / (double)itemStack.method_7936() * (double)100.0F <= threshold;
      } else {
         return false;
      }
   }

   public static enum Mode {
      Armor,
      Hands,
      Both;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Armor, Hands, Both};
      }
   }
}
