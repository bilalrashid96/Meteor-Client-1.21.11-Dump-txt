package meteordevelopment.meteorclient.utils.render;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_10799;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_1277;
import net.minecraft.class_1733;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_495;
import net.minecraft.class_9848;

public class PeekScreen extends class_495 {
   private final class_2960 TEXTURE = class_2960.method_60654("textures/gui/container/shulker_box.png");
   private final class_1799 storageBlock;

   public PeekScreen(class_1799 storageBlock, class_1799[] contents) {
      super(new class_1733(0, MeteorClient.mc.field_1724.method_31548(), new class_1277(contents)), MeteorClient.mc.field_1724.method_31548(), storageBlock.method_7964());
      this.storageBlock = storageBlock;
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      BetterTooltips tooltips = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      if (tooltips.shouldOpenContents(click) && this.field_2787 != null && !this.field_2787.method_7677().method_7960() && MeteorClient.mc.field_1724.field_7512.method_34255().method_7960()) {
         class_1799 itemStack = this.field_2787.method_7677();
         return tooltips.openContent(itemStack);
      } else {
         return false;
      }
   }

   public boolean method_25406(class_11909 click) {
      return false;
   }

   public boolean method_25404(class_11908 input) {
      BetterTooltips tooltips = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      if (tooltips.shouldOpenContents(input) && this.field_2787 != null && !this.field_2787.method_7677().method_7960() && MeteorClient.mc.field_1724.field_7512.method_34255().method_7960()) {
         class_1799 itemStack = this.field_2787.method_7677();
         if (tooltips.openContent(itemStack)) {
            return true;
         }
      }

      if (input.comp_4795() != 256 && !MeteorClient.mc.field_1690.field_1822.method_1417(input)) {
         return false;
      } else {
         this.method_25419();
         return true;
      }
   }

   protected void method_2389(class_332 context, float delta, int mouseX, int mouseY) {
      Color color = Utils.getShulkerColor(this.storageBlock);
      int i = (this.field_22789 - this.field_2792) / 2;
      int j = (this.field_22790 - this.field_2779) / 2;
      context.method_25293(class_10799.field_56883, this.TEXTURE, i, j, 0.0F, 0.0F, this.field_2792, this.field_2779, this.field_2792, this.field_2779, 256, 256, class_9848.method_61318((float)color.a / 255.0F, (float)color.r / 255.0F, (float)color.g / 255.0F, (float)color.b / 255.0F));
   }
}
