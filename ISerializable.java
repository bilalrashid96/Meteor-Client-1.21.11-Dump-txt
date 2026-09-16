package meteordevelopment.meteorclient.utils.misc;

import net.minecraft.class_2487;

public interface ISerializable<T> {
   class_2487 toTag();

   T fromTag(class_2487 var1);
}
