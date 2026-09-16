package meteordevelopment.meteorclient.events.entity.player;

import net.minecraft.class_3610;

public class CanWalkOnFluidEvent {
   private static final CanWalkOnFluidEvent INSTANCE = new CanWalkOnFluidEvent();
   public class_3610 fluidState;
   public boolean walkOnFluid;

   public static CanWalkOnFluidEvent get(class_3610 fluid) {
      INSTANCE.fluidState = fluid;
      INSTANCE.walkOnFluid = false;
      return INSTANCE;
   }
}
