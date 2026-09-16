package meteordevelopment.meteorclient.utils.world;

import java.util.Iterator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.ClientChunkManagerAccessor;
import meteordevelopment.meteorclient.mixin.ClientChunkMapAccessor;
import net.minecraft.class_2791;

public class ChunkIterator implements Iterator<class_2791> {
   private final ClientChunkMapAccessor map;
   private final boolean onlyWithLoadedNeighbours;
   private int i;
   private class_2791 chunk;

   public ChunkIterator(boolean onlyWithLoadedNeighbours) {
      this.map = (ClientChunkMapAccessor)((ClientChunkManagerAccessor)MeteorClient.mc.field_1687.method_2935()).meteor$getChunks();
      this.i = 0;
      this.onlyWithLoadedNeighbours = onlyWithLoadedNeighbours;
      this.getNext();
   }

   private class_2791 getNext() {
      class_2791 prev = this.chunk;
      this.chunk = null;

      while(this.i < this.map.meteor$getChunks().length()) {
         this.chunk = (class_2791)this.map.meteor$getChunks().get(this.i++);
         if (this.chunk != null && (!this.onlyWithLoadedNeighbours || this.isInRadius(this.chunk))) {
            break;
         }
      }

      return prev;
   }

   private boolean isInRadius(class_2791 chunk) {
      int x = chunk.method_12004().field_9181;
      int z = chunk.method_12004().field_9180;
      return MeteorClient.mc.field_1687.method_2935().method_12123(x + 1, z) && MeteorClient.mc.field_1687.method_2935().method_12123(x - 1, z) && MeteorClient.mc.field_1687.method_2935().method_12123(x, z + 1) && MeteorClient.mc.field_1687.method_2935().method_12123(x, z - 1);
   }

   public boolean hasNext() {
      return this.chunk != null;
   }

   public class_2791 next() {
      return this.getNext();
   }
}
