package meteordevelopment.meteorclient.gui.themes.meteor.widgets.input;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.themes.meteor.widgets.WMeteorLabel;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_3532;

public class WMeteorTextBox extends WTextBox implements MeteorWidget {
   private boolean cursorVisible;
   private double cursorTimer;
   private double animProgress;

   public WMeteorTextBox(String text, String placeholder, CharFilter filter, Class<? extends WTextBox.Renderer> renderer) {
      super(text, placeholder, filter, renderer);
   }

   protected WContainer createCompletionsRootWidget() {
      return new WVerticalList() {
         protected void onRender(GuiRenderer renderer1, double mouseX, double mouseY, double delta) {
            MeteorGuiTheme theme1 = WMeteorTextBox.this.theme();
            double s = theme1.scale((double)2.0F);
            Color c = theme1.outlineColor.get();
            Color col = theme1.backgroundColor.get();
            int preA = col.a;
            col.a += col.a / 2;
            col.validate();
            renderer1.quad(this, col);
            col.a = preA;
            renderer1.quad(this.x, this.y + this.height - s, this.width, s, c);
            renderer1.quad(this.x, this.y, s, this.height - s, c);
            renderer1.quad(this.x + this.width - s, this.y, s, this.height - s, c);
         }
      };
   }

   protected <T extends WWidget & WTextBox.ICompletionItem> T createCompletionsValueWidth(String completion, boolean selected) {
      return (T)(new CompletionItem(completion, false, selected));
   }

   protected void onCursorChanged() {
      this.cursorVisible = true;
      this.cursorTimer = (double)0.0F;
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (this.cursorTimer >= (double)1.0F) {
         this.cursorVisible = !this.cursorVisible;
         this.cursorTimer = (double)0.0F;
      } else {
         this.cursorTimer += delta * (double)1.75F;
      }

      this.renderBackground(renderer, this, false, false);
      MeteorGuiTheme theme = this.theme();
      double pad = this.pad();
      double overflowWidth = this.getOverflowWidthForRender();
      renderer.scissorStart(this.x + pad, this.y + pad, this.width - pad * (double)2.0F, this.height - pad * (double)2.0F);
      if (!this.text.isEmpty()) {
         this.renderer.render(renderer, this.x + pad - overflowWidth, this.y + pad, this.text, theme.textColor.get());
      } else if (this.placeholder != null) {
         this.renderer.render(renderer, this.x + pad - overflowWidth, this.y + pad, this.placeholder, theme.placeholderColor.get());
      }

      if (this.focused && (this.cursor != this.selectionStart || this.cursor != this.selectionEnd)) {
         double selStart = this.x + pad + this.getTextWidth(this.selectionStart) - overflowWidth;
         double selEnd = this.x + pad + this.getTextWidth(this.selectionEnd) - overflowWidth;
         renderer.quad(selStart, this.y + pad, selEnd - selStart, theme.textHeight(), theme.textHighlightColor.get());
      }

      this.animProgress += delta * (double)10.0F * (double)(this.focused && this.cursorVisible ? 1 : -1);
      this.animProgress = class_3532.method_15350(this.animProgress, (double)0.0F, (double)1.0F);
      if (this.focused && this.cursorVisible || this.animProgress > (double)0.0F) {
         renderer.setAlpha(this.animProgress);
         renderer.quad(this.x + pad + this.getTextWidth(this.cursor) - overflowWidth, this.y + pad, theme.scale((double)1.0F), theme.textHeight(), theme.textColor.get());
         renderer.setAlpha((double)1.0F);
      }

      renderer.scissorEnd();
   }

   private static class CompletionItem extends WMeteorLabel implements WTextBox.ICompletionItem {
      private static final Color SELECTED_COLOR = new Color(255, 255, 255, 15);
      private boolean selected;

      public CompletionItem(String text, boolean title, boolean selected) {
         super(text, title);
         this.selected = selected;
      }

      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         super.onRender(renderer, mouseX, mouseY, delta);
         if (this.selected) {
            renderer.quad(this, SELECTED_COLOR);
         }

      }

      public boolean isSelected() {
         return this.selected;
      }

      public void setSelected(boolean selected) {
         this.selected = selected;
      }

      public String getCompletion() {
         return this.text;
      }
   }
}
