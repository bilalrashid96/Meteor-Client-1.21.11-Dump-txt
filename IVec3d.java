package meteordevelopment.meteorclient.mixininterface;

import net.minecraft.class_2382;
import net.minecraft.class_243;
import org.joml.Vector3d;

public interface IVec3d {
   class_243 meteor$set(double var1, double var3, double var5);

   default class_243 meteor$set(class_2382 vec) {
      return this.meteor$set((double)vec.method_10263(), (double)vec.method_10264(), (double)vec.method_10260());
   }

   default class_243 meteor$set(Vector3d vec) {
      return this.meteor$set(vec.x, vec.y, vec.z);
   }

   default class_243 meteor$set(class_243 pos) {
      return this.meteor$set(pos.field_1352, pos.field_1351, pos.field_1350);
   }

   class_243 meteor$setXZ(double var1, double var3);

   class_243 meteor$setY(double var1);
}
