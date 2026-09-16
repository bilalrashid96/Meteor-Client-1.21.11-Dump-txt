package meteordevelopment.meteorclient.gui.widgets.containers;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;

public class WTable extends WContainer {
   public double horizontalSpacing = (double)3.0F;
   public double verticalSpacing = (double)3.0F;
   private final List<List<Cell<?>>> rows = new ArrayList();
   private int rowI;
   private final DoubleList rowHeights = new DoubleArrayList();
   private final DoubleList columnWidths = new DoubleArrayList();
   private final DoubleList rowWidths = new DoubleArrayList();
   private final IntList rowExpandCellXCounts = new IntArrayList();

   public <T extends WWidget> Cell<T> add(T widget) {
      Cell<T> cell = super.<T>add(widget);
      if (this.rows.size() <= this.rowI) {
         List<Cell<?>> row = new ArrayList();
         row.add(cell);
         this.rows.add(row);
      } else {
         ((List)this.rows.get(this.rowI)).add(cell);
      }

      return cell;
   }

   public void row() {
      ++this.rowI;
   }

   public int rowI() {
      return this.rowI;
   }

   public void removeRow(int i) {
      for(Cell<?> cell : (List)this.rows.remove(i)) {
         Iterator<Cell<?>> it = this.cells.iterator();

         while(it.hasNext()) {
            if (it.next() == cell) {
               it.remove();
               break;
            }
         }
      }

      --this.rowI;
   }

   public List<Cell<?>> getRow(int i) {
      return i >= 0 && i < this.rows.size() ? (List)this.rows.get(i) : null;
   }

   public void clear() {
      super.clear();
      this.rows.clear();
      this.rowI = 0;
   }

   protected double horizontalSpacing() {
      return this.theme.scale(this.horizontalSpacing);
   }

   protected double verticalSpacing() {
      return this.theme.scale(this.verticalSpacing);
   }

   protected void onCalculateSize() {
      this.calculateInfo();
      this.rowWidths.clear();
      this.width = (double)0.0F;
      this.height = (double)0.0F;

      for(int rowI = 0; rowI < this.rows.size(); ++rowI) {
         List<Cell<?>> row = (List)this.rows.get(rowI);
         double rowWidth = (double)0.0F;

         for(int cellI = 0; cellI < row.size(); ++cellI) {
            if (cellI > 0) {
               rowWidth += this.horizontalSpacing();
            }

            rowWidth += this.columnWidths.getDouble(cellI);
         }

         this.rowWidths.add(rowWidth);
         this.width = Math.max(this.width, rowWidth);
         if (rowI > 0) {
            this.height += this.verticalSpacing();
         }

         this.height += this.rowHeights.getDouble(rowI);
      }

   }

   protected void onCalculateWidgetPositions() {
      double y = this.y;

      for(int rowI = 0; rowI < this.rows.size(); ++rowI) {
         List<Cell<?>> row = (List)this.rows.get(rowI);
         if (rowI > 0) {
            y += this.verticalSpacing();
         }

         double x = this.x;
         double rowHeight = this.rowHeights.getDouble(rowI);
         double expandXAdd = this.rowExpandCellXCounts.getInt(rowI) > 0 ? (this.width - this.rowWidths.getDouble(rowI)) / (double)this.rowExpandCellXCounts.getInt(rowI) : (double)0.0F;

         for(int cellI = 0; cellI < row.size(); ++cellI) {
            Cell<?> cell = (Cell)row.get(cellI);
            if (cellI > 0) {
               x += this.horizontalSpacing();
            }

            double columnWidth = this.columnWidths.getDouble(cellI);
            cell.x = x;
            cell.y = y;
            cell.width = columnWidth + (cell.expandCellX ? expandXAdd : (double)0.0F);
            cell.height = rowHeight;
            cell.alignWidget();
            x += columnWidth + (cell.expandCellX ? expandXAdd : (double)0.0F);
         }

         y += rowHeight;
      }

   }

   private void calculateInfo() {
      this.rowHeights.clear();
      this.columnWidths.clear();
      this.rowExpandCellXCounts.clear();
      Map<String, IntList> columnGroups = new HashMap();

      for(List<Cell<?>> row : this.rows) {
         double rowHeight = (double)0.0F;
         int rowExpandXCount = 0;

         for(int i = 0; i < row.size(); ++i) {
            Cell<?> cell = (Cell)row.get(i);
            rowHeight = Math.max(rowHeight, cell.padTop() + cell.widget().height + cell.padBottom());
            double cellWidth = cell.padLeft() + cell.widget().width + cell.padRight();
            if (this.columnWidths.size() <= i) {
               this.columnWidths.add(cellWidth);
            } else {
               this.columnWidths.set(i, Math.max(this.columnWidths.getDouble(i), cellWidth));
            }

            if (cell.group != null) {
               ((IntList)columnGroups.computeIfAbsent(cell.group, (k) -> new IntArrayList())).add(i);
            }

            if (cell.expandCellX) {
               ++rowExpandXCount;
            }
         }

         this.rowHeights.add(rowHeight);
         this.rowExpandCellXCounts.add(rowExpandXCount);
      }

      columnGroups.values().forEach((columns) -> {
         double maxWidth = (double)Integer.MIN_VALUE;

         int i;
         for(IntListIterator var4 = columns.iterator(); var4.hasNext(); maxWidth = Math.max(maxWidth, this.columnWidths.getDouble(i))) {
            i = (Integer)var4.next();
         }

         IntListIterator var6 = columns.iterator();

         while(var6.hasNext()) {
            i = (Integer)var6.next();
            this.columnWidths.set(i, maxWidth);
         }

      });
   }
}
