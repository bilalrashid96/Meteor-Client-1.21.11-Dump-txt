package meteordevelopment.meteorclient.events.meteor;

import meteordevelopment.meteorclient.events.Cancellable;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import net.minecraft.class_11909;
import net.minecraft.class_11910;

public class MouseClickEvent extends Cancellable {
   private static final MouseClickEvent INSTANCE = new MouseClickEvent();
   public class_11909 click;
   public class_11910 input;
   public KeyAction action;

   public static MouseClickEvent get(class_11909 click, KeyAction action) {
      INSTANCE.setCancelled(false);
      INSTANCE.click = click;
      INSTANCE.input = click.comp_4800();
      INSTANCE.action = action;
      return INSTANCE;
   }

   public int button() {
      return INSTANCE.input.comp_4801();
   }
}
