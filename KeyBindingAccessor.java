package meteordevelopment.meteorclient.mixin;

import java.util.Map;
import net.minecraft.class_304;
import net.minecraft.class_3675;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_304.class})
public interface KeyBindingAccessor {
   @Accessor("field_1657")
   static Map<String, class_304> getKeysById() {
      return null;
   }

   @Accessor("field_1655")
   class_3675.class_306 meteor$getKey();

   @Accessor("field_1661")
   int meteor$getTimesPressed();

   @Accessor("field_1661")
   void meteor$setTimesPressed(int var1);

   @Invoker("method_1425")
   void meteor$invokeReset();
}
