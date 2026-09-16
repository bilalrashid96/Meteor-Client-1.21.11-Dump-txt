package meteordevelopment.meteorclient.events.meteor;

import meteordevelopment.meteorclient.events.Cancellable;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import net.minecraft.class_11908;

public class KeyEvent extends Cancellable {
   private static final KeyEvent INSTANCE = new KeyEvent();
   public class_11908 input;
   public KeyAction action;

   public static KeyEvent get(class_11908 input, KeyAction action) {
      INSTANCE.setCancelled(false);
      INSTANCE.input = input;
      INSTANCE.action = action;
      return INSTANCE;
   }

   public int key() {
      return INSTANCE.input.comp_4795();
   }

   public int modifiers() {
      return INSTANCE.input.comp_4797();
   }
}
