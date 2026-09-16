package meteordevelopment.meteorclient.systems.modules.world;

import java.util.List;
import meteordevelopment.meteorclient.mixininterface.IAbstractFurnaceScreenHandler;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.class_10290;
import net.minecraft.class_1720;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;

public class AutoSmelter extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<class_1792>> fuelItems;
   private final Setting<List<class_1792>> smeltableItems;
   private final Setting<Boolean> disableWhenOutOfItems;

   public AutoSmelter() {
      super(Categories.World, "auto-smelter", "Automatically smelts items from your inventory");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.fuelItems = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("fuel-items")).description("Items to use as fuel")).defaultValue(class_1802.field_8713, class_1802.field_8665).filter(this::fuelItemFilter).bypassFilterWhenSavingAndLoading().build());
      this.smeltableItems = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("smeltable-items")).description("Items to smelt")).defaultValue(class_1802.field_8599, class_1802.field_8775, class_1802.field_27018, class_1802.field_33400, class_1802.field_33401, class_1802.field_33402).filter(this::smeltableItemFilter).bypassFilterWhenSavingAndLoading().build());
      this.disableWhenOutOfItems = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-when-out-of-items")).description("Disable the module when you run out of items")).defaultValue(true)).build());
   }

   private boolean fuelItemFilter(class_1792 item) {
      return !Utils.canUpdate() ? false : this.mc.method_1562().method_62147().method_61751().contains(item);
   }

   private boolean smeltableItemFilter(class_1792 item) {
      return this.mc.field_1687 != null && this.mc.field_1687.method_8433().method_64678(class_10290.field_54650).method_64701(item.method_7854());
   }

   public void tick(class_1720 c) {
      if (this.mc.field_1724.field_6012 % 10 != 0) {
         this.checkFuel(c);
         this.takeResults(c);
         this.insertItems(c);
      }
   }

   private void insertItems(class_1720 c) {
      class_1799 inputItemStack = ((class_1735)c.field_7761.getFirst()).method_7677();
      if (inputItemStack.method_7960()) {
         int slot = -1;

         for(int i = 3; i < c.field_7761.size(); ++i) {
            class_1799 item = ((class_1735)c.field_7761.get(i)).method_7677();
            if (((IAbstractFurnaceScreenHandler)c).meteor$isItemSmeltable(item) && ((List)this.smeltableItems.get()).contains(item.method_7909()) && this.smeltableItemFilter(item.method_7909())) {
               slot = i;
               break;
            }
         }

         if ((Boolean)this.disableWhenOutOfItems.get() && slot == -1) {
            this.error("You do not have any items in your inventory that can be smelted. Disabling.", new Object[0]);
            this.toggle();
         } else {
            InvUtils.move().fromId(slot).toId(0);
         }
      }
   }

   private void checkFuel(class_1720 c) {
      class_1799 fuelStack = ((class_1735)c.field_7761.get(1)).method_7677();
      if (!(c.method_17364() > 0.0F)) {
         if (fuelStack.method_7960()) {
            int slot = -1;

            for(int i = 3; i < c.field_7761.size(); ++i) {
               class_1799 item = ((class_1735)c.field_7761.get(i)).method_7677();
               if (((List)this.fuelItems.get()).contains(item.method_7909()) && this.fuelItemFilter(item.method_7909())) {
                  slot = i;
                  break;
               }
            }

            if ((Boolean)this.disableWhenOutOfItems.get() && slot == -1) {
               this.error("You do not have any fuel in your inventory. Disabling.", new Object[0]);
               this.toggle();
            } else {
               InvUtils.move().fromId(slot).toId(1);
            }
         }
      }
   }

   private void takeResults(class_1720 c) {
      class_1799 resultStack = ((class_1735)c.field_7761.get(2)).method_7677();
      if (!resultStack.method_7960()) {
         InvUtils.shiftClick().slotId(2);
         if (!resultStack.method_7960()) {
            this.error("Your inventory is full. Disabling.", new Object[0]);
            this.toggle();
         }

      }
   }
}
