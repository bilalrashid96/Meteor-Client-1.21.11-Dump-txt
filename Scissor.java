package meteordevelopment.meteorclient.gui.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.mixininterface.IGpuDevice;
import meteordevelopment.meteorclient.utils.Utils;

public class Scissor {
   public int x;
   public int y;
   public int width;
   public int height;
   public final List<Runnable> postTasks = new ArrayList();

   public Scissor set(double x, double y, double width, double height) {
      if (width < (double)0.0F) {
         width = (double)0.0F;
      }

      if (height < (double)0.0F) {
         height = (double)0.0F;
      }

      this.x = (int)Math.round(x);
      this.y = (int)Math.round(y);
      this.width = (int)Math.round(width);
      this.height = (int)Math.round(height);
      this.postTasks.clear();
      return this;
   }

   public void push() {
      ((IGpuDevice)RenderSystem.getDevice()).meteor$pushScissor(this.x, Utils.getWindowHeight() - this.y - this.height, this.width, this.height);
   }

   public void pop() {
      ((IGpuDevice)RenderSystem.getDevice()).meteor$popScissor();
   }
}
