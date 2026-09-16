package meteordevelopment.meteorclient.systems.modules.render.blockesp;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.UnorderedArrayList;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import net.minecraft.class_2248;

public class ESPGroup {
   private static final BlockESP blockEsp = (BlockESP)Modules.get().get(BlockESP.class);
   private final class_2248 block;
   public final UnorderedArrayList<ESPBlock> blocks = new UnorderedArrayList<ESPBlock>();
   private double sumX;
   private double sumY;
   private double sumZ;

   public ESPGroup(class_2248 block) {
      this.block = block;
   }

   public void add(ESPBlock block, boolean removeFromOld, boolean splitGroup) {
      this.blocks.add(block);
      this.sumX += (double)block.x;
      this.sumY += (double)block.y;
      this.sumZ += (double)block.z;
      if (block.group != null && removeFromOld) {
         block.group.remove(block, splitGroup);
      }

      block.group = this;
   }

   public void add(ESPBlock block) {
      this.add(block, true, true);
   }

   public void remove(ESPBlock block, boolean splitGroup) {
      this.blocks.remove(block);
      this.sumX -= (double)block.x;
      this.sumY -= (double)block.y;
      this.sumZ -= (double)block.z;
      if (this.blocks.isEmpty()) {
         blockEsp.removeGroup(block.group);
      } else if (splitGroup) {
         this.trySplit(block);
      }

   }

   public void remove(ESPBlock block) {
      this.remove(block, true);
   }

   private void trySplit(ESPBlock block) {
      Set<ESPBlock> neighbours = new ObjectOpenHashSet(6);

      for(int side : ESPBlock.SIDES) {
         if ((block.neighbours & side) == side) {
            ESPBlock neighbour = block.getSideBlock(side);
            if (neighbour != null) {
               neighbours.add(neighbour);
            }
         }
      }

      if (neighbours.size() > 1) {
         Set<ESPBlock> remainingBlocks = new ObjectOpenHashSet(this.blocks);
         Queue<ESPBlock> blocksToCheck = new ArrayDeque();
         blocksToCheck.offer((ESPBlock)this.blocks.getFirst());
         remainingBlocks.remove(this.blocks.getFirst());
         neighbours.remove(this.blocks.getFirst());

         label86:
         while(!blocksToCheck.isEmpty()) {
            ESPBlock b = (ESPBlock)blocksToCheck.poll();

            for(int side : ESPBlock.SIDES) {
               if ((b.neighbours & side) == side) {
                  ESPBlock neighbour = b.getSideBlock(side);
                  if (neighbour != null && remainingBlocks.contains(neighbour)) {
                     blocksToCheck.offer(neighbour);
                     remainingBlocks.remove(neighbour);
                     neighbours.remove(neighbour);
                     if (neighbours.isEmpty()) {
                        break label86;
                     }
                  }
               }
            }
         }

         if (!neighbours.isEmpty()) {
            ESPGroup group = blockEsp.newGroup(this.block);
            group.blocks.ensureCapacity(remainingBlocks.size());
            UnorderedArrayList var10000 = this.blocks;
            Objects.requireNonNull(remainingBlocks);
            var10000.removeIf(remainingBlocks::contains);

            for(ESPBlock b : remainingBlocks) {
               group.add(b, false, false);
               this.sumX -= (double)b.x;
               this.sumY -= (double)b.y;
               this.sumZ -= (double)b.z;
            }

            if (neighbours.size() > 1) {
               block.neighbours = 0;

               for(ESPBlock b : neighbours) {
                  int x = b.x - block.x;
                  if (x == 1) {
                     block.neighbours |= 8;
                  } else if (x == -1) {
                     block.neighbours |= 128;
                  }

                  int y = b.y - block.y;
                  if (y == 1) {
                     block.neighbours |= 512;
                  } else if (y == -1) {
                     block.neighbours |= 16384;
                  }

                  int z = b.z - block.z;
                  if (z == 1) {
                     block.neighbours |= 2;
                  } else if (z == -1) {
                     block.neighbours |= 32;
                  }
               }

               group.trySplit(block);
            }
         }

      }
   }

   public void merge(ESPGroup group) {
      this.blocks.ensureCapacity(this.blocks.size() + group.blocks.size());

      for(ESPBlock block : group.blocks) {
         this.add(block, false, false);
      }

      blockEsp.removeGroup(group);
   }

   public void render(Render3DEvent event) {
      ESPBlockData blockData = blockEsp.getBlockData(this.block);
      if (blockData.tracer) {
         event.renderer.line(RenderUtils.center.field_1352, RenderUtils.center.field_1351, RenderUtils.center.field_1350, this.sumX / (double)this.blocks.size() + (double)0.5F, this.sumY / (double)this.blocks.size() + (double)0.5F, this.sumZ / (double)this.blocks.size() + (double)0.5F, blockData.tracerColor);
      }

   }
}
