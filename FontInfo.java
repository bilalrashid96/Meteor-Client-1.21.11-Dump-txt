package meteordevelopment.meteorclient.renderer.text;

public record FontInfo(String family, Type type) {
   public String toString() {
      String var10000 = this.family;
      return var10000 + " " + String.valueOf(this.type);
   }

   public boolean equals(FontInfo info) {
      if (this == info) {
         return true;
      } else if (info != null && this.family != null && this.type != null) {
         return this.family.equals(info.family) && this.type == info.type;
      } else {
         return false;
      }
   }

   public static enum Type {
      Regular,
      Bold,
      Italic,
      BoldItalic;

      public static Type fromString(String str) {
         Type var10000;
         switch (str) {
            case "Bold":
               var10000 = Bold;
               break;
            case "Italic":
               var10000 = Italic;
               break;
            case "Bold Italic":
            case "BoldItalic":
               var10000 = BoldItalic;
               break;
            default:
               var10000 = Regular;
         }

         return var10000;
      }

      public String toString() {
         String var10000;
         switch (this.ordinal()) {
            case 1 -> var10000 = "Bold";
            case 2 -> var10000 = "Italic";
            case 3 -> var10000 = "Bold Italic";
            default -> var10000 = "Regular";
         }

         return var10000;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{Regular, Bold, Italic, BoldItalic};
      }
   }
}
