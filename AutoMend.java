package meteordevelopment.meteorclient.systems.modules.player;

import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1893;
import net.minecraft.class_9334;

public class AutoMend extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<class_1792>> blacklist;
   private final Setting<Boolean> force;
   private final Setting<Boolean> autoDisable;
   private boolean didMove;

   public AutoMend() {
      super(Categories.Player, "auto-mend", "Automatically replaces items in your offhand with mending when fully repaired.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.blacklist = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("blacklist")).description("Item blacklist.")).filter((item) -> item.method_57347().method_58694(class_9334.field_49629) != null).build());
      this.force = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("force")).description("Replaces item in offhand even if there is some other non-repairable item.")).defaultValue(false)).build());
      this.autoDisable = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-disable")).description("Automatically disables when there are no more items to repair.")).defaultValue(true)).build());
   }

   public void onActivate() {
      this.didMove = false;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (!this.shouldWait()) {
         int slot = this.getSlot();
         if (slot == -1) {
            if ((Boolean)this.autoDisable.get()) {
               this.info("Repaired all items, disabling", new Object[0]);
               if (this.didMove) {
                  int emptySlot = this.getEmptySlot();
                  InvUtils.move().fromOffhand().to(emptySlot);
               }

               this.toggle();
            }
         } else {
            InvUtils.move().from(slot).toOffhand();
            this.didMove = true;
         }

      }
   }

   private boolean shouldWait() {
      class_1799 itemStack = this.mc.field_1724.method_6079();
      if (itemStack.method_7960()) {
         return false;
      } else if (Utils.hasEnchantments(itemStack, class_1893.field_9101)) {
         return itemStack.method_7919() != 0;
      } else {
         return !(Boolean)this.force.get();
      }
   }

   private int getSlot() {
      for(int i = 0; i < this.mc.field_1724.method_31548().method_67533().size(); ++i) {
         class_1799 itemStack = this.mc.field_1724.method_31548().method_5438(i);
         if (!((List)this.blacklist.get()).contains(itemStack.method_7909()) && Utils.hasEnchantments(itemStack, class_1893.field_9101) && itemStack.method_7919() > 0) {
            return i;
         }
      }

      return -1;
   }

   private int getEmptySlot() {
      for(int i = 0; i < this.mc.field_1724.method_31548().method_67533().size(); ++i) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_7960()) {
            return i;
         }
      }

      return -1;
   }
}
