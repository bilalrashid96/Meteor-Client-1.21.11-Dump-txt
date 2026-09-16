package meteordevelopment.meteorclient.utils.tooltip;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.DrawContextAccessor;
import meteordevelopment.meteorclient.utils.render.CustomBannerGuiElementRenderState;
import net.minecraft.class_10377;
import net.minecraft.class_1746;
import net.minecraft.class_1767;
import net.minecraft.class_1799;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5602;
import net.minecraft.class_5684;
import net.minecraft.class_630;
import net.minecraft.class_9307;
import net.minecraft.class_9334;

public class BannerTooltipComponent implements MeteorTooltipData, class_5684 {
   private final class_1767 color;
   private final class_9307 patterns;
   private final class_10377 bannerFlag;

   public BannerTooltipComponent(class_1799 banner) {
      this.color = ((class_1746)banner.method_7909()).method_7706();
      this.patterns = (class_9307)banner.method_58695(class_9334.field_49619, class_9307.field_49404);
      class_630 modelPart = MeteorClient.mc.method_31974().method_32072(class_5602.field_55122);
      this.bannerFlag = new class_10377(modelPart);
   }

   public BannerTooltipComponent(class_1767 color, class_9307 patterns) {
      this.color = color;
      this.patterns = patterns;
      class_630 modelPart = MeteorClient.mc.method_31974().method_32072(class_5602.field_55122);
      this.bannerFlag = new class_10377(modelPart);
   }

   public class_5684 getComponent() {
      return this;
   }

   public int method_32661(class_327 textRenderer) {
      return 80;
   }

   public int method_32664(class_327 textRenderer) {
      return 40;
   }

   public void method_32666(class_327 textRenderer, int x, int y, int width, int height, class_332 context) {
      int centerX = width / 2 - this.method_32664((class_327)null) / 2;
      DrawContextAccessor contextAccessor = (DrawContextAccessor)context;
      contextAccessor.getState().method_70922(new CustomBannerGuiElementRenderState(this.bannerFlag, this.color, this.patterns, centerX + x, y, centerX + x + this.method_32664((class_327)null), y + this.method_32661((class_327)null), contextAccessor.getScissorStack().method_70863(), 32.0F));
   }
}
