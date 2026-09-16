package meteordevelopment.meteorclient.utils.render;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_332;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class RenderUtils {
   public static class_243 center;
   public static final Matrix4f projection = new Matrix4f();
   private static final Pool<RenderBlock> renderBlockPool = new Pool<RenderBlock>(RenderBlock::new);
   private static final List<RenderBlock> renderBlocks = new ObjectArrayList();

   private RenderUtils() {
   }

   @PostInit
   public static void init() {
      MeteorClient.EVENT_BUS.subscribe(RenderUtils.class);
   }

   public static boolean isShaderPackInUse() {
      return IrisApi.getInstance().isShaderPackInUse();
   }

   public static void drawItem(class_332 drawContext, class_1799 itemStack, int x, int y, float scale, boolean overlay, String countOverride, boolean disableGuiScale) {
      Matrix3x2fStack matrices = drawContext.method_51448();
      matrices.pushMatrix();
      if (disableGuiScale) {
         matrices.scale(1.0F / (float)MeteorClient.mc.method_22683().method_4495());
      }

      matrices.scale(scale, scale);
      int scaledX = (int)((float)x / scale);
      int scaledY = (int)((float)y / scale);
      drawContext.method_51427(itemStack, scaledX, scaledY);
      if (overlay) {
         drawContext.method_51432(MeteorClient.mc.field_1772, itemStack, scaledX, scaledY, countOverride);
      }

      matrices.popMatrix();
   }

   public static void drawItem(class_332 drawContext, class_1799 itemStack, int x, int y, float scale, boolean overlay) {
      drawItem(drawContext, itemStack, x, y, scale, overlay, (String)null, true);
   }

   public static void updateScreenCenter(Matrix4f projection, Matrix4f view) {
      RenderUtils.projection.set(projection);
      Matrix4f invProjection = (new Matrix4f(projection)).invert();
      Matrix4f invView = (new Matrix4f(view)).invert();
      Vector4f center4 = (new Vector4f(0.0F, 0.0F, 0.0F, 1.0F)).mul(invProjection).mul(invView);
      center4.div(center4.w);
      class_243 camera = MeteorClient.mc.field_1773.method_19418().method_71156();
      center = new class_243(camera.field_1352 + (double)center4.x, camera.field_1351 + (double)center4.y, camera.field_1350 + (double)center4.z);
   }

   public static void renderTickingBlock(class_2338 blockPos, Color sideColor, Color lineColor, ShapeMode shapeMode, int excludeDir, int duration, boolean fade, boolean shrink) {
      renderBlocks.removeIf((next) -> {
         if (next.pos.equals(blockPos)) {
            renderBlockPool.free(next);
            return true;
         } else {
            return false;
         }
      });
      renderBlocks.add(((RenderBlock)renderBlockPool.get()).set(blockPos, sideColor, lineColor, shapeMode, excludeDir, duration, fade, shrink));
   }

   @EventHandler
   private static void onTick(TickEvent.Pre event) {
      if (!renderBlocks.isEmpty()) {
         renderBlocks.removeIf((next) -> {
            next.tick();
            if (next.ticks <= 0) {
               renderBlockPool.free(next);
               return true;
            } else {
               return false;
            }
         });
      }
   }

   @EventHandler
   private static void onRender(Render3DEvent event) {
      renderBlocks.forEach((block) -> block.render(event));
   }

   public static class RenderBlock {
      public class_2338.class_2339 pos = new class_2338.class_2339();
      public Color sideColor;
      public Color lineColor;
      public ShapeMode shapeMode;
      public int excludeDir;
      public int ticks;
      public int duration;
      public boolean fade;
      public boolean shrink;

      public RenderBlock set(class_2338 blockPos, Color sideColor, Color lineColor, ShapeMode shapeMode, int excludeDir, int duration, boolean fade, boolean shrink) {
         this.pos.method_10101(blockPos);
         this.sideColor = sideColor;
         this.lineColor = lineColor;
         this.shapeMode = shapeMode;
         this.excludeDir = excludeDir;
         this.fade = fade;
         this.shrink = shrink;
         this.ticks = duration;
         this.duration = duration;
         return this;
      }

      public void tick() {
         --this.ticks;
      }

      public void render(Render3DEvent event) {
         int preSideA = this.sideColor.a;
         int preLineA = this.lineColor.a;
         double x1 = (double)this.pos.method_10263();
         double y1 = (double)this.pos.method_10264();
         double z1 = (double)this.pos.method_10260();
         double x2 = (double)(this.pos.method_10263() + 1);
         double y2 = (double)(this.pos.method_10264() + 1);
         double z2 = (double)(this.pos.method_10260() + 1);
         double d = (double)((float)this.ticks - event.tickDelta) / (double)this.duration;
         if (this.fade) {
            this.sideColor.a = (int)((double)this.sideColor.a * d);
            this.lineColor.a = (int)((double)this.lineColor.a * d);
         }

         if (this.shrink) {
            x1 += d;
            y1 += d;
            z1 += d;
            x2 -= d;
            y2 -= d;
            z2 -= d;
         }

         event.renderer.box(x1, y1, z1, x2, y2, z2, this.sideColor, this.lineColor, this.shapeMode, this.excludeDir);
         this.sideColor.a = preSideA;
         this.lineColor.a = preLineA;
      }
   }
}
