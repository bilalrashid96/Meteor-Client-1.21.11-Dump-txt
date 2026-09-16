package meteordevelopment.meteorclient.utils.render;

import net.minecraft.class_1088;
import net.minecraft.class_11239;
import net.minecraft.class_11661;
import net.minecraft.class_11683;
import net.minecraft.class_11684;
import net.minecraft.class_11701;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_823;
import net.minecraft.class_308.class_11274;

public class CustomBannerGuiElementRenderer extends class_11239<CustomBannerGuiElementRenderState> {
   private final class_11701 sprite;

   public CustomBannerGuiElementRenderer(class_4597.class_4598 immediate, class_11701 sprite) {
      super(immediate);
      this.sprite = sprite;
   }

   public Class<CustomBannerGuiElementRenderState> method_70903() {
      return CustomBannerGuiElementRenderState.class;
   }

   protected void render(CustomBannerGuiElementRenderState state, class_4587 matrixStack) {
      class_310.method_1551().field_1773.method_71114().method_71034(class_11274.field_60026);
      matrixStack.method_46416(0.0F, 0.25F, 0.0F);
      class_11684 renderDispatcher = class_310.method_1551().field_1773.method_72911();
      class_11661 orderedRenderCommandQueueImpl = renderDispatcher.method_73003();
      class_823.method_23802(this.sprite, matrixStack, orderedRenderCommandQueueImpl, 15728880, class_4608.field_21444, state.flag(), 0.0F, class_1088.field_20847, true, state.baseColor(), state.resultBannerPatterns(), false, (class_11683.class_11792)null, 0);
      renderDispatcher.method_73002();
   }

   protected String method_70906() {
      return "custom banner";
   }
}
