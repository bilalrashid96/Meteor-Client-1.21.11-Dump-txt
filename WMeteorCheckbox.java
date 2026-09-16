package meteordevelopment.meteorclient.gui.themes.meteor.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import net.minecraft.class_3532;

public class WMeteorCheckbox extends WCheckbox implements MeteorWidget {
   private double animProgress;

   public WMeteorCheckbox(boolean checked) {
      super(checked);
      this.animProgress = checked ? (double)1.0F : (double)0.0F;
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      MeteorGuiTheme theme = this.theme();
      this.animProgress += (double)(this.checked ? 1 : -1) * delta * (double)14.0F;
      this.animProgress = class_3532.method_15350(this.animProgress, (double)0.0F, (double)1.0F);
      this.renderBackground(renderer, this, this.pressed, this.mouseOver);
      if (this.animProgress > (double)0.0F) {
         double cs = (this.width - theme.scale((double)2.0F)) / (double)1.75F * this.animProgress;
         renderer.quad(this.x + (this.width - cs) / (double)2.0F, this.y + (this.height - cs) / (double)2.0F, cs, cs, theme.checkboxColor.get());
      }

   }
}
