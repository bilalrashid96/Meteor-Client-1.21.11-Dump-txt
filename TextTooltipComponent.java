package meteordevelopment.meteorclient.utils.tooltip;

import net.minecraft.class_2561;
import net.minecraft.class_5481;
import net.minecraft.class_5683;
import net.minecraft.class_5684;

public class TextTooltipComponent extends class_5683 implements MeteorTooltipData {
   public TextTooltipComponent(class_5481 text) {
      super(text);
   }

   public TextTooltipComponent(class_2561 text) {
      this(text.method_30937());
   }

   public class_5684 getComponent() {
      return this;
   }
}
