package meteordevelopment.meteorclient.systems.modules.movement;

import com.google.common.collect.Streams;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_238;
import net.minecraft.class_265;

public class Parkour extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> edgeDistance;

   public Parkour() {
      super(Categories.Movement, "parkour", "Automatically jumps at the edges of blocks.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.edgeDistance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("edge-distance")).description("How far from the edge should you jump.")).range(0.001, 0.1).defaultValue(0.001).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.field_1724.method_24828() && !this.mc.field_1690.field_1903.method_1434()) {
         if (!this.mc.field_1724.method_5715() && !this.mc.field_1690.field_1832.method_1434()) {
            class_238 box = this.mc.field_1724.method_5829();
            class_238 adjustedBox = box.method_989((double)0.0F, (double)-0.5F, (double)0.0F).method_1009(-(Double)this.edgeDistance.get(), (double)0.0F, -(Double)this.edgeDistance.get());
            Stream<class_265> blockCollisions = Streams.stream(this.mc.field_1687.method_20812(this.mc.field_1724, adjustedBox));
            if (!blockCollisions.findAny().isPresent()) {
               this.mc.field_1724.method_6043();
            }
         }
      }
   }
}
