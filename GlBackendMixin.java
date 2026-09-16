package meteordevelopment.meteorclient.mixin;

import com.mojang.blaze3d.systems.RenderPass;
import meteordevelopment.meteorclient.mixininterface.IGpuDevice;
import net.minecraft.class_10865;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({class_10865.class})
public abstract class GlBackendMixin implements IGpuDevice {
   @Unique
   private int x;
   @Unique
   private int y;
   @Unique
   private int width;
   @Unique
   private int height;
   @Unique
   private boolean set;

   public void meteor$pushScissor(int x, int y, int width, int height) {
      if (this.set) {
         throw new IllegalStateException("Currently there can only be one global scissor pushed");
      } else {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.set = true;
      }
   }

   public void meteor$popScissor() {
      if (!this.set) {
         throw new IllegalStateException("No scissor pushed");
      } else {
         this.set = false;
      }
   }

   /** @deprecated */
   @Deprecated
   public void meteor$onCreateRenderPass(RenderPass pass) {
      if (this.set) {
         pass.enableScissor(this.x, this.y, this.width, this.height);
      }

   }
}
