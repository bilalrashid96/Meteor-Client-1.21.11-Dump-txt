package meteordevelopment.meteorclient.systems.modules.misc;

import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.PacketListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.PacketUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2596;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PacketLogger extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgOutput;
   private final Setting<Set<Class<? extends class_2596<?>>>> s2cPackets;
   private final Setting<Set<Class<? extends class_2596<?>>>> c2sPackets;
   private final Setting<Boolean> showTimestamp;
   private final Setting<Boolean> showPacketData;
   private final Setting<Boolean> showCount;
   private final Setting<Boolean> showSummary;
   private final Setting<Boolean> logToChat;
   private final Setting<Boolean> logToFile;
   private final Setting<Integer> flushInterval;
   private final Setting<Integer> maxFileSizeMB;
   private final Setting<Integer> maxTotalLogsMB;
   private static final Path PACKET_LOGS_DIR;
   private static final int LINE_SEPARATOR_BYTES;
   private static final DateTimeFormatter TIME_FORMATTER;
   private static final DateTimeFormatter FILE_NAME_FORMATTER;
   private final Reference2IntOpenHashMap<Class<? extends class_2596<?>>> packetCounts;
   private @Nullable BufferedWriter fileWriter;
   private long lastFlushMs;
   private long currentFileSizeBytes;
   private int currentFileIndex;
   private @Nullable LocalDateTime sessionStartTime;

   public PacketLogger() {
      super(Categories.Misc, "packet-logger", "Allows you to log certain packets.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgOutput = this.settings.createGroup("Output");
      this.s2cPackets = this.sgGeneral.add(((PacketListSetting.Builder)((PacketListSetting.Builder)(new PacketListSetting.Builder()).name("S2C-packets")).description("Server-to-client packets to log.")).filter((aClass) -> PacketUtils.getS2CPackets().contains(aClass)).build());
      this.c2sPackets = this.sgGeneral.add(((PacketListSetting.Builder)((PacketListSetting.Builder)(new PacketListSetting.Builder()).name("C2S-packets")).description("Client-to-server packets to log.")).filter((aClass) -> PacketUtils.getC2SPackets().contains(aClass)).build());
      this.showTimestamp = this.sgOutput.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-timestamp")).description("Show timestamp for each logged packet.")).defaultValue(true)).build());
      this.showPacketData = this.sgOutput.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-packet-data")).description("Show the packet's toString() data for debugging.")).defaultValue(false)).build());
      this.showCount = this.sgOutput.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-count")).description("Show how many times each packet type has been logged.")).defaultValue(true)).build());
      this.showSummary = this.sgOutput.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-summary")).description("Show final packet count summary when module is deactivated.")).defaultValue(true)).build());
      this.logToChat = this.sgOutput.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("log-to-chat")).description("Log packets to chat.")).defaultValue(true)).build());
      this.logToFile = this.sgOutput.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("log-to-file")).description("Save packet logs to a file in the meteor-client folder.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgOutput;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("flush-interval")).description("How often to flush logs to disk (in seconds).")).defaultValue(1)).min(1).sliderMax(10);
      Setting var10003 = this.logToFile;
      Objects.requireNonNull(var10003);
      this.flushInterval = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgOutput;
      var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-file-size-mb")).description("Maximum size per log file in MB. Creates new file when exceeded.")).defaultValue(10)).min(1).sliderMax(100);
      var10003 = this.logToFile;
      Objects.requireNonNull(var10003);
      this.maxFileSizeMB = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgOutput;
      var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-total-logs-mb")).description("Maximum total disk space for all packet logs in MB. Deletes oldest when exceeded.")).defaultValue(50)).min(1).sliderMax(500);
      var10003 = this.logToFile;
      Objects.requireNonNull(var10003);
      this.maxTotalLogsMB = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.packetCounts = new Reference2IntOpenHashMap();
      this.runInMainMenu = true;
   }

   public void onActivate() {
      this.closeFileWriter();
      this.packetCounts.clear();
      this.lastFlushMs = System.currentTimeMillis();
      this.sessionStartTime = LocalDateTime.now();
      this.currentFileIndex = 0;
      this.currentFileSizeBytes = 0L;
      if ((Boolean)this.logToFile.get()) {
         try {
            Files.createDirectories(PACKET_LOGS_DIR);
            this.cleanupOldLogs();
            this.openNewLogFile();
         } catch (IOException e) {
            this.error("Failed to initialize packet logging: %s", new Object[]{e.getMessage()});
            this.fileWriter = null;
         }
      }

   }

   public void onDeactivate() {
      if ((Boolean)this.showSummary.get() && !this.packetCounts.isEmpty()) {
         this.logSummary();
      }

      this.closeFileWriter();
   }

   private void logPacket(String direction, class_2596<?> packet) {
      if ((Boolean)this.logToChat.get() || (Boolean)this.logToFile.get()) {
         Class<? extends class_2596<?>> packetClass = packet.getClass();
         this.packetCounts.addTo(packetClass, 1);
         StringBuilder msg = new StringBuilder(128);
         if ((Boolean)this.showTimestamp.get()) {
            msg.append("[").append(LocalDateTime.now().format(TIME_FORMATTER)).append("] ");
         }

         msg.append(direction).append(" ").append(PacketUtils.getName(packetClass));
         if ((Boolean)this.showCount.get()) {
            msg.append(" (#").append(this.packetCounts.getInt(packetClass)).append(")");
         }

         if ((Boolean)this.showPacketData.get()) {
            msg.append("\n  Data: ").append(packet);
         }

         String line = msg.toString();
         if ((Boolean)this.logToChat.get()) {
            this.info(line, new Object[0]);
         }

         if ((Boolean)this.logToFile.get()) {
            this.writeLine(line);
         }

      }
   }

   private void logSummary() {
      int totalPackets = this.packetCounts.values().intStream().sum();
      List<String> lines = new ArrayList();
      lines.add("--- SUMMARY ---");
      lines.add("Final packet counts (total " + totalPackets + "):");
      this.packetCounts.reference2IntEntrySet().stream().sorted((a, b) -> Integer.compare(b.getIntValue(), a.getIntValue())).forEach((e) -> lines.add("  %s: %d".formatted(PacketUtils.getName((Class)e.getKey()), e.getIntValue())));

      for(String line : lines) {
         if ((Boolean)this.logToChat.get()) {
            this.info(line, new Object[0]);
         }

         if ((Boolean)this.logToFile.get()) {
            this.writeLine(line);
         }
      }

   }

   private void writeLine(String line) {
      if (this.fileWriter != null) {
         try {
            int lineBytes = line.getBytes(StandardCharsets.UTF_8).length + LINE_SEPARATOR_BYTES;
            if (this.currentFileSizeBytes + (long)lineBytes > (long)(Integer)this.maxFileSizeMB.get() * 1024L * 1024L) {
               this.openNewLogFile();
            }

            this.fileWriter.write(line);
            this.fileWriter.newLine();
            this.currentFileSizeBytes += (long)lineBytes;
            long now = System.currentTimeMillis();
            if (now - this.lastFlushMs >= (long)(Integer)this.flushInterval.get() * 1000L) {
               this.fileWriter.flush();
               this.lastFlushMs = now;
            }
         } catch (IOException e) {
            this.error("Failed to write to packet log file: %s. File logging disabled.", new Object[]{e.getMessage()});
            this.closeFileWriter();
         }

      }
   }

   private void openNewLogFile() throws IOException {
      if (this.fileWriter != null) {
         this.fileWriter.close();
      }

      if (this.sessionStartTime == null) {
         this.sessionStartTime = LocalDateTime.now();
      }

      String fileName = "packets-%s-%d.log".formatted(this.sessionStartTime.format(FILE_NAME_FORMATTER), this.currentFileIndex++);
      this.fileWriter = Files.newBufferedWriter(PACKET_LOGS_DIR.resolve(fileName), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
      this.currentFileSizeBytes = 0L;
      this.cleanupOldLogs();
   }

   private void closeFileWriter() {
      if (this.fileWriter != null) {
         try {
            this.fileWriter.flush();
            this.fileWriter.close();
         } catch (IOException var2) {
         }

         this.fileWriter = null;
      }

   }

   private void cleanupOldLogs() throws IOException {
      long maxBytes = (long)(Integer)this.maxTotalLogsMB.get() * 1024L * 1024L;
      List<LogFileEntry> logFiles = new ArrayList();
      Stream<Path> stream = Files.list(PACKET_LOGS_DIR);

      try {
         for(Path p : stream.toList()) {
            String name = p.getFileName().toString();
            if (name.startsWith("packets-") && name.endsWith(".log")) {
               try {
                  logFiles.add(new LogFileEntry(p, Files.size(p), Files.getLastModifiedTime(p).toMillis()));
               } catch (IOException var10) {
               }
            }
         }
      } catch (Throwable var11) {
         if (stream != null) {
            try {
               stream.close();
            } catch (Throwable var9) {
               var11.addSuppressed(var9);
            }
         }

         throw var11;
      }

      if (stream != null) {
         stream.close();
      }

      logFiles.sort(Comparator.comparingLong(LogFileEntry::lastModified));
      long totalSize = 0L;

      for(LogFileEntry entry : logFiles) {
         totalSize += entry.size();
         if (totalSize > maxBytes) {
            Files.deleteIfExists(entry.path());
         }
      }

   }

   @EventHandler(
      priority = 201
   )
   private void onReceivePacket(PacketEvent.Receive event) {
      if (((Set)this.s2cPackets.get()).contains(event.packet.getClass())) {
         this.logPacket("<- S2C", event.packet);
      }

   }

   @EventHandler(
      priority = 201
   )
   private void onSendPacket(PacketEvent.Send event) {
      if (((Set)this.c2sPackets.get()).contains(event.packet.getClass())) {
         this.logPacket("-> C2S", event.packet);
      }

   }

   static {
      PACKET_LOGS_DIR = MeteorClient.FOLDER.toPath().resolve("packet-logs");
      LINE_SEPARATOR_BYTES = System.lineSeparator().getBytes(StandardCharsets.UTF_8).length;
      TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
      FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
   }

   private static record LogFileEntry(Path path, long size, long lastModified) {
   }
}
