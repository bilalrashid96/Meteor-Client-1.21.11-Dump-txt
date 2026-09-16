package meteordevelopment.meteorclient.events.render;

import net.minecraft.class_1799;
import net.minecraft.class_5632;

public class TooltipDataEvent {
   private static final TooltipDataEvent INSTANCE = new TooltipDataEvent();
   public class_5632 tooltipData;
   public class_1799 itemStack;

   public static TooltipDataEvent get(class_1799 itemStack) {
      INSTANCE.tooltipData = null;
      INSTANCE.itemStack = itemStack;
      return INSTANCE;
   }
}
