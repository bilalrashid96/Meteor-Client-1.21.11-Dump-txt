package meteordevelopment.meteorclient.gui.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_10799;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_5537;
import net.minecraft.class_9276;
import net.minecraft.class_9334;

public class ContainerInventoryScreen extends class_437 {
   private static final class_2960 SLOT_TEXTURE = class_2960.method_60656("container/slot");
   private static final int SLOT_SIZE = 18;
   private static final int SCREEN_WIDTH = 176;
   private final List<class_1799> containerItems;
   private final class_1661 playerInventory;
   private final int containerRows;
   private int x;
   private int y;
   private int baseX;
   private int baseY;
   private int playerY;

   public ContainerInventoryScreen(class_1799 containerItem) {
      super(containerItem.method_7964());
      this.playerInventory = MeteorClient.mc.field_1724.method_31548();
      this.containerItems = new ArrayList();
      if (containerItem.method_7909() instanceof class_5537) {
         class_9276 bundleContents = (class_9276)containerItem.method_58694(class_9334.field_49650);
         if (bundleContents != null) {
            Iterable var10000 = bundleContents.method_57421();
            List var10001 = this.containerItems;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::add);
         }
      } else {
         class_1799[] tempItems = new class_1799[64];
         Utils.getItemsInContainerItem(containerItem, tempItems);
         Collections.addAll(this.containerItems, tempItems);
      }

      this.containerRows = Math.max(1, class_3532.method_38788(this.containerItems.size(), 9));
   }

   protected void method_25426() {
      super.method_25426();
      this.x = (this.field_22789 - 176) / 2;
      this.y = (this.field_22790 - (114 + this.containerRows * 18 + 20)) / 2;
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      this.baseX = this.x + 8;
      this.baseY = this.y + 18;
      this.playerY = this.baseY + this.containerRows * 18 + 20;

      for(int row = 0; row < this.containerRows + 4; ++row) {
         for(int col = 0; col < 9; ++col) {
            int slotY = row < this.containerRows ? this.baseY + row * 18 : this.playerY + (row - this.containerRows) * 18;
            context.method_52706(class_10799.field_56883, SLOT_TEXTURE, this.baseX + col * 18, slotY, 18, 18);
         }
      }

      for(int i = 0; i < this.containerItems.size(); ++i) {
         class_1799 item = (class_1799)this.containerItems.get(i);
         if (!item.method_7960()) {
            int itemX = this.baseX + i % 9 * 18 + 1;
            int itemY = this.baseY + i / 9 * 18 + 1;
            context.method_51427(item, itemX, itemY);
            context.method_51431(this.field_22793, item, itemX, itemY);
         }
      }

      for(int row = 0; row < 4; ++row) {
         for(int col = 0; col < 9; ++col) {
            int slotIndex = row < 3 ? 9 + row * 9 + col : col;
            class_1799 item = this.playerInventory.method_5438(slotIndex);
            if (!item.method_7960()) {
               int itemX = this.baseX + col * 18 + 1;
               int itemY = this.playerY + row * 18 + 1;
               context.method_51427(item, itemX, itemY);
               context.method_51431(this.field_22793, item, itemX, itemY);
            }
         }
      }

      context.method_51448().pushMatrix();
      context.method_51448().translate((float)this.x, (float)this.y);
      if (this.field_22793 != null) {
         context.method_51439(this.field_22793, this.field_22785, 8, 6, -12566464, false);
         context.method_51439(this.field_22793, this.playerInventory.method_5476(), 8, 18 + this.containerRows * 18 + 10, -12566464, false);
      }

      context.method_51448().popMatrix();
      class_1799 item = this.getSelectedItem(mouseX, mouseY);
      if (!item.method_7960()) {
         context.method_64038(this.field_22793, method_25408(MeteorClient.mc, item), item.method_32347(), mouseX, mouseY);
      }

   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      BetterTooltips tooltips = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      class_1799 stack = this.getSelectedItem((int)click.comp_4798(), (int)click.comp_4799());
      return tooltips.shouldOpenContents(click) ? tooltips.openContent(stack) : false;
   }

   public boolean method_25404(class_11908 input) {
      BetterTooltips tooltips = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      class_1799 stack = this.getSelectedItem((int)MeteorClient.mc.field_1729.method_68879(MeteorClient.mc.method_22683()), (int)MeteorClient.mc.field_1729.method_68883(MeteorClient.mc.method_22683()));
      if (tooltips.shouldOpenContents(input)) {
         return tooltips.openContent(stack);
      } else if (input.comp_4795() != 256 && !MeteorClient.mc.field_1690.field_1822.method_1417(input)) {
         return false;
      } else {
         this.method_25419();
         return true;
      }
   }

   private class_1799 getSelectedItem(int mouseX, int mouseY) {
      if (mouseX >= this.baseX && mouseX <= this.baseX + 162) {
         int col = (mouseX - this.baseX) / 18;
         if (col > 8) {
            return class_1799.field_8037;
         } else if (mouseY >= this.baseY && mouseY < this.baseY + this.containerRows * 18) {
            int index = (mouseY - this.baseY) / 18 * 9 + col;
            return index < this.containerItems.size() ? (class_1799)this.containerItems.get(index) : class_1799.field_8037;
         } else if (mouseY >= this.playerY && mouseY < this.playerY + 72) {
            int row = (mouseY - this.playerY) / 18;
            int slotIndex = row < 3 ? 9 + row * 9 + col : col;
            return this.playerInventory.method_5438(slotIndex);
         } else {
            return class_1799.field_8037;
         }
      } else {
         return class_1799.field_8037;
      }
   }
}
