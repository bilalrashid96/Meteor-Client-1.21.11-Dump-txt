package meteordevelopment.meteorclient.utils.misc;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2680;

public class MBlockPos {
   private static final class_2338.class_2339 POS = new class_2338.class_2339();
   public int x;
   public int y;
   public int z;

   public MBlockPos() {
   }

   public MBlockPos(class_1297 entity) {
      this.set(entity);
   }

   public MBlockPos set(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
      return this;
   }

   public MBlockPos set(MBlockPos pos) {
      return this.set(pos.x, pos.y, pos.z);
   }

   public MBlockPos set(class_1297 entity) {
      return this.set(entity.method_31477(), entity.method_31478(), entity.method_31479());
   }

   public MBlockPos coerceBlockLevel(class_1297 entity) {
      return this.set(entity.method_31477(), (int)Math.round(entity.method_23318()), entity.method_31479());
   }

   public MBlockPos offset(HorizontalDirection dir, int amount) {
      this.x += dir.offsetX * amount;
      this.z += dir.offsetZ * amount;
      return this;
   }

   public MBlockPos offset(HorizontalDirection dir) {
      return this.offset(dir, 1);
   }

   public MBlockPos add(int x, int y, int z) {
      this.x += x;
      this.y += y;
      this.z += z;
      return this;
   }

   public class_2338 getBlockPos() {
      return POS.method_10103(this.x, this.y, this.z);
   }

   public class_2680 getState() {
      return MeteorClient.mc.field_1687.method_8320(this.getBlockPos());
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MBlockPos mBlockPos = (MBlockPos)o;
         if (this.x != mBlockPos.x) {
            return false;
         } else if (this.y != mBlockPos.y) {
            return false;
         } else {
            return this.z == mBlockPos.z;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.x;
      result = 31 * result + this.y;
      result = 31 * result + this.z;
      return result;
   }

   public String toString() {
      return this.x + ", " + this.y + ", " + this.z;
   }
}
