package meteordevelopment.meteorclient.utils.entity.simulator;

import net.minecraft.class_239;

public class SimulationStep {
   public static final SimulationStep MISS = new SimulationStep(true, new class_239[0]);
   public boolean shouldStop;
   public class_239[] hitResults;

   public SimulationStep(boolean stop, class_239... hitResults) {
      this.shouldStop = stop;
      this.hitResults = hitResults;
   }
}
