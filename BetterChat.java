package meteordevelopment.meteorclient.systems.modules.misc;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.chars.Char2CharMap;
import it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.mixin.ChatHudAccessor;
import meteordevelopment.meteorclient.mixininterface.IChatHudLine;
import meteordevelopment.meteorclient.mixininterface.IChatHudLineVisible;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.text.MeteorClickEvent;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_10799;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_303;
import net.minecraft.class_332;
import net.minecraft.class_5250;
import net.minecraft.class_640;
import net.minecraft.class_7532;

public class BetterChat extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgFilter;
   private final SettingGroup sgLongerChat;
   private final SettingGroup sgPrefix;
   private final SettingGroup sgSuffix;
   private final Setting<Boolean> annoy;
   private final Setting<Boolean> fancy;
   private final Setting<Boolean> timestamps;
   private final Setting<Boolean> showSeconds;
   private final Setting<Boolean> playerHeads;
   private final Setting<Boolean> coordsProtection;
   private final Setting<Boolean> keepHistory;
   private final Setting<Boolean> antiSpam;
   private final Setting<Integer> antiSpamDepth;
   private final Setting<Boolean> antiClear;
   private final Setting<Boolean> filterRegex;
   private final Setting<List<String>> regexFilters;
   private final Setting<Boolean> infiniteChatBox;
   private final Setting<Boolean> longerChatHistory;
   private final Setting<Integer> longerChatLines;
   private final Setting<Boolean> prefix;
   private final Setting<Boolean> prefixRandom;
   private final Setting<String> prefixText;
   private final Setting<Boolean> prefixSmallCaps;
   private final Setting<Boolean> suffix;
   private final Setting<Boolean> suffixRandom;
   private final Setting<String> suffixText;
   private final Setting<Boolean> suffixSmallCaps;
   private static final Pattern antiSpamRegex = Pattern.compile(" \\(([0-9]{1,9})\\)$");
   private static final Pattern antiClearRegex = Pattern.compile("\\n(\\n|\\s)+\\n");
   private static final Pattern timestampRegex = Pattern.compile("^(<[0-9]{2}:[0-9]{2}(?::[0-9]{2})?> )");
   private static final Pattern usernameRegex = Pattern.compile("^(?:<[0-9]{2}:[0-9]{2}>\\s)?<(.*?)>.*");
   private final Char2CharMap SMALL_CAPS;
   public final IntList lines;
   private static final List<CustomHeadEntry> CUSTOM_HEAD_ENTRIES = new ArrayList();
   private static final Pattern TIMESTAMP_REGEX = Pattern.compile("^<\\d{1,2}:\\d{1,2}>");
   public class_303.class_7590 line;
   private SimpleDateFormat dateFormat;
   private final List<Pattern> filterRegexList;
   private static final Pattern coordRegex;

   public BetterChat() {
      super(Categories.Misc, "better-chat", "Improves your chat experience in various ways.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgFilter = this.settings.createGroup("Filter");
      this.sgLongerChat = this.settings.createGroup("Longer Chat");
      this.sgPrefix = this.settings.createGroup("Prefix");
      this.sgSuffix = this.settings.createGroup("Suffix");
      this.annoy = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("annoy")).description("Makes your messages aNnOyInG.")).defaultValue(false)).build());
      this.fancy = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fancy-chat")).description("Makes your messages ғᴀɴᴄʏ!")).defaultValue(false)).build());
      this.timestamps = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("timestamps")).description("Adds client-side time stamps to the beginning of chat messages.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-seconds")).description("Shows seconds in the chat message timestamps")).defaultValue(false);
      Setting var10003 = this.timestamps;
      Objects.requireNonNull(var10003);
      this.showSeconds = var10001.add(((BoolSetting.Builder)((BoolSetting.Builder)var10002.visible(var10003::get)).onChanged((o) -> this.updateDateFormat())).build());
      this.playerHeads = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("player-heads")).description("Displays player heads next to their messages.")).defaultValue(true)).build());
      this.coordsProtection = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("coords-protection")).description("Prevents you from sending messages in chat that may contain coordinates.")).defaultValue(true)).build());
      this.keepHistory = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("keep-history")).description("Prevents the chat history from being cleared when disconnecting.")).defaultValue(true)).build());
      this.antiSpam = this.sgFilter.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-spam")).description("Blocks duplicate messages from filling your chat.")).defaultValue(true)).build());
      var10001 = this.sgFilter;
      IntSetting.Builder var7 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("depth")).description("How many messages to filter.")).defaultValue(20)).min(1).sliderMin(1);
      var10003 = this.antiSpam;
      Objects.requireNonNull(var10003);
      this.antiSpamDepth = var10001.add(((IntSetting.Builder)var7.visible(var10003::get)).build());
      this.antiClear = this.sgFilter.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-clear")).description("Prevents servers from clearing chat.")).defaultValue(true)).build());
      this.filterRegex = this.sgFilter.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("filter-regex")).description("Filter out chat messages that match the regex filter.")).defaultValue(false)).build());
      var10001 = this.sgFilter;
      StringListSetting.Builder var8 = (StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("regex-filter")).description("Regex filter used for filtering chat messages.");
      var10003 = this.filterRegex;
      Objects.requireNonNull(var10003);
      this.regexFilters = var10001.add(((StringListSetting.Builder)((StringListSetting.Builder)var8.visible(var10003::get)).onChanged((strings) -> this.compileFilterRegexList())).build());
      this.infiniteChatBox = this.sgLongerChat.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("infinite-chat-box")).description("Lets you type infinitely long messages.")).defaultValue(true)).build());
      this.longerChatHistory = this.sgLongerChat.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("longer-chat-history")).description("Extends chat length.")).defaultValue(true)).build());
      var10001 = this.sgLongerChat;
      IntSetting.Builder var9 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("extra-lines")).description("The amount of extra chat lines.")).defaultValue(1000)).min(0).sliderRange(0, 1000);
      var10003 = this.longerChatHistory;
      Objects.requireNonNull(var10003);
      this.longerChatLines = var10001.add(((IntSetting.Builder)var9.visible(var10003::get)).build());
      this.prefix = this.sgPrefix.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("prefix")).description("Adds a prefix to your chat messages.")).defaultValue(false)).build());
      this.prefixRandom = this.sgPrefix.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("random")).description("Uses a random number as your prefix.")).defaultValue(false)).build());
      this.prefixText = this.sgPrefix.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("text")).description("The text to add as your prefix.")).defaultValue("> ")).visible(() -> !(Boolean)this.prefixRandom.get())).build());
      this.prefixSmallCaps = this.sgPrefix.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("small-caps")).description("Uses small caps in the prefix.")).defaultValue(false)).visible(() -> !(Boolean)this.prefixRandom.get())).build());
      this.suffix = this.sgSuffix.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("suffix")).description("Adds a suffix to your chat messages.")).defaultValue(false)).build());
      this.suffixRandom = this.sgSuffix.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("random")).description("Uses a random number as your suffix.")).defaultValue(false)).build());
      this.suffixText = this.sgSuffix.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("text")).description("The text to add as your suffix.")).defaultValue(" | meteor on crack!")).visible(() -> !(Boolean)this.suffixRandom.get())).build());
      this.suffixSmallCaps = this.sgSuffix.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("small-caps")).description("Uses small caps in the suffix.")).defaultValue(true)).visible(() -> !(Boolean)this.suffixRandom.get())).build());
      this.SMALL_CAPS = new Char2CharOpenHashMap();
      this.lines = new IntArrayList();
      this.filterRegexList = new ArrayList();
      String[] a = "abcdefghijklmnopqrstuvwxyz".split("");
      String[] b = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴩqʀꜱᴛᴜᴠᴡxyᴢ".split("");

      for(int i = 0; i < a.length; ++i) {
         this.SMALL_CAPS.put(a[i].charAt(0), b[i].charAt(0));
      }

      this.compileFilterRegexList();
   }

   @EventHandler
   private void onMessageReceive(ReceiveMessageEvent event) {
      class_2561 message = event.getMessage();
      if ((Boolean)this.filterRegex.get()) {
         String messageString = message.getString();

         for(Pattern pattern : this.filterRegexList) {
            if (pattern.matcher(messageString).find()) {
               event.cancel();
               return;
            }
         }
      }

      if ((Boolean)this.antiClear.get()) {
         String messageString = message.getString();
         if (antiClearRegex.matcher(messageString).find()) {
            class_5250 newMessage = class_2561.method_43473();
            message.method_27658((style, string) -> {
               Matcher antiClearMatcher = antiClearRegex.matcher(string);
               newMessage.method_10852(class_2561.method_43470(antiClearMatcher.find() ? antiClearMatcher.replaceAll("\n\n") : string).method_10862(style));
               return Optional.empty();
            }, class_2583.field_24360);
            message = newMessage;
         }
      }

      if ((Boolean)this.antiSpam.get()) {
         class_2561 antiSpammed = this.appendAntiSpam(message);
         if (antiSpammed != null) {
            message = antiSpammed;
         }
      }

      if ((Boolean)this.timestamps.get()) {
         SimpleDateFormat var10000 = this.dateFormat;
         Date var10001 = new Date();
         class_2561 timestamp = class_2561.method_43470("<" + var10000.format(var10001) + "> ").method_27692(class_124.field_1080);
         message = class_2561.method_43473().method_10852(timestamp).method_10852(message);
      }

      event.setMessage(message);
   }

   @EventHandler
   private void onMessageSend(SendMessageEvent event) {
      String message = event.message;
      if ((Boolean)this.annoy.get()) {
         message = this.applyAnnoy(message);
      }

      if ((Boolean)this.fancy.get()) {
         message = this.applyFancy(message);
      }

      message = this.getPrefix() + message + this.getSuffix();
      if ((Boolean)this.coordsProtection.get() && this.containsCoordinates(message)) {
         class_5250 warningMessage = class_2561.method_43470("It looks like there are coordinates in your message! ");
         class_5250 sendButton = this.getSendButton(message);
         warningMessage.method_10852(sendButton);
         ChatUtils.sendMsg(warningMessage);
         event.cancel();
      } else {
         event.message = message;
      }
   }

   private class_2561 appendAntiSpam(class_2561 text) {
      String textString = text.getString();
      class_2561 returnText = null;
      int messageIndex = -1;
      List<class_303> messages = ((ChatHudAccessor)this.mc.field_1705.method_1743()).meteor$getMessages();
      if (messages.isEmpty()) {
         return null;
      } else {
         for(int i = 0; i < Math.min((Integer)this.antiSpamDepth.get(), messages.size()); ++i) {
            String stringToCheck = ((class_303)messages.get(i)).comp_893().getString();
            Matcher timestampMatcher = timestampRegex.matcher(stringToCheck);
            if (timestampMatcher.find()) {
               stringToCheck = stringToCheck.substring(timestampMatcher.end());
            }

            if (textString.equals(stringToCheck)) {
               messageIndex = i;
               returnText = text.method_27661().method_10852(class_2561.method_43470(" (2)").method_27692(class_124.field_1080));
               break;
            }

            Matcher matcher = antiSpamRegex.matcher(stringToCheck);
            if (matcher.find()) {
               String group = matcher.group(matcher.groupCount());
               int number = Integer.parseInt(group);
               if (stringToCheck.substring(0, matcher.start()).equals(textString)) {
                  messageIndex = i;
                  returnText = text.method_27661().method_10852(class_2561.method_43470(" (" + (number + 1) + ")").method_27692(class_124.field_1080));
                  break;
               }
            }
         }

         if (returnText != null) {
            List<class_303.class_7590> visible = ((ChatHudAccessor)this.mc.field_1705.method_1743()).meteor$getVisibleMessages();
            int start = -1;

            for(int i = 0; i < messageIndex; ++i) {
               start += this.lines.getInt(i);
            }

            for(int i = this.lines.getInt(messageIndex); i > 0; --i) {
               visible.remove(start + 1);
            }

            messages.remove(messageIndex);
            this.lines.removeInt(messageIndex);
         }

         return returnText;
      }
   }

   public void removeLine(int index) {
      if (index >= this.lines.size()) {
         if ((Boolean)this.antiSpam.get()) {
            this.error("Issue detected with the anti-spam system! Likely a compatibility issue with another mod. Disabling anti-spam to protect chat integrity.", new Object[0]);
            this.antiSpam.set(false);
         }

      } else {
         this.lines.removeInt(index);
      }
   }

   public static void registerCustomHead(String prefix, class_2960 texture) {
      CUSTOM_HEAD_ENTRIES.add(new CustomHeadEntry(prefix, texture));
   }

   public int modifyChatWidth(int width) {
      return this.isActive() && (Boolean)this.playerHeads.get() ? width + 10 : width;
   }

   public void beforeDrawMessage(class_332 context, int y, int color) {
      if (this.isActive() && (Boolean)this.playerHeads.get() && this.line != null) {
         if (((IChatHudLineVisible)this.line).meteor$isStartOfEntry()) {
            this.drawTexture(context, (IChatHudLine)this.line, y, color);
         }

      }
   }

   public void afterDrawMessage() {
      if (this.isActive() && (Boolean)this.playerHeads.get()) {
         this.line = null;
      }
   }

   private void drawTexture(class_332 context, IChatHudLine line, int y, int color) {
      String text = line.meteor$getText().trim();
      int startOffset = 0;

      try {
         Matcher m = TIMESTAMP_REGEX.matcher(text);
         if (m.find()) {
            startOffset = m.end() + 1;
         }
      } catch (IllegalStateException var9) {
      }

      for(CustomHeadEntry entry : CUSTOM_HEAD_ENTRIES) {
         if (text.startsWith(entry.prefix(), startOffset)) {
            context.method_25293(class_10799.field_56883, entry.texture(), 0, y, 0.0F, 0.0F, 8, 8, 64, 64, 64, 64, color);
            return;
         }
      }

      GameProfile sender = this.getSender(line, text);
      if (sender != null) {
         class_640 entry = this.mc.method_1562().method_2871(sender.id());
         if (entry != null) {
            class_7532.method_44443(context, entry.method_52810(), 0, y, 8, color);
         }
      }
   }

   private GameProfile getSender(IChatHudLine line, String text) {
      GameProfile sender = line.meteor$getSender();
      if (sender == null) {
         Matcher usernameMatcher = usernameRegex.matcher(text);
         if (usernameMatcher.matches()) {
            String username = usernameMatcher.group(1);
            class_640 entry = this.mc.method_1562().method_2874(username);
            if (entry != null) {
               sender = entry.method_2966();
            }
         }
      }

      return sender;
   }

   private void updateDateFormat() {
      this.dateFormat = new SimpleDateFormat((Boolean)this.showSeconds.get() ? "HH:mm:ss" : "HH:mm");
   }

   private String applyAnnoy(String message) {
      StringBuilder sb = new StringBuilder(message.length());
      boolean upperCase = true;

      for(int cp : message.codePoints().toArray()) {
         if (upperCase) {
            sb.appendCodePoint(Character.toUpperCase(cp));
         } else {
            sb.appendCodePoint(Character.toLowerCase(cp));
         }

         upperCase = !upperCase;
      }

      message = sb.toString();
      return message;
   }

   private String applyFancy(String message) {
      StringBuilder sb = new StringBuilder();

      for(char ch : message.toCharArray()) {
         sb.append(this.SMALL_CAPS.getOrDefault(ch, ch));
      }

      return sb.toString();
   }

   private void compileFilterRegexList() {
      this.filterRegexList.clear();

      for(int i = 0; i < ((List)this.regexFilters.get()).size(); ++i) {
         try {
            this.filterRegexList.add(Pattern.compile((String)((List)this.regexFilters.get()).get(i)));
         } catch (PatternSyntaxException var4) {
            String removed = (String)((List)this.regexFilters.get()).remove(i);
            this.error("Removing Invalid regex: %s", new Object[]{removed});
         }
      }

   }

   private String getPrefix() {
      return (Boolean)this.prefix.get() ? this.getAffix(this.prefixText.get(), (Boolean)this.prefixSmallCaps.get(), (Boolean)this.prefixRandom.get()) : "";
   }

   private String getSuffix() {
      return (Boolean)this.suffix.get() ? this.getAffix(this.suffixText.get(), (Boolean)this.suffixSmallCaps.get(), (Boolean)this.suffixRandom.get()) : "";
   }

   private String getAffix(String text, boolean smallcaps, boolean random) {
      if (random) {
         return String.format("(%03d) ", Utils.random(0, 1000));
      } else {
         return smallcaps ? this.applyFancy(text) : text;
      }
   }

   private boolean containsCoordinates(String message) {
      return coordRegex.matcher(message).find();
   }

   private class_5250 getSendButton(String message) {
      class_5250 sendButton = class_2561.method_43470("[SEND ANYWAY]");
      class_5250 hintBaseText = class_2561.method_43470("");
      class_5250 hintMsg = class_2561.method_43470("Send your message to the global chat even if there are coordinates:");
      hintMsg.method_10862(hintBaseText.method_10866().method_27706(class_124.field_1080));
      hintBaseText.method_10852(hintMsg);
      hintBaseText.method_10852(class_2561.method_43470("\n" + message));
      sendButton.method_10862(sendButton.method_10866().method_27706(class_124.field_1079).method_10958(new MeteorClickEvent(Commands.get("say").toString(message))).method_10949(new class_2568.class_10613(hintBaseText)));
      return sendButton;
   }

   public boolean isInfiniteChatBox() {
      return this.isActive() && (Boolean)this.infiniteChatBox.get();
   }

   public boolean isLongerChat() {
      return this.isActive() && (Boolean)this.longerChatHistory.get();
   }

   public boolean keepHistory() {
      return this.isActive() && (Boolean)this.keepHistory.get();
   }

   public int getExtraChatLines() {
      return (Integer)this.longerChatLines.get();
   }

   static {
      registerCustomHead("[Meteor]", MeteorClient.identifier("textures/icons/chat/meteor.png"));
      registerCustomHead("[Baritone]", MeteorClient.identifier("textures/icons/chat/baritone.png"));
      coordRegex = Pattern.compile("(?<x>-?\\d{3,}(?:\\.\\d*)?)(?:\\s+(?<y>-?\\d{1,3}(?:\\.\\d*)?))?\\s+(?<z>-?\\d{3,}(?:\\.\\d*)?)");
   }

   private static record CustomHeadEntry(String prefix, class_2960 texture) {
   }
}
