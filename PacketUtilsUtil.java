package meteordevelopment.meteorclient.utils.network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import net.minecraft.class_2596;
import net.minecraft.class_8037;
import net.minecraft.class_8038;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.Scanners;

public class PacketUtilsUtil {
   private PacketUtilsUtil() {
   }

   public static void main(String[] args) {
      try {
         init();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public static void init() throws IOException {
      File file = new File("src/main/java/%s/PacketUtils.java".formatted(PacketUtilsUtil.class.getPackageName().replace('.', '/')));
      if (!file.exists()) {
         file.getParentFile().mkdirs();
         file.createNewFile();
      }

      BufferedWriter writer = new BufferedWriter(new FileWriter(file));

      try {
         writer.write("/*\n");
         writer.write(" * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).\n");
         writer.write(" * Copyright (c) Meteor Development.\n");
         writer.write(" */\n\n");
         writer.write("package meteordevelopment.meteorclient.utils.network;\n\n");
         writer.write("import com.google.common.collect.Sets;\n");
         writer.write("import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;\n");
         writer.write("import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;\n");
         writer.write("import net.minecraft.network.packet.Packet;\n\n");
         writer.write("import java.util.Map;\n");
         writer.write("import java.util.Set;\n");
         writer.write("\npublic class PacketUtils {\n");
         writer.write("    private static final Map<Class<? extends Packet<?>>, String> S2C_PACKETS = new Reference2ObjectOpenHashMap<>();\n");
         writer.write("    private static final Map<Class<? extends Packet<?>>, String> C2S_PACKETS = new Reference2ObjectOpenHashMap<>();\n\n");
         writer.write("    private static final Map<String, Class<? extends Packet<?>>> S2C_PACKETS_R = new Object2ReferenceOpenHashMap<>();\n");
         writer.write("    private static final Map<String, Class<? extends Packet<?>>> C2S_PACKETS_R = new Object2ReferenceOpenHashMap<>();\n\n");
         writer.write("    public static final Set<Class<? extends Packet<?>>> PACKETS = Sets.union(getC2SPackets(), getS2CPackets());\n\n");
         writer.write("    static {\n");
         processPackets(writer, "net.minecraft.network.packet.c2s", "C2S_PACKETS", "C2S_PACKETS_R", (packet) -> false);
         writer.newLine();
         processPackets(writer, "net.minecraft.network.packet.s2c", "S2C_PACKETS", "S2C_PACKETS_R", (packet) -> class_8038.class.isAssignableFrom(packet) || class_8037.class.isAssignableFrom(packet));
         writer.write("    }\n\n");
         writer.write("    private PacketUtils() {\n");
         writer.write("    }\n\n");
         writer.write("    public static String getName(Class<? extends Packet<?>> packetClass) {\n");
         writer.write("        String name = S2C_PACKETS.get(packetClass);\n");
         writer.write("        if (name != null) return name;\n");
         writer.write("        return C2S_PACKETS.get(packetClass);\n");
         writer.write("    }\n\n");
         writer.write("    public static Class<? extends Packet<?>> getPacket(String name) {\n");
         writer.write("        Class<? extends Packet<?>> packet = S2C_PACKETS_R.get(name);\n");
         writer.write("        if (packet != null) return packet;\n");
         writer.write("        return C2S_PACKETS_R.get(name);\n");
         writer.write("    }\n\n");
         writer.write("    public static Set<Class<? extends Packet<?>>> getS2CPackets() {\n");
         writer.write("        return S2C_PACKETS.keySet();\n");
         writer.write("    }\n\n");
         writer.write("    public static Set<Class<? extends Packet<?>>> getC2SPackets() {\n");
         writer.write("        return C2S_PACKETS.keySet();\n");
         writer.write("    }\n");
         writer.write("}\n");
      } catch (Throwable var5) {
         try {
            writer.close();
         } catch (Throwable var4) {
            var5.addSuppressed(var4);
         }

         throw var5;
      }

      writer.close();
   }

   private static void processPackets(BufferedWriter writer, String packageName, String packetMapName, String reverseMapName, Predicate<Class<?>> exclusionFilter) throws IOException {
      Comparator<Class<?>> packetsComparator = Comparator.comparing((cls) -> cls.getName().substring(cls.getName().lastIndexOf(46) + 1)).thenComparing(Class::getName);
      Reflections reflections = new Reflections(packageName, new Scanner[]{Scanners.SubTypes});
      Set<Class<? extends class_2596>> packets = reflections.getSubTypesOf(class_2596.class);
      SortedSet<Class<? extends class_2596>> sortedPackets = new TreeSet(packetsComparator);
      sortedPackets.addAll(packets);

      for(Class<? extends class_2596> packet : sortedPackets) {
         if (!exclusionFilter.test(packet)) {
            String name = packet.getName();
            String className = name.substring(name.lastIndexOf(46) + 1).replace('$', '.');
            String fullName = name.replace('$', '.');
            writer.write("        %s.put(%s.class, \"%s\");%n".formatted(packetMapName, fullName, className));
            writer.write("        %s.put(\"%s\", %s.class);%n".formatted(reverseMapName, className, fullName));
         }
      }

   }
}
