package meteordevelopment.meteorclient.utils.world;

import java.util.Arrays;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2761;
import net.minecraft.class_3532;

public class TickRate {
   public static TickRate INSTANCE = new TickRate();
   private final float[] tickRates = new float[20];
   private int nextIndex = 0;
   private long timeLastTimeUpdate = -1L;
   private long timeGameJoined;

   private TickRate() {
      MeteorClient.EVENT_BUS.subscribe(this);
   }

   @EventHandler
   private void onReceivePacket(PacketEvent.Receive event) {
      if (event.packet instanceof class_2761) {
         long now = System.currentTimeMillis();
         float timeElapsed = (float)(now - this.timeLastTimeUpdate) / 1000.0F;
         this.tickRates[this.nextIndex] = class_3532.method_15363(20.0F / timeElapsed, 0.0F, 20.0F);
         this.nextIndex = (this.nextIndex + 1) % this.tickRates.length;
         this.timeLastTimeUpdate = now;
      }

   }

   @EventHandler
   private void onGameJoined(GameJoinedEvent event) {
      Arrays.fill(this.tickRates, 0.0F);
      this.nextIndex = 0;
      this.timeGameJoined = this.timeLastTimeUpdate = System.currentTimeMillis();
   }

   public float getTickRate() {
      if (!Utils.canUpdate()) {
         return 0.0F;
      } else if (System.currentTimeMillis() - this.timeGameJoined < 4000L) {
         return 20.0F;
      } else {
         int numTicks = 0;
         float sumTickRates = 0.0F;

         for(float tickRate : this.tickRates) {
            if (tickRate > 0.0F) {
               sumTickRates += tickRate;
               ++numTicks;
            }
         }

         return sumTickRates / (float)numTicks;
      }
   }

   public float getTimeSinceLastTick() {
      long now = System.currentTimeMillis();
      return now - this.timeGameJoined < 4000L ? 0.0F : (float)(now - this.timeLastTimeUpdate) / 1000.0F;
   }
}
