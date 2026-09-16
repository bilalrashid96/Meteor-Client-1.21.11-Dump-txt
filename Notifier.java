package meteordevelopment.meteorclient.systems.modules.misc;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_1684;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_2703;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_5250;
import net.minecraft.class_640;
import net.minecraft.class_7828;
import net.minecraft.class_8623;
import net.minecraft.class_2703.class_5893;

public class Notifier extends Module {
   private final SettingGroup sgTotemPops;
   private final SettingGroup sgVisualRange;
   private final SettingGroup sgPearl;
   private final SettingGroup sgJoinsLeaves;
   private final Setting<Boolean> totemPops;
   private final Setting<Boolean> totemsDistanceCheck;
   private final Setting<Integer> totemsDistance;
   private final Setting<Boolean> totemsIgnoreOwn;
   private final Setting<Boolean> totemsIgnoreFriends;
   private final Setting<Boolean> totemsIgnoreOthers;
   private final Setting<Boolean> visualRange;
   private final Setting<Event> event;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<Boolean> visualRangeIgnoreFriends;
   private final Setting<Boolean> visualRangeIgnoreFakes;
   private final Setting<Boolean> visualMakeSound;
   private final Setting<Boolean> pearl;
   private final Setting<Boolean> pearlIgnoreOwn;
   private final Setting<Boolean> pearlIgnoreFriends;
   private final Setting<JoinLeaveModes> joinsLeavesMode;
   private final Setting<Integer> notificationDelay;
   private final Setting<Boolean> simpleNotifications;
   private int timer;
   private boolean loginPacket;
   private final Object2IntMap<UUID> totemPopMap;
   private final Object2IntMap<UUID> chatIdMap;
   private final Map<Integer, class_243> pearlStartPosMap;
   private final class_8623<class_2561> messageQueue;
   private final Random random;

   public Notifier() {
      super(Categories.Misc, "notifier", "Notifies you of different events.");
      this.sgTotemPops = this.settings.createGroup("Totem Pops");
      this.sgVisualRange = this.settings.createGroup("Visual Range");
      this.sgPearl = this.settings.createGroup("Pearl");
      this.sgJoinsLeaves = this.settings.createGroup("Joins/Leaves");
      this.totemPops = this.sgTotemPops.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("totem-pops")).description("Notifies you when a player pops a totem.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgTotemPops;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("distance-check")).description("Limits the distance in which the pops are recognized.")).defaultValue(false);
      Setting var10003 = this.totemPops;
      Objects.requireNonNull(var10003);
      this.totemsDistanceCheck = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.totemsDistance = this.sgTotemPops.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("player-radius")).description("The radius in which to log totem pops.")).defaultValue(30)).sliderRange(1, 50).range(1, 100).visible(() -> (Boolean)this.totemPops.get() && (Boolean)this.totemsDistanceCheck.get())).build());
      this.totemsIgnoreOwn = this.sgTotemPops.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-own")).description("Ignores your own totem pops.")).defaultValue(false)).build());
      this.totemsIgnoreFriends = this.sgTotemPops.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-friends")).description("Ignores friends totem pops.")).defaultValue(false)).build());
      this.totemsIgnoreOthers = this.sgTotemPops.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-others")).description("Ignores other players totem pops.")).defaultValue(false)).build());
      this.visualRange = this.sgVisualRange.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("visual-range")).description("Notifies you when an entity enters your render distance.")).defaultValue(false)).build());
      this.event = this.sgVisualRange.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("event")).description("When to log the entities.")).defaultValue(Notifier.Event.Both)).build());
      this.entities = this.sgVisualRange.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Which entities to notify about.")).defaultValue(class_1299.field_6097).build());
      this.visualRangeIgnoreFriends = this.sgVisualRange.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-friends")).description("Ignores friends.")).defaultValue(true)).build());
      this.visualRangeIgnoreFakes = this.sgVisualRange.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-fake-players")).description("Ignores fake players.")).defaultValue(true)).build());
      this.visualMakeSound = this.sgVisualRange.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sound")).description("Emits a sound effect on enter / leave")).defaultValue(true)).build());
      this.pearl = this.sgPearl.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pearl")).description("Notifies you when a player is teleported using an ender pearl.")).defaultValue(true)).build());
      this.pearlIgnoreOwn = this.sgPearl.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-own")).description("Ignores your own pearls.")).defaultValue(false)).build());
      this.pearlIgnoreFriends = this.sgPearl.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-friends")).description("Ignores friends pearls.")).defaultValue(false)).build());
      this.joinsLeavesMode = this.sgJoinsLeaves.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("player-joins-leaves")).description("How to handle player join/leave notifications.")).defaultValue(Notifier.JoinLeaveModes.None)).build());
      this.notificationDelay = this.sgJoinsLeaves.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("notification-delay")).description("How long to wait in ticks before posting the next join/leave notification in your chat.")).range(0, 1000).sliderRange(0, 100).defaultValue(0)).build());
      this.simpleNotifications = this.sgJoinsLeaves.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("simple-notifications")).description("Display join/leave notifications without a prefix, to reduce chat clutter.")).defaultValue(true)).build());
      this.loginPacket = true;
      this.totemPopMap = new Object2IntOpenHashMap();
      this.chatIdMap = new Object2IntOpenHashMap();
      this.pearlStartPosMap = new HashMap();
      this.messageQueue = new class_8623();
      this.random = new Random();
   }

   @EventHandler
   private void onEntityAdded(EntityAddedEvent event) {
      if (!event.entity.method_5667().equals(this.mc.field_1724.method_5667()) && ((Set)this.entities.get()).contains(event.entity.method_5864()) && (Boolean)this.visualRange.get() && this.event.get() != Notifier.Event.Despawn) {
         if (event.entity instanceof class_1657) {
            if ((!(Boolean)this.visualRangeIgnoreFriends.get() || !Friends.get().isFriend((class_1657)event.entity)) && (!(Boolean)this.visualRangeIgnoreFakes.get() || !(event.entity instanceof FakePlayerEntity))) {
               ChatUtils.sendMsg(event.entity.method_5628() + 100, class_124.field_1080, "(highlight)%s(default) has entered your visual range!", event.entity.method_5477().getString());
               if ((Boolean)this.visualMakeSound.get()) {
                  this.mc.field_1687.method_43129(this.mc.field_1724, this.mc.field_1724, class_3417.field_14627, class_3419.field_15256, 3.0F, 1.0F);
               }
            }
         } else {
            class_5250 text = class_2561.method_43470(event.entity.method_5864().method_5897().getString()).method_27692(class_124.field_1068);
            text.method_10852(class_2561.method_43470(" has spawned at ").method_27692(class_124.field_1080));
            text.method_10852(ChatUtils.formatCoords(event.entity.method_73189()));
            text.method_10852(class_2561.method_43470(".").method_27692(class_124.field_1080));
            this.info(text);
         }
      }

      if ((Boolean)this.pearl.get()) {
         class_1297 var3 = event.entity;
         if (var3 instanceof class_1684) {
            class_1684 pearlEntity = (class_1684)var3;
            this.pearlStartPosMap.put(pearlEntity.method_5628(), new class_243(pearlEntity.method_23317(), pearlEntity.method_23318(), pearlEntity.method_23321()));
         }
      }

   }

   @EventHandler
   private void onEntityRemoved(EntityRemovedEvent event) {
      if (!event.entity.method_5667().equals(this.mc.field_1724.method_5667()) && ((Set)this.entities.get()).contains(event.entity.method_5864()) && (Boolean)this.visualRange.get() && this.event.get() != Notifier.Event.Spawn) {
         if (event.entity instanceof class_1657) {
            if ((!(Boolean)this.visualRangeIgnoreFriends.get() || !Friends.get().isFriend((class_1657)event.entity)) && (!(Boolean)this.visualRangeIgnoreFakes.get() || !(event.entity instanceof FakePlayerEntity))) {
               ChatUtils.sendMsg(event.entity.method_5628() + 100, class_124.field_1080, "(highlight)%s(default) has left your visual range!", event.entity.method_5477().getString());
               if ((Boolean)this.visualMakeSound.get()) {
                  this.mc.field_1687.method_43129(this.mc.field_1724, this.mc.field_1724, class_3417.field_14627, class_3419.field_15256, 3.0F, 1.0F);
               }
            }
         } else {
            class_5250 text = class_2561.method_43470(event.entity.method_5864().method_5897().getString()).method_27692(class_124.field_1068);
            text.method_10852(class_2561.method_43470(" has despawned at ").method_27692(class_124.field_1080));
            text.method_10852(ChatUtils.formatCoords(event.entity.method_73189()));
            text.method_10852(class_2561.method_43470(".").method_27692(class_124.field_1080));
            this.info(text);
         }
      }

      if ((Boolean)this.pearl.get()) {
         class_1297 e = event.entity;
         int i = e.method_5628();
         if (this.pearlStartPosMap.containsKey(i)) {
            class_1684 pearl = (class_1684)e;
            if (pearl.method_24921() != null) {
               class_1297 var6 = pearl.method_24921();
               if (var6 instanceof class_1657) {
                  class_1657 p = (class_1657)var6;
                  double d = ((class_243)this.pearlStartPosMap.get(i)).method_1022(e.method_73189());
                  if ((!Friends.get().isFriend(p) || !(Boolean)this.pearlIgnoreFriends.get()) && (!p.equals(this.mc.field_1724) || !(Boolean)this.pearlIgnoreOwn.get())) {
                     this.info("(highlight)%s's(default) pearl landed at %d, %d, %d (highlight)(%.1fm away, travelled %.1fm)(default).", new Object[]{pearl.method_24921().method_5477().getString(), pearl.method_24515().method_10263(), pearl.method_24515().method_10264(), pearl.method_24515().method_10260(), pearl.method_5739(this.mc.field_1724), d});
                  }
               }
            }

            this.pearlStartPosMap.remove(i);
         }
      }

   }

   public void onActivate() {
      this.totemPopMap.clear();
      this.chatIdMap.clear();
      this.pearlStartPosMap.clear();
   }

   public void onDeactivate() {
      this.timer = 0;
      this.messageQueue.clear();
   }

   @EventHandler
   private void onGameJoin(GameJoinedEvent event) {
      this.timer = 0;
      this.totemPopMap.clear();
      this.chatIdMap.clear();
      this.messageQueue.clear();
      this.pearlStartPosMap.clear();
   }

   @EventHandler
   private void onGameLeave(GameLeftEvent event) {
      this.loginPacket = true;
   }

   @EventHandler
   private void onReceivePacket(PacketEvent.Receive event) {
      class_2596 var10000 = event.packet;
      Objects.requireNonNull(var10000);
      class_2596 var2 = var10000;
      byte var3 = 0;

      while(true) {
         label85:
         while(true) {
            //$FF: var3->value
            //0->net/minecraft/class_2703
            //1->net/minecraft/class_7828
            //2->net/minecraft/class_2663
            switch (var2.typeSwitch<invokedynamic>(var2, var3)) {
               case 0:
                  class_2703 packet = (class_2703)var2;
                  if (!((JoinLeaveModes)this.joinsLeavesMode.get()).equals(Notifier.JoinLeaveModes.Both) && !((JoinLeaveModes)this.joinsLeavesMode.get()).equals(Notifier.JoinLeaveModes.Joins)) {
                     var3 = 1;
                     continue;
                  }

                  if (this.loginPacket) {
                     this.loginPacket = false;
                     return;
                  }

                  if (packet.method_46327().contains(class_5893.field_29136)) {
                     this.createJoinNotifications(packet);
                  }
                  break;
               case 1:
                  class_7828 packet = (class_7828)var2;
                  if (!((JoinLeaveModes)this.joinsLeavesMode.get()).equals(Notifier.JoinLeaveModes.Both) && !((JoinLeaveModes)this.joinsLeavesMode.get()).equals(Notifier.JoinLeaveModes.Leaves)) {
                     var3 = 2;
                     continue;
                  }

                  this.createLeaveNotification(packet);
                  break;
               case 2:
                  class_2663 packet = (class_2663)var2;
                  if (!(Boolean)this.totemPops.get() || packet.method_11470() != 35) {
                     break label85;
                  }

                  class_1297 var8 = packet.method_11469(this.mc.field_1687);
                  if (!(var8 instanceof class_1657)) {
                     break label85;
                  }

                  class_1657 entity = (class_1657)var8;
                  if (entity.equals(this.mc.field_1724) && (Boolean)this.totemsIgnoreOwn.get() || Friends.get().isFriend(entity) && (Boolean)this.totemsIgnoreOthers.get() || !Friends.get().isFriend(entity) && (Boolean)this.totemsIgnoreFriends.get()) {
                     return;
                  }

                  synchronized(this.totemPopMap) {
                     int pops = this.totemPopMap.getOrDefault(entity.method_5667(), 0);
                     Object2IntMap var15 = this.totemPopMap;
                     UUID var10001 = entity.method_5667();
                     ++pops;
                     var15.put(var10001, pops);
                     double distance = PlayerUtils.distanceTo((class_1297)entity);
                     if ((Boolean)this.totemsDistanceCheck.get() && distance > (double)(Integer)this.totemsDistance.get()) {
                        return;
                     }

                     ChatUtils.sendMsg(this.getChatId(entity), class_124.field_1080, "(highlight)%s (default)popped (highlight)%d (default)%s.", entity.method_5477().getString(), pops, pops == 1 ? "totem" : "totems");
                  }
            }

            return;
         }

         var3 = 3;
      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.joinsLeavesMode.get() != Notifier.JoinLeaveModes.None) {
         ++this.timer;

         while(this.timer >= (Integer)this.notificationDelay.get() && !this.messageQueue.isEmpty()) {
            this.timer = 0;
            if ((Boolean)this.simpleNotifications.get()) {
               this.mc.field_1724.method_7353((class_2561)this.messageQueue.removeFirst(), false);
            } else {
               ChatUtils.sendMsg((class_2561)this.messageQueue.removeFirst());
            }
         }
      }

      if ((Boolean)this.totemPops.get()) {
         synchronized(this.totemPopMap) {
            for(class_1657 player : this.mc.field_1687.method_18456()) {
               if (this.totemPopMap.containsKey(player.method_5667()) && (player.field_6213 > 0 || player.method_6032() <= 0.0F)) {
                  int pops = this.totemPopMap.removeInt(player.method_5667());
                  ChatUtils.sendMsg(this.getChatId(player), class_124.field_1080, "(highlight)%s (default)died after popping (highlight)%d (default)%s.", player.method_5477().getString(), pops, pops == 1 ? "totem" : "totems");
                  this.chatIdMap.removeInt(player.method_5667());
               }
            }

         }
      }
   }

   private int getChatId(class_1297 entity) {
      return this.chatIdMap.computeIfAbsent(entity.method_5667(), (value) -> this.random.nextInt());
   }

   private void createJoinNotifications(class_2703 packet) {
      for(class_2703.class_2705 entry : packet.method_46330()) {
         if (entry.comp_1107() != null) {
            if ((Boolean)this.simpleNotifications.get()) {
               class_8623 var10000 = this.messageQueue;
               String var10001 = String.valueOf(class_124.field_1080);
               var10000.addLast(class_2561.method_43470(var10001 + "[" + String.valueOf(class_124.field_1060) + "+" + String.valueOf(class_124.field_1080) + "] " + entry.comp_1107().name()));
            } else {
               class_8623 var4 = this.messageQueue;
               String var5 = String.valueOf(class_124.field_1068);
               var4.addLast(class_2561.method_43470(var5 + entry.comp_1107().name() + String.valueOf(class_124.field_1080) + " joined."));
            }
         }
      }

   }

   private void createLeaveNotification(class_7828 packet) {
      if (this.mc.method_1562() != null) {
         for(UUID id : packet.comp_1105()) {
            class_640 toRemove = this.mc.method_1562().method_2871(id);
            if (toRemove != null) {
               if ((Boolean)this.simpleNotifications.get()) {
                  class_8623 var10000 = this.messageQueue;
                  String var10001 = String.valueOf(class_124.field_1080);
                  var10000.addLast(class_2561.method_43470(var10001 + "[" + String.valueOf(class_124.field_1061) + "-" + String.valueOf(class_124.field_1080) + "] " + toRemove.method_2966().name()));
               } else {
                  class_8623 var5 = this.messageQueue;
                  String var6 = String.valueOf(class_124.field_1068);
                  var5.addLast(class_2561.method_43470(var6 + toRemove.method_2966().name() + String.valueOf(class_124.field_1080) + " left."));
               }
            }
         }

      }
   }

   public static enum Event {
      Spawn,
      Despawn,
      Both;

      // $FF: synthetic method
      private static Event[] $values() {
         return new Event[]{Spawn, Despawn, Both};
      }
   }

   public static enum JoinLeaveModes {
      None,
      Joins,
      Leaves,
      Both;

      // $FF: synthetic method
      private static JoinLeaveModes[] $values() {
         return new JoinLeaveModes[]{None, Joins, Leaves, Both};
      }
   }
}
