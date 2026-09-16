package meteordevelopment.meteorclient.mixin;

import com.mojang.blaze3d.opengl.GlStateManager;
import meteordevelopment.meteorclient.mixininterface.ICapabilityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({GlStateManager.class_1018.class})
public abstract class CapabilityTrackerMixin implements ICapabilityTracker {
   @Shadow
   private boolean field_5051;

   @Shadow
   public abstract void method_4470(boolean var1);

   public boolean meteor$get() {
      return this.field_5051;
   }

   public void meteor$set(boolean state) {
      this.method_4470(state);
   }
}
