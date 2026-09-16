package meteordevelopment.meteorclient.events;

import meteordevelopment.orbit.ICancellable;

public class Cancellable implements ICancellable {
   private boolean cancelled = false;

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }
}
