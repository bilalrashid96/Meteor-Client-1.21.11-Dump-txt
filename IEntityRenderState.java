package meteordevelopment.meteorclient.mixininterface;

import net.minecraft.class_1297;
import org.jetbrains.annotations.Nullable;

public interface IEntityRenderState {
   @Nullable("EntityCulling mod can prevent the code that sets the entity from running") class_1297 meteor$getEntity();

   void meteor$setEntity(class_1297 var1);
}
