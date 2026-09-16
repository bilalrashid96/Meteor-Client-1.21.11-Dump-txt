package meteordevelopment.meteorclient.gui.widgets.containers;

import java.util.function.Consumer;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.utils.WindowConfig;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_11909;
import net.minecraft.class_3532;

public abstract class WWindow extends WVerticalList {
   public double padding = (double)8.0F;
   public Consumer<WContainer> beforeHeaderInit;
   public String id;
   public final WWidget icon;
   protected final String title;
   protected WHeader header;
   public WView view;
   protected boolean dragging;
   protected boolean expanded = true;
   protected boolean dragged;
   protected double animProgress = (double)1.0F;
   protected boolean moved = false;
   protected double movedX;
   protected double movedY;
   private boolean propagateEventsExpanded;

   public WWindow(WWidget icon, String title) {
      this.icon = icon;
      this.title = title;
   }

   public void init() {
      this.header = this.header(this.icon);
      this.header.theme = this.theme;
      super.add(this.header).expandWidgetX().widget();
      this.view = (WView)super.add(this.theme.view()).expandX().pad(this.padding).widget();
      if (this.id != null) {
         this.expanded = this.theme.getWindowConfig(this.id).expanded;
         this.animProgress = this.expanded ? (double)1.0F : (double)0.0F;
      }

   }

   protected abstract WHeader header(WWidget var1);

   public <T extends WWidget> Cell<T> add(T widget) {
      return this.view.add(widget);
   }

   public void clear() {
      this.view.clear();
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
      if (this.id != null) {
         WindowConfig config = this.theme.getWindowConfig(this.id);
         config.expanded = expanded;
      }

   }

   protected void onCalculateWidgetPositions() {
      if (this.id != null) {
         WindowConfig config = this.theme.getWindowConfig(this.id);
         if (config.x != (double)-1.0F) {
            this.x = config.x;
            if (this.x + this.width > (double)Utils.getWindowWidth()) {
               this.x = (double)Utils.getWindowWidth() - this.width;
            }
         }

         if (config.y != (double)-1.0F) {
            this.y = config.y;
            if (this.y + this.height > (double)Utils.getWindowHeight()) {
               this.y = (double)Utils.getWindowHeight() - this.height;
            }
         }
      }

      super.onCalculateWidgetPositions();
      if (this.moved) {
         this.move(this.movedX - this.x, this.movedY - this.y);
      }

   }

   public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (!this.visible) {
         return true;
      } else {
         boolean scissor = this.animProgress != (double)0.0F && this.animProgress != (double)1.0F || this.expanded && this.animProgress != (double)1.0F;
         if (scissor) {
            renderer.scissorStart(this.x, this.y, this.width, (this.height - this.header.height) * this.animProgress + this.header.height);
         }

         boolean toReturn = super.render(renderer, mouseX, mouseY, delta);
         if (scissor) {
            renderer.scissorEnd();
         }

         return toReturn;
      }
   }

   protected void renderWidget(WWidget widget, GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (this.expanded || this.animProgress > (double)0.0F || widget instanceof WHeader) {
         widget.render(renderer, mouseX, mouseY, delta);
      }

      this.propagateEventsExpanded = this.expanded;
   }

   protected boolean propagateEvents(WWidget widget) {
      return widget instanceof WHeader || this.propagateEventsExpanded;
   }

   protected abstract class WHeader extends WContainer {
      private final WWidget icon;
      private WTriangle triangle;
      private WHorizontalList list;

      public WHeader(WWidget icon) {
         this.icon = icon;
      }

      public void init() {
         if (this.icon != null) {
            this.createList();
            this.add(this.icon).centerY();
         }

         if (WWindow.this.beforeHeaderInit != null) {
            this.createList();
            WWindow.this.beforeHeaderInit.accept(this);
         }

         this.add(this.theme.label(WWindow.this.title, true)).expandCellX().center().pad((double)4.0F);
         this.triangle = (WTriangle)this.add(this.theme.triangle()).pad((double)4.0F).right().centerY().widget();
         this.triangle.action = () -> WWindow.this.setExpanded(!WWindow.this.expanded);
      }

      private void createList() {
         this.list = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
         this.list.spacing = (double)0.0F;
      }

      public <T extends WWidget> Cell<T> add(T widget) {
         return this.list != null ? this.list.add(widget) : super.add(widget);
      }

      protected void onCalculateSize() {
         this.width = (double)0.0F;
         this.height = (double)0.0F;

         for(Cell<?> cell : this.cells) {
            double w = cell.padLeft() + cell.widget().width + cell.padRight();
            if (cell.widget() instanceof WTriangle) {
               w *= (double)2.0F;
            }

            this.width += w;
            this.height = Math.max(this.height, cell.padTop() + cell.widget().height + cell.padBottom());
         }

      }

      public boolean onMouseClicked(class_11909 click, boolean doubled) {
         if (this.mouseOver && !doubled) {
            if (click.method_74245() == 1) {
               WWindow.this.setExpanded(!WWindow.this.expanded);
            } else {
               WWindow.this.dragging = true;
               WWindow.this.dragged = false;
            }

            return true;
         } else {
            return false;
         }
      }

      public boolean onMouseReleased(class_11909 click) {
         if (WWindow.this.dragging) {
            WWindow.this.dragging = false;
            if (!WWindow.this.dragged) {
               WWindow.this.setExpanded(!WWindow.this.expanded);
            }
         }

         return false;
      }

      public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
         if (WWindow.this.dragging) {
            WWindow.this.move(mouseX - lastMouseX, mouseY - lastMouseY);
            WWindow.this.moved = true;
            WWindow.this.movedX = this.x;
            WWindow.this.movedY = this.y;
            if (WWindow.this.id != null) {
               WindowConfig config = this.theme.getWindowConfig(WWindow.this.id);
               config.x = this.x;
               config.y = this.y;
            }

            WWindow.this.dragged = true;
         }

      }

      public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         WWindow var10000 = WWindow.this;
         var10000.animProgress += (double)(WWindow.this.expanded ? 1 : -1) * delta * (double)14.0F;
         WWindow.this.animProgress = class_3532.method_15350(WWindow.this.animProgress, (double)0.0F, (double)1.0F);
         this.triangle.rotation = ((double)1.0F - WWindow.this.animProgress) * (double)-90.0F;
         return super.render(renderer, mouseX, mouseY, delta);
      }
   }
}
