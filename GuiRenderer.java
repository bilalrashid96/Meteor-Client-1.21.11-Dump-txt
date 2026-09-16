package meteordevelopment.meteorclient.gui.renderer;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.operations.TextOperation;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.renderer.packer.TexturePacker;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import org.joml.Matrix3x2fStack;

public class GuiRenderer {
   private static final Color WHITE = new Color(255, 255, 255);
   private static final TexturePacker TEXTURE_PACKER = new TexturePacker();
   private static Texture TEXTURE;
   public static GuiTexture CIRCLE;
   public static GuiTexture TRIANGLE;
   public static GuiTexture EDIT;
   public static GuiTexture RESET;
   public static GuiTexture FAVORITE_NO;
   public static GuiTexture FAVORITE_YES;
   public static GuiTexture COPY;
   public static GuiTexture PASTE;
   public GuiTheme theme;
   private final Renderer2D r = new Renderer2D(false);
   private final Renderer2D rTex = new Renderer2D(true);
   private final Pool<Scissor> scissorPool = new Pool<Scissor>(Scissor::new);
   private final Stack<Scissor> scissorStack = new ObjectArrayList();
   private final Pool<TextOperation> textPool = new Pool<TextOperation>(TextOperation::new);
   private final List<TextOperation> texts = new ObjectArrayList();
   private final List<Runnable> postTasks = new ObjectArrayList();
   public String tooltip;
   public String lastTooltip;
   public WWidget tooltipWidget;
   private double tooltipAnimProgress;
   private class_332 drawContext;

   public static GuiTexture addTexture(class_2960 id) {
      return TEXTURE_PACKER.add(id);
   }

   @PostInit
   public static void init() {
      CIRCLE = addTexture(MeteorClient.identifier("textures/icons/gui/circle.png"));
      TRIANGLE = addTexture(MeteorClient.identifier("textures/icons/gui/triangle.png"));
      EDIT = addTexture(MeteorClient.identifier("textures/icons/gui/edit.png"));
      RESET = addTexture(MeteorClient.identifier("textures/icons/gui/reset.png"));
      FAVORITE_NO = addTexture(MeteorClient.identifier("textures/icons/gui/favorite_no.png"));
      FAVORITE_YES = addTexture(MeteorClient.identifier("textures/icons/gui/favorite_yes.png"));
      COPY = addTexture(MeteorClient.identifier("textures/icons/gui/copy.png"));
      PASTE = addTexture(MeteorClient.identifier("textures/icons/gui/paste.png"));
      TEXTURE = TEXTURE_PACKER.pack();
   }

   public void begin(class_332 drawContext) {
      this.drawContext = drawContext;
      this.drawContext.method_71048();
      Matrix3x2fStack matrices = drawContext.method_51448();
      matrices.pushMatrix();
      matrices.scale(1.0F / (float)MeteorClient.mc.method_22683().method_4495());
      this.scissorStart((double)0.0F, (double)0.0F, (double)Utils.getWindowWidth(), (double)Utils.getWindowHeight());
   }

   public void end() {
      this.scissorEnd();

      for(Runnable task : this.postTasks) {
         task.run();
      }

      this.postTasks.clear();
      this.drawContext.method_51448().popMatrix();
      this.drawContext.method_71048();
   }

   public void beginRender() {
      this.r.begin();
      this.rTex.begin();
   }

   public void endRender() {
      this.endRender((Scissor)null);
   }

   public void endRender(Scissor scissor) {
      if (scissor != null) {
         scissor.push();
      }

      this.r.end();
      this.rTex.end();
      this.r.render();
      this.rTex.render("u_Texture", TEXTURE.method_71659(), TEXTURE.method_75484());
      this.theme.textRenderer().begin(this.theme.scale((double)1.0F));

      for(TextOperation text : this.texts) {
         if (!text.title) {
            text.run(this.textPool);
         }
      }

      this.theme.textRenderer().end();
      this.theme.textRenderer().begin(this.theme.scale((double)1.25F));

      for(TextOperation text : this.texts) {
         if (text.title) {
            text.run(this.textPool);
         }
      }

      this.theme.textRenderer().end();
      this.texts.clear();
      if (scissor != null) {
         scissor.pop();
      }

   }

   public void scissorStart(double x, double y, double width, double height) {
      if (!this.scissorStack.isEmpty()) {
         Scissor parent = (Scissor)this.scissorStack.top();
         if (x < (double)parent.x) {
            x = (double)parent.x;
         } else if (x + width > (double)(parent.x + parent.width)) {
            width -= x + width - (double)(parent.x + parent.width);
         }

         if (y < (double)parent.y) {
            y = (double)parent.y;
         } else if (y + height > (double)(parent.y + parent.height)) {
            height -= y + height - (double)(parent.y + parent.height);
         }

         this.endRender(parent);
      }

      this.scissorStack.push(((Scissor)this.scissorPool.get()).set(x, y, width, height));
      this.drawContext.method_44379((int)x, (int)y, (int)(x + width), (int)(y + height));
      this.beginRender();
   }

   public void scissorEnd() {
      Scissor scissor = (Scissor)this.scissorStack.pop();
      this.endRender(scissor);
      scissor.push();

      for(Runnable task : scissor.postTasks) {
         task.run();
      }

      scissor.pop();
      this.drawContext.method_44380();
      if (!this.scissorStack.isEmpty()) {
         this.beginRender();
      }

      this.scissorPool.free(scissor);
   }

   public boolean renderTooltip(class_332 drawContext, double mouseX, double mouseY, double delta) {
      this.tooltipAnimProgress += (double)(this.tooltip != null ? 1 : -1) * delta * (double)14.0F;
      this.tooltipAnimProgress = class_3532.method_15350(this.tooltipAnimProgress, (double)0.0F, (double)1.0F);
      boolean toReturn = false;
      if (this.tooltipAnimProgress > (double)0.0F) {
         if (this.tooltip != null && !this.tooltip.equals(this.lastTooltip)) {
            this.tooltipWidget = this.theme.tooltip(this.tooltip);
            this.tooltipWidget.init();
         }

         double deltaX = -this.tooltipWidget.x + mouseX + (double)12.0F;
         double deltaY = -this.tooltipWidget.y + mouseY + (double)12.0F;
         if (mouseX + (double)12.0F + this.tooltipWidget.width > (double)Utils.getWindowWidth()) {
            deltaX = -this.tooltipWidget.x + (double)Utils.getWindowWidth() - this.tooltipWidget.width;
         }

         if (mouseY + (double)12.0F + this.tooltipWidget.height > (double)Utils.getWindowHeight()) {
            deltaY = -this.tooltipWidget.y + (double)Utils.getWindowHeight() - this.tooltipWidget.height;
         }

         this.tooltipWidget.move(deltaX, deltaY);
         this.setAlpha(this.tooltipAnimProgress);
         this.begin(drawContext);
         this.tooltipWidget.render(this, mouseX, mouseY, delta);
         this.end();
         this.setAlpha((double)1.0F);
         this.lastTooltip = this.tooltip;
         toReturn = true;
      }

      this.tooltip = null;
      return toReturn;
   }

   public void setAlpha(double a) {
      this.r.setAlpha(a);
      this.rTex.setAlpha(a);
      this.theme.textRenderer().setAlpha(a);
   }

   public void tooltip(String text) {
      this.tooltip = text;
   }

   public void quad(double x, double y, double width, double height, Color cTopLeft, Color cTopRight, Color cBottomRight, Color cBottomLeft) {
      this.r.quad(x, y, width, height, cTopLeft, cTopRight, cBottomRight, cBottomLeft);
   }

   public void quad(double x, double y, double width, double height, Color colorLeft, Color colorRight) {
      this.quad(x, y, width, height, colorLeft, colorRight, colorRight, colorLeft);
   }

   public void quad(double x, double y, double width, double height, Color color) {
      this.quad(x, y, width, height, color, color);
   }

   public void quad(WWidget widget, Color color) {
      this.quad(widget.x, widget.y, widget.width, widget.height, color);
   }

   public void quad(double x, double y, double width, double height, GuiTexture texture, Color color) {
      this.rTex.texQuad(x, y, width, height, texture.get(width, height), color);
   }

   public void rotatedQuad(double x, double y, double width, double height, double rotation, GuiTexture texture, Color color) {
      this.rTex.texQuad(x, y, width, height, rotation, texture.get(width, height), color);
   }

   public void triangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
      this.r.triangle(x1, y1, x2, y2, x3, y3, color);
   }

   public void text(String text, double x, double y, Color color, boolean title) {
      this.texts.add(((TextOperation)this.getOp(this.textPool, x, y, color)).set(text, this.theme.textRenderer(), title));
   }

   public void texture(double x, double y, double width, double height, double rotation, Texture texture) {
      this.post(() -> {
         this.rTex.begin();
         this.rTex.texQuad(x, y, width, height, rotation, (double)0.0F, (double)0.0F, (double)1.0F, (double)1.0F, WHITE);
         this.rTex.end();
         this.rTex.render(texture.method_71659(), texture.method_75484());
      });
   }

   public void post(Runnable task) {
      ((Scissor)this.scissorStack.top()).postTasks.add(task);
   }

   public void item(class_1799 itemStack, int x, int y, float scale, boolean overlay) {
      RenderUtils.drawItem(this.drawContext, itemStack, x, y, scale, overlay, (String)null, false);
   }

   public void absolutePost(Runnable task) {
      this.postTasks.add(task);
   }

   private <T extends GuiRenderOperation<T>> T getOp(Pool<T> pool, double x, double y, Color color) {
      T op = pool.get();
      op.set(x, y, color);
      return op;
   }
}
