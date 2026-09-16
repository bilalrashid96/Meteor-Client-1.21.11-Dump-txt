package meteordevelopment.meteorclient.utils.render;

import java.util.Objects;

public class Box {
   public double x;
   public double y;
   public double width;
   public double height;

   public Box(double x, double y, double width, double height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public Box() {
      this((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Box box = (Box)o;
         return Double.compare(box.x, this.x) == 0 && Double.compare(box.y, this.y) == 0 && Double.compare(box.width, this.width) == 0 && Double.compare(box.height, this.height) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.x, this.y, this.width, this.height});
   }
}
