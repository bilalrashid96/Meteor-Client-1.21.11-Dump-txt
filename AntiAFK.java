package meteordevelopment.meteorclient.systems.modules.player;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;

public class AntiAFK extends Module {
   private final SettingGroup sgActions;
   private final SettingGroup sgMessages;
   private final Setting<Boolean> jump;
   private final Setting<Boolean> swing;
   private final Setting<Boolean> sneak;
   private final Setting<Integer> sneakTime;
   private final Setting<Boolean> strafe;
   private final Setting<Boolean> spin;
   private final Setting<SpinMode> spinMode;
   private final Setting<Integer> spinSpeed;
   private final Setting<Integer> pitch;
   private final Setting<Boolean> sendMessages;
   private final Setting<Boolean> randomMessage;
   private final Setting<Integer> delay;
   private final Setting<List<String>> messages;
   private final Random random;
   private int messageTimer;
   private int messageI;
   private int sneakTimer;
   private int strafeTimer;
   private boolean direction;
   private float lastYaw;

   public AntiAFK() {
      super(Categories.Player, "anti-afk", "Performs different actions to prevent getting kicked while AFK.");
      this.sgActions = this.settings.createGroup("Actions");
      this.sgMessages = this.settings.createGroup("Messages");
      this.jump = this.sgActions.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("jump")).description("Jump randomly.")).defaultValue(true)).build());
      this.swing = this.sgActions.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing")).description("Swings your hand.")).defaultValue(false)).build());
      this.sneak = this.sgActions.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sneak")).description("Sneaks and unsneaks quickly.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgActions;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("sneak-time")).description("How many ticks to stay sneaked.")).defaultValue(5)).min(1).sliderMin(1);
      Setting var10003 = this.sneak;
      Objects.requireNonNull(var10003);
      this.sneakTime = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.strafe = this.sgActions.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("strafe")).description("Strafe right and left.")).defaultValue(false)).onChanged((aBoolean) -> {
         this.strafeTimer = 0;
         this.direction = false;
         if (this.isActive()) {
            this.mc.field_1690.field_1913.method_23481(false);
            this.mc.field_1690.field_1849.method_23481(false);
         }

      })).build());
      this.spin = this.sgActions.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("spin")).description("Spins the player in place.")).defaultValue(true)).build());
      var10001 = this.sgActions;
      EnumSetting.Builder var6 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("spin-mode")).description("The method of rotating.")).defaultValue(AntiAFK.SpinMode.Server);
      var10003 = this.spin;
      Objects.requireNonNull(var10003);
      this.spinMode = var10001.add(((EnumSetting.Builder)var6.visible(var10003::get)).build());
      var10001 = this.sgActions;
      IntSetting.Builder var7 = (IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("speed")).description("The speed to spin you.")).defaultValue(7);
      var10003 = this.spin;
      Objects.requireNonNull(var10003);
      this.spinSpeed = var10001.add(((IntSetting.Builder)var7.visible(var10003::get)).build());
      this.pitch = this.sgActions.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("pitch")).description("The pitch to send to the server.")).defaultValue(0)).range(-90, 90).sliderRange(-90, 90).visible(() -> (Boolean)this.spin.get() && this.spinMode.get() == AntiAFK.SpinMode.Server)).build());
      this.sendMessages = this.sgMessages.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("send-messages")).description("Sends messages to prevent getting kicked for AFK.")).defaultValue(false)).build());
      var10001 = this.sgMessages;
      BoolSetting.Builder var8 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("random")).description("Selects a random message from your message list.")).defaultValue(false);
      var10003 = this.sendMessages;
      Objects.requireNonNull(var10003);
      this.randomMessage = var10001.add(((BoolSetting.Builder)var8.visible(var10003::get)).build());
      var10001 = this.sgMessages;
      IntSetting.Builder var9 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("The delay between specified messages in seconds.")).defaultValue(15)).min(0).sliderMax(30);
      var10003 = this.sendMessages;
      Objects.requireNonNull(var10003);
      this.delay = var10001.add(((IntSetting.Builder)var9.visible(var10003::get)).build());
      var10001 = this.sgMessages;
      StringListSetting.Builder var10 = ((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("messages")).description("The messages to choose from.")).defaultValue("Meteor on top!", "Meteor on crack!");
      var10003 = this.sendMessages;
      Objects.requireNonNull(var10003);
      this.messages = var10001.add(((StringListSetting.Builder)var10.visible(var10003::get)).build());
      this.random = new Random();
      this.messageTimer = 0;
      this.messageI = 0;
      this.sneakTimer = 0;
      this.strafeTimer = 0;
      this.direction = false;
   }

   public void onActivate() {
      if ((Boolean)this.sendMessages.get() && ((List)this.messages.get()).isEmpty()) {
         this.warning("Message list is empty, disabling messages...", new Object[0]);
         this.sendMessages.set(false);
      }

      this.lastYaw = this.mc.field_1724.method_36454();
      this.messageTimer = (Integer)this.delay.get() * 20;
   }

   public void onDeactivate() {
      if ((Boolean)this.strafe.get()) {
         this.mc.field_1690.field_1913.method_23481(false);
         this.mc.field_1690.field_1849.method_23481(false);
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (Utils.canUpdate()) {
         if ((Boolean)this.jump.get()) {
            if (this.mc.field_1690.field_1903.method_1434()) {
               this.mc.field_1690.field_1903.method_23481(false);
            } else if (this.random.nextInt(99) == 0) {
               this.mc.field_1690.field_1903.method_23481(true);
            }
         }

         if ((Boolean)this.swing.get() && this.random.nextInt(99) == 0) {
            this.mc.field_1724.method_6104(this.mc.field_1724.method_6058());
         }

         if ((Boolean)this.sneak.get()) {
            if (this.sneakTimer++ >= (Integer)this.sneakTime.get()) {
               this.mc.field_1690.field_1832.method_23481(false);
               if (this.random.nextInt(99) == 0) {
                  this.sneakTimer = 0;
               }
            } else {
               this.mc.field_1690.field_1832.method_23481(true);
            }
         }

         if ((Boolean)this.strafe.get() && this.strafeTimer-- <= 0) {
            this.mc.field_1690.field_1913.method_23481(!this.direction);
            this.mc.field_1690.field_1849.method_23481(this.direction);
            this.direction = !this.direction;
            this.strafeTimer = 20;
         }

         if ((Boolean)this.spin.get()) {
            this.lastYaw += (float)(Integer)this.spinSpeed.get();
            switch (((SpinMode)this.spinMode.get()).ordinal()) {
               case 0 -> Rotations.rotate((double)this.lastYaw, (double)(Integer)this.pitch.get(), -15);
               case 1 -> this.mc.field_1724.method_36456(this.lastYaw);
            }
         }

         if ((Boolean)this.sendMessages.get() && !((List)this.messages.get()).isEmpty() && this.messageTimer-- <= 0) {
            if ((Boolean)this.randomMessage.get()) {
               this.messageI = this.random.nextInt(((List)this.messages.get()).size());
            } else if (++this.messageI >= ((List)this.messages.get()).size()) {
               this.messageI = 0;
            }

            ChatUtils.sendPlayerMsg((String)((List)this.messages.get()).get(this.messageI));
            this.messageTimer = (Integer)this.delay.get() * 20;
         }

      }
   }

   public static enum SpinMode {
      Server,
      Client;

      // $FF: synthetic method
      private static SpinMode[] $values() {
         return new SpinMode[]{Server, Client};
      }
   }
}
