package meteordevelopment.meteorclient.gui.utils;

import meteordevelopment.meteorclient.gui.widgets.WWidget;
import org.jetbrains.annotations.Nullable;

public class Cell<T extends WWidget> {
   private final T widget;
   public double x;
   public double y;
   public double width;
   public double height;
   private AlignmentX alignX;
   private AlignmentY alignY;
   private double padTop;
   private double padRight;
   private double padBottom;
   private double padLeft;
   private double marginTop;
   private boolean expandWidgetX;
   private boolean expandWidgetY;
   public boolean expandCellX;
   public @Nullable String group;

   public Cell(T widget) {
      this.alignX = AlignmentX.Left;
      this.alignY = AlignmentY.Top;
      this.widget = widget;
   }

   public T widget() {
      return this.widget;
   }

   public void move(double deltaX, double deltaY) {
      this.x += deltaX;
      this.y += deltaY;
      this.widget.move(deltaX, deltaY);
   }

   public Cell<T> minWidth(double width) {
      this.widget.minWidth = width;
      return this;
   }

   public Cell<T> centerX() {
      this.alignX = AlignmentX.Center;
      return this;
   }

   public Cell<T> right() {
      this.alignX = AlignmentX.Right;
      return this;
   }

   public Cell<T> centerY() {
      this.alignY = AlignmentY.Center;
      return this;
   }

   public Cell<T> bottom() {
      this.alignY = AlignmentY.Bottom;
      return this;
   }

   public Cell<T> center() {
      this.alignX = AlignmentX.Center;
      this.alignY = AlignmentY.Center;
      return this;
   }

   public Cell<T> top() {
      this.alignY = AlignmentY.Top;
      return this;
   }

   public Cell<T> padTop(double pad) {
      this.padTop = pad;
      return this;
   }

   public Cell<T> padRight(double pad) {
      this.padRight = pad;
      return this;
   }

   public Cell<T> padBottom(double pad) {
      this.padBottom = pad;
      return this;
   }

   public Cell<T> padLeft(double pad) {
      this.padLeft = pad;
      return this;
   }

   public Cell<T> padHorizontal(double pad) {
      this.padRight = this.padLeft = pad;
      return this;
   }

   public Cell<T> padVertical(double pad) {
      this.padTop = this.padBottom = pad;
      return this;
   }

   public Cell<T> pad(double pad) {
      this.padTop = this.padRight = this.padBottom = this.padLeft = pad;
      return this;
   }

   public double padTop() {
      return this.s(this.padTop);
   }

   public double padRight() {
      return this.s(this.padRight);
   }

   public double padBottom() {
      return this.s(this.padBottom);
   }

   public double padLeft() {
      return this.s(this.padLeft);
   }

   public Cell<T> marginTop(double m) {
      this.marginTop = m;
      return this;
   }

   public Cell<T> expandWidgetX() {
      this.expandWidgetX = true;
      return this;
   }

   public Cell<T> expandWidgetY() {
      this.expandWidgetY = true;
      return this;
   }

   public Cell<T> expandCellX() {
      this.expandCellX = true;
      return this;
   }

   public Cell<T> expandX() {
      this.expandWidgetX = true;
      this.expandCellX = true;
      return this;
   }

   public Cell<T> group(String group) {
      this.group = group;
      return this;
   }

   public void alignWidget() {
      if (this.expandWidgetX) {
         this.widget.x = this.x;
         this.widget.width = this.width;
      } else {
         switch (this.alignX) {
            case Left -> this.widget.x = this.x;
            case Center -> this.widget.x = this.x + this.width / (double)2.0F - this.widget.width / (double)2.0F;
            case Right -> this.widget.x = this.x + this.width - this.widget.width;
         }
      }

      if (this.expandWidgetY) {
         this.widget.y = this.y;
         this.widget.height = this.height;
      } else {
         switch (this.alignY) {
            case Top -> this.widget.y = this.y + this.s(this.marginTop);
            case Center -> this.widget.y = this.y + this.height / (double)2.0F - this.widget.height / (double)2.0F;
            case Bottom -> this.widget.y = this.y + this.height - this.widget.height;
         }
      }

   }

   private double s(double value) {
      return this.widget.theme.scale(value);
   }
}
