package meteordevelopment.meteorclient.utils.entity;

import java.util.Comparator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.class_1297;
import net.minecraft.class_1309;

public enum SortPriority implements Comparator<class_1297> {
   LowestDistance(Comparator.comparingDouble(PlayerUtils::squaredDistanceTo)),
   HighestDistance((e1, e2) -> Double.compare(PlayerUtils.squaredDistanceTo(e2), PlayerUtils.squaredDistanceTo(e1))),
   LowestHealth(SortPriority::sortHealth),
   HighestHealth((e1, e2) -> sortHealth(e2, e1)),
   ClosestAngle(SortPriority::sortAngle);

   private final Comparator<class_1297> comparator;

   private SortPriority(Comparator<class_1297> comparator) {
      this.comparator = comparator;
   }

   public int compare(class_1297 o1, class_1297 o2) {
      return this.comparator.compare(o1, o2);
   }

   private static int sortHealth(class_1297 e1, class_1297 e2) {
      boolean e1l = e1 instanceof class_1309;
      boolean e2l = e2 instanceof class_1309;
      if (!e1l && !e2l) {
         return 0;
      } else if (e1l && !e2l) {
         return 1;
      } else {
         return !e1l ? -1 : Float.compare(((class_1309)e1).method_6032(), ((class_1309)e2).method_6032());
      }
   }

   private static int sortAngle(class_1297 e1, class_1297 e2) {
      boolean e1l = e1 instanceof class_1309;
      boolean e2l = e2 instanceof class_1309;
      if (!e1l && !e2l) {
         return 0;
      } else if (e1l && !e2l) {
         return 1;
      } else if (!e1l) {
         return -1;
      } else {
         double e1yaw = Math.abs(Rotations.getYaw(e1) - (double)MeteorClient.mc.field_1724.method_36454());
         double e2yaw = Math.abs(Rotations.getYaw(e2) - (double)MeteorClient.mc.field_1724.method_36454());
         double e1pitch = Math.abs(Rotations.getPitch(e1) - (double)MeteorClient.mc.field_1724.method_36455());
         double e2pitch = Math.abs(Rotations.getPitch(e2) - (double)MeteorClient.mc.field_1724.method_36455());
         return Double.compare(e1yaw * e1yaw + e1pitch * e1pitch, e2yaw * e2yaw + e2pitch * e2pitch);
      }
   }

   // $FF: synthetic method
   private static SortPriority[] $values() {
      return new SortPriority[]{LowestDistance, HighestDistance, LowestHealth, HighestHealth, ClosestAngle};
   }
}
