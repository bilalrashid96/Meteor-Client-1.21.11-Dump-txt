package meteordevelopment.meteorclient.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.renderer.GuiDebugRenderer;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WRoot;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.CursorStyle;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_6417;

public abstract class WidgetScreen extends class_437 {
   private static final GuiRenderer RENDERER = new GuiRenderer();
   private static final GuiDebugRenderer DEBUG_RENDERER = new GuiDebugRenderer();
   public Runnable taskAfterRender;
   protected Runnable enterAction;
   public class_437 parent;
   private final WContainer root;
   protected final GuiTheme theme;
   public boolean locked;
   public boolean lockedAllowClose;
   private boolean closed;
   private boolean onClose;
   private boolean debug;
   private boolean closing;
   private double lastMouseX;
   private double lastMouseY;
   public double animProgress;
   private List<Runnable> onClosed;
   protected boolean firstInit = true;

   public WidgetScreen(GuiTheme theme, String title) {
      super(class_2561.method_43470(title));
      this.parent = MeteorClient.mc.field_1755;
      this.root = new WFullScreenRoot();
      this.theme = theme;
      this.root.theme = theme;
      if (this.parent != null) {
         this.animProgress = (double)1.0F;
         if (this instanceof TabScreen && this.parent instanceof TabScreen) {
            this.parent = ((TabScreen)this.parent).parent;
         }
      }

   }

   public <W extends WWidget> Cell<W> add(W widget) {
      return this.root.<W>add(widget);
   }

   public void clear() {
      this.root.clear();
   }

   public void invalidate() {
      this.root.invalidate();
   }

   protected void method_25426() {
      MeteorClient.EVENT_BUS.subscribe(this);
      this.closed = false;
      if (this.firstInit) {
         this.firstInit = false;
         this.initWidgets();
      }

   }

   public abstract void initWidgets();

   public void reload() {
      this.clear();
      this.initWidgets();
   }

   public void onClosed(Runnable action) {
      if (this.onClosed == null) {
         this.onClosed = new ArrayList(2);
      }

      this.onClosed.add(action);
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      if (this.locked) {
         return false;
      } else {
         double mouseX = click.comp_4798();
         double mouseY = click.comp_4799();
         double s = (double)MeteorClient.mc.method_22683().method_4495();
         mouseX *= s;
         mouseY *= s;
         return this.root.mouseClicked(new class_11909(mouseX, mouseY, click.comp_4800()), doubled);
      }
   }

   public boolean method_25406(class_11909 click) {
      if (this.locked) {
         return false;
      } else {
         double mouseX = click.comp_4798();
         double mouseY = click.comp_4799();
         double s = (double)MeteorClient.mc.method_22683().method_4495();
         mouseX *= s;
         mouseY *= s;
         if (this.debug && click.method_74245() == 1) {
            DEBUG_RENDERER.mouseReleased(this.root, new class_11909(mouseX, mouseY, click.comp_4800()), 0);
         }

         return this.root.mouseReleased(new class_11909(mouseX, mouseY, click.comp_4800()));
      }
   }

   public void method_16014(double mouseX, double mouseY) {
      if (!this.locked) {
         double s = (double)MeteorClient.mc.method_22683().method_4495();
         mouseX *= s;
         mouseY *= s;
         this.root.mouseMoved(mouseX, mouseY, this.lastMouseX, this.lastMouseY);
         this.lastMouseX = mouseX;
         this.lastMouseY = mouseY;
      }
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.locked) {
         return false;
      } else {
         this.root.mouseScrolled(verticalAmount);
         return super.method_25401(mouseX, mouseY, horizontalAmount, verticalAmount);
      }
   }

   public boolean method_16803(class_11908 input) {
      if (this.locked) {
         return false;
      } else if ((input.comp_4797() == 2 || input.comp_4797() == 8) && input.comp_4795() == 57) {
         this.debug = !this.debug;
         return true;
      } else if ((input.comp_4795() == 257 || input.comp_4795() == 335) && this.enterAction != null) {
         this.enterAction.run();
         return true;
      } else {
         return super.method_16803(input);
      }
   }

   public boolean method_25404(class_11908 input) {
      if (this.locked) {
         return false;
      } else {
         boolean shouldReturn = this.root.keyPressed(input) || super.method_25404(input);
         if (shouldReturn) {
            return true;
         } else if (input.comp_4795() == 258) {
            AtomicReference<WTextBox> firstTextBox = new AtomicReference((Object)null);
            AtomicBoolean done = new AtomicBoolean(false);
            AtomicBoolean foundFocused = new AtomicBoolean(false);
            this.loopWidgets(this.root, (wWidget) -> {
               if (!done.get() && wWidget instanceof WTextBox textBox) {
                  if (foundFocused.get()) {
                     textBox.setFocused(true);
                     textBox.setCursorMax();
                     done.set(true);
                  } else if (textBox.isFocused()) {
                     textBox.setFocused(false);
                     foundFocused.set(true);
                  }

                  if (firstTextBox.get() == null) {
                     firstTextBox.set(textBox);
                  }

               }
            });
            if (!done.get() && firstTextBox.get() != null) {
               ((WTextBox)firstTextBox.get()).setFocused(true);
               ((WTextBox)firstTextBox.get()).setCursorMax();
            }

            return true;
         } else {
            boolean control = class_6417.field_52734 ? input.comp_4797() == 8 : input.comp_4797() == 2;
            return control && input.comp_4795() == 67 && this.toClipboard() || control && input.comp_4795() == 86 && this.fromClipboard();
         }
      }
   }

   public void keyRepeated(class_11908 input) {
      if (!this.locked) {
         this.root.keyRepeated(input);
      }
   }

   public boolean method_25400(class_11905 input) {
      return this.locked ? false : this.root.charTyped(input);
   }

   public void method_25420(class_332 context, int mouseX, int mouseY, float deltaTicks) {
      if (this.field_22787.field_1687 == null) {
         this.method_57728(context, deltaTicks);
      }

   }

   public void renderCustom(class_332 context, int mouseX, int mouseY, float delta) {
      int s = MeteorClient.mc.method_22683().method_4495();
      mouseX *= s;
      mouseY *= s;
      this.animProgress += (double)(delta / 20.0F * 14.0F * (float)(this.closing ? -1 : 1));
      this.animProgress = class_3532.method_15350(this.animProgress, (double)0.0F, (double)1.0F);
      if (this.closing && (this.animProgress == (double)0.0F || this.parent != null)) {
         this.closeInternal();
      }

      GuiKeyEvents.canUseKeys = true;
      Utils.unscaledProjection();
      this.onRenderBefore(context, delta);
      RENDERER.theme = this.theme;
      this.theme.beforeRender();
      RENDERER.begin(context);
      RENDERER.setAlpha(this.animProgress);
      this.root.render(RENDERER, (double)mouseX, (double)mouseY, (double)(delta / 20.0F));
      RENDERER.setAlpha((double)1.0F);
      RENDERER.end();
      boolean tooltip = RENDERER.renderTooltip(context, (double)mouseX, (double)mouseY, (double)(delta / 20.0F));
      if (this.debug) {
         DEBUG_RENDERER.render(this.root);
         if (tooltip) {
            DEBUG_RENDERER.render(RENDERER.tooltipWidget);
         }
      }

      Utils.scaledProjection();
      this.runAfterRenderTasks();
   }

   protected void runAfterRenderTasks() {
      if (this.taskAfterRender != null) {
         this.taskAfterRender.run();
         this.taskAfterRender = null;
      }

   }

   protected void onRenderBefore(class_332 drawContext, float delta) {
   }

   public void method_25410(int width, int height) {
      super.method_25410(width, height);
      this.root.invalidate();
   }

   public void method_25419() {
      if (!this.locked || this.lockedAllowClose) {
         this.closing = true;
      }

   }

   public void method_25432() {
      if (!this.closed || this.lockedAllowClose) {
         this.closed = true;
         this.onClosed();
         Input.setCursorStyle(CursorStyle.Default);
         this.loopWidgets(this.root, (widget) -> {
            if (widget instanceof WTextBox textBox) {
               if (textBox.isFocused()) {
                  textBox.setFocused(false);
               }
            }

         });
         MeteorClient.EVENT_BUS.unsubscribe(this);
         GuiKeyEvents.canUseKeys = true;
         if (this.onClosed != null) {
            for(Runnable action : this.onClosed) {
               action.run();
            }
         }

         if (this.onClose) {
            this.taskAfterRender = () -> {
               this.locked = true;
               MeteorClient.mc.method_1507(this.parent);
            };
         }
      }

   }

   private void closeInternal() {
      boolean preOnClose = this.onClose;
      this.onClose = true;
      super.method_25419();
      this.method_25432();
      this.onClose = preOnClose;
   }

   private void loopWidgets(WWidget widget, Consumer<WWidget> action) {
      action.accept(widget);
      if (widget instanceof WContainer) {
         for(Cell<?> cell : ((WContainer)widget).cells) {
            this.loopWidgets(cell.widget(), action);
         }
      }

   }

   protected void onClosed() {
   }

   public boolean toClipboard() {
      return false;
   }

   public boolean fromClipboard() {
      return false;
   }

   public boolean method_25422() {
      return !this.locked || this.lockedAllowClose;
   }

   public boolean method_25421() {
      return false;
   }

   private static class WFullScreenRoot extends WContainer implements WRoot {
      private boolean valid;

      public void invalidate() {
         this.valid = false;
      }

      protected void onCalculateSize() {
         this.width = (double)Utils.getWindowWidth();
         this.height = (double)Utils.getWindowHeight();
      }

      protected void onCalculateWidgetPositions() {
         for(Cell<?> cell : this.cells) {
            cell.x = (double)0.0F;
            cell.y = (double)0.0F;
            cell.width = this.width;
            cell.height = this.height;
            cell.alignWidget();
         }

      }

      public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         if (!this.valid) {
            this.calculateSize();
            this.calculateWidgetPositions();
            this.valid = true;
            this.mouseMoved(MeteorClient.mc.field_1729.method_1603(), MeteorClient.mc.field_1729.method_1604(), MeteorClient.mc.field_1729.method_1603(), MeteorClient.mc.field_1729.method_1604());
         }

         return super.render(renderer, mouseX, mouseY, delta);
      }
   }
}
