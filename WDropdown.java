package meteordevelopment.meteorclient.gui.widgets.input;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WRoot;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_3532;

public abstract class WDropdown<T> extends WPressable {
   public Runnable action;
   protected T[] values;
   protected T value;
   protected double maxValueWidth;
   protected WDropdownRoot root;
   protected boolean expanded;
   protected double animProgress;

   public WDropdown(T[] values, T value) {
      this.values = values;
      this.set(value);
   }

   public void init() {
      this.root = this.createRootWidget();
      this.root.theme = this.theme;
      this.root.spacing = (double)0.0F;

      for(int i = 0; i < this.values.length; ++i) {
         WDropdown<T>.WDropdownValue widget = this.createValueWidget();
         widget.theme = this.theme;
         widget.value = (T)this.values[i];
         Cell<?> cell = this.root.add(widget).padHorizontal((double)2.0F).expandWidgetX();
         if (i >= this.values.length - 1) {
            cell.padBottom((double)2.0F);
         }
      }

   }

   protected abstract WDropdownRoot createRootWidget();

   protected abstract WDropdown<T>.WDropdownValue createValueWidget();

   protected void onCalculateSize() {
      double pad = this.pad();
      this.maxValueWidth = (double)0.0F;

      for(T value : this.values) {
         double valueWidth = this.theme.textWidth(value.toString());
         this.maxValueWidth = Math.max(this.maxValueWidth, valueWidth);
      }

      this.root.calculateSize();
      this.width = pad + this.maxValueWidth + pad + this.theme.textHeight() + pad;
      this.height = pad + this.theme.textHeight() + pad;
      this.root.width = this.width;
   }

   protected void onCalculateWidgetPositions() {
      super.onCalculateWidgetPositions();
      this.root.x = this.x;
      this.root.y = this.y + this.height;
      this.root.calculateWidgetPositions();
   }

   protected void onPressed(int button) {
      this.expanded = !this.expanded;
      this.root.setFocused(this.expanded);
      this.setFocused(this.expanded);
   }

   public T get() {
      return this.value;
   }

   public void set(T value) {
      this.value = value;
   }

   public void move(double deltaX, double deltaY) {
      super.move(deltaX, deltaY);
      this.root.move(deltaX, deltaY);
   }

   public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      boolean render = super.render(renderer, mouseX, mouseY, delta);
      this.animProgress += (double)(this.expanded ? 1 : -1) * delta * (double)14.0F;
      this.animProgress = class_3532.method_15350(this.animProgress, (double)0.0F, (double)1.0F);
      WView view = this.getView();
      boolean rootInView = view == null || view.isWidgetInView(this.root);
      if (!render && this.animProgress > (double)0.0F && rootInView) {
         renderer.absolutePost(() -> {
            renderer.scissorStart(this.x, this.y + this.height, this.width, this.root.height * this.animProgress);
            this.root.render(renderer, mouseX, mouseY, delta);
            renderer.scissorEnd();
         });
      }

      if (this.expanded && this.root.mouseOver) {
         this.theme.disableHoverColor = true;
      }

      return render;
   }

   public boolean onMouseClicked(class_11909 click, boolean doubled) {
      boolean used = false;
      if (!this.mouseOver && !this.root.mouseOver) {
         this.expanded = false;
      }

      if (super.onMouseClicked(click, doubled)) {
         used = true;
      }

      if (this.expanded && this.root.mouseClicked(click, doubled)) {
         used = true;
      }

      return used;
   }

   public boolean onMouseReleased(class_11909 click) {
      if (super.onMouseReleased(click)) {
         return true;
      } else {
         return this.expanded && this.root.mouseReleased(click);
      }
   }

   public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
      super.onMouseMoved(mouseX, mouseY, lastMouseX, lastMouseY);
      if (this.expanded) {
         this.root.mouseMoved(mouseX, mouseY, lastMouseX, lastMouseY);
      }

   }

   public boolean onMouseScrolled(double amount) {
      if (super.onMouseScrolled(amount)) {
         return true;
      } else {
         return this.expanded ? this.root.mouseScrolled(amount) : false;
      }
   }

   public boolean onKeyPressed(class_11908 input) {
      if (super.onKeyPressed(input)) {
         return true;
      } else {
         return this.expanded && this.root.keyPressed(input);
      }
   }

   public boolean onKeyRepeated(class_11908 input) {
      if (super.onKeyRepeated(input)) {
         return true;
      } else {
         return this.expanded && this.root.keyRepeated(input);
      }
   }

   public boolean onCharTyped(class_11905 input) {
      if (super.onCharTyped(input)) {
         return true;
      } else {
         return this.expanded && this.root.charTyped(input);
      }
   }

   protected abstract static class WDropdownRoot extends WVerticalList implements WRoot {
      public void invalidate() {
      }
   }

   protected abstract class WDropdownValue extends WPressable {
      protected T value;

      protected void onPressed(int button) {
         boolean isNew = !WDropdown.this.value.equals(this.value);
         WDropdown.this.value = this.value;
         WDropdown.this.expanded = false;
         if (isNew && WDropdown.this.action != null) {
            WDropdown.this.action.run();
         }

      }
   }
}
