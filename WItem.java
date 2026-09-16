package meteordevelopment.meteorclient.gui.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import net.minecraft.class_1799;

public class WItem extends WWidget {
   protected class_1799 itemStack;

   public WItem(class_1799 itemStack) {
      this.itemStack = itemStack;
   }

   protected void onCalculateSize() {
      double s = this.theme.scale((double)32.0F);
      this.width = s;
      this.height = s;
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (!this.itemStack.method_7960()) {
         renderer.post(() -> {
            double s = this.theme.scale((double)2.0F);
            renderer.item(this.itemStack, (int)this.x, (int)this.y, (float)s, true);
         });
      }

   }

   public void set(class_1799 itemStack) {
      this.itemStack = itemStack;
   }
}
