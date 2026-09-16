package meteordevelopment.meteorclient.gui.widgets;

public class WVerticalSeparator extends WWidget {
   protected void onCalculateSize() {
      this.width = this.theme.scale((double)3.0F);
      this.height = (double)1.0F;
   }
}
