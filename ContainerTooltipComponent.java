package meteordevelopment.meteorclient.utils.tooltip;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_10799;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5684;

public class ContainerTooltipComponent implements class_5684, MeteorTooltipData {
   private static final class_2960 TEXTURE_CONTAINER_BACKGROUND = MeteorClient.identifier("textures/container.png");
   private final class_1799[] items;
   private final Color color;

   public ContainerTooltipComponent(class_1799[] items, Color color) {
      this.items = items;
      this.color = color;
   }

   public class_5684 getComponent() {
      return this;
   }

   public int method_32661(class_327 textRenderer) {
      return 67;
   }

   public int method_32664(class_327 textRenderer) {
      return 176;
   }

   public void method_32666(class_327 textRenderer, int x, int y, int width, int height, class_332 context) {
      context.method_25291(class_10799.field_56883, TEXTURE_CONTAINER_BACKGROUND, x, y, 0.0F, 0.0F, 176, 67, 176, 67, this.color.getPacked());
      int row = 0;
      int i = 0;

      for(class_1799 itemStack : this.items) {
         RenderUtils.drawItem(context, itemStack, x + 8 + i * 18, y + 7 + row * 18, 1.0F, true, (String)null, false);
         ++i;
         if (i >= 9) {
            i = 0;
            ++row;
         }
      }

   }
}
