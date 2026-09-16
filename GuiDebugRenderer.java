package meteordevelopment.meteorclient.gui.renderer;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_11909;
import net.minecraft.class_310;

public class GuiDebugRenderer {
   private static final Color CELL_COLOR = new Color(25, 225, 25);
   private static final Color WIDGET_COLOR = new Color(25, 25, 225);
   private final MeshBuilder mesh;

   public GuiDebugRenderer() {
      this.mesh = new MeshBuilder(MeteorRenderPipelines.WORLD_COLORED_LINES);
   }

   public void render(WWidget widget) {
      if (widget != null) {
         this.mesh.begin();
         this.renderWidget(widget);
         this.mesh.end();
         MeshRenderer.begin().attachments(class_310.method_1551().method_1522()).pipeline(MeteorRenderPipelines.WORLD_COLORED_LINES).mesh(this.mesh).end();
      }
   }

   public void mouseReleased(WWidget widget, class_11909 click, int i) {
      if (widget != null) {
         MeteorClient.LOG.info("{} {}", widget.getClass(), i);
         if (widget instanceof WContainer) {
            WContainer container = (WContainer)widget;

            for(Cell<?> cell : container.cells) {
               if (cell.widget().isOver(click.comp_4798(), click.comp_4799())) {
                  this.mouseReleased(cell.widget(), click, i + 1);
               }
            }
         }

      }
   }

   private void renderWidget(WWidget widget) {
      this.lineBox(widget.x, widget.y, widget.width, widget.height, WIDGET_COLOR);
      if (widget instanceof WContainer container) {
         for(Cell<?> cell : container.cells) {
            this.lineBox(cell.x, cell.y, cell.width, cell.height, CELL_COLOR);
            this.renderWidget(cell.widget());
         }
      }

   }

   private void lineBox(double x, double y, double width, double height, Color color) {
      this.line(x, y, x + width, y, color);
      this.line(x + width, y, x + width, y + height, color);
      this.line(x, y, x, y + height, color);
      this.line(x, y + height, x + width, y + height, color);
   }

   private void line(double x1, double y1, double x2, double y2, Color color) {
      this.mesh.ensureLineCapacity();
      this.mesh.line(this.mesh.vec3(x1, y1, (double)0.0F).color(color).next(), this.mesh.vec3(x2, y2, (double)0.0F).color(color).next());
   }
}
