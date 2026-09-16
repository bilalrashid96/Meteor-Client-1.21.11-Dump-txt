package meteordevelopment.meteorclient.systems.modules.player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.AutoTotem;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;

public class AutoReplenish extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Integer> minCount;
   private final Setting<Integer> tickDelay;
   private final Setting<Boolean> offhand;
   private final Setting<Boolean> unstackable;
   private final Setting<Boolean> sameEnchants;
   private final Setting<Boolean> searchHotbar;
   private final Setting<List<class_1792>> excludedItems;
   private final class_1799[] items;
   private boolean prevHadOpenScreen;
   private int tickDelayLeft;

   public AutoReplenish() {
      super(Categories.Player, "auto-replenish", "Automatically refills items in your hotbar, main hand, or offhand.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.minCount = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("min-count")).description("Replenish a slot when it reaches this item count.")).defaultValue(8)).min(1).sliderRange(1, 63).build());
      this.tickDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("How long in ticks to wait between replenishing your hotbar.")).defaultValue(1)).min(0).build());
      this.offhand = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("offhand")).description("Whether or not to replenish items in your offhand.")).defaultValue(true)).build());
      this.unstackable = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("unstackable")).description("Replenish unstackable items.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("same-enchants")).description("Only replace unstackables with items that have the same enchants.")).defaultValue(true);
      Setting var10003 = this.unstackable;
      Objects.requireNonNull(var10003);
      this.sameEnchants = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.searchHotbar = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("search-hotbar")).description("Combine stacks in your hotbar/offhand as a last resort.")).defaultValue(false)).build());
      this.excludedItems = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("excluded-items")).description("Items that won't be replenished.")).build());
      this.items = new class_1799[10];
      Arrays.fill(this.items, class_1802.field_8162.method_7854());
   }

   public void onActivate() {
      this.fillItems();
      this.tickDelayLeft = (Integer)this.tickDelay.get();
      this.prevHadOpenScreen = this.mc.field_1755 != null;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.mc.field_1755 == null && this.prevHadOpenScreen) {
         this.fillItems();
      }

      this.prevHadOpenScreen = this.mc.field_1755 != null;
      if (this.mc.field_1724.field_7512.method_7602().size() == 46 && this.mc.field_1755 == null) {
         if (this.tickDelayLeft > 0) {
            --this.tickDelayLeft;
         } else {
            for(int i = 0; i < 9; ++i) {
               class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
               this.checkSlot(i, stack);
            }

            if ((Boolean)this.offhand.get() && !((AutoTotem)Modules.get().get(AutoTotem.class)).isLocked()) {
               class_1799 stack = this.mc.field_1724.method_6079();
               this.checkSlot(9, stack);
            }

            this.tickDelayLeft = (Integer)this.tickDelay.get();
         }
      }
   }

   private void checkSlot(int slot, class_1799 stack) {
      class_1799 prevStack = this.items[slot];
      this.items[slot] = stack.method_7972();
      if (slot == 9) {
         slot = 40;
      }

      if (!((List)this.excludedItems.get()).contains(stack.method_7909())) {
         if (!((List)this.excludedItems.get()).contains(prevStack.method_7909())) {
            int fromSlot = -1;
            if (stack.method_7946() && !stack.method_7960() && stack.method_7947() <= (Integer)this.minCount.get()) {
               fromSlot = this.findItem(stack, slot, (Integer)this.minCount.get() - stack.method_7947() + 1, true);
            }

            if (prevStack.method_7946() && stack.method_7960() && !prevStack.method_7960()) {
               fromSlot = this.findItem(prevStack, slot, (Integer)this.minCount.get() - stack.method_7947() + 1, false);
            }

            if ((Boolean)this.unstackable.get() && !prevStack.method_7946() && stack.method_7960() && !prevStack.method_7960()) {
               fromSlot = this.findItem(prevStack, slot, 1, false);
            }

            if (fromSlot != this.mc.field_1724.method_31548().method_67532() && fromSlot != 40) {
               if (fromSlot >= 9 || fromSlot >= slot || slot == this.mc.field_1724.method_31548().method_67532() || slot == 40) {
                  InvUtils.move().from(fromSlot).to(slot);
               }
            }
         }
      }
   }

   private int findItem(class_1799 lookForStack, int excludedSlot, int goodEnoughCount, boolean mustCombine) {
      int slot = -1;
      int count = 0;

      for(int i = this.mc.field_1724.method_31548().method_5439() - 2; i >= ((Boolean)this.searchHotbar.get() ? 0 : 9); --i) {
         if (i != excludedSlot) {
            class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
            if (stack.method_7909() == lookForStack.method_7909() && (!mustCombine || class_1799.method_31577(lookForStack, stack)) && (!(Boolean)this.sameEnchants.get() || stack.method_58657().equals(lookForStack.method_58657())) && stack.method_7947() > count) {
               slot = i;
               count = stack.method_7947();
               if (count >= goodEnoughCount) {
                  break;
               }
            }
         }
      }

      return slot;
   }

   private void fillItems() {
      for(int i = 0; i < 9; ++i) {
         this.items[i] = this.mc.field_1724.method_31548().method_5438(i).method_7972();
      }

      this.items[9] = this.mc.field_1724.method_6079().method_7972();
   }
}
