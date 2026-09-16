package meteordevelopment.meteorclient.utils.render.color;

public class RainbowColor extends Color {
   private double speed;
   private static final float[] hsb = new float[3];

   public double getSpeed() {
      return this.speed;
   }

   public RainbowColor setSpeed(double speed) {
      this.speed = speed;
      return this;
   }

   public RainbowColor getNext() {
      return this.getNext((double)1.0F);
   }

   public RainbowColor getNext(double delta) {
      if (this.speed > (double)0.0F) {
         java.awt.Color.RGBtoHSB(this.r, this.g, this.b, hsb);
         int c = java.awt.Color.HSBtoRGB(hsb[0] + (float)(this.speed * delta), 1.0F, 1.0F);
         this.r = toRGBAR(c);
         this.g = toRGBAG(c);
         this.b = toRGBAB(c);
      }

      return this;
   }

   public RainbowColor set(RainbowColor color) {
      this.r = color.r;
      this.g = color.g;
      this.b = color.b;
      this.a = color.a;
      this.speed = color.speed;
      return this;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         if (!super.equals(o)) {
            return false;
         } else {
            return Double.compare(((RainbowColor)o).speed, this.speed) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      long temp = Double.doubleToLongBits(this.speed);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      return result;
   }
}
