package meteordevelopment.meteorclient.gui.widgets.containers;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_11909;
import net.minecraft.class_3532;

public abstract class WView extends WVerticalList {
   public double maxHeight = Double.MAX_VALUE;
   public boolean scrollOnlyWhenMouseOver = true;
   public boolean hasScrollBar = true;
   protected boolean canScroll;
   private double actualHeight;
   private double scroll;
   private double targetScroll;
   private boolean moveAfterPositionWidgets;
   protected boolean handleMouseOver;

   public void init() {
      this.maxHeight = (double)Utils.getWindowHeight() - this.theme.scale((double)128.0F);
   }

   protected void onCalculateSize() {
      boolean couldScroll = this.canScroll;
      this.canScroll = false;
      this.widthRemove = (double)0.0F;
      super.onCalculateSize();
      if (this.height > this.maxHeight) {
         this.actualHeight = this.height;
         this.height = this.maxHeight;
         this.canScroll = true;
         if (this.hasScrollBar) {
            this.widthRemove = this.handleWidth() * (double)2.0F;
            this.width += this.widthRemove;
         }

         if (couldScroll) {
            this.moveAfterPositionWidgets = true;
         }
      } else {
         this.actualHeight = this.height;
         this.scroll = (double)0.0F;
         this.targetScroll = (double)0.0F;
      }

   }

   protected void onCalculateWidgetPositions() {
      super.onCalculateWidgetPositions();
      if (this.moveAfterPositionWidgets) {
         this.scroll = class_3532.method_15350(this.scroll, (double)0.0F, this.actualHeight - this.height);
         this.targetScroll = this.scroll;
         this.moveCells((double)0.0F, -this.scroll);
         this.moveAfterPositionWidgets = false;
      }

   }

   public boolean onMouseClicked(class_11909 click, boolean doubled) {
      if (this.handleMouseOver && click.method_74245() == 0 && !doubled) {
         this.setFocused(true);
         return true;
      } else {
         return false;
      }
   }

   public boolean onMouseReleased(class_11909 click) {
      if (this.focused) {
         this.setFocused(false);
      }

      return false;
   }

   public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
      this.handleMouseOver = false;
      if (this.canScroll && this.hasScrollBar) {
         double x = this.handleX();
         double y = this.handleY();
         if (mouseX >= x && mouseX <= x + this.handleWidth() && mouseY >= y && mouseY <= y + this.handleHeight()) {
            this.handleMouseOver = true;
         }
      }

      if (this.focused) {
         double preScroll = this.scroll;
         double mouseDelta = mouseY - lastMouseY;
         this.scroll += (double)Math.round(mouseDelta * ((this.actualHeight - this.handleHeight() / (double)2.0F) / this.height));
         this.scroll = class_3532.method_15350(this.scroll, (double)0.0F, this.actualHeight - this.height);
         this.targetScroll = this.scroll;
         double delta = this.scroll - preScroll;
         if (delta != (double)0.0F) {
            this.moveCells((double)0.0F, -delta);
         }
      }

   }

   public boolean onMouseScrolled(double amount) {
      if (this.scrollOnlyWhenMouseOver && !this.mouseOver) {
         return false;
      } else {
         double max = this.actualHeight - this.height;
         this.targetScroll -= (double)Math.round(this.theme.scale(amount * (double)40.0F));
         this.targetScroll = class_3532.method_15350(this.targetScroll, (double)0.0F, max);
         return this.targetScroll > (double)0.0F && this.targetScroll < max;
      }
   }

   public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      this.updateScroll(delta);
      if (this.canScroll) {
         renderer.scissorStart(this.x, this.y, this.width, this.height);
      }

      boolean render = super.render(renderer, mouseX, mouseY, delta);
      if (this.canScroll) {
         renderer.scissorEnd();
      }

      return render;
   }

   private void updateScroll(double delta) {
      double preScroll = this.scroll;
      double max = this.actualHeight - this.height;
      if (Math.abs(this.targetScroll - this.scroll) < (double)1.0F) {
         this.scroll = this.targetScroll;
      } else if (this.targetScroll > this.scroll) {
         this.scroll += (double)Math.round(this.theme.scale(delta * (double)300.0F + delta * (double)100.0F * (Math.abs(this.targetScroll - this.scroll) / (double)10.0F)));
         if (this.scroll > this.targetScroll) {
            this.scroll = this.targetScroll;
         }
      } else if (this.targetScroll < this.scroll) {
         this.scroll -= (double)Math.round(this.theme.scale(delta * (double)300.0F + delta * (double)100.0F * (Math.abs(this.targetScroll - this.scroll) / (double)10.0F)));
         if (this.scroll < this.targetScroll) {
            this.scroll = this.targetScroll;
         }
      }

      this.scroll = class_3532.method_15350(this.scroll, (double)0.0F, max);
      double change = this.scroll - preScroll;
      if (change != (double)0.0F) {
         this.moveCells((double)0.0F, -change);
      }

   }

   protected boolean propagateEvents(WWidget widget) {
      if (widget.isFocused()) {
         return true;
      } else if (widget instanceof WView) {
         return this.isWidgetInView(widget);
      } else {
         return this.mouseOver && this.isWidgetInView(widget);
      }
   }

   protected double handleWidth() {
      return this.theme.scale((double)6.0F);
   }

   protected double handleHeight() {
      return this.height / this.actualHeight * this.height;
   }

   protected double handleX() {
      return this.x + this.width - this.handleWidth();
   }

   protected double handleY() {
      return this.y + (this.height - this.handleHeight()) * (this.scroll / (this.actualHeight - this.height));
   }

   public boolean isWidgetInView(WWidget widget) {
      return widget.y < this.y + this.height && widget.y + widget.height > this.y;
   }
}
