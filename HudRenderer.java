package meteordevelopment.meteorclient.systems.hud;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.CustomFontChangedEvent;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.CustomTextRenderer;
import meteordevelopment.meteorclient.renderer.text.Font;
import meteordevelopment.meteorclient.renderer.text.VanillaTextRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_10042;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HudRenderer {
   public static final HudRenderer INSTANCE = new HudRenderer();
   private static final double SCALE_TO_HEIGHT = 0.05555555555555555;
   private final Hud hud = Hud.get();
   private final List<Runnable> postTasks = new ArrayList();
   private final Int2ObjectMap<FontHolder> fontsInUse = new Int2ObjectOpenHashMap();
   private final LoadingCache<Integer, FontHolder> fontCache = CacheBuilder.newBuilder().maximumSize(4L).expireAfterAccess(Duration.ofMinutes(10L)).removalListener((notification) -> {
      if (notification.wasEvicted()) {
         ((FontHolder)notification.getValue()).destroy();
      }

   }).build(CacheLoader.from(HudRenderer::loadFont));
   public class_332 drawContext;
   public double delta;

   private HudRenderer() {
      MeteorClient.EVENT_BUS.subscribe(this);
   }

   public void begin(class_332 drawContext) {
      Renderer2D.COLOR.begin();
      this.drawContext = drawContext;
      this.delta = Utils.frameTime;
      drawContext.method_71048();
      if (!this.hud.hasCustomFont()) {
         VanillaTextRenderer.INSTANCE.scaleIndividually = true;
         VanillaTextRenderer.INSTANCE.begin();
      }

   }

   public void end() {
      Renderer2D.COLOR.render();
      FontHolder fontHolder;
      if (this.hud.hasCustomFont()) {
         for(Iterator<FontHolder> it = this.fontsInUse.values().iterator(); it.hasNext(); fontHolder.visited = false) {
            fontHolder = (FontHolder)it.next();
            if (fontHolder.visited) {
               MeshRenderer.begin().attachments(MeteorClient.mc.method_1522()).pipeline(MeteorRenderPipelines.UI_TEXT).mesh(fontHolder.getMesh()).sampler("u_Texture", fontHolder.font.texture.method_71659(), fontHolder.font.texture.method_75484()).end();
            } else {
               it.remove();
               this.fontCache.put(fontHolder.font.getHeight(), fontHolder);
            }
         }
      } else {
         VanillaTextRenderer.INSTANCE.end();
         VanillaTextRenderer.INSTANCE.scaleIndividually = false;
      }

      for(Runnable task : this.postTasks) {
         task.run();
      }

      this.postTasks.clear();
      this.drawContext.method_71048();
      this.drawContext = null;
   }

   public void line(double x1, double y1, double x2, double y2, Color color) {
      Renderer2D.COLOR.line(x1, y1, x2, y2, color);
   }

   public void quad(double x, double y, double width, double height, Color color) {
      Renderer2D.COLOR.quad(x, y, width, height, color);
   }

   public void quad(double x, double y, double width, double height, Color cTopLeft, Color cTopRight, Color cBottomRight, Color cBottomLeft) {
      Renderer2D.COLOR.quad(x, y, width, height, cTopLeft, cTopRight, cBottomRight, cBottomLeft);
   }

   public void triangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
      Renderer2D.COLOR.triangle(x1, y1, x2, y2, x3, y3, color);
   }

   public void texture(class_2960 id, double x, double y, double width, double height, Color color) {
      Renderer2D.TEXTURE.begin();
      Renderer2D.TEXTURE.texQuad(x, y, width, height, color);
      Renderer2D.TEXTURE.render(MeteorClient.mc.method_1531().method_4619(id).method_71659(), MeteorClient.mc.method_1531().method_4619(id).method_75484());
   }

   public double text(String text, double x, double y, Color color, boolean shadow, double scale) {
      if (scale == (double)-1.0F) {
         scale = this.hud.getTextScale();
      }

      if (!this.hud.hasCustomFont()) {
         VanillaTextRenderer.INSTANCE.scale = scale * (double)2.0F;
         return VanillaTextRenderer.INSTANCE.render(text, x, y, color, shadow);
      } else {
         FontHolder fontHolder = this.getFontHolder(scale, true);
         Font font = fontHolder.font;
         MeshBuilder mesh = fontHolder.getMesh();
         double width;
         if (shadow) {
            int preShadowA = CustomTextRenderer.SHADOW_COLOR.a;
            CustomTextRenderer.SHADOW_COLOR.a = (int)((double)color.a / (double)255.0F * (double)preShadowA);
            width = font.render(mesh, text, x + (double)1.0F, y + (double)1.0F, CustomTextRenderer.SHADOW_COLOR, scale);
            font.render(mesh, text, x, y, color, scale);
            CustomTextRenderer.SHADOW_COLOR.a = preShadowA;
         } else {
            width = font.render(mesh, text, x, y, color, scale);
         }

         return width;
      }
   }

   public double text(String text, double x, double y, Color color, boolean shadow) {
      return this.text(text, x, y, color, shadow, (double)-1.0F);
   }

   public double textWidth(String text, boolean shadow, double scale) {
      if (text.isEmpty()) {
         return (double)0.0F;
      } else if (this.hud.hasCustomFont()) {
         double width = this.getFont(scale).getWidth(text, text.length());
         return (width + (double)(shadow ? 1 : 0)) * (scale == (double)-1.0F ? this.hud.getTextScale() : scale) + (double)(shadow ? 1 : 0);
      } else {
         VanillaTextRenderer.INSTANCE.scale = (scale == (double)-1.0F ? this.hud.getTextScale() : scale) * (double)2.0F;
         return VanillaTextRenderer.INSTANCE.getWidth(text, shadow);
      }
   }

   public double textWidth(String text, boolean shadow) {
      return this.textWidth(text, shadow, (double)-1.0F);
   }

   public double textWidth(String text, double scale) {
      return this.textWidth(text, false, scale);
   }

   public double textWidth(String text) {
      return this.textWidth(text, false, (double)-1.0F);
   }

   public double textHeight(boolean shadow, double scale) {
      if (this.hud.hasCustomFont()) {
         double height = (double)(this.getFont(scale).getHeight() + 1);
         return (height + (double)(shadow ? 1 : 0)) * (scale == (double)-1.0F ? this.hud.getTextScale() : scale);
      } else {
         VanillaTextRenderer.INSTANCE.scale = (scale == (double)-1.0F ? this.hud.getTextScale() : scale) * (double)2.0F;
         return VanillaTextRenderer.INSTANCE.getHeight(shadow);
      }
   }

   public double textHeight(boolean shadow) {
      return this.textHeight(shadow, (double)-1.0F);
   }

   public double textHeight() {
      return this.textHeight(false, (double)-1.0F);
   }

   public void post(Runnable task) {
      this.postTasks.add(task);
   }

   public void item(class_1799 itemStack, int x, int y, float scale, boolean overlay, String countOverlay) {
      RenderUtils.drawItem(this.drawContext, itemStack, x, y, scale, overlay, countOverlay, true);
   }

   public void item(class_1799 itemStack, int x, int y, float scale, boolean overlay) {
      RenderUtils.drawItem(this.drawContext, itemStack, x, y, scale, overlay);
   }

   public void entity(class_1309 entity, int x, int y, int width, int height, float yaw, float pitch) {
      float previousBodyYaw = entity.field_6283;
      float previousYaw = entity.method_36454();
      float previousPitch = entity.method_36455();
      float lastLastHeadYaw = entity.field_6259;
      float lastHeadYaw = entity.field_6241;
      float tanYaw = (float)Math.atan((double)(yaw / 40.0F));
      float tanPitch = (float)Math.atan((double)(pitch / 40.0F));
      entity.field_6283 = 180.0F + tanYaw * 20.0F;
      entity.method_36456(180.0F + tanYaw * 40.0F);
      entity.method_36457(-tanPitch * 20.0F);
      entity.field_6241 = entity.method_36454();
      entity.field_6259 = entity.method_36454();
      class_10042 state = (class_10042)MeteorClient.mc.method_1561().method_3953(entity).method_62425(entity, 1.0F);
      entity.field_6283 = previousBodyYaw;
      entity.method_36456(previousYaw);
      entity.method_36457(previousPitch);
      entity.field_6259 = lastLastHeadYaw;
      entity.field_6241 = lastHeadYaw;
      float s = 1.0F / (float)MeteorClient.mc.method_22683().method_4495();
      int x1 = (int)((float)x * s);
      int y1 = (int)((float)y * s);
      int x2 = (int)((float)(x + width) * s);
      int y2 = (int)((float)(y + height) * s);
      float scale = (float)Math.max(width, height) * s / 2.0F;
      Vector3f translation = new Vector3f(0.0F, 1.0F, 0.0F);
      Quaternionf rotation = (new Quaternionf()).rotateZ((float)Math.PI);
      this.drawContext.method_70856(state, scale, translation, rotation, (Quaternionf)null, x1, y1, x2, y2);
   }

   private FontHolder getFontHolder(double scale, boolean render) {
      if (scale == (double)-1.0F) {
         scale = this.hud.getTextScale();
      }

      int height = (int)Math.round(scale / 0.05555555555555555);
      FontHolder fontHolder = (FontHolder)this.fontsInUse.get(height);
      if (fontHolder != null) {
         if (render) {
            fontHolder.visited = true;
         }

         return fontHolder;
      } else if (render) {
         fontHolder = (FontHolder)this.fontCache.getIfPresent(height);
         if (fontHolder == null) {
            fontHolder = loadFont(height);
         } else {
            this.fontCache.invalidate(height);
         }

         this.fontsInUse.put(height, fontHolder);
         fontHolder.visited = true;
         return fontHolder;
      } else {
         return (FontHolder)this.fontCache.getUnchecked(height);
      }
   }

   private Font getFont(double scale) {
      return this.getFontHolder(scale, false).font;
   }

   @EventHandler
   private void onCustomFontChanged(CustomFontChangedEvent event) {
      ObjectIterator var2 = this.fontsInUse.values().iterator();

      while(var2.hasNext()) {
         FontHolder fontHolder = (FontHolder)var2.next();
         fontHolder.destroy();
      }

      for(FontHolder fontHolder : this.fontCache.asMap().values()) {
         fontHolder.destroy();
      }

      this.fontsInUse.clear();
      this.fontCache.invalidateAll();
   }

   private static FontHolder loadFont(int height) {
      try {
         ByteBuffer buffer = Fonts.RENDERER.fontFace.readToDirectByteBuffer();
         return new FontHolder(new Font(buffer, height));
      } catch (IOException e) {
         throw new RuntimeException("Failed to load font: " + String.valueOf(Fonts.RENDERER.fontFace), e);
      }
   }

   private static class FontHolder {
      public final Font font;
      public boolean visited;
      private MeshBuilder mesh;

      public FontHolder(Font font) {
         this.font = font;
      }

      public MeshBuilder getMesh() {
         if (this.mesh == null) {
            this.mesh = new MeshBuilder(MeteorRenderPipelines.UI_TEXT);
         }

         if (!this.mesh.isBuilding()) {
            this.mesh.begin();
         }

         return this.mesh;
      }

      public void destroy() {
         this.font.texture.close();
      }
   }
}
