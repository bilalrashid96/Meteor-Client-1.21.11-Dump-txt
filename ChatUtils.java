package meteordevelopment.meteorclient.utils.player;

import com.mojang.brigadier.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.pathing.BaritoneUtils;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.misc.text.MeteorClickEvent;
import net.minecraft.class_124;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2583;
import net.minecraft.class_3545;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import org.jetbrains.annotations.Nullable;

public class ChatUtils {
   private static final List<class_3545<String, Supplier<class_2561>>> customPrefixes = new ArrayList();
   private static String forcedPrefixClassName;
   private static class_2561 PREFIX;

   private ChatUtils() {
   }

   @PostInit
   public static void init() {
      PREFIX = class_2561.method_43473().method_10862(class_2583.field_24360.method_27706(class_124.field_1080)).method_27693("[").method_10852(class_2561.method_43470("Meteor").method_10862(class_2583.field_24360.method_27703(class_5251.method_27717(MeteorClient.ADDON.color.getPacked())))).method_27693("] ");
   }

   public static class_2561 getMeteorPrefix() {
      return PREFIX;
   }

   public static void registerCustomPrefix(String packageName, Supplier<class_2561> supplier) {
      for(class_3545<String, Supplier<class_2561>> pair : customPrefixes) {
         if (((String)pair.method_15442()).equals(packageName)) {
            pair.method_34965(supplier);
            return;
         }
      }

      customPrefixes.add(new class_3545(packageName, supplier));
   }

   public static void unregisterCustomPrefix(String packageName) {
      customPrefixes.removeIf((pair) -> ((String)pair.method_15442()).equals(packageName));
   }

   public static void forceNextPrefixClass(Class<?> klass) {
      forcedPrefixClassName = klass.getName();
   }

   public static void sendPlayerMsg(String message) {
      sendPlayerMsg(message, true);
   }

   public static void sendPlayerMsg(String message, boolean addToHistory) {
      if (addToHistory) {
         MeteorClient.mc.field_1705.method_1743().method_1803(message);
      }

      if (message.startsWith("/")) {
         MeteorClient.mc.field_1724.field_3944.method_45730(message.substring(1));
      } else {
         MeteorClient.mc.field_1724.field_3944.method_45729(message);
      }

   }

   public static void info(String message, Object... args) {
      sendMsg(class_124.field_1080, message, args);
   }

   public static void infoPrefix(String prefix, String message, Object... args) {
      sendMsg(0, prefix, class_124.field_1076, class_124.field_1080, message, args);
   }

   public static void warning(String message, Object... args) {
      sendMsg(class_124.field_1054, message, args);
   }

   public static void warningPrefix(String prefix, String message, Object... args) {
      sendMsg(0, prefix, class_124.field_1076, class_124.field_1054, message, args);
   }

   public static void error(String message, Object... args) {
      sendMsg(class_124.field_1061, message, args);
   }

   public static void errorPrefix(String prefix, String message, Object... args) {
      sendMsg(0, prefix, class_124.field_1076, class_124.field_1061, message, args);
   }

   public static void sendMsg(class_2561 message) {
      sendMsg((String)null, message);
   }

   public static void sendMsg(String prefix, class_2561 message) {
      sendMsg(0, (String)prefix, (class_124)class_124.field_1076, (class_2561)message);
   }

   public static void sendMsg(class_124 color, String message, Object... args) {
      sendMsg(0, (String)null, (class_124)null, color, message, args);
   }

   public static void sendMsg(int id, class_124 color, String message, Object... args) {
      sendMsg(id, (String)null, (class_124)null, color, message, args);
   }

   public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable class_124 prefixColor, class_124 messageColor, String messageContent, Object... args) {
      class_5250 message = formatMsg(String.format(messageContent, args), messageColor);
      sendMsg(id, (String)prefixTitle, (class_124)prefixColor, (class_2561)message);
   }

   public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable class_124 prefixColor, String messageContent, class_124 messageColor) {
      class_5250 message = formatMsg(messageContent, messageColor);
      sendMsg(id, (String)prefixTitle, (class_124)prefixColor, (class_2561)message);
   }

   public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable class_124 prefixColor, class_2561 msg) {
      if (MeteorClient.mc.field_1687 != null) {
         class_5250 message = class_2561.method_43473();
         message.method_10852(getPrefix());
         if (prefixTitle != null) {
            message.method_10852(getCustomPrefix(prefixTitle, prefixColor));
         }

         message.method_10852(msg);
         if (!(Boolean)Config.get().deleteChatFeedback.get()) {
            id = 0;
         }

         MeteorClient.mc.execute(() -> ((IChatHud)MeteorClient.mc.field_1705.method_1743()).meteor$add(message, id));
      }
   }

   private static class_5250 getCustomPrefix(String prefixTitle, class_124 prefixColor) {
      class_5250 prefix = class_2561.method_43473();
      prefix.method_10862(prefix.method_10866().method_27706(class_124.field_1080));
      prefix.method_27693("[");
      class_5250 moduleTitle = class_2561.method_43470(prefixTitle);
      moduleTitle.method_10862(moduleTitle.method_10866().method_27706(prefixColor));
      prefix.method_10852(moduleTitle);
      prefix.method_27693("] ");
      return prefix;
   }

   private static class_2561 getPrefix() {
      if (customPrefixes.isEmpty()) {
         forcedPrefixClassName = null;
         return PREFIX;
      } else {
         boolean foundChatUtils = false;
         String className = null;
         if (forcedPrefixClassName != null) {
            className = forcedPrefixClassName;
            forcedPrefixClassName = null;
         } else {
            for(StackTraceElement element : Thread.currentThread().getStackTrace()) {
               if (foundChatUtils) {
                  if (!element.getClassName().equals(ChatUtils.class.getName())) {
                     className = element.getClassName();
                     break;
                  }
               } else if (element.getClassName().equals(ChatUtils.class.getName())) {
                  foundChatUtils = true;
               }
            }
         }

         if (className == null) {
            return PREFIX;
         } else {
            for(class_3545<String, Supplier<class_2561>> pair : customPrefixes) {
               if (className.startsWith((String)pair.method_15442())) {
                  class_2561 prefix = (class_2561)((Supplier)pair.method_15441()).get();
                  return prefix != null ? prefix : PREFIX;
               }
            }

            return PREFIX;
         }
      }
   }

   private static class_5250 formatMsg(String message, class_124 defaultColor) {
      StringReader reader = new StringReader(message);
      class_5250 text = class_2561.method_43473();
      class_2583 style = class_2583.field_24360.method_27706(defaultColor);
      StringBuilder result = new StringBuilder();
      boolean formatting = false;

      while(reader.canRead()) {
         char c = reader.read();
         if (c == '(') {
            text.method_10852(class_2561.method_43470(result.toString()).method_10862(style));
            result.setLength(0);
            result.append(c);
            formatting = true;
         } else {
            result.append(c);
            if (formatting && c == ')') {
               switch (result.toString()) {
                  case "(default)":
                     style = style.method_27706(defaultColor);
                     result.setLength(0);
                     break;
                  case "(highlight)":
                     style = style.method_27706(class_124.field_1068);
                     result.setLength(0);
                     break;
                  case "(underline)":
                     style = style.method_27706(class_124.field_1073);
                     result.setLength(0);
                     break;
                  case "(bold)":
                     style = style.method_27706(class_124.field_1067);
                     result.setLength(0);
               }

               formatting = false;
            }
         }
      }

      if (!result.isEmpty()) {
         text.method_10852(class_2561.method_43470(result.toString()).method_10862(style));
      }

      return text;
   }

   public static class_5250 formatCoords(class_243 pos) {
      String coordsString = String.format("(highlight)(underline)%.0f, %.0f, %.0f(default)", pos.field_1352, pos.field_1351, pos.field_1350);
      class_5250 coordsText = formatMsg(coordsString, class_124.field_1080);
      if (BaritoneUtils.IS_AVAILABLE) {
         class_2583 style = coordsText.method_10866().method_27706(class_124.field_1067).method_10949(new class_2568.class_10613(class_2561.method_43470("Set as Baritone goal"))).method_10958(new MeteorClickEvent(String.format("%sgoto %d %d %d", BaritoneUtils.getPrefix(), (int)pos.field_1352, (int)pos.field_1351, (int)pos.field_1350)));
         coordsText.method_10862(style);
      }

      return coordsText;
   }
}
