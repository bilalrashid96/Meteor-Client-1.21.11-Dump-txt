package meteordevelopment.meteorclient.gui.renderer;

import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.Color;

public abstract class GuiRenderOperation<T extends GuiRenderOperation<T>> {
   protected double x;
   protected double y;
   protected Color color;

   public void set(double x, double y, Color color) {
      this.x = x;
      this.y = y;
      this.color = color;
   }

   public void run(Pool<T> pool) {
      this.onRun();
      pool.free(this);
   }

   protected abstract void onRun();
}
