package meteordevelopment.meteorclient.gui.widgets.pressable;

import net.minecraft.class_11909;

public class WConfirmedMinus extends WMinus {
   protected boolean pressedOnce = false;

   public boolean onMouseClicked(class_11909 click, boolean doubled) {
      boolean pressed = super.onMouseClicked(click, doubled);
      if (!pressed) {
         this.pressedOnce = false;
      }

      return pressed;
   }

   public boolean onMouseReleased(class_11909 click) {
      if (this.pressed && this.pressedOnce) {
         super.onMouseReleased(click);
      }

      this.pressedOnce = this.pressed;
      return this.pressed = false;
   }
}
