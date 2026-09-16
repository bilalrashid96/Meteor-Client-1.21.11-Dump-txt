package meteordevelopment.meteorclient.modintegration;

import java.util.function.BooleanSupplier;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Fullbright;
import meteordevelopment.meteorclient.systems.modules.render.Xray;

public class BadOptimizationsHook implements BooleanSupplier {
   private int lastState;

   public boolean getAsBoolean() {
      Modules m = Modules.get();
      if (m == null) {
         return false;
      } else {
         int state = (((Fullbright)m.get(Fullbright.class)).getGamma() ? 1 : 0) | (m.isActive(Xray.class) ? 2 : 0);
         boolean changed = state != this.lastState;
         this.lastState = state;
         return changed;
      }
   }
}
