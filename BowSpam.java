package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1744;
import net.minecraft.class_1764;
import net.minecraft.class_1799;
import net.minecraft.class_1802;

public class BowSpam extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgCrossbows;
   private final Setting<Integer> charge;
   private final Setting<Boolean> onlyWhenHoldingRightClick;
   private final Setting<Boolean> spamCrossbows;
   private final Setting<Integer> crossbowDelay;
   private final Setting<Boolean> searchInventory;
   private boolean wasBow;
   private boolean wasHoldingRightClick;
   private int ticks;

   public BowSpam() {
      super(Categories.Combat, "bow-spam", "Spams bows and crossbows.", "auto-bow", "crossbow-spam", "auto-crossbow");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgCrossbows = this.settings.createGroup("Crossbows");
      this.charge = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("charge")).description("How long to charge the bow before releasing in ticks.")).defaultValue(5)).range(4, 20).sliderRange(4, 20).build());
      this.onlyWhenHoldingRightClick = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("when-holding-right-click")).description("Works only when holding right click.")).defaultValue(false)).build());
      this.spamCrossbows = this.sgCrossbows.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("spam-crossbows")).description("Whether to spam loaded crossbows; takes priority over charging bows.")).defaultValue(true)).build());
      this.crossbowDelay = this.sgCrossbows.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("crossbow-delay")).description("Delay between shooting crossbows in ticks.")).defaultValue(10)).sliderRange(0, 20).min(0).build());
      this.searchInventory = this.sgCrossbows.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("search-inventory")).description("Whether to search your inventory to find loaded crossbows.")).defaultValue(true)).build());
      this.wasBow = false;
      this.wasHoldingRightClick = false;
      this.ticks = 0;
   }

   public void onActivate() {
      this.wasBow = false;
      this.wasHoldingRightClick = false;
   }

   public void onDeactivate() {
      this.setPressed(false);
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      FindItemResult crossbow = (Boolean)this.searchInventory.get() ? InvUtils.find(this::crossbow) : InvUtils.find(this::crossbow, 0, 8);
      if ((Boolean)this.spamCrossbows.get() && crossbow.found()) {
         if (this.ticks >= (Integer)this.crossbowDelay.get()) {
            int slot = crossbow.slot();
            if (!crossbow.isHotbar()) {
               FindItemResult valid = InvUtils.find((stack) -> stack.method_7960() || stack.method_31574(class_1802.field_8399) || stack.method_31574(class_1802.field_8107), 0, 8);
               if (!valid.found()) {
                  return;
               }

               InvUtils.quickSwap().fromId(valid.slot()).to(crossbow.slot());
               slot = valid.slot();
            }

            InvUtils.swap(slot, true);
            this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
            InvUtils.swapBack();
            this.ticks = 0;
         } else {
            ++this.ticks;
         }

      } else if (this.mc.field_1724.method_31549().field_7477 || InvUtils.find((Predicate)((itemStack) -> itemStack.method_7909() instanceof class_1744)).found()) {
         if ((Boolean)this.onlyWhenHoldingRightClick.get() && !this.mc.field_1690.field_1904.method_1434()) {
            if (this.wasHoldingRightClick) {
               this.setPressed(false);
               this.wasHoldingRightClick = false;
            }
         } else {
            boolean isBow = InvUtils.testInHands(class_1802.field_8102);
            if (!isBow && this.wasBow) {
               this.setPressed(false);
            }

            this.wasBow = isBow;
            if (!isBow) {
               return;
            }

            if (this.mc.field_1724.method_6048() >= (Integer)this.charge.get()) {
               this.mc.field_1761.method_2897(this.mc.field_1724);
            } else {
               this.setPressed(true);
            }

            this.wasHoldingRightClick = this.mc.field_1690.field_1904.method_1434();
         }

      }
   }

   private void setPressed(boolean pressed) {
      this.mc.field_1690.field_1904.method_23481(pressed);
   }

   private boolean crossbow(class_1799 stack) {
      return stack.method_7909() instanceof class_1764 && class_1764.method_7781(stack);
   }
}
