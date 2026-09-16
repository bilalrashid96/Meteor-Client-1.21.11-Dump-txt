package meteordevelopment.meteorclient.utils.world;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2338;
import net.minecraft.class_2680;

public class BlockIterator {
   private static final Pool<Callback> callbackPool = new Pool<Callback>(Callback::new);
   private static final List<Callback> callbacks = new ReferenceArrayList();
   private static final List<Runnable> afterCallbacks = new ReferenceArrayList();
   private static final class_2338.class_2339 blockPos = new class_2338.class_2339();
   private static int hRadius;
   private static int vRadius;
   private static boolean disableCurrent;

   private BlockIterator() {
   }

   @PreInit
   public static void init() {
      MeteorClient.EVENT_BUS.subscribe(BlockIterator.class);
   }

   @EventHandler(
      priority = -201
   )
   private static void onTick(TickEvent.Pre event) {
      if (Utils.canUpdate()) {
         int px = MeteorClient.mc.field_1724.method_31477();
         int py = MeteorClient.mc.field_1724.method_31478();
         int pz = MeteorClient.mc.field_1724.method_31479();

         for(int x = px - hRadius; x <= px + hRadius; ++x) {
            for(int z = pz - hRadius; z <= pz + hRadius; ++z) {
               for(int y = Math.max(MeteorClient.mc.field_1687.method_31607(), py - vRadius); y <= py + vRadius && y <= MeteorClient.mc.field_1687.method_31605(); ++y) {
                  blockPos.method_10103(x, y, z);
                  class_2680 blockState = MeteorClient.mc.field_1687.method_8320(blockPos);
                  int dx = Math.abs(x - px);
                  int dy = Math.abs(y - py);
                  int dz = Math.abs(z - pz);
                  callbacks.removeIf((callback) -> {
                     if (dx <= callback.hRadius && dy <= callback.vRadius && dz <= callback.hRadius) {
                        disableCurrent = false;
                        callback.function.accept(blockPos, blockState);
                        return disableCurrent;
                     } else {
                        return false;
                     }
                  });
               }
            }
         }

         hRadius = 0;
         vRadius = 0;
         callbackPool.freeAll(callbacks);
         callbacks.clear();

         for(Runnable callback : afterCallbacks) {
            callback.run();
         }

         afterCallbacks.clear();
      }
   }

   public static void register(int horizontalRadius, int verticalRadius, BiConsumer<class_2338, class_2680> function) {
      hRadius = Math.max(hRadius, horizontalRadius);
      vRadius = Math.max(vRadius, verticalRadius);
      Callback callback = callbackPool.get();
      callback.function = function;
      callback.hRadius = horizontalRadius;
      callback.vRadius = verticalRadius;
      callbacks.add(callback);
   }

   public static void disableCurrent() {
      disableCurrent = true;
   }

   public static void after(Runnable callback) {
      afterCallbacks.add(callback);
   }

   private static class Callback {
      public BiConsumer<class_2338, class_2680> function;
      public int hRadius;
      public int vRadius;
   }
}
