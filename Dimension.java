package meteordevelopment.meteorclient.utils.world;

public enum Dimension {
   Overworld,
   Nether,
   End;

   public Dimension opposite() {
      Dimension var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = Nether;
         case 1 -> var10000 = Overworld;
         default -> var10000 = this;
      }

      return var10000;
   }

   // $FF: synthetic method
   private static Dimension[] $values() {
      return new Dimension[]{Overworld, Nether, End};
   }
}
