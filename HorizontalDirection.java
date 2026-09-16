package meteordevelopment.meteorclient.utils.misc;

public enum HorizontalDirection {
   South("South", "Z+", false, 0.0F, 0, 1),
   SouthEast("South East", "X+ Z+", true, -45.0F, 1, 1),
   West("West", "X-", false, 90.0F, -1, 0),
   NorthWest("North West", "X- Z-", true, 135.0F, -1, -1),
   North("North", "Z-", false, 180.0F, 0, -1),
   NorthEast("North East", "X+ Z-", true, -135.0F, 1, -1),
   East("East", "X+", false, -90.0F, 1, 0),
   SouthWest("South West", "X- Z+", true, 45.0F, -1, 1);

   public final String name;
   public final String axis;
   public final boolean diagonal;
   public final float yaw;
   public final int offsetX;
   public final int offsetZ;

   private HorizontalDirection(String name, String axis, boolean diagonal, float yaw, int offsetX, int offsetZ) {
      this.axis = axis;
      this.name = name;
      this.diagonal = diagonal;
      this.yaw = yaw;
      this.offsetX = offsetX;
      this.offsetZ = offsetZ;
   }

   public HorizontalDirection opposite() {
      HorizontalDirection var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = North;
         case 1 -> var10000 = NorthWest;
         case 2 -> var10000 = East;
         case 3 -> var10000 = SouthEast;
         case 4 -> var10000 = South;
         case 5 -> var10000 = SouthWest;
         case 6 -> var10000 = West;
         case 7 -> var10000 = NorthEast;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public HorizontalDirection rotateLeft() {
      HorizontalDirection var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = SouthEast;
         case 1 -> var10000 = East;
         case 2 -> var10000 = SouthWest;
         case 3 -> var10000 = West;
         case 4 -> var10000 = NorthWest;
         case 5 -> var10000 = North;
         case 6 -> var10000 = NorthEast;
         case 7 -> var10000 = South;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public HorizontalDirection rotateLeftSkipOne() {
      HorizontalDirection var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = East;
         case 1 -> var10000 = NorthEast;
         case 2 -> var10000 = South;
         case 3 -> var10000 = SouthWest;
         case 4 -> var10000 = West;
         case 5 -> var10000 = NorthWest;
         case 6 -> var10000 = North;
         case 7 -> var10000 = SouthEast;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public HorizontalDirection rotateRight() {
      HorizontalDirection var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = SouthWest;
         case 1 -> var10000 = South;
         case 2 -> var10000 = NorthWest;
         case 3 -> var10000 = North;
         case 4 -> var10000 = NorthEast;
         case 5 -> var10000 = East;
         case 6 -> var10000 = SouthEast;
         case 7 -> var10000 = West;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static HorizontalDirection get(float yaw) {
      yaw %= 360.0F;
      if (yaw < 0.0F) {
         yaw += 360.0F;
      }

      if (!((double)yaw >= (double)337.5F) && !((double)yaw < (double)22.5F)) {
         if ((double)yaw >= (double)22.5F && (double)yaw < (double)67.5F) {
            return SouthWest;
         } else if ((double)yaw >= (double)67.5F && (double)yaw < (double)112.5F) {
            return West;
         } else if ((double)yaw >= (double)112.5F && (double)yaw < (double)157.5F) {
            return NorthWest;
         } else if ((double)yaw >= (double)157.5F && (double)yaw < (double)202.5F) {
            return North;
         } else if ((double)yaw >= (double)202.5F && (double)yaw < (double)247.5F) {
            return NorthEast;
         } else if ((double)yaw >= (double)247.5F && (double)yaw < (double)292.5F) {
            return East;
         } else {
            return (double)yaw >= (double)292.5F && (double)yaw < (double)337.5F ? SouthEast : South;
         }
      } else {
         return South;
      }
   }

   // $FF: synthetic method
   private static HorizontalDirection[] $values() {
      return new HorizontalDirection[]{South, SouthEast, West, NorthWest, North, NorthEast, East, SouthWest};
   }
}
