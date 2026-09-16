package meteordevelopment.meteorclient.gui.widgets.pressable;

public abstract class WMinus extends WPressable {
   protected void onCalculateSize() {
      double pad = this.pad();
      double s = this.theme.textHeight();
      this.width = pad + s + pad;
      this.height = pad + s + pad;
   }
}
