package meteordevelopment.meteorclient.systems.modules.misc;

import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_419;
import org.apache.commons.lang3.RandomStringUtils;

public class Spam extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<String>> messages;
   private final Setting<Integer> delay;
   private final Setting<Boolean> disableOnLeave;
   private final Setting<Boolean> disableOnDisconnect;
   private final Setting<Boolean> random;
   private final Setting<Boolean> autoSplitMessages;
   private final Setting<Integer> splitLength;
   private final Setting<Integer> autoSplitDelay;
   private final Setting<Boolean> bypass;
   private final Setting<Boolean> uppercase;
   private final Setting<Integer> length;
   private int messageI;
   private int timer;
   private int splitNum;
   private String text;

   public Spam() {
      super(Categories.Misc, "spam", "Spams specified messages in chat.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.messages = this.sgGeneral.add(((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("messages")).description("Messages to use for spam.")).defaultValue(List.of("Meteor on Crack!"))).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("The delay between specified messages in ticks.")).defaultValue(20)).min(0).sliderMax(200).build());
      this.disableOnLeave = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-leave")).description("Disables spam when you leave a server.")).defaultValue(true)).build());
      this.disableOnDisconnect = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-disconnect")).description("Disables spam when you are disconnected from a server.")).defaultValue(true)).build());
      this.random = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("randomise")).description("Selects a random message from your spam message list.")).defaultValue(false)).build());
      this.autoSplitMessages = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-split-messages")).description("Automatically split up large messages after a certain length")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      IntSetting.Builder var10002 = (IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("split-length")).description("The length after which to split messages in chat");
      Setting var10003 = this.autoSplitMessages;
      Objects.requireNonNull(var10003);
      this.splitLength = var10001.add(((IntSetting.Builder)((IntSetting.Builder)var10002.visible(var10003::get)).defaultValue(256)).min(1).sliderMax(256).build());
      var10001 = this.sgGeneral;
      var10002 = (IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("split-delay")).description("The delay between split messages in ticks.");
      var10003 = this.autoSplitMessages;
      Objects.requireNonNull(var10003);
      this.autoSplitDelay = var10001.add(((IntSetting.Builder)((IntSetting.Builder)var10002.visible(var10003::get)).defaultValue(20)).min(0).sliderMax(200).build());
      this.bypass = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("bypass")).description("Add random text at the end of the message to try to bypass anti spams.")).defaultValue(false)).build());
      var10001 = this.sgGeneral;
      BoolSetting.Builder var5 = (BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("include-uppercase-characters")).description("Whether the bypass text should include uppercase characters.");
      var10003 = this.bypass;
      Objects.requireNonNull(var10003);
      this.uppercase = var10001.add(((BoolSetting.Builder)((BoolSetting.Builder)var5.visible(var10003::get)).defaultValue(true)).build());
      var10001 = this.sgGeneral;
      IntSetting.Builder var6 = (IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("length")).description("Number of characters used to bypass anti spam.");
      var10003 = this.bypass;
      Objects.requireNonNull(var10003);
      this.length = var10001.add(((IntSetting.Builder)((IntSetting.Builder)var6.visible(var10003::get)).defaultValue(16)).sliderRange(1, 256).build());
   }

   public void onActivate() {
      this.timer = (Integer)this.delay.get();
      this.messageI = 0;
      this.splitNum = 0;
   }

   @EventHandler
   private void onScreenOpen(OpenScreenEvent event) {
      if ((Boolean)this.disableOnDisconnect.get() && event.screen instanceof class_419) {
         this.toggle();
      }

   }

   @EventHandler
   private void onGameLeft(GameLeftEvent event) {
      if ((Boolean)this.disableOnLeave.get()) {
         this.toggle();
      }

   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (!((List)this.messages.get()).isEmpty()) {
         if (this.timer <= 0) {
            if (this.text == null) {
               int i;
               if ((Boolean)this.random.get()) {
                  i = Utils.random(0, ((List)this.messages.get()).size());
               } else {
                  if (this.messageI >= ((List)this.messages.get()).size()) {
                     this.messageI = 0;
                  }

                  i = this.messageI++;
               }

               this.text = (String)((List)this.messages.get()).get(i);
               if ((Boolean)this.bypass.get()) {
                  String bypass = RandomStringUtils.insecure().nextAlphabetic((Integer)this.length.get());
                  if (!(Boolean)this.uppercase.get()) {
                     bypass = bypass.toLowerCase();
                  }

                  this.text = this.text + " " + bypass;
               }
            }

            if ((Boolean)this.autoSplitMessages.get() && this.text.length() > (Integer)this.splitLength.get()) {
               double length = (double)this.text.length();
               int splits = (int)Math.ceil(length / (double)(Integer)this.splitLength.get());
               int start = this.splitNum * (Integer)this.splitLength.get();
               int end = Math.min(start + (Integer)this.splitLength.get(), this.text.length());
               ChatUtils.sendPlayerMsg(this.text.substring(start, end));
               this.splitNum = ++this.splitNum % splits;
               this.timer = (Integer)this.autoSplitDelay.get();
               if (this.splitNum == 0) {
                  this.timer = (Integer)this.delay.get();
                  this.text = null;
               }
            } else {
               if (this.text.length() > 256) {
                  this.text = this.text.substring(0, 256);
               }

               ChatUtils.sendPlayerMsg(this.text);
               this.timer = (Integer)this.delay.get();
               this.text = null;
            }
         } else {
            --this.timer;
         }

      }
   }
}
