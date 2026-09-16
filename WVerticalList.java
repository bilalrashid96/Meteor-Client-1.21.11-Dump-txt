package meteordevelopment.meteorclient.gui.widgets.containers;

import meteordevelopment.meteorclient.gui.utils.Cell;

public class WVerticalList extends WContainer {
   public double spacing = (double)3.0F;
   protected double widthRemove;

   protected double spacing() {
      return this.theme.scale(this.spacing);
   }

   protected void onCalculateSize() {
      this.width = (double)0.0F;
      this.height = (double)0.0F;

      for(int i = 0; i < this.cells.size(); ++i) {
         Cell<?> cell = (Cell)this.cells.get(i);
         if (i > 0) {
            this.height += this.spacing();
         }

         this.width = Math.max(this.width, cell.padLeft() + cell.widget().width + cell.padRight());
         this.height += cell.padTop() + cell.widget().height + cell.padBottom();
      }

   }

   protected void onCalculateWidgetPositions() {
      double y = this.y;

      for(int i = 0; i < this.cells.size(); ++i) {
         Cell<?> cell = (Cell)this.cells.get(i);
         if (i > 0) {
            y += this.spacing();
         }

         y += cell.padTop();
         cell.x = this.x + cell.padLeft();
         cell.y = y;
         cell.width = this.width - this.widthRemove - cell.padLeft() - cell.padRight();
         cell.height = cell.widget().height;
         cell.alignWidget();
         y += cell.height + cell.padBottom();
      }

   }
}
