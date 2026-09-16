package meteordevelopment.meteorclient.systems.modules.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.events.entity.EntityMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1316;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2692;
import net.minecraft.class_2833;
import net.minecraft.class_7923;

public class EntityControl extends Module {
   private final SettingGroup sgControl;
   private final SettingGroup sgSpeed;
   private final SettingGroup sgFlight;
   List<class_1299<?>> list;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<Boolean> spoofSaddle;
   private final Setting<Boolean> maxJump;
   public final Setting<Boolean> lockYaw;
   private final Setting<Boolean> cancelServerPackets;
   private final Setting<Boolean> speed;
   private final Setting<Double> horizontalSpeed;
   private final Setting<Boolean> onlyOnGround;
   private final Setting<Boolean> inWater;
   private final Setting<Boolean> flight;
   private final Setting<Double> verticalSpeed;
   private final Setting<Double> fallSpeed;
   private final Setting<Boolean> antiKick;
   private final Setting<Integer> delay;
   private int delayLeft;
   private double lastPacketY;
   private boolean sentPacket;

   public EntityControl() {
      super(Categories.Movement, "entity-control", "Lets you control rideable entities without a saddle.", "entity-speed", "entity-fly", "boat-fly");
      this.sgControl = this.settings.createGroup("Control");
      this.sgSpeed = this.settings.createGroup("Speed");
      this.sgFlight = this.settings.createGroup("Flight");
      this.list = new ArrayList();
      class_7923.field_41177.forEach((entityType) -> {
         if (EntityUtils.isRideable(entityType) && entityType != class_1299.field_6096 && entityType != class_1299.field_6074 && entityType != class_1299.field_17714) {
            this.list.add(entityType);
         }

      });
      this.entities = this.sgControl.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Target entities.")).filter((entityType) -> EntityUtils.isRideable(entityType) && entityType != class_1299.field_6096 && entityType != class_1299.field_6074 && entityType != class_1299.field_17714).defaultValue((class_1299[])this.list.toArray(new class_1299[0])).build());
      this.spoofSaddle = this.sgControl.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("spoof-saddle*")).description("Lets you control rideable entities without them being saddled. Only works on older server versions.")).defaultValue(false)).build());
      this.maxJump = this.sgControl.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("max-jump")).description("Sets jump power to maximum.")).defaultValue(true)).build());
      this.lockYaw = this.sgControl.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("lock-yaw")).description("Locks the Entity's yaw.")).defaultValue(true)).build());
      this.cancelServerPackets = this.sgControl.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("cancel-server-packets")).description("Cancels incoming vehicle move packets. WILL desync you from the server if you make an invalid movement.")).defaultValue(false)).build());
      this.speed = this.sgSpeed.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("speed")).description("Makes you go faster horizontally when riding entities.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgSpeed;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("horizontal-speed")).description("Horizontal speed in blocks per second.")).defaultValue((double)10.0F).min((double)0.0F).sliderMax((double)50.0F);
      Setting var10003 = this.speed;
      Objects.requireNonNull(var10003);
      this.horizontalSpeed = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgSpeed;
      BoolSetting.Builder var6 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Use speed only when standing on a block.")).defaultValue(false);
      var10003 = this.speed;
      Objects.requireNonNull(var10003);
      this.onlyOnGround = var10001.add(((BoolSetting.Builder)var6.visible(var10003::get)).build());
      var10001 = this.sgSpeed;
      var6 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("in-water")).description("Use speed when in water.")).defaultValue(true);
      var10003 = this.speed;
      Objects.requireNonNull(var10003);
      this.inWater = var10001.add(((BoolSetting.Builder)var6.visible(var10003::get)).build());
      this.flight = this.sgFlight.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fly")).description("Allows you to fly with entities.")).defaultValue(false)).build());
      var10001 = this.sgFlight;
      DoubleSetting.Builder var8 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("vertical-speed")).description("Vertical speed in blocks per second.")).defaultValue((double)6.0F).min((double)0.0F).sliderMax((double)20.0F);
      var10003 = this.flight;
      Objects.requireNonNull(var10003);
      this.verticalSpeed = var10001.add(((DoubleSetting.Builder)var8.visible(var10003::get)).build());
      var10001 = this.sgFlight;
      var8 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fall-speed")).description("How fast you will fall in blocks per second. Set to a small value to prevent fly kicks.")).defaultValue((double)0.0F).min((double)0.0F);
      var10003 = this.flight;
      Objects.requireNonNull(var10003);
      this.fallSpeed = var10001.add(((DoubleSetting.Builder)var8.visible(var10003::get)).build());
      var10001 = this.sgFlight;
      BoolSetting.Builder var10 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-fly-kick")).description("Whether to prevent the server from kicking you for flying.")).defaultValue(true);
      var10003 = this.flight;
      Objects.requireNonNull(var10003);
      this.antiKick = var10001.add(((BoolSetting.Builder)var10.visible(var10003::get)).build());
      this.delay = this.sgFlight.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("The amount of delay, in ticks, between flying down a bit and return to original position")).defaultValue(40)).min(1).sliderMax(80).visible(() -> (Boolean)this.flight.get() && (Boolean)this.antiKick.get())).build());
      this.lastPacketY = Double.MAX_VALUE;
      this.sentPacket = false;
   }

   public void onActivate() {
      this.delayLeft = (Integer)this.delay.get();
      this.sentPacket = false;
      this.lastPacketY = Double.MAX_VALUE;
   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      if (this.sentPacket && this.mc.field_1724.method_5854() != null) {
         class_2833 packet = class_2833.method_65307(this.mc.field_1724.method_5854());
         ((IVec3d)packet.comp_3350()).meteor$setY(this.lastPacketY);
         this.mc.method_1562().method_52787(packet);
         this.sentPacket = false;
      }

      --this.delayLeft;
   }

   @EventHandler
   private void onEntityMove(EntityMoveEvent event) {
      class_1297 entity = event.entity;
      if (event.entity.method_5642() == this.mc.field_1724 && ((Set)this.entities.get()).contains(entity.method_5864())) {
         double velX = entity.method_18798().field_1352;
         double velY = entity.method_18798().field_1351;
         double velZ = entity.method_18798().field_1350;
         if ((Boolean)this.speed.get() && (!(Boolean)this.onlyOnGround.get() || entity.method_24828() || entity.method_70987()) && ((Boolean)this.inWater.get() || !entity.method_5799())) {
            class_243 vel = PlayerUtils.getHorizontalVelocity((Double)this.horizontalSpeed.get());
            velX = vel.field_1352;
            velZ = vel.field_1350;
         }

         if ((Boolean)this.flight.get()) {
            velY = (double)0.0F;
            if (Input.isPressed(this.mc.field_1690.field_1903)) {
               velY += (Double)this.verticalSpeed.get() / (double)20.0F;
            }

            if (Input.isPressed(this.mc.field_1690.field_1867)) {
               velY -= (Double)this.verticalSpeed.get() / (double)20.0F;
            } else {
               velY -= (Double)this.fallSpeed.get() / (double)20.0F;
            }
         }

         if ((Boolean)this.lockYaw.get()) {
            entity.method_36456(this.mc.field_1724.method_36454());
         }

         ((IVec3d)event.movement).meteor$set(velX, velY, velZ);
      }
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2833 packet) {
         if ((Boolean)this.antiKick.get()) {
            double currentY = packet.comp_3350().field_1351;
            if (this.delayLeft <= 0 && !this.sentPacket && this.shouldFlyDown(currentY) && EntityUtils.isOnAir(this.mc.field_1724.method_5854()) && !this.mc.field_1724.method_5854().method_70987()) {
               ((IVec3d)packet.comp_3350()).meteor$setY(this.lastPacketY - 0.0313);
               this.sentPacket = true;
               this.delayLeft = (Integer)this.delay.get();
            }

            this.lastPacketY = currentY;
            return;
         }
      }

   }

   @EventHandler
   private void onReceivePacket(PacketEvent.Receive event) {
      if (event.packet instanceof class_2692 && (Boolean)this.cancelServerPackets.get()) {
         event.cancel();
      }

   }

   private boolean shouldFlyDown(double currentY) {
      if (currentY >= this.lastPacketY) {
         return true;
      } else {
         return this.lastPacketY - currentY < 0.0313;
      }
   }

   public boolean spoofSaddle() {
      return this.isActive() && (Boolean)this.spoofSaddle.get();
   }

   public boolean maxJump() {
      return this.isActive() && (Boolean)this.maxJump.get();
   }

   public boolean cancelJump() {
      if (!(this.mc.field_1724.method_5854() instanceof class_1316)) {
         return false;
      } else {
         return this.isActive() && ((Set)this.entities.get()).contains(this.mc.field_1724.method_5854().method_5864()) && (Boolean)this.flight.get();
      }
   }
}
