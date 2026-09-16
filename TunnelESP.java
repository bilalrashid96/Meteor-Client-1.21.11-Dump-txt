package meteordevelopment.meteorclient.systems.modules.render;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dir;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_2826;
import net.minecraft.class_2902.class_2903;

public class TunnelESP extends Module {
   private static final class_2338.class_2339 BP = new class_2338.class_2339();
   private static final class_2350[] DIRECTIONS;
   private final SettingGroup sgGeneral;
   private final Setting<Double> height;
   private final Setting<Boolean> connected;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Long2ObjectMap<TChunk> chunks;

   public TunnelESP() {
      super(Categories.Render, "tunnel-esp", "Highlights tunnels.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.height = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("height")).description("Height of the rendered box.")).defaultValue(0.1).sliderMax((double)2.0F).build());
      this.connected = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("connected")).description("If neighbouring holes should be connected.")).defaultValue(true)).build());
      this.shapeMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(255, 175, 25, 50)).build());
      this.lineColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(255, 175, 25, 255)).build());
      this.chunks = new Long2ObjectOpenHashMap();
   }

   public void onDeactivate() {
      this.chunks.clear();
   }

   private static int pack(int x, int y, int z) {
      return (x & 255) << 24 | (y & '\uffff') << 8 | z & 255;
   }

   private static byte getPackedX(int p) {
      return (byte)(p >> 24 & 255);
   }

   private static short getPackedY(int p) {
      return (short)(p >> 8 & '\uffff');
   }

   private static byte getPackedZ(int p) {
      return (byte)(p & 255);
   }

   private void searchChunk(class_2791 chunk, TChunk tChunk) {
      Context ctx = new Context();
      IntSet set = new IntOpenHashSet();
      int startX = chunk.method_12004().method_8326();
      int startZ = chunk.method_12004().method_8328();
      int endX = chunk.method_12004().method_8327();
      int endZ = chunk.method_12004().method_8329();

      for(int x = startX; x <= endX; ++x) {
         for(int z = startZ; z <= endZ; ++z) {
            int height = chunk.method_12032(class_2903.field_13202).method_12603(x - startX, z - startZ);

            for(short y = (short)this.mc.field_1687.method_31607(); y < height; ++y) {
               if (this.isTunnel(ctx, x, y, z)) {
                  set.add(pack(x - startX, y, z - startZ));
               }
            }
         }
      }

      IntSet positions = new IntOpenHashSet();
      IntIterator it = set.iterator();

      while(it.hasNext()) {
         int packed = it.nextInt();
         byte x = getPackedX(packed);
         short y = getPackedY(packed);
         byte z = getPackedZ(packed);
         if (x != 0 && x != 15 && z != 0 && z != 15) {
            boolean has = false;

            for(class_2350 dir : DIRECTIONS) {
               if (set.contains(pack(x + dir.method_10148(), y, z + dir.method_10165()))) {
                  has = true;
                  break;
               }
            }

            if (has) {
               positions.add(packed);
            }
         } else {
            positions.add(packed);
         }
      }

      tChunk.positions = positions;
   }

   private boolean isTunnel(Context ctx, int x, int y, int z) {
      if (!this.canWalkIn(ctx, x, y, z)) {
         return false;
      } else {
         TunnelSide s1 = this.getTunnelSide(ctx, x + 1, y, z);
         if (s1 == TunnelESP.TunnelSide.PartiallyBlocked) {
            return false;
         } else {
            TunnelSide s2 = this.getTunnelSide(ctx, x - 1, y, z);
            if (s2 == TunnelESP.TunnelSide.PartiallyBlocked) {
               return false;
            } else {
               TunnelSide s3 = this.getTunnelSide(ctx, x, y, z + 1);
               if (s3 == TunnelESP.TunnelSide.PartiallyBlocked) {
                  return false;
               } else {
                  TunnelSide s4 = this.getTunnelSide(ctx, x, y, z - 1);
                  if (s4 == TunnelESP.TunnelSide.PartiallyBlocked) {
                     return false;
                  } else {
                     return s1 == TunnelESP.TunnelSide.Walkable && s2 == TunnelESP.TunnelSide.Walkable && s3 == TunnelESP.TunnelSide.FullyBlocked && s4 == TunnelESP.TunnelSide.FullyBlocked || s1 == TunnelESP.TunnelSide.FullyBlocked && s2 == TunnelESP.TunnelSide.FullyBlocked && s3 == TunnelESP.TunnelSide.Walkable && s4 == TunnelESP.TunnelSide.Walkable;
                  }
               }
            }
         }
      }
   }

   private TunnelSide getTunnelSide(Context ctx, int x, int y, int z) {
      if (this.canWalkIn(ctx, x, y, z)) {
         return TunnelESP.TunnelSide.Walkable;
      } else {
         return !this.canWalkThrough(ctx, x, y, z) && !this.canWalkThrough(ctx, x, y + 1, z) ? TunnelESP.TunnelSide.FullyBlocked : TunnelESP.TunnelSide.PartiallyBlocked;
      }
   }

   private boolean canWalkOn(Context ctx, int x, int y, int z) {
      class_2680 state = ctx.get(x, y, z);
      if (state.method_26215()) {
         return false;
      } else if (!state.method_26227().method_15769()) {
         return false;
      } else {
         return !state.method_26220(this.mc.field_1687, BP.method_10103(x, y, z)).method_1110();
      }
   }

   private boolean canWalkThrough(Context ctx, int x, int y, int z) {
      class_2680 state = ctx.get(x, y, z);
      if (state.method_26215()) {
         return true;
      } else {
         return !state.method_26227().method_15769() ? false : state.method_26220(this.mc.field_1687, BP.method_10103(x, y, z)).method_1110();
      }
   }

   private boolean canWalkIn(Context ctx, int x, int y, int z) {
      if (!this.canWalkOn(ctx, x, y - 1, z)) {
         return false;
      } else if (!this.canWalkThrough(ctx, x, y, z)) {
         return false;
      } else {
         return this.canWalkThrough(ctx, x, y + 2, z) ? false : this.canWalkThrough(ctx, x, y + 1, z);
      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      synchronized(this.chunks) {
         TChunk tChunk;
         for(ObjectIterator var3 = this.chunks.values().iterator(); var3.hasNext(); tChunk.marked = false) {
            tChunk = (TChunk)var3.next();
         }

         int added = 0;

         for(class_2791 chunk : Utils.chunks(true)) {
            long key = class_1923.method_8331(chunk.method_12004().field_9181, chunk.method_12004().field_9180);
            if (this.chunks.containsKey(key)) {
               ((TChunk)this.chunks.get(key)).marked = true;
            } else if (added < 48) {
               TChunk tChunk = new TChunk(chunk.method_12004().field_9181, chunk.method_12004().field_9180);
               this.chunks.put(tChunk.getKey(), tChunk);
               MeteorExecutor.execute(() -> this.searchChunk(chunk, tChunk));
               ++added;
            }
         }

         this.chunks.values().removeIf((tChunkx) -> !tChunkx.marked);
      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      synchronized(this.chunks) {
         ObjectIterator var3 = this.chunks.values().iterator();

         while(var3.hasNext()) {
            TChunk chunk = (TChunk)var3.next();
            chunk.render(event.renderer);
         }

      }
   }

   private boolean chunkContains(TChunk chunk, int x, int y, int z) {
      int key;
      if (x == -1) {
         chunk = (TChunk)this.chunks.get(class_1923.method_8331(chunk.x - 1, chunk.z));
         key = pack(15, y, z);
      } else if (x == 16) {
         chunk = (TChunk)this.chunks.get(class_1923.method_8331(chunk.x + 1, chunk.z));
         key = pack(0, y, z);
      } else if (z == -1) {
         chunk = (TChunk)this.chunks.get(class_1923.method_8331(chunk.x, chunk.z - 1));
         key = pack(x, y, 15);
      } else if (z == 16) {
         chunk = (TChunk)this.chunks.get(class_1923.method_8331(chunk.x, chunk.z + 1));
         key = pack(x, y, 0);
      } else {
         key = pack(x, y, z);
      }

      return chunk != null && chunk.positions != null && chunk.positions.contains(key);
   }

   static {
      DIRECTIONS = new class_2350[]{class_2350.field_11034, class_2350.field_11043, class_2350.field_11035, class_2350.field_11039};
   }

   private class TChunk {
      private final int x;
      private final int z;
      public IntSet positions;
      public boolean marked;

      public TChunk(int x, int z) {
         this.x = x;
         this.z = z;
         this.marked = true;
      }

      public void render(Renderer3D renderer) {
         if (this.positions != null) {
            IntIterator it = this.positions.iterator();

            while(it.hasNext()) {
               int pos = it.nextInt();
               int x = TunnelESP.getPackedX(pos);
               int y = TunnelESP.getPackedY(pos);
               int z = TunnelESP.getPackedZ(pos);
               int excludeDir = 0;
               if ((Boolean)TunnelESP.this.connected.get()) {
                  for(class_2350 dir : TunnelESP.DIRECTIONS) {
                     if (TunnelESP.this.chunkContains(this, x + dir.method_10148(), y, z + dir.method_10165())) {
                        excludeDir |= Dir.get(dir);
                     }
                  }
               }

               x += this.x * 16;
               z += this.z * 16;
               renderer.box((double)x, (double)y, (double)z, (double)(x + 1), (double)y + (Double)TunnelESP.this.height.get(), (double)(z + 1), TunnelESP.this.sideColor.get(), TunnelESP.this.lineColor.get(), TunnelESP.this.shapeMode.get(), excludeDir);
            }

         }
      }

      public long getKey() {
         return class_1923.method_8331(this.x, this.z);
      }
   }

   private static class Context {
      private final class_1937 world;
      private class_2791 lastChunk;

      public Context() {
         this.world = MeteorClient.mc.field_1687;
      }

      public class_2680 get(int x, int y, int z) {
         if (this.world.method_31601(y)) {
            return class_2246.field_10243.method_9564();
         } else {
            int cx = x >> 4;
            int cz = z >> 4;
            class_2791 chunk;
            if (this.lastChunk != null && this.lastChunk.method_12004().field_9181 == cx && this.lastChunk.method_12004().field_9180 == cz) {
               chunk = this.lastChunk;
            } else {
               chunk = this.world.method_8402(cx, cz, class_2806.field_12803, false);
            }

            if (chunk == null) {
               return class_2246.field_10243.method_9564();
            } else {
               class_2826 section = chunk.method_12006()[chunk.method_31602(y)];
               if (section == null) {
                  return class_2246.field_10243.method_9564();
               } else {
                  this.lastChunk = chunk;
                  return section.method_12254(x & 15, y & 15, z & 15);
               }
            }
         }
      }
   }

   private static enum TunnelSide {
      Walkable,
      PartiallyBlocked,
      FullyBlocked;

      // $FF: synthetic method
      private static TunnelSide[] $values() {
         return new TunnelSide[]{Walkable, PartiallyBlocked, FullyBlocked};
      }
   }
}
