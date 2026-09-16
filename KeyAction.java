package meteordevelopment.meteorclient.utils.misc.input;

public enum KeyAction {
   Press,
   Repeat,
   Release;

   public static KeyAction get(int action) {
      KeyAction var10000;
      switch (action) {
         case 0 -> var10000 = Release;
         case 1 -> var10000 = Press;
         default -> var10000 = Repeat;
      }

      return var10000;
   }

   // $FF: synthetic method
   private static KeyAction[] $values() {
      return new KeyAction[]{Press, Repeat, Release};
   }
}
