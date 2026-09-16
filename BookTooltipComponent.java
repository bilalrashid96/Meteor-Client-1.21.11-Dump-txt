package meteordevelopment.meteorclient.utils.tooltip;

import net.minecraft.class_10799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5481;
import net.minecraft.class_5684;
import org.joml.Matrix3x2fStack;

public class BookTooltipComponent implements class_5684, MeteorTooltipData {
   private static final class_2960 TEXTURE_BOOK_BACKGROUND = class_2960.method_60654("textures/gui/book.png");
   private final class_2561 page;

   public BookTooltipComponent(class_2561 page) {
      this.page = page;
   }

   public class_5684 getComponent() {
      return this;
   }

   public int method_32661(class_327 textRenderer) {
      return 134;
   }

   public int method_32664(class_327 textRenderer) {
      return 112;
   }

   public void method_32666(class_327 textRenderer, int x, int y, int width, int height, class_332 context) {
      context.method_25290(class_10799.field_56883, TEXTURE_BOOK_BACKGROUND, x - 10, y, 0.0F, 0.0F, 128, 128, 179, 179);
      Matrix3x2fStack matrices = context.method_51448();
      matrices.pushMatrix();
      matrices.translate((float)(x + 16), (float)(y + 12));
      matrices.scale(0.7F, 0.7F);
      int offset = 0;

      for(class_5481 line : textRenderer.method_1728(this.page, 112)) {
         context.method_51430(textRenderer, line, 0, offset, -16777216, false);
         offset += 8;
      }

      matrices.popMatrix();
   }
}
