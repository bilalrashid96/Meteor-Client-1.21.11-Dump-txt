package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IEntityRenderState;
import net.minecraft.class_10017;
import net.minecraft.class_1297;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({class_10017.class})
public abstract class EntityRenderStateMixin implements IEntityRenderState {
   @Unique
   private class_1297 entity;

   public @Nullable("EntityCulling mod can prevent the code that sets the entity from running") class_1297 meteor$getEntity() {
      return this.entity;
   }

   public void meteor$setEntity(class_1297 entity) {
      this.entity = entity;
   }
}
