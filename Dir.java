package meteordevelopment.meteorclient.utils.world;

import net.minecraft.class_2350;

public class Dir {
   public static final byte UP = 2;
   public static final byte DOWN = 4;
   public static final byte NORTH = 8;
   public static final byte SOUTH = 16;
   public static final byte WEST = 32;
   public static final byte EAST = 64;

   private Dir() {
   }

   public static byte get(class_2350 dir) {
      byte var10000;
      switch (dir) {
         case field_11036 -> var10000 = 2;
         case field_11033 -> var10000 = 4;
         case field_11043 -> var10000 = 8;
         case field_11035 -> var10000 = 16;
         case field_11039 -> var10000 = 32;
         case field_11034 -> var10000 = 64;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static boolean is(int dir, byte idk) {
      return (dir & idk) == idk;
   }

   public static boolean isNot(int dir, byte idk) {
      return (dir & idk) != idk;
   }
}
