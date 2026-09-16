package meteordevelopment.meteorclient.renderer;

public enum ShapeMode {
   Lines,
   Sides,
   Both;

   public boolean lines() {
      return this == Lines || this == Both;
   }

   public boolean sides() {
      return this == Sides || this == Both;
   }

   // $FF: synthetic method
   private static ShapeMode[] $values() {
      return new ShapeMode[]{Lines, Sides, Both};
   }
}
