package meteordevelopment.meteorclient.gui.widgets;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.BaseWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;

public abstract class WWidget implements BaseWidget {
   public boolean visible = true;
   public GuiTheme theme;
   public double x;
   public double y;
   public double width;
   public double height;
   public double minWidth;
   public WWidget parent;
   public String tooltip;
   public boolean mouseOver;
   public boolean focused;
   protected boolean instantTooltips;
   protected double mouseOverTimer;

   public void init() {
   }

   public void move(double deltaX, double deltaY) {
      this.x = (double)Math.round(this.x + deltaX);
      this.y = (double)Math.round(this.y + deltaY);
   }

   public GuiTheme getTheme() {
      return this.theme;
   }

   public double pad() {
      return this.theme.pad();
   }

   public void calculateSize() {
      this.onCalculateSize();
      double minWidth = this.theme.scale(this.minWidth);
      if (this.width < minWidth) {
         this.width = minWidth;
      }

      this.width = (double)Math.round(this.width);
      this.height = (double)Math.round(this.height);
   }

   protected void onCalculateSize() {
   }

   public void calculateWidgetPositions() {
      this.x = (double)Math.round(this.x);
      this.y = (double)Math.round(this.y);
      this.onCalculateWidgetPositions();
   }

   protected void onCalculateWidgetPositions() {
   }

   public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (!this.visible) {
         return true;
      } else {
         if (this.isOver(mouseX, mouseY)) {
            this.mouseOverTimer += delta;
            if ((this.instantTooltips || this.mouseOverTimer >= (double)1.0F) && this.tooltip != null) {
               WView view = this.getView();
               if (view == null || view.mouseOver) {
                  renderer.tooltip(this.tooltip);
               }
            }
         } else {
            this.mouseOverTimer = (double)0.0F;
         }

         this.onRender(renderer, mouseX, mouseY, delta);
         return false;
      }
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
   }

   public boolean mouseClicked(class_11909 click, boolean doubled) {
      return this.onMouseClicked(click, doubled);
   }

   public boolean onMouseClicked(class_11909 click, boolean doubled) {
      return false;
   }

   public boolean mouseReleased(class_11909 click) {
      return this.onMouseReleased(click);
   }

   public boolean onMouseReleased(class_11909 click) {
      return false;
   }

   public void mouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
      this.mouseOver = this.isOver(mouseX, mouseY);
      this.onMouseMoved(mouseX, mouseY, lastMouseX, lastMouseY);
   }

   public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
   }

   public boolean mouseScrolled(double amount) {
      return this.onMouseScrolled(amount);
   }

   public boolean onMouseScrolled(double amount) {
      return false;
   }

   public boolean keyPressed(class_11908 input) {
      return this.onKeyPressed(input);
   }

   public boolean onKeyPressed(class_11908 input) {
      return false;
   }

   public boolean keyRepeated(class_11908 input) {
      return this.onKeyRepeated(input);
   }

   public boolean onKeyRepeated(class_11908 input) {
      return false;
   }

   public boolean charTyped(class_11905 input) {
      return this.onCharTyped(input);
   }

   public boolean onCharTyped(class_11905 input) {
      return false;
   }

   public void invalidate() {
      WWidget root = this.getRoot();
      if (root != null) {
         root.invalidate();
      }

   }

   protected WWidget getRoot() {
      return this.parent != null ? this.parent.getRoot() : (this instanceof WRoot ? this : null);
   }

   public WView getView() {
      return this instanceof WView ? (WView)this : (this.parent != null ? this.parent.getView() : null);
   }

   public boolean isOver(double x, double y) {
      return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
   }

   public boolean isFocused() {
      return this.focused;
   }

   public void setFocused(boolean focused) {
      if (this.focused != focused) {
         this.focused = focused;
      }

   }
}
