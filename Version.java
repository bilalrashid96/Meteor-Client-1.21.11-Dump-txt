package meteordevelopment.meteorclient.utils.misc;

public class Version {
   private final String string;
   private final int[] numbers;

   public Version(String string) {
      this.string = string;
      this.numbers = new int[3];
      String[] split = string.split("\\.");
      if (split.length != 3) {
         throw new IllegalArgumentException("Version string needs to have 3 numbers.");
      } else {
         for(int i = 0; i < 3; ++i) {
            try {
               this.numbers[i] = Integer.parseInt(split[i]);
            } catch (NumberFormatException var5) {
               throw new IllegalArgumentException("Failed to parse version string.");
            }
         }

      }
   }

   public boolean isZero() {
      return this.numbers[0] == 0 && this.numbers[1] == 0 && this.numbers[2] == 0;
   }

   public boolean isHigherThan(Version version) {
      for(int i = 0; i < 3; ++i) {
         if (this.numbers[i] > version.numbers[i]) {
            return true;
         }

         if (this.numbers[i] < version.numbers[i]) {
            return false;
         }
      }

      return false;
   }

   public String toString() {
      return this.string;
   }
}
