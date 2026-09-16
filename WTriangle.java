package meteordevelopment.meteorclient.gui.widgets.pressable;

public abstract class WTriangle extends WPressable {
   public double rotation;

   protected void onCalculateSize() {
      double s = this.theme.textHeight();
      this.width = s;
      this.height = s;
   }
}
