package meteordevelopment.meteorclient.gui.themes.meteor.widgets.input;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorDropdown<T> extends WDropdown<T> implements MeteorWidget {
   public WMeteorDropdown(T[] values, T value) {
      super(values, value);
   }

   protected WDropdown.WDropdownRoot createRootWidget() {
      return new WRoot();
   }

   protected WDropdown<T>.WDropdownValue createValueWidget() {
      return new WValue();
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      MeteorGuiTheme theme = this.theme();
      double pad = this.pad();
      double s = theme.textHeight();
      this.renderBackground(renderer, this, this.pressed, this.mouseOver);
      String text = this.get().toString();
      double w = theme.textWidth(text);
      renderer.text(text, this.x + pad + this.maxValueWidth / (double)2.0F - w / (double)2.0F, this.y + pad, theme.textColor.get(), false);
      renderer.rotatedQuad(this.x + pad + this.maxValueWidth + pad, this.y + pad, s, s, (double)0.0F, GuiRenderer.TRIANGLE, theme.textColor.get());
   }

   private static class WRoot extends WDropdown.WDropdownRoot implements MeteorWidget {
      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         MeteorGuiTheme theme = this.theme();
         double s = theme.scale((double)2.0F);
         Color c = theme.outlineColor.get();
         renderer.quad(this.x, this.y + this.height - s, this.width, s, c);
         renderer.quad(this.x, this.y, s, this.height - s, c);
         renderer.quad(this.x + this.width - s, this.y, s, this.height - s, c);
      }
   }

   private class WValue extends WDropdown<T>.WDropdownValue implements MeteorWidget {
      protected void onCalculateSize() {
         double pad = this.pad();
         this.width = pad + this.theme.textWidth(this.value.toString()) + pad;
         this.height = pad + this.theme.textHeight() + pad;
      }

      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         MeteorGuiTheme theme = this.theme();
         Color color = theme.backgroundColor.get(this.pressed, this.mouseOver, true);
         int preA = color.a;
         color.a += color.a / 2;
         color.validate();
         renderer.quad(this, color);
         color.a = preA;
         String text = this.value.toString();
         renderer.text(text, this.x + this.width / (double)2.0F - theme.textWidth(text) / (double)2.0F, this.y + this.pad(), theme.textColor.get(), false);
      }
   }
}
