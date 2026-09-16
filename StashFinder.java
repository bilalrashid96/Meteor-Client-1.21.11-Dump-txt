package meteordevelopment.meteorclient.systems.modules.world;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.ChunkDataEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StorageBlockListSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.text.RunnableClickEvent;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.MeteorToast;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1802;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2583;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_2595;
import net.minecraft.class_2601;
import net.minecraft.class_2609;
import net.minecraft.class_2611;
import net.minecraft.class_2614;
import net.minecraft.class_2627;
import net.minecraft.class_3719;
import net.minecraft.class_5250;

public class StashFinder extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private static final List<class_2248> DEFAULT_SUPPORT_BLOCK_BLACKLIST;
   private final Setting<List<class_2591<?>>> storageBlocks;
   private final Setting<Integer> minimumStorageCount;
   private final Setting<List<class_2248>> blacklistedBlocks;
   private final Setting<Integer> minimumDistance;
   private final Setting<Boolean> sendNotifications;
   private final Setting<Mode> notificationMode;
   private final Setting<Boolean> renderTracer;
   private final Setting<SettingColor> traceColor;
   private final Setting<Integer> traceArrivalDistance;
   private final Setting<Integer> traceMaxDistance;
   private final Setting<Boolean> renderChunkColumn;
   private final Setting<SettingColor> traceColumnColor;
   private final Setting<Keybind> clearTracesBind;
   private static final Gson GSON;
   private final Map<class_1923, class_243> tracerPositions;
   public List<Chunk> chunks;

   public StashFinder() {
      super(Categories.World, "stash-finder", "Searches loaded chunks for storage blocks. Saves to <your minecraft folder>/meteor-client");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.storageBlocks = this.sgGeneral.add(((StorageBlockListSetting.Builder)((StorageBlockListSetting.Builder)(new StorageBlockListSetting.Builder()).name("storage-blocks")).description("Select the storage blocks to search for.")).defaultValue(StorageBlockListSetting.STORAGE_BLOCKS).build());
      this.minimumStorageCount = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("minimum-storage-count")).description("The minimum amount of storage blocks in a chunk to record the chunk.")).defaultValue(4)).min(1).sliderMin(1).build());
      this.blacklistedBlocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blacklisted-support-blocks")).description("Blocks that prevent counting a storage block entity when it sits on them.")).defaultValue(DEFAULT_SUPPORT_BLOCK_BLACKLIST)).build());
      this.minimumDistance = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("minimum-distance")).description("The minimum distance you must be from spawn to record a certain chunk.")).defaultValue(0)).min(0).sliderMax(10000).build());
      this.sendNotifications = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("notifications")).description("Sends Minecraft notifications when new stashes are found.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("notification-mode")).description("The mode to use for notifications.")).defaultValue(StashFinder.Mode.Both);
      Setting var10003 = this.sendNotifications;
      Objects.requireNonNull(var10003);
      this.notificationMode = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.renderTracer = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-tracer")).description("Renders a tracer to the last found stash.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      ColorSetting.Builder var5 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("tracer-color")).description("Color of the stash tracer.")).defaultValue(new SettingColor(255, 215, 0, 255));
      var10003 = this.renderTracer;
      Objects.requireNonNull(var10003);
      this.traceColor = var10001.add(((ColorSetting.Builder)var5.visible(var10003::get)).build());
      var10001 = this.sgRender;
      IntSetting.Builder var6 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("tracer-hide-at-distance")).description("Hide the trace when you are this close to the stash.")).defaultValue(16)).min(1).sliderMin(1).sliderMax(50);
      var10003 = this.renderTracer;
      Objects.requireNonNull(var10003);
      this.traceArrivalDistance = var10001.add(((IntSetting.Builder)var6.visible(var10003::get)).build());
      var10001 = this.sgRender;
      var6 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("tracer-max-distance")).description("Hide the trace when you are farther than this distance from the stash.")).defaultValue(2000)).min(10).sliderMin(50).sliderMax(10000);
      var10003 = this.renderTracer;
      Objects.requireNonNull(var10003);
      this.traceMaxDistance = var10001.add(((IntSetting.Builder)var6.visible(var10003::get)).build());
      this.renderChunkColumn = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-chunk-column")).description("Renders a vertical column at the center of traced chunks.")).defaultValue(false)).build());
      var10001 = this.sgRender;
      ColorSetting.Builder var8 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("chunk-column-color")).description("Color of the stash tracer column.")).defaultValue(new SettingColor(255, 215, 0, 100));
      var10003 = this.renderChunkColumn;
      Objects.requireNonNull(var10003);
      this.traceColumnColor = var10001.add(((ColorSetting.Builder)var8.visible(var10003::get)).build());
      this.clearTracesBind = this.sgRender.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("clear-traces-bind")).description("Keybind to clear all stash traces.")).defaultValue(Keybind.none())).build());
      this.tracerPositions = new HashMap();
      this.chunks = new ArrayList();
   }

   public void onActivate() {
      this.load();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (((Keybind)this.clearTracesBind.get()).isPressed()) {
         this.tracerPositions.clear();
      }
   }

   @EventHandler
   private void onChunkData(ChunkDataEvent event) {
      double chunkXAbs = (double)Math.abs(event.chunk().method_12004().field_9181 * 16);
      double chunkZAbs = (double)Math.abs(event.chunk().method_12004().field_9180 * 16);
      if (!(Math.sqrt(chunkXAbs * chunkXAbs + chunkZAbs * chunkZAbs) < (double)(Integer)this.minimumDistance.get())) {
         Chunk chunk = new Chunk(event.chunk().method_12004());
         List<class_2248> blockBlacklist = this.blacklistedBlocks.get();

         for(class_2586 blockEntity : event.chunk().method_12214().values()) {
            if (((List)this.storageBlocks.get()).contains(blockEntity.method_11017())) {
               if (!blockBlacklist.isEmpty()) {
                  class_2338 below = blockEntity.method_11016().method_10074();
                  if (blockBlacklist.contains(event.chunk().method_8320(below).method_26204())) {
                     continue;
                  }
               }

               if (blockEntity instanceof class_2595) {
                  ++chunk.chests;
               } else if (blockEntity instanceof class_3719) {
                  ++chunk.barrels;
               } else if (blockEntity instanceof class_2627) {
                  ++chunk.shulkers;
               } else if (blockEntity instanceof class_2611) {
                  ++chunk.enderChests;
               } else if (blockEntity instanceof class_2609) {
                  ++chunk.furnaces;
               } else if (blockEntity instanceof class_2601) {
                  ++chunk.dispensersDroppers;
               } else if (blockEntity instanceof class_2614) {
                  ++chunk.hoppers;
               }
            }
         }

         if (chunk.getTotal() >= (Integer)this.minimumStorageCount.get()) {
            Chunk prevChunk = null;
            int i = this.chunks.indexOf(chunk);
            if (i < 0) {
               this.chunks.add(chunk);
            } else {
               prevChunk = (Chunk)this.chunks.set(i, chunk);
            }

            if ((Boolean)this.renderTracer.get()) {
               double y = this.mc.field_1724 != null ? this.mc.field_1724.method_23320() : (double)0.0F;
               this.tracerPositions.put(chunk.chunkPos, new class_243((double)chunk.x, y, (double)chunk.z));
            }

            this.saveJson();
            this.saveCsv();
            if ((Boolean)this.sendNotifications.get() && (!chunk.equals(prevChunk) || !chunk.countsEqual(prevChunk))) {
               switch (((Mode)this.notificationMode.get()).ordinal()) {
                  case 0:
                     this.sendChatNotification(chunk);
                     break;
                  case 1:
                     MeteorToast toast = (new MeteorToast.Builder(this.title)).icon(class_1802.field_8106).text("Found Stash!").build();
                     this.mc.method_1566().method_1999(toast);
                     break;
                  case 2:
                     this.sendChatNotification(chunk);
                     MeteorToast toast = (new MeteorToast.Builder(this.title)).icon(class_1802.field_8106).text("Found Stash!").build();
                     this.mc.method_1566().method_1999(toast);
               }
            }
         }

      }
   }

   public WWidget getWidget(GuiTheme theme) {
      this.chunks.sort(Comparator.comparingInt((value) -> -value.getTotal()));
      WVerticalList list = theme.verticalList();
      WHorizontalList hl = theme.horizontalList();
      WButton clear = (WButton)hl.add(theme.button("Clear Chunks")).widget();
      WButton resetTracers = (WButton)hl.add(theme.button("Reset Tracers")).widget();
      list.add(hl);
      WTable table = new WTable();
      if (!this.chunks.isEmpty()) {
         list.add(table);
      }

      clear.action = () -> {
         this.chunks.clear();
         table.clear();
         this.tracerPositions.clear();
      };
      resetTracers.action = () -> {
         table.clear();
         this.tracerPositions.clear();
         this.fillTable(theme, table);
      };
      this.fillTable(theme, table);
      return list;
   }

   private void fillTable(GuiTheme theme, WTable table) {
      for(Chunk chunk : this.chunks) {
         table.add(theme.label("Pos: " + chunk.x + ", " + chunk.z)).padRight((double)10.0F);
         table.add(theme.label("Total: " + chunk.getTotal())).padRight((double)10.0F);
         WCheckbox visible = (WCheckbox)table.add(theme.checkbox(this.tracerPositions.containsKey(chunk.chunkPos))).widget();
         visible.action = () -> {
            if (visible.checked) {
               double y = this.mc.field_1724 != null ? this.mc.field_1724.method_23320() : (double)0.0F;
               this.tracerPositions.put(chunk.chunkPos, new class_243((double)chunk.x, y, (double)chunk.z));
            } else {
               this.tracerPositions.remove(chunk.chunkPos);
            }

         };
         WButton open = (WButton)table.add(theme.button("Open")).widget();
         open.action = () -> this.mc.method_1507(new ChunkScreen(theme, chunk));
         WButton gotoBtn = (WButton)table.add(theme.button("Goto")).widget();
         gotoBtn.action = () -> PathManagers.get().moveTo(new class_2338(chunk.x, 0, chunk.z), true);
         WMinus delete = (WMinus)table.add(theme.minus()).widget();
         delete.action = () -> {
            if (this.chunks.remove(chunk)) {
               this.tracerPositions.remove(chunk.chunkPos);
               table.clear();
               this.fillTable(theme, table);
               this.saveJson();
               this.saveCsv();
            }

         };
         table.row();
      }

   }

   private void load() {
      boolean loaded = false;
      File file = this.getJsonFile();
      if (file.exists()) {
         try {
            FileReader reader = new FileReader(file);
            this.chunks = (List)GSON.fromJson(reader, (new TypeToken<List<Chunk>>() {
            }).getType());
            reader.close();

            for(Chunk chunk : this.chunks) {
               chunk.calculatePos();
            }

            loaded = true;
         } catch (Exception var8) {
            if (this.chunks == null) {
               this.chunks = new ArrayList();
            }
         }
      }

      file = this.getCsvFile();
      if (!loaded && file.exists()) {
         try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.readLine();

            String line;
            while((line = reader.readLine()) != null) {
               String[] values = line.split(" ");
               Chunk chunk = new Chunk(new class_1923(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
               chunk.chests = Integer.parseInt(values[2]);
               chunk.shulkers = Integer.parseInt(values[3]);
               chunk.enderChests = Integer.parseInt(values[4]);
               chunk.furnaces = Integer.parseInt(values[5]);
               chunk.dispensersDroppers = Integer.parseInt(values[6]);
               chunk.hoppers = Integer.parseInt(values[7]);
               this.chunks.add(chunk);
            }

            reader.close();
         } catch (Exception var7) {
            if (this.chunks == null) {
               this.chunks = new ArrayList();
            }
         }
      }

   }

   private void saveCsv() {
      try {
         File file = this.getCsvFile();
         file.getParentFile().mkdirs();
         Writer writer = new FileWriter(file);
         writer.write("X,Z,Chests,Barrels,Shulkers,EnderChests,Furnaces,DispensersDroppers,Hoppers\n");

         for(Chunk chunk : this.chunks) {
            chunk.write(writer);
         }

         writer.close();
      } catch (IOException e) {
         MeteorClient.LOG.error("Error while writing the stash list to csv", e);
      }

   }

   private void saveJson() {
      try {
         File file = this.getJsonFile();
         file.getParentFile().mkdirs();
         Writer writer = new FileWriter(file);
         GSON.toJson(this.chunks, writer);
         writer.close();
      } catch (IOException e) {
         MeteorClient.LOG.error("Error while writing the stash list to json", e);
      }

   }

   private File getJsonFile() {
      return new File(new File(new File(MeteorClient.FOLDER, "stashes"), Utils.getFileWorldName()), "stashes.json");
   }

   private File getCsvFile() {
      return new File(new File(new File(MeteorClient.FOLDER, "stashes"), Utils.getFileWorldName()), "stashes.csv");
   }

   public String getInfoString() {
      return String.valueOf(this.chunks.size());
   }

   private void sendChatNotification(Chunk chunk) {
      class_5250 coords = class_2561.method_43470(chunk.x + ", " + chunk.z).method_10862(class_2583.field_24360.method_10977(class_124.field_1068).method_27706(class_124.field_1073).method_10949(new class_2568.class_10613(class_2561.method_43470("Path to stash"))).method_10958(new RunnableClickEvent(() -> PathManagers.get().moveTo(new class_2338(chunk.x, 0, chunk.z), true))));
      class_5250 message = class_2561.method_43470("Found stash at ").method_27692(class_124.field_1080).method_10852(class_2561.method_43470("[").method_27692(class_124.field_1080)).method_10852(coords).method_10852(class_2561.method_43470("]").method_27692(class_124.field_1080)).method_10852(class_2561.method_43470(".").method_27692(class_124.field_1080));
      ChatUtils.sendMsg(message);
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if (!this.tracerPositions.isEmpty() && this.mc.field_1724 != null) {
         double playerX = this.mc.field_1724.method_23317();
         double playerZ = this.mc.field_1724.method_23321();
         this.tracerPositions.entrySet().removeIf((entry) -> {
            class_243 pos = (class_243)entry.getValue();
            double horizontalDist = Math.hypot(pos.field_1352 - playerX, pos.field_1350 - playerZ);
            return horizontalDist <= (double)(Integer)this.traceArrivalDistance.get();
         });
         if ((Boolean)this.renderTracer.get() || (Boolean)this.renderChunkColumn.get()) {
            for(class_243 pos : this.tracerPositions.values()) {
               double horizontalDist = Math.hypot(pos.field_1352 - playerX, pos.field_1350 - playerZ);
               if (!(horizontalDist > (double)(Integer)this.traceMaxDistance.get())) {
                  if ((Boolean)this.renderTracer.get()) {
                     event.renderer.line(RenderUtils.center.field_1352, RenderUtils.center.field_1351, RenderUtils.center.field_1350, pos.field_1352, this.mc.field_1724.method_23320(), pos.field_1350, this.traceColor.get());
                  }

                  if ((Boolean)this.renderChunkColumn.get()) {
                     double x1 = pos.field_1352 - (double)0.5F;
                     double x2 = pos.field_1352 + (double)0.5F;
                     double z1 = pos.field_1350 - (double)0.5F;
                     double z2 = pos.field_1350 + (double)0.5F;
                     int bottomY = this.mc.field_1687.method_31607();
                     int topY = bottomY + this.mc.field_1687.method_8597().comp_652();
                     event.renderer.line(x1, (double)bottomY, z1, x1, (double)topY, z1, this.traceColumnColor.get());
                     event.renderer.line(x1, (double)bottomY, z2, x1, (double)topY, z2, this.traceColumnColor.get());
                     event.renderer.line(x2, (double)bottomY, z1, x2, (double)topY, z1, this.traceColumnColor.get());
                     event.renderer.line(x2, (double)bottomY, z2, x2, (double)topY, z2, this.traceColumnColor.get());
                  }
               }
            }

         }
      }
   }

   static {
      DEFAULT_SUPPORT_BLOCK_BLACKLIST = List.of(class_2246.field_27116, class_2246.field_27121, class_2246.field_47035, class_2246.field_27133, class_2246.field_33407, class_2246.field_33408, class_2246.field_16328, class_2246.field_47076);
      GSON = (new GsonBuilder()).setPrettyPrinting().create();
   }

   public static enum Mode {
      Chat,
      Toast,
      Both;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Chat, Toast, Both};
      }
   }

   public static class Chunk {
      private static final StringBuilder sb = new StringBuilder();
      public class_1923 chunkPos;
      public transient int x;
      public transient int z;
      public int chests;
      public int barrels;
      public int shulkers;
      public int enderChests;
      public int furnaces;
      public int dispensersDroppers;
      public int hoppers;

      public Chunk(class_1923 chunkPos) {
         this.chunkPos = chunkPos;
         this.calculatePos();
      }

      public void calculatePos() {
         this.x = this.chunkPos.field_9181 * 16 + 8;
         this.z = this.chunkPos.field_9180 * 16 + 8;
      }

      public int getTotal() {
         return this.chests + this.barrels + this.shulkers + this.enderChests + this.furnaces + this.dispensersDroppers + this.hoppers;
      }

      public void write(Writer writer) throws IOException {
         sb.setLength(0);
         sb.append(this.x).append(',').append(this.z).append(',');
         sb.append(this.chests).append(',').append(this.barrels).append(',').append(this.shulkers).append(',').append(this.enderChests).append(',').append(this.furnaces).append(',').append(this.dispensersDroppers).append(',').append(this.hoppers).append('\n');
         writer.write(sb.toString());
      }

      public boolean countsEqual(Chunk c) {
         if (c == null) {
            return false;
         } else {
            return this.chests != c.chests || this.barrels != c.barrels || this.shulkers != c.shulkers || this.enderChests != c.enderChests || this.furnaces != c.furnaces || this.dispensersDroppers != c.dispensersDroppers || this.hoppers != c.hoppers;
         }
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Chunk chunk = (Chunk)o;
            return Objects.equals(this.chunkPos, chunk.chunkPos);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.chunkPos});
      }
   }

   private static class ChunkScreen extends WindowScreen {
      private final Chunk chunk;

      public ChunkScreen(GuiTheme theme, Chunk chunk) {
         super(theme, "Chunk at " + chunk.x + ", " + chunk.z);
         this.chunk = chunk;
      }

      public void initWidgets() {
         WTable t = (WTable)this.add(this.theme.table()).expandX().widget();
         t.add(this.theme.label("Total:"));
         t.add(this.theme.label("" + this.chunk.getTotal()));
         t.row();
         t.add(this.theme.horizontalSeparator()).expandX();
         t.row();
         t.add(this.theme.label("Chests:"));
         t.add(this.theme.label("" + this.chunk.chests));
         t.row();
         t.add(this.theme.label("Barrels:"));
         t.add(this.theme.label("" + this.chunk.barrels));
         t.row();
         t.add(this.theme.label("Shulkers:"));
         t.add(this.theme.label("" + this.chunk.shulkers));
         t.row();
         t.add(this.theme.label("Ender Chests:"));
         t.add(this.theme.label("" + this.chunk.enderChests));
         t.row();
         t.add(this.theme.label("Furnaces:"));
         t.add(this.theme.label("" + this.chunk.furnaces));
         t.row();
         t.add(this.theme.label("Dispensers and droppers:"));
         t.add(this.theme.label("" + this.chunk.dispensersDroppers));
         t.row();
         t.add(this.theme.label("Hoppers:"));
         t.add(this.theme.label("" + this.chunk.hoppers));
      }
   }
}
