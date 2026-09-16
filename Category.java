package meteordevelopment.meteorclient.systems.modules;

import net.minecraft.class_1799;
import net.minecraft.class_1802;

public class Category {
   public final String name;
   public final class_1799 icon;
   private final int nameHash;

   public Category(String name, class_1799 icon) {
      this.name = name;
      this.nameHash = name.hashCode();
      this.icon = icon == null ? class_1802.field_8162.method_7854() : icon;
   }

   public Category(String name) {
      this(name, (class_1799)null);
   }

   public String toString() {
      return this.name;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Category category = (Category)o;
         return this.nameHash == category.nameHash;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.nameHash;
   }
}
