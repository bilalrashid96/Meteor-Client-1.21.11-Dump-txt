package meteordevelopment.meteorclient.gui.widgets;

import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_11909;

public abstract class WLabel extends WPressable {
   public Color color;
   protected String text;
   protected boolean title;

   public WLabel(String text, boolean title) {
      this.text = text;
      this.title = title;
   }

   protected void onCalculateSize() {
      this.width = this.theme.textWidth(this.text, this.text.length(), this.title);
      this.height = this.theme.textHeight(this.title);
   }

   public boolean onMouseClicked(class_11909 click, boolean doubled) {
      return this.action != null ? super.onMouseClicked(click, doubled) : false;
   }

   public boolean onMouseReleased(class_11909 click) {
      return this.action != null ? super.onMouseReleased(click) : false;
   }

   public void set(String text) {
      if ((double)Math.round(this.theme.textWidth(text, text.length(), this.title)) != this.width) {
         this.invalidate();
      }

      this.text = text;
   }

   public String get() {
      return this.text;
   }

   public WLabel color(Color color) {
      this.color = color;
      return this;
   }
}
