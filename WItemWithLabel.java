package meteordevelopment.meteorclient.gui.widgets;

import java.util.Iterator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.class_1292;
import net.minecraft.class_1293;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_9334;

public class WItemWithLabel extends WHorizontalList {
   private class_1799 itemStack;
   private String name;
   private WItem item;
   private WLabel label;

   public WItemWithLabel(class_1799 itemStack, String name) {
      this.itemStack = itemStack;
      this.name = name;
   }

   public void init() {
      this.item = (WItem)this.add(this.theme.item(this.itemStack)).widget();
      String var10003 = this.name;
      this.label = (WLabel)this.add(this.theme.label(var10003 + this.getStringToAppend())).widget();
   }

   private String getStringToAppend() {
      String str = "";
      if (this.itemStack.method_7909() == class_1802.field_8574) {
         Iterator<class_1293> effects = ((class_1844)this.itemStack.method_7909().method_57347().method_58694(class_9334.field_49651)).method_57397().iterator();
         if (!effects.hasNext()) {
            return str;
         }

         str = str + " ";
         class_1293 effect = (class_1293)effects.next();
         if (effect.method_5578() > 0) {
            str = str + "%d ".formatted(effect.method_5578() + 1);
         }

         str = str + "(%s)".formatted(class_1292.method_5577(effect, 1.0F, MeteorClient.mc.field_1687 != null ? MeteorClient.mc.field_1687.method_54719().method_54748() : 20.0F).getString());
      }

      return str;
   }

   public void set(class_1799 itemStack) {
      this.itemStack = itemStack;
      this.item.itemStack = itemStack;
      this.name = Names.get(itemStack);
      String var10001 = this.name;
      this.label.set(var10001 + this.getStringToAppend());
   }

   public String getLabelText() {
      return this.label == null ? this.name : this.label.get();
   }
}
