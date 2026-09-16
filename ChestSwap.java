package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.class_10192;
import net.minecraft.class_1304;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2815;
import net.minecraft.class_9334;

public class ChestSwap extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Chestplate> chestplate;
   private final Setting<Boolean> stayOn;
   private final Setting<Boolean> closeInventory;

   public ChestSwap() {
      super(Categories.Player, "chest-swap", "Automatically swaps between a chestplate and an elytra.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.chestplate = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("chestplate")).description("Which type of chestplate to swap to.")).defaultValue(ChestSwap.Chestplate.PreferNetherite)).build());
      this.stayOn = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("stay-on")).description("Stays on and activates when you turn it off.")).defaultValue(false)).build());
      this.closeInventory = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("close-inventory")).description("Sends inventory close after swap.")).defaultValue(true)).build());
   }

   public void onActivate() {
      this.swap();
      if (!(Boolean)this.stayOn.get()) {
         this.toggle();
      }

   }

   public void onDeactivate() {
      if ((Boolean)this.stayOn.get()) {
         this.swap();
      }

   }

   public void swap() {
      class_1799 currentItem = this.mc.field_1724.method_6118(class_1304.field_6174);
      if (currentItem.method_57826(class_9334.field_54197)) {
         this.equipChestplate();
      } else if (currentItem.method_57826(class_9334.field_54196) && ((class_10192)currentItem.method_58694(class_9334.field_54196)).comp_3174().method_5927() == class_1304.field_6174.method_5927()) {
         this.equipElytra();
      } else if (!this.equipChestplate()) {
         this.equipElytra();
      }

   }

   private boolean equipChestplate() {
      int bestSlot = -1;
      boolean breakLoop = false;

      for(int i = 0; i < this.mc.field_1724.method_31548().method_67533().size(); ++i) {
         class_1792 item = ((class_1799)this.mc.field_1724.method_31548().method_67533().get(i)).method_7909();
         switch (((Chestplate)this.chestplate.get()).ordinal()) {
            case 0:
               if (item == class_1802.field_8058) {
                  bestSlot = i;
                  breakLoop = true;
               }
               break;
            case 1:
               if (item == class_1802.field_22028) {
                  bestSlot = i;
                  breakLoop = true;
               }
               break;
            case 2:
               if (item == class_1802.field_8058) {
                  bestSlot = i;
                  breakLoop = true;
               } else if (item == class_1802.field_22028) {
                  bestSlot = i;
               }
               break;
            case 3:
               if (item == class_1802.field_8058) {
                  bestSlot = i;
               } else if (item == class_1802.field_22028) {
                  bestSlot = i;
                  breakLoop = true;
               }
         }

         if (breakLoop) {
            break;
         }
      }

      if (bestSlot != -1) {
         this.equip(bestSlot);
      }

      return bestSlot != -1;
   }

   private void equipElytra() {
      for(int i = 0; i < this.mc.field_1724.method_31548().method_67533().size(); ++i) {
         class_1799 item = (class_1799)this.mc.field_1724.method_31548().method_67533().get(i);
         if (item.method_57826(class_9334.field_54197)) {
            this.equip(i);
            break;
         }
      }

   }

   private void equip(int slot) {
      InvUtils.move().from(slot).toArmor(2);
      if ((Boolean)this.closeInventory.get()) {
         this.mc.method_1562().method_52787(new class_2815(0));
      }

   }

   public void sendToggledMsg() {
      if ((Boolean)this.stayOn.get()) {
         super.sendToggledMsg();
      } else if ((Boolean)Config.get().chatFeedback.get() && this.chatFeedback) {
         this.info("Triggered (highlight)%s(default).", new Object[]{this.title});
      }

   }

   public static enum Chestplate {
      Diamond,
      Netherite,
      PreferDiamond,
      PreferNetherite;

      // $FF: synthetic method
      private static Chestplate[] $values() {
         return new Chestplate[]{Diamond, Netherite, PreferDiamond, PreferNetherite};
      }
   }
}
