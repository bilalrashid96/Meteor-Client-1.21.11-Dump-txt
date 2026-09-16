package meteordevelopment.meteorclient.systems.modules.render.blockesp;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.BlockUpdateEvent;
import meteordevelopment.meteorclient.events.world.ChunkDataEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockDataSetting;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.GenericSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.RainbowColors;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1923;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2791;
import net.minecraft.class_2874;

public class BlockESP extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<class_2248>> blocks;
   private final Setting<ESPBlockData> defaultBlockConfig;
   private final Setting<Map<class_2248, ESPBlockData>> blockConfigs;
   private final Setting<Boolean> tracers;
   private final class_2338.class_2339 blockPos;
   private final Long2ObjectMap<ESPChunk> chunks;
   private final Set<ESPGroup> groups;
   private final ExecutorService workerThread;
   private class_2874 lastDimension;

   public BlockESP() {
      super(Categories.Render, "block-esp", "Renders specified blocks through walls.", "search");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks")).description("Blocks to search for.")).onChanged((blocks1) -> {
         if (this.isActive() && Utils.canUpdate()) {
            this.onActivate();
         }

      })).build());
      this.defaultBlockConfig = this.sgGeneral.add(((GenericSetting.Builder)((GenericSetting.Builder)((GenericSetting.Builder)(new GenericSetting.Builder()).name("default-block-config")).description("Default block config.")).defaultValue(new ESPBlockData(ShapeMode.Lines, new SettingColor(0, 255, 200), new SettingColor(0, 255, 200, 25), true, new SettingColor(0, 255, 200, 125)))).build());
      this.blockConfigs = this.sgGeneral.add(((BlockDataSetting.Builder)((BlockDataSetting.Builder)(new BlockDataSetting.Builder()).name("block-configs")).description("Config for each block.")).defaultData(this.defaultBlockConfig).build());
      this.tracers = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("tracers")).description("Render tracer lines.")).defaultValue(false)).build());
      this.blockPos = new class_2338.class_2339();
      this.chunks = new Long2ObjectOpenHashMap();
      this.groups = new ReferenceOpenHashSet();
      this.workerThread = Executors.newSingleThreadExecutor();
      RainbowColors.register(this::onTickRainbow);
   }

   public void onActivate() {
      synchronized(this.chunks) {
         this.chunks.clear();
         this.groups.clear();
      }

      for(class_2791 chunk : Utils.chunks()) {
         this.searchChunk(chunk);
      }

      this.lastDimension = this.mc.field_1687.method_8597();
   }

   public void onDeactivate() {
      synchronized(this.chunks) {
         this.chunks.clear();
         this.groups.clear();
      }
   }

   private void onTickRainbow() {
      if (this.isActive()) {
         ((ESPBlockData)this.defaultBlockConfig.get()).tickRainbow();

         for(ESPBlockData blockData : ((Map)this.blockConfigs.get()).values()) {
            blockData.tickRainbow();
         }

      }
   }

   ESPBlockData getBlockData(class_2248 block) {
      ESPBlockData blockData = (ESPBlockData)((Map)this.blockConfigs.get()).get(block);
      return blockData == null ? (ESPBlockData)this.defaultBlockConfig.get() : blockData;
   }

   private void updateChunk(int x, int z) {
      ESPChunk chunk = (ESPChunk)this.chunks.get(class_1923.method_8331(x, z));
      if (chunk != null) {
         chunk.update();
      }

   }

   private void updateBlock(int x, int y, int z) {
      ESPChunk chunk = (ESPChunk)this.chunks.get(class_1923.method_8331(x >> 4, z >> 4));
      if (chunk != null) {
         chunk.update(x, y, z);
      }

   }

   public ESPBlock getBlock(int x, int y, int z) {
      ESPChunk chunk = (ESPChunk)this.chunks.get(class_1923.method_8331(x >> 4, z >> 4));
      return chunk == null ? null : chunk.get(x, y, z);
   }

   public ESPGroup newGroup(class_2248 block) {
      synchronized(this.chunks) {
         ESPGroup group = new ESPGroup(block);
         this.groups.add(group);
         return group;
      }
   }

   public void removeGroup(ESPGroup group) {
      synchronized(this.chunks) {
         this.groups.remove(group);
      }
   }

   @EventHandler
   private void onChunkData(ChunkDataEvent event) {
      this.searchChunk(event.chunk());
   }

   private void searchChunk(class_2791 chunk) {
      this.workerThread.submit(() -> {
         if (this.isActive()) {
            ESPChunk schunk = ESPChunk.searchChunk(chunk, this.blocks.get());
            if (schunk.size() > 0) {
               synchronized(this.chunks) {
                  this.chunks.put(chunk.method_12004().method_8324(), schunk);
                  schunk.update();
                  this.updateChunk(chunk.method_12004().field_9181 - 1, chunk.method_12004().field_9180);
                  this.updateChunk(chunk.method_12004().field_9181 + 1, chunk.method_12004().field_9180);
                  this.updateChunk(chunk.method_12004().field_9181, chunk.method_12004().field_9180 - 1);
                  this.updateChunk(chunk.method_12004().field_9181, chunk.method_12004().field_9180 + 1);
               }
            }

         }
      });
   }

   @EventHandler
   private void onBlockUpdate(BlockUpdateEvent event) {
      int bx = event.pos.method_10263();
      int by = event.pos.method_10264();
      int bz = event.pos.method_10260();
      int chunkX = bx >> 4;
      int chunkZ = bz >> 4;
      long key = class_1923.method_8331(chunkX, chunkZ);
      boolean added = ((List)this.blocks.get()).contains(event.newState.method_26204()) && !((List)this.blocks.get()).contains(event.oldState.method_26204());
      boolean removed = !added && !((List)this.blocks.get()).contains(event.newState.method_26204()) && ((List)this.blocks.get()).contains(event.oldState.method_26204());
      if (added || removed) {
         this.workerThread.submit(() -> {
            synchronized(this.chunks) {
               ESPChunk chunk = (ESPChunk)this.chunks.get(key);
               if (chunk == null) {
                  chunk = new ESPChunk(chunkX, chunkZ);
                  if (chunk.shouldBeDeleted()) {
                     return;
                  }

                  this.chunks.put(key, chunk);
               }

               this.blockPos.method_10103(bx, by, bz);
               if (added) {
                  chunk.add(this.blockPos);
               } else {
                  chunk.remove(this.blockPos);
               }

               for(int x = -1; x < 2; ++x) {
                  for(int z = -1; z < 2; ++z) {
                     for(int y = -1; y < 2; ++y) {
                        if (x != 0 || y != 0 || z != 0) {
                           this.updateBlock(bx + x, by + y, bz + z);
                        }
                     }
                  }
               }

            }
         });
      }

   }

   @EventHandler
   private void onPostTick(TickEvent.Post event) {
      class_2874 dimension = this.mc.field_1687.method_8597();
      if (this.lastDimension != dimension) {
         this.onActivate();
      }

      this.lastDimension = dimension;
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      synchronized(this.chunks) {
         Iterator<ESPChunk> it = this.chunks.values().iterator();

         while(it.hasNext()) {
            ESPChunk chunk = (ESPChunk)it.next();
            if (chunk.shouldBeDeleted()) {
               this.workerThread.submit(() -> {
                  ESPBlock block;
                  for(ObjectIterator var1 = chunk.blocks.values().iterator(); var1.hasNext(); block.loaded = false) {
                     block = (ESPBlock)var1.next();
                     block.group.remove(block, false);
                  }

               });
               it.remove();
            } else {
               chunk.render(event);
            }
         }

         if ((Boolean)this.tracers.get()) {
            for(ESPGroup group : this.groups) {
               group.render(event);
            }
         }

      }
   }

   public String getInfoString() {
      return "%s groups".formatted(this.groups.size());
   }
}
