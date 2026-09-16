package meteordevelopment.meteorclient.events.render;

import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_4587;

public class Render3DEvent {
   private static final Render3DEvent INSTANCE = new Render3DEvent();
   public class_4587 matrices;
   public Renderer3D renderer;
   public Renderer3D depthRenderer;
   public double frameTime;
   public float tickDelta;
   public double offsetX;
   public double offsetY;
   public double offsetZ;

   public static Render3DEvent get(class_4587 matrices, Renderer3D renderer, Renderer3D depthRenderer, float tickDelta, double offsetX, double offsetY, double offsetZ) {
      INSTANCE.matrices = matrices;
      INSTANCE.renderer = renderer;
      INSTANCE.depthRenderer = depthRenderer;
      INSTANCE.frameTime = Utils.frameTime;
      INSTANCE.tickDelta = tickDelta;
      INSTANCE.offsetX = offsetX;
      INSTANCE.offsetY = offsetY;
      INSTANCE.offsetZ = offsetZ;
      return INSTANCE;
   }
}
