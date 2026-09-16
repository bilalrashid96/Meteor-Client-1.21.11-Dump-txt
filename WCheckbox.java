package meteordevelopment.meteorclient.gui.widgets.pressable;

public abstract class WCheckbox extends WPressable {
   public boolean checked;

   public WCheckbox(boolean checked) {
      this.checked = checked;
   }

   protected void onCalculateSize() {
      double pad = this.pad();
      double s = this.theme.textHeight();
      this.width = pad + s + pad;
      this.height = pad + s + pad;
   }

   protected void onPressed(int button) {
      this.checked = !this.checked;
   }
}
