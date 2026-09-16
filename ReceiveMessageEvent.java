package meteordevelopment.meteorclient.events.game;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_2561;
import net.minecraft.class_7591;

public class ReceiveMessageEvent extends Cancellable {
   private static final ReceiveMessageEvent INSTANCE = new ReceiveMessageEvent();
   private class_2561 message;
   private class_7591 indicator;
   private boolean modified;
   public int id;

   public static ReceiveMessageEvent get(class_2561 message, class_7591 indicator, int id) {
      INSTANCE.setCancelled(false);
      INSTANCE.message = message;
      INSTANCE.indicator = indicator;
      INSTANCE.modified = false;
      INSTANCE.id = id;
      return INSTANCE;
   }

   public class_2561 getMessage() {
      return this.message;
   }

   public class_7591 getIndicator() {
      return this.indicator;
   }

   public void setMessage(class_2561 message) {
      this.message = message;
      this.modified = true;
   }

   public void setIndicator(class_7591 indicator) {
      this.indicator = indicator;
      this.modified = true;
   }

   public boolean isModified() {
      return this.modified;
   }
}
