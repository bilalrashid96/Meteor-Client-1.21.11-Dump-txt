package meteordevelopment.meteorclient.utils.world;

import java.util.Iterator;
import java.util.Map;
import meteordevelopment.meteorclient.mixin.ChunkAccessor;
import net.minecraft.class_2338;
import net.minecraft.class_2586;
import net.minecraft.class_2791;

public class BlockEntityIterator implements Iterator<class_2586> {
   private final Iterator<class_2791> chunks = new ChunkIterator(false);
   private Iterator<class_2586> blockEntities;

   public BlockEntityIterator() {
      this.nextChunk();
   }

   private void nextChunk() {
      while(true) {
         if (this.chunks.hasNext()) {
            Map<class_2338, class_2586> blockEntityMap = ((ChunkAccessor)this.chunks.next()).getBlockEntities();
            if (blockEntityMap.isEmpty()) {
               continue;
            }

            this.blockEntities = blockEntityMap.values().iterator();
         }

         return;
      }
   }

   public boolean hasNext() {
      if (this.blockEntities == null) {
         return false;
      } else if (this.blockEntities.hasNext()) {
         return true;
      } else {
         this.nextChunk();
         return this.blockEntities.hasNext();
      }
   }

   public class_2586 next() {
      return (class_2586)this.blockEntities.next();
   }
}
