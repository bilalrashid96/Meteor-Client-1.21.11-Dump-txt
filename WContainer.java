package meteordevelopment.meteorclient.gui.widgets.containers;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_312;

public abstract class WContainer extends WWidget {
   public final List<Cell<?>> cells = new ArrayList();

   public <T extends WWidget> Cell<T> add(T widget) {
      widget.parent = this;
      widget.theme = this.theme;
      Cell<T> cell = (new Cell<T>(widget)).centerY();
      this.cells.add(cell);
      widget.init();
      this.invalidate();
      return cell;
   }

   public void clear() {
      if (!this.cells.isEmpty()) {
         this.cells.clear();
         this.invalidate();
      }

   }

   public void remove(Cell<?> cell) {
      if (this.cells.remove(cell)) {
         this.invalidate();
      }

   }

   public void move(double deltaX, double deltaY) {
      super.move(deltaX, deltaY);

      for(Cell<?> cell : this.cells) {
         cell.move(deltaX, deltaY);
      }

   }

   public void moveCells(double deltaX, double deltaY) {
      for(Cell<?> cell : this.cells) {
         cell.move(deltaX, deltaY);
         class_312 mouse = MeteorClient.mc.field_1729;
         cell.widget().mouseMoved(mouse.method_1603(), mouse.method_1604(), mouse.method_1603(), mouse.method_1604());
      }

   }

   public boolean isFocused() {
      if (this.focused) {
         return true;
      } else {
         for(Cell<?> cell : this.cells) {
            if (cell.widget().isFocused()) {
               return true;
            }
         }

         return false;
      }
   }

   public void calculateSize() {
      for(Cell<?> cell : this.cells) {
         cell.widget().calculateSize();
      }

      super.calculateSize();
   }

   protected void onCalculateSize() {
      this.width = (double)0.0F;
      this.height = (double)0.0F;

      for(Cell<?> cell : this.cells) {
         this.width = Math.max(this.width, cell.padLeft() + cell.widget().width + cell.padRight());
         this.height = Math.max(this.height, cell.padTop() + cell.widget().height + cell.padBottom());
      }

   }

   public void calculateWidgetPositions() {
      super.calculateWidgetPositions();

      for(Cell<?> cell : this.cells) {
         cell.widget().calculateWidgetPositions();
      }

   }

   protected void onCalculateWidgetPositions() {
      for(Cell<?> cell : this.cells) {
         cell.x = this.x + cell.padLeft();
         cell.y = this.y + cell.padTop();
         cell.width = this.width - cell.padLeft() - cell.padRight();
         cell.height = this.height - cell.padTop() - cell.padBottom();
         cell.alignWidget();
      }

   }

   public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (super.render(renderer, mouseX, mouseY, delta)) {
         return true;
      } else {
         WView view = this.getView();
         double windowHeight = (double)Utils.getWindowHeight();

         for(Cell<?> cell : this.cells) {
            WWidget widget = cell.widget();
            if (widget.y > windowHeight) {
               break;
            }

            if (!(widget.y + widget.height <= (double)0.0F) && this.shouldRenderWidget(widget, view)) {
               this.renderWidget(widget, renderer, mouseX, mouseY, delta);
            }
         }

         return false;
      }
   }

   protected void renderWidget(WWidget widget, GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      widget.render(renderer, mouseX, mouseY, delta);
   }

   private boolean shouldRenderWidget(WWidget widget, WView view) {
      if (view == null) {
         return true;
      } else if (!view.isWidgetInView(widget)) {
         return false;
      } else {
         if (widget.mouseOver && !view.mouseOver) {
            widget.mouseOver = false;
         }

         return true;
      }
   }

   protected boolean propagateEvents(WWidget widget) {
      return true;
   }

   public boolean mouseClicked(class_11909 click, boolean doubled) {
      try {
         for(Cell<?> cell : this.cells) {
            if (this.propagateEvents(cell.widget()) && cell.widget().mouseClicked(click, doubled)) {
               return true;
            }
         }
      } catch (ConcurrentModificationException var5) {
      }

      return super.mouseClicked(click, doubled);
   }

   public boolean mouseReleased(class_11909 click) {
      try {
         for(Cell<?> cell : this.cells) {
            if (this.propagateEvents(cell.widget()) && cell.widget().mouseReleased(click)) {
               return true;
            }
         }
      } catch (ConcurrentModificationException var4) {
      }

      return super.mouseReleased(click);
   }

   public void mouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
      try {
         for(Cell<?> cell : this.cells) {
            if (this.propagateEvents(cell.widget())) {
               cell.widget().mouseMoved(mouseX, mouseY, lastMouseX, lastMouseY);
            }
         }
      } catch (ConcurrentModificationException var11) {
      }

      super.mouseMoved(mouseX, mouseY, lastMouseX, lastMouseY);
   }

   public boolean mouseScrolled(double amount) {
      try {
         for(Cell<?> cell : this.cells) {
            if (this.propagateEvents(cell.widget()) && cell.widget().mouseScrolled(amount)) {
               return true;
            }
         }
      } catch (ConcurrentModificationException var5) {
      }

      return super.mouseScrolled(amount);
   }

   public boolean keyPressed(class_11908 input) {
      try {
         for(Cell<?> cell : this.cells) {
            if (this.propagateEvents(cell.widget()) && cell.widget().keyPressed(input)) {
               return true;
            }
         }
      } catch (ConcurrentModificationException var4) {
      }

      return this.onKeyPressed(input);
   }

   public boolean keyRepeated(class_11908 input) {
      try {
         for(Cell<?> cell : this.cells) {
            if (this.propagateEvents(cell.widget()) && cell.widget().keyRepeated(input)) {
               return true;
            }
         }
      } catch (ConcurrentModificationException var4) {
      }

      return this.onKeyRepeated(input);
   }

   public boolean charTyped(class_11905 input) {
      try {
         for(Cell<?> cell : this.cells) {
            if (this.propagateEvents(cell.widget()) && cell.widget().charTyped(input)) {
               return true;
            }
         }
      } catch (ConcurrentModificationException var4) {
      }

      return super.charTyped(input);
   }
}
