package meteordevelopment.meteorclient.utils.world;

import net.minecraft.class_2350;

public enum CardinalDirection {
   North,
   East,
   South,
   West;

   public class_2350 toDirection() {
      class_2350 var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = class_2350.field_11043;
         case 1 -> var10000 = class_2350.field_11034;
         case 2 -> var10000 = class_2350.field_11035;
         case 3 -> var10000 = class_2350.field_11039;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static CardinalDirection fromDirection(class_2350 direction) {
      CardinalDirection var10000;
      switch (direction) {
         case field_11043:
            var10000 = North;
            break;
         case field_11035:
            var10000 = South;
            break;
         case field_11039:
            var10000 = East;
            break;
         case field_11034:
            var10000 = West;
            break;
         case field_11033:
         case field_11036:
            var10000 = null;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static CardinalDirection[] $values() {
      return new CardinalDirection[]{North, East, South, West};
   }
}
