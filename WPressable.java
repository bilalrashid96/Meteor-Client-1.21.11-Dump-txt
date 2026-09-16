package meteordevelopment.meteorclient.gui.widgets.pressable;

import meteordevelopment.meteorclient.gui.widgets.WWidget;
import net.minecraft.class_11909;

public abstract class WPressable extends WWidget {
   public Runnable action;
   protected boolean pressed;

   public boolean onMouseClicked(class_11909 click, boolean doubled) {
      if (this.mouseOver && (click.method_74245() == 0 || click.method_74245() == 1)) {
         this.pressed = true;
      }

      return this.pressed;
   }

   public boolean onMouseReleased(class_11909 click) {
      if (this.pressed) {
         this.onPressed(click.method_74245());
         if (this.action != null) {
            this.action.run();
         }

         this.pressed = false;
      }

      return false;
   }

   protected void onPressed(int button) {
   }
}
