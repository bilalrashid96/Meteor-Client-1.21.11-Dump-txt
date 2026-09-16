package meteordevelopment.meteorclient.systems.modules.render.blockesp;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2791;
import net.minecraft.class_4076;
import net.minecraft.class_2902.class_2903;

public class ESPChunk {
   private final int x;
   private final int z;
   public Long2ObjectMap<ESPBlock> blocks;

   public ESPChunk(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public ESPBlock get(int x, int y, int z) {
      return this.blocks == null ? null : (ESPBlock)this.blocks.get(ESPBlock.getKey(x, y, z));
   }

   public void add(class_2338 blockPos, boolean update) {
      ESPBlock block = new ESPBlock(blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260());
      if (this.blocks == null) {
         this.blocks = new Long2ObjectOpenHashMap(64);
      }

      this.blocks.put(ESPBlock.getKey(blockPos), block);
      if (update) {
         block.update();
      }

   }

   public void add(class_2338 blockPos) {
      this.add(blockPos, true);
   }

   public void remove(class_2338 blockPos) {
      if (this.blocks != null) {
         ESPBlock block = (ESPBlock)this.blocks.remove(ESPBlock.getKey(blockPos));
         if (block != null) {
            block.group.remove(block);
         }
      }

   }

   public void update() {
      if (this.blocks != null) {
         ObjectIterator var1 = this.blocks.values().iterator();

         while(var1.hasNext()) {
            ESPBlock block = (ESPBlock)var1.next();
            block.update();
         }
      }

   }

   public void update(int x, int y, int z) {
      if (this.blocks != null) {
         ESPBlock block = (ESPBlock)this.blocks.get(ESPBlock.getKey(x, y, z));
         if (block != null) {
            block.update();
         }
      }

   }

   public int size() {
      return this.blocks == null ? 0 : this.blocks.size();
   }

   public boolean shouldBeDeleted() {
      int viewDist = Utils.getRenderDistance() + 1;
      int chunkX = class_4076.method_18675(MeteorClient.mc.field_1724.method_24515().method_10263());
      int chunkZ = class_4076.method_18675(MeteorClient.mc.field_1724.method_24515().method_10260());
      return this.x > chunkX + viewDist || this.x < chunkX - viewDist || this.z > chunkZ + viewDist || this.z < chunkZ - viewDist;
   }

   public void render(Render3DEvent event) {
      if (this.blocks != null) {
         ObjectIterator var2 = this.blocks.values().iterator();

         while(var2.hasNext()) {
            ESPBlock block = (ESPBlock)var2.next();
            block.render(event);
         }
      }

   }

   public static ESPChunk searchChunk(class_2791 chunk, List<class_2248> blocks) {
      ESPChunk schunk = new ESPChunk(chunk.method_12004().field_9181, chunk.method_12004().field_9180);
      if (schunk.shouldBeDeleted()) {
         return schunk;
      } else {
         class_2338.class_2339 blockPos = new class_2338.class_2339();

         for(int x = chunk.method_12004().method_8326(); x <= chunk.method_12004().method_8327(); ++x) {
            for(int z = chunk.method_12004().method_8328(); z <= chunk.method_12004().method_8329(); ++z) {
               int height = chunk.method_12032(class_2903.field_13202).method_12603(x - chunk.method_12004().method_8326(), z - chunk.method_12004().method_8328());

               for(int y = MeteorClient.mc.field_1687.method_31607(); y < height; ++y) {
                  blockPos.method_10103(x, y, z);
                  class_2680 bs = chunk.method_8320(blockPos);
                  if (blocks.contains(bs.method_26204())) {
                     schunk.add(blockPos, false);
                  }
               }
            }
         }

         return schunk;
      }
   }
}
