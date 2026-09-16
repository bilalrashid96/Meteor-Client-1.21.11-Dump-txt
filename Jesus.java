package meteordevelopment.meteorclient.systems.modules.movement;

import java.util.Objects;
import meteordevelopment.meteorclient.events.entity.player.CanWalkOnFluidEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.speed.modes.Strafe;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_10255;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1934;
import net.minecraft.class_2246;
import net.minecraft.class_238;
import net.minecraft.class_259;
import net.minecraft.class_2596;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2828;
import net.minecraft.class_3545;
import net.minecraft.class_3612;
import net.minecraft.class_4985;
import net.minecraft.class_5134;
import org.joml.Vector2d;

public class Jesus extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgWater;
   private final SettingGroup sgLava;
   private final Setting<Boolean> powderSnow;
   private final Setting<Boolean> ncpBypass;
   private final Setting<Boolean> slowDown;
   private final Setting<Mode> waterMode;
   private final Setting<Boolean> dipIfBurning;
   private final Setting<Boolean> dipOnSneakWater;
   private final Setting<Boolean> dipOnFallWater;
   private final Setting<Integer> dipFallHeightWater;
   private final Setting<Mode> lavaMode;
   private final Setting<Boolean> dipIfFireResistant;
   private final Setting<Boolean> dipOnSneakLava;
   private final Setting<Boolean> dipOnFallLava;
   private final Setting<Integer> dipFallHeightLava;
   private int ascending;
   private int swimmingTicks;
   private boolean prePathManagerWalkOnWater;
   private boolean prePathManagerWalkOnLava;
   public boolean isInBubbleColumn;

   public Jesus() {
      super(Categories.Movement, "jesus", "Walk on liquids and powder snow like Jesus.");
      this.sgGeneral = this.settings.createGroup("General");
      this.sgWater = this.settings.createGroup("Water");
      this.sgLava = this.settings.createGroup("Lava");
      this.powderSnow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("powder-snow")).description("Walk on powder snow.")).defaultValue(true)).build());
      this.ncpBypass = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ncp-bypass")).description("Whether to apply a bypass for NCP.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("slow-down")).description("Further movement option to try bypassing NCP")).defaultValue(false);
      Setting var10003 = this.ncpBypass;
      Objects.requireNonNull(var10003);
      this.slowDown = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.waterMode = this.sgWater.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("How to treat the water.")).defaultValue(Jesus.Mode.Solid)).build());
      this.dipIfBurning = this.sgWater.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dip-if-burning")).description("Lets you go into the water when you are burning.")).defaultValue(true)).visible(() -> this.waterMode.get() == Jesus.Mode.Solid)).build());
      this.dipOnSneakWater = this.sgWater.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dip-on-sneak")).description("Lets you go into the water when your sneak key is held.")).defaultValue(true)).visible(() -> this.waterMode.get() == Jesus.Mode.Solid)).build());
      this.dipOnFallWater = this.sgWater.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dip-on-fall")).description("Lets you go into the water when you fall over a certain height.")).defaultValue(true)).visible(() -> this.waterMode.get() == Jesus.Mode.Solid)).build());
      this.dipFallHeightWater = this.sgWater.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("dip-fall-height")).description("The fall height at which you will go into the water.")).defaultValue(4)).range(1, 255).sliderRange(3, 20).visible(() -> this.waterMode.get() == Jesus.Mode.Solid && (Boolean)this.dipOnFallWater.get())).build());
      this.lavaMode = this.sgLava.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("How to treat the lava.")).defaultValue(Jesus.Mode.Solid)).build());
      this.dipIfFireResistant = this.sgLava.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dip-if-resistant")).description("Lets you go into the lava if you have Fire Resistance effect.")).defaultValue(true)).visible(() -> this.lavaMode.get() == Jesus.Mode.Solid)).build());
      this.dipOnSneakLava = this.sgLava.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dip-on-sneak")).description("Lets you go into the lava when your sneak key is held.")).defaultValue(true)).visible(() -> this.lavaMode.get() == Jesus.Mode.Solid)).build());
      this.dipOnFallLava = this.sgLava.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dip-on-fall")).description("Lets you go into the lava when you fall over a certain height.")).defaultValue(true)).visible(() -> this.lavaMode.get() == Jesus.Mode.Solid)).build());
      this.dipFallHeightLava = this.sgLava.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("dip-fall-height")).description("The fall height at which you will go into the lava.")).defaultValue(4)).range(1, 255).sliderRange(3, 20).visible(() -> this.lavaMode.get() == Jesus.Mode.Solid && (Boolean)this.dipOnFallLava.get())).build());
      this.ascending = 10;
      this.swimmingTicks = 0;
      this.isInBubbleColumn = false;
   }

   public void onActivate() {
      this.prePathManagerWalkOnWater = (Boolean)PathManagers.get().getSettings().getWalkOnWater().get();
      this.prePathManagerWalkOnLava = (Boolean)PathManagers.get().getSettings().getWalkOnLava().get();
      PathManagers.get().getSettings().getWalkOnWater().set(this.waterMode.get() == Jesus.Mode.Solid);
      PathManagers.get().getSettings().getWalkOnLava().set(this.lavaMode.get() == Jesus.Mode.Solid);
   }

   public void onDeactivate() {
      PathManagers.get().getSettings().getWalkOnWater().set(this.prePathManagerWalkOnWater);
      PathManagers.get().getSettings().getWalkOnLava().set(this.prePathManagerWalkOnLava);
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      boolean bubbleColumn = this.isInBubbleColumn;
      this.isInBubbleColumn = false;
      if (!this.mc.field_1724.method_20232()) {
         if (!this.mc.field_1724.method_5799() || this.waterShouldBeSolid()) {
            if (!this.mc.field_1724.method_5771() || this.lavaShouldBeSolid()) {
               class_1297 movingEntity = (class_1297)(this.mc.field_1724.method_5765() ? this.mc.field_1724.method_5854() : this.mc.field_1724);
               if (bubbleColumn) {
                  if (this.mc.field_1690.field_1903.method_1434() && movingEntity.method_18798().method_10214() < 0.11) {
                     ((IVec3d)movingEntity.method_18798()).meteor$setY(0.11);
                  }

               } else if (!movingEntity.method_5799() && !movingEntity.method_5771()) {
                  class_2680 blockBelowState = this.mc.field_1687.method_8320(movingEntity.method_24515().method_10074());
                  boolean waterLogged = (Boolean)blockBelowState.method_61767(class_2741.field_12508, false);
                  if (this.ascending == 0) {
                     ((IVec3d)movingEntity.method_18798()).meteor$setY(0.11);
                  } else if (this.ascending == 1 && (blockBelowState.method_26204() == class_2246.field_10382 || blockBelowState.method_26204() == class_2246.field_10164 || waterLogged)) {
                     ((IVec3d)movingEntity.method_18798()).meteor$setY((double)0.0F);
                  }

                  ++this.ascending;
               } else {
                  ((IVec3d)movingEntity.method_18798()).meteor$setY(0.11);
                  this.ascending = 0;
               }
            }
         }
      }
   }

   @EventHandler
   private void onCanWalkOnFluid(CanWalkOnFluidEvent event) {
      if (this.mc.field_1724 == null || !this.mc.field_1724.method_5681()) {
         if ((event.fluidState.method_15772() == class_3612.field_15910 || event.fluidState.method_15772() == class_3612.field_15909) && this.waterShouldBeSolid()) {
            event.walkOnFluid = true;
         } else if ((event.fluidState.method_15772() == class_3612.field_15908 || event.fluidState.method_15772() == class_3612.field_15907) && this.lavaShouldBeSolid()) {
            event.walkOnFluid = true;
         }

      }
   }

   @EventHandler
   private void onFluidCollisionShape(CollisionShapeEvent event) {
      if (!event.state.method_26227().method_15769()) {
         if ((event.state.method_26204() == class_2246.field_10382 || event.state.method_26227().method_15772() == class_3612.field_15910) && !this.mc.field_1724.method_5799() && this.waterShouldBeSolid() && (double)event.pos.method_10264() <= this.mc.field_1724.method_23318() - (double)1.0F) {
            event.shape = class_259.method_1077();
         } else if (event.state.method_26204() == class_2246.field_10164 && !this.mc.field_1724.method_5771() && this.lavaShouldBeSolid() && (this.isLavaDangerous() || (double)event.pos.method_10264() <= this.mc.field_1724.method_23318() - (double)1.0F)) {
            event.shape = class_259.method_1077();
         }

      }
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2828 packet) {
         if (!this.mc.field_1724.method_5799() || this.waterShouldBeSolid()) {
            if (!this.mc.field_1724.method_5771() || this.lavaShouldBeSolid()) {
               if ((Boolean)this.ncpBypass.get()) {
                  class_3545<Boolean, Boolean> overLiquid = this.isOverLiquid();
                  boolean shouldWork = (Boolean)overLiquid.method_15442() && this.waterShouldBeSolid() || (Boolean)overLiquid.method_15441() && this.lavaShouldBeSolid();
                  if (!this.mc.field_1724.method_5799() && !this.mc.field_1724.method_5771() && !(this.mc.field_1724.field_6017 > (double)3.0F) && shouldWork) {
                     ((PlayerMoveC2SPacketAccessor)packet).meteor$setOnGround(false);
                     if (this.mc.field_1724.method_24828() && packet.method_36171()) {
                        ((PlayerMoveC2SPacketAccessor)packet).meteor$setY(packet.method_12268((double)0.0F) - (0.02 + 1.0E-4 * (double)this.swimmingTicks));
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onMoveEvent(PlayerMoveEvent event) {
      if ((Boolean)this.ncpBypass.get()) {
         class_3545<Boolean, Boolean> overLiquid = this.isOverLiquid();
         boolean water = (Boolean)overLiquid.method_15442() && this.waterShouldBeSolid();
         boolean lava = (Boolean)overLiquid.method_15441() && this.lavaShouldBeSolid();
         if (!water && !lava) {
            this.swimmingTicks = 0;
         } else if (++this.swimmingTicks < 15) {
            if (this.mc.field_1724.method_24828()) {
               Vector2d vel = Strafe.transformStrafe(PlayerUtils.isMoving() ? 0.2873 : (double)0.0F);
               ((IVec3d)event.movement).meteor$setXZ(vel.x, vel.y);
            }

         } else {
            this.swimmingTicks = 0;
            if ((Boolean)this.slowDown.get()) {
               ((IVec3d)event.movement).meteor$setXZ((double)0.0F, (double)0.0F);
            }

            ((IVec3d)this.mc.field_1724.method_18798()).meteor$setY(0.08);
         }
      }
   }

   private boolean waterShouldBeSolid() {
      if (EntityUtils.getGameMode(this.mc.field_1724) != class_1934.field_9219 && !this.mc.field_1724.method_31549().field_7479) {
         if (this.mc.field_1724.method_5854() != null && this.mc.field_1724.method_5854() instanceof class_10255) {
            return false;
         } else if (((Flight)Modules.get().get(Flight.class)).isActive()) {
            return false;
         } else if ((Boolean)this.dipIfBurning.get() && this.mc.field_1724.method_5809()) {
            return false;
         } else if ((Boolean)this.dipOnSneakWater.get() && this.mc.field_1690.field_1832.method_1434()) {
            return false;
         } else if ((Boolean)this.dipOnFallWater.get() && this.mc.field_1724.field_6017 > (double)(Integer)this.dipFallHeightWater.get()) {
            return false;
         } else {
            return this.waterMode.get() == Jesus.Mode.Solid;
         }
      } else {
         return false;
      }
   }

   private boolean lavaShouldBeSolid() {
      if (EntityUtils.getGameMode(this.mc.field_1724) != class_1934.field_9219 && !this.mc.field_1724.method_31549().field_7479) {
         if (this.mc.field_1724.method_5854() != null && this.mc.field_1724.method_5854() instanceof class_4985) {
            return false;
         } else if (this.isLavaDangerous() && this.lavaMode.get() == Jesus.Mode.Solid) {
            return true;
         } else if ((Boolean)this.dipOnSneakLava.get() && this.mc.field_1690.field_1832.method_1434()) {
            return false;
         } else if ((Boolean)this.dipOnFallLava.get() && this.mc.field_1724.field_6017 > (double)(Integer)this.dipFallHeightLava.get()) {
            return false;
         } else {
            return this.lavaMode.get() == Jesus.Mode.Solid;
         }
      } else {
         return false;
      }
   }

   private boolean isLavaDangerous() {
      if (!(Boolean)this.dipIfFireResistant.get()) {
         return true;
      } else {
         return !this.mc.field_1724.method_6059(class_1294.field_5918) || !((double)this.mc.field_1724.method_6112(class_1294.field_5918).method_5584() > (double)300.0F * this.mc.field_1724.method_45325(class_5134.field_51579));
      }
   }

   private class_3545<Boolean, Boolean> isOverLiquid() {
      class_238 box = this.mc.field_1724.method_5765() ? this.mc.field_1724.method_5829().method_991(this.mc.field_1724.method_5854().method_5829()) : this.mc.field_1724.method_5829();
      class_2680[] states = (class_2680[])this.mc.field_1687.method_29546(box.method_989((double)0.0F, -0.01, (double)0.0F)).toArray((x$0) -> new class_2680[x$0]);
      boolean water = false;
      boolean lava = false;
      boolean foundSolid = false;

      for(class_2680 state : states) {
         if (state.method_26204() != class_2246.field_10382 && state.method_26227().method_15772() != class_3612.field_15910) {
            if (state.method_26204() == class_2246.field_10164) {
               lava = true;
            } else if (!state.method_26215()) {
               foundSolid = true;
               break;
            }
         } else {
            water = true;
         }
      }

      return new class_3545(water && !foundSolid, lava && !foundSolid);
   }

   public boolean canWalkOnPowderSnow() {
      return this.isActive() && (Boolean)this.powderSnow.get();
   }

   public static enum Mode {
      Solid,
      Ignore;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Solid, Ignore};
      }
   }
}
