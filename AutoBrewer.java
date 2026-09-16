package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.PotionSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.MyPotion;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.class_1074;
import net.minecraft.class_1708;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_1842;
import net.minecraft.class_1844;
import net.minecraft.class_1847;
import net.minecraft.class_6880;
import net.minecraft.class_9334;

public class AutoBrewer extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<MyPotion> potion;
   private int ingredientI;
   private boolean first;
   private int timer;

   public AutoBrewer() {
      super(Categories.World, "auto-brewer", "Automatically brews the specified potion.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.potion = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new PotionSetting.Builder()).name("potion")).description("The type of potion to brew.")).defaultValue(MyPotion.Strength)).build());
   }

   public void onActivate() {
      this.first = false;
   }

   public void onBrewingStandClose() {
      this.first = false;
   }

   public void tick(class_1708 c) {
      ++this.timer;
      if (!this.first) {
         this.first = true;
         this.ingredientI = -2;
         this.timer = 0;
      }

      if (c.method_17378() == 0 && this.timer >= 5) {
         if (this.ingredientI == -2) {
            if (this.takePotions(c)) {
               return;
            }

            ++this.ingredientI;
            this.timer = 0;
         } else if (this.ingredientI == -1) {
            if (this.insertWaterBottles(c)) {
               return;
            }

            ++this.ingredientI;
            this.timer = 0;
         } else if (this.ingredientI < (this.potion.get()).ingredients.length) {
            if (this.checkFuel(c)) {
               return;
            }

            if (this.insertIngredient(c, (this.potion.get()).ingredients[this.ingredientI])) {
               return;
            }

            ++this.ingredientI;
            this.timer = 0;
         } else {
            this.ingredientI = -2;
            this.timer = 0;
         }

      }
   }

   private boolean insertIngredient(class_1708 c, class_1792 ingredient) {
      int slot = -1;

      for(int slotI = 5; slotI < c.field_7761.size(); ++slotI) {
         if (((class_1735)c.field_7761.get(slotI)).method_7677().method_7909() == ingredient) {
            slot = slotI;
            break;
         }
      }

      if (slot == -1) {
         this.error("You do not have any %s left in your inventory... disabling.", new Object[]{class_1074.method_4662(ingredient.method_7876(), new Object[0])});
         this.toggle();
         return true;
      } else {
         this.moveOneItem(c, slot, 3);
         return false;
      }
   }

   private boolean checkFuel(class_1708 c) {
      if (c.method_17377() == 0) {
         int slot = -1;

         for(int slotI = 5; slotI < c.field_7761.size(); ++slotI) {
            if (((class_1735)c.field_7761.get(slotI)).method_7677().method_7909() == class_1802.field_8183) {
               slot = slotI;
               break;
            }
         }

         if (slot == -1) {
            this.error("You do not have a sufficient amount of blaze powder to use as fuel for the brew... disabling.", new Object[0]);
            this.toggle();
            return true;
         }

         this.moveOneItem(c, slot, 4);
      }

      return false;
   }

   private void moveOneItem(class_1708 c, int from, int to) {
      InvUtils.move().fromId(from).toId(to);
   }

   private boolean insertWaterBottles(class_1708 c) {
      for(int i = 0; i < 3; ++i) {
         int slot = -1;

         for(int slotI = 5; slotI < c.field_7761.size(); ++slotI) {
            if (((class_1735)c.field_7761.get(slotI)).method_7677().method_7909() == class_1802.field_8574) {
               class_1842 potion = (class_1842)((class_6880)((class_1844)((class_1735)c.field_7761.get(slotI)).method_7677().method_58694(class_9334.field_49651)).comp_2378().get()).comp_349();
               if (potion == class_1847.field_8991.comp_349()) {
                  slot = slotI;
                  break;
               }
            }
         }

         if (slot == -1) {
            this.error("You do not have a sufficient amount of water bottles to complete this brew... disabling.", new Object[0]);
            this.toggle();
            return true;
         }

         InvUtils.move().fromId(slot).toId(i);
      }

      return false;
   }

   private boolean takePotions(class_1708 c) {
      for(int i = 0; i < 3; ++i) {
         InvUtils.shiftClick().slotId(i);
         if (!((class_1735)c.field_7761.get(i)).method_7677().method_7960()) {
            this.error("You do not have a sufficient amount of inventory space... disabling.", new Object[0]);
            this.toggle();
            return true;
         }
      }

      return false;
   }
}
