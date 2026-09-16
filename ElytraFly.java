package meteordevelopment.meteorclient.systems.modules.movement.elytrafly;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.AbstractBlockAccessor;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes.Bounce;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes.Packet;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes.Pitch40;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes.Vanilla;
import meteordevelopment.meteorclient.systems.modules.player.ChestSwap;
import meteordevelopment.meteorclient.systems.modules.player.Rotation;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1304;
import net.minecraft.class_1802;
import net.minecraft.class_2248;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2708;
import net.minecraft.class_2828;
import net.minecraft.class_2848;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_9334;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2848.class_2849;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class ElytraFly extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgInventory;
   private final SettingGroup sgAutopilot;
   public final Setting<ElytraFlightModes> flightMode;
   public final Setting<Boolean> autoTakeOff;
   public final Setting<Double> fallMultiplier;
   public final Setting<Double> horizontalSpeed;
   public final Setting<Double> verticalSpeed;
   public final Setting<Boolean> acceleration;
   public final Setting<Double> accelerationStep;
   public final Setting<Double> accelerationMin;
   public final Setting<Boolean> stopInWater;
   public final Setting<Boolean> dontGoIntoUnloadedChunks;
   public final Setting<Boolean> autoHover;
   public final Setting<Boolean> noCrash;
   public final Setting<Integer> crashLookAhead;
   private final Setting<Boolean> instaDrop;
   public final Setting<Double> pitch40lowerBounds;
   public final Setting<Double> pitch40upperBounds;
   public final Setting<Double> pitch40rotationSpeedUp;
   public final Setting<Double> pitch40rotationSpeedDown;
   public final Setting<Boolean> autoJump;
   public final Setting<Rotation.LockMode> yawLockMode;
   public final Setting<Double> yaw;
   public final Setting<Boolean> lockPitch;
   public final Setting<Double> pitch;
   public final Setting<Boolean> restart;
   public final Setting<Integer> restartDelay;
   public final Setting<Boolean> sprint;
   public final Setting<Boolean> manualTakeoff;
   public final Setting<Boolean> replace;
   public final Setting<Integer> replaceDurability;
   public final Setting<ChestSwapMode> chestSwap;
   public final Setting<Boolean> autoReplenish;
   public final Setting<Integer> replenishSlot;
   public final Setting<Boolean> autoPilot;
   public final Setting<Boolean> useFireworks;
   public final Setting<Double> autoPilotFireworkDelay;
   public final Setting<Double> autoPilotMinimumHeight;
   private ElytraFlightMode currentMode;
   private final StaticGroundListener staticGroundListener;
   private final StaticInstaDropListener staticInstadropListener;

   public ElytraFly() {
      super(Categories.Movement, "elytra-fly", "Gives you more control over your elytra.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgInventory = this.settings.createGroup("Inventory");
      this.sgAutopilot = this.settings.createGroup("Autopilot");
      this.flightMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The mode of flying.")).defaultValue(ElytraFlightModes.Vanilla)).onModuleActivated((flightModesSetting) -> this.onModeChanged((ElytraFlightModes)flightModesSetting.get()))).onChanged(this::onModeChanged)).build());
      this.autoTakeOff = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-take-off")).description("Automatically takes off when you hold jump without needing to double jump.")).defaultValue(false)).visible(() -> this.flightMode.get() != ElytraFlightModes.Pitch40 && this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.fallMultiplier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fall-multiplier")).description("Controls how fast will you go down naturally.")).defaultValue(0.01).min((double)0.0F).visible(() -> this.flightMode.get() != ElytraFlightModes.Pitch40 && this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.horizontalSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("horizontal-speed")).description("How fast you go forward and backward.")).defaultValue((double)1.0F).min((double)0.0F).visible(() -> this.flightMode.get() != ElytraFlightModes.Pitch40 && this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.verticalSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("vertical-speed")).description("How fast you go up and down.")).defaultValue((double)1.0F).min((double)0.0F).visible(() -> this.flightMode.get() != ElytraFlightModes.Pitch40 && this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.acceleration = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("acceleration")).defaultValue(false)).visible(() -> this.flightMode.get() != ElytraFlightModes.Pitch40 && this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.accelerationStep = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("acceleration-step")).min(0.1).max((double)5.0F).defaultValue((double)1.0F).visible(() -> this.flightMode.get() != ElytraFlightModes.Pitch40 && (Boolean)this.acceleration.get() && this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.accelerationMin = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("acceleration-start")).min(0.1).defaultValue((double)0.0F).visible(() -> this.flightMode.get() != ElytraFlightModes.Pitch40 && (Boolean)this.acceleration.get() && this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.stopInWater = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("stop-in-water")).description("Stops flying in water.")).defaultValue(true)).visible(() -> this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.dontGoIntoUnloadedChunks = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("no-unloaded-chunks")).description("Stops you from going into unloaded chunks.")).defaultValue(true)).build());
      this.autoHover = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-hover")).description("Automatically hover .3 blocks off ground when holding shift.")).defaultValue(false)).visible(() -> this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.noCrash = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("no-crash")).description("Stops you from going into walls.")).defaultValue(false)).visible(() -> this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.crashLookAhead = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("crash-look-ahead")).description("Distance to look ahead when flying.")).defaultValue(5)).range(1, 15).sliderMin(1).visible(() -> (Boolean)this.noCrash.get() && this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.instaDrop = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("insta-drop")).description("Makes you drop out of flight instantly.")).defaultValue(false)).visible(() -> this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.pitch40lowerBounds = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("pitch40-lower-bounds")).description("The bottom height boundary for pitch40. You must be at least 40 blocks above this boundary when starting the module.\nAfter descending below this boundary you will start pitching upwards.")).defaultValue((double)180.0F).min((double)-128.0F).sliderMax((double)360.0F).visible(() -> this.flightMode.get() == ElytraFlightModes.Pitch40)).build());
      this.pitch40upperBounds = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("pitch40-upper-bounds")).description("The upper height boundary for pitch40. You must be above this boundary when starting the module.\nWhen ascending above this boundary, if you are not already, you will start pitching downwards.")).defaultValue((double)220.0F).min((double)-128.0F).sliderMax((double)360.0F).visible(() -> this.flightMode.get() == ElytraFlightModes.Pitch40)).build());
      this.pitch40rotationSpeedUp = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("pitch40-rotate-speed-up")).description("The speed for pitch rotation upwards (degrees per tick).")).defaultValue(5.45).min((double)1.0F).sliderMax((double)20.0F).visible(() -> this.flightMode.get() == ElytraFlightModes.Pitch40)).build());
      this.pitch40rotationSpeedDown = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("pitch40-rotate-speed-down")).description("The speed for pitch rotation downwards (degrees per tick).")).defaultValue(0.9).min((double)0.5F).sliderMax((double)2.0F).visible(() -> this.flightMode.get() == ElytraFlightModes.Pitch40)).build());
      this.autoJump = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-jump")).description("Automatically jumps for you.")).defaultValue(true)).visible(() -> this.flightMode.get() == ElytraFlightModes.Bounce)).build());
      this.yawLockMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("yaw-lock")).description("Whether to enable yaw lock or not")).defaultValue(Rotation.LockMode.Smart)).visible(() -> this.flightMode.get() == ElytraFlightModes.Bounce)).build());
      this.yaw = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("yaw")).description("The yaw angle to look at when using simple rotation lock in bounce mode.")).defaultValue((double)0.0F).range((double)0.0F, (double)360.0F).sliderRange((double)0.0F, (double)360.0F).visible(() -> this.flightMode.get() == ElytraFlightModes.Bounce && this.yawLockMode.get() == Rotation.LockMode.Simple)).build());
      this.lockPitch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pitch-lock")).description("Whether to lock your pitch angle.")).defaultValue(true)).visible(() -> this.flightMode.get() == ElytraFlightModes.Bounce)).build());
      this.pitch = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("pitch")).description("The pitch angle to look at when using the bounce mode.")).defaultValue((double)85.0F).range((double)0.0F, (double)90.0F).sliderRange((double)0.0F, (double)90.0F).visible(() -> this.flightMode.get() == ElytraFlightModes.Bounce && (Boolean)this.lockPitch.get())).build());
      this.restart = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("restart")).description("Restarts flying with the elytra when rubberbanding.")).defaultValue(true)).visible(() -> this.flightMode.get() == ElytraFlightModes.Bounce)).build());
      this.restartDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("restart-delay")).description("How many ticks to wait before restarting the elytra again after rubberbanding.")).defaultValue(7)).min(0).sliderRange(0, 20).visible(() -> this.flightMode.get() == ElytraFlightModes.Bounce && (Boolean)this.restart.get())).build());
      this.sprint = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sprint-constantly")).description("Sprints all the time. If turned off, it will only sprint when the player is touching the ground.")).defaultValue(true)).visible(() -> this.flightMode.get() == ElytraFlightModes.Bounce)).build());
      this.manualTakeoff = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("manual-takeoff")).description("Does not automatically take off.")).defaultValue(false)).visible(() -> this.flightMode.get() == ElytraFlightModes.Bounce)).build());
      this.replace = this.sgInventory.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("elytra-replace")).description("Replaces broken elytra with a new elytra.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgInventory;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("replace-durability")).description("The durability threshold your elytra will be replaced at.")).defaultValue(2)).range(1, (Integer)class_1802.field_8833.method_57347().method_58695(class_9334.field_50072, 432) - 1).sliderRange(1, (Integer)class_1802.field_8833.method_57347().method_58695(class_9334.field_50072, 432) - 1);
      Setting var10003 = this.replace;
      Objects.requireNonNull(var10003);
      this.replaceDurability = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.chestSwap = this.sgInventory.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("chest-swap")).description("Enables ChestSwap when toggling this module.")).defaultValue(ElytraFly.ChestSwapMode.Never)).build());
      this.autoReplenish = this.sgInventory.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("replenish-fireworks")).description("Moves fireworks into a selected hotbar slot.")).defaultValue(false)).build());
      var10001 = this.sgInventory;
      var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("replenish-slot")).description("The slot auto move moves fireworks to.")).defaultValue(9)).range(1, 9).sliderRange(1, 9);
      var10003 = this.autoReplenish;
      Objects.requireNonNull(var10003);
      this.replenishSlot = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.autoPilot = this.sgAutopilot.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-pilot")).description("Moves forward while elytra flying.")).defaultValue(false)).visible(() -> this.flightMode.get() != ElytraFlightModes.Pitch40 && this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.useFireworks = this.sgAutopilot.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("use-fireworks")).description("Uses firework rockets every second of your choice.")).defaultValue(false)).visible(() -> (Boolean)this.autoPilot.get() && this.flightMode.get() != ElytraFlightModes.Pitch40 && this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.autoPilotFireworkDelay = this.sgAutopilot.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("firework-delay")).description("The delay in seconds in between using fireworks if \"Use Fireworks\" is enabled.")).min((double)1.0F).defaultValue((double)8.0F).sliderMax((double)20.0F).visible(() -> (Boolean)this.useFireworks.get() && this.flightMode.get() != ElytraFlightModes.Pitch40 && this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.autoPilotMinimumHeight = this.sgAutopilot.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("minimum-height")).description("The minimum height for autopilot.")).defaultValue((double)120.0F).min((double)-128.0F).sliderMax((double)260.0F).visible(() -> (Boolean)this.autoPilot.get() && this.flightMode.get() != ElytraFlightModes.Pitch40 && this.flightMode.get() != ElytraFlightModes.Bounce)).build());
      this.currentMode = new Vanilla();
      this.staticGroundListener = new StaticGroundListener();
      this.staticInstadropListener = new StaticInstaDropListener();
   }

   public void onActivate() {
      this.currentMode.onActivate();
      if ((this.chestSwap.get() == ElytraFly.ChestSwapMode.Always || this.chestSwap.get() == ElytraFly.ChestSwapMode.WaitForGround) && this.mc.field_1724.method_6118(class_1304.field_6174).method_7909() != class_1802.field_8833 && this.isActive()) {
         ((ChestSwap)Modules.get().get(ChestSwap.class)).swap();
      }

   }

   public void onDeactivate() {
      if ((Boolean)this.autoPilot.get()) {
         this.mc.field_1690.field_1894.method_23481(false);
      }

      if (this.chestSwap.get() == ElytraFly.ChestSwapMode.Always && this.mc.field_1724.method_6118(class_1304.field_6174).method_7909() == class_1802.field_8833) {
         ((ChestSwap)Modules.get().get(ChestSwap.class)).swap();
      } else if (this.chestSwap.get() == ElytraFly.ChestSwapMode.WaitForGround) {
         this.enableGroundListener();
      }

      if (this.mc.field_1724.method_6128() && (Boolean)this.instaDrop.get()) {
         this.enableInstaDropListener();
      }

      this.currentMode.onDeactivate();
   }

   @EventHandler
   private void onPlayerMove(PlayerMoveEvent event) {
      if (this.mc.field_1724.method_6118(class_1304.field_6174).method_57826(class_9334.field_54197)) {
         this.currentMode.autoTakeoff();
         if (this.mc.field_1724.method_6128()) {
            if (this.flightMode.get() != ElytraFlightModes.Bounce) {
               this.currentMode.velX = (double)0.0F;
               this.currentMode.velY = event.movement.field_1351;
               this.currentMode.velZ = (double)0.0F;
               this.currentMode.forward = class_243.method_1030(0.0F, this.mc.field_1724.method_36454()).method_1021(0.1);
               this.currentMode.right = class_243.method_1030(0.0F, this.mc.field_1724.method_36454() + 90.0F).method_1021(0.1);
               if (this.mc.field_1724.method_5799() && (Boolean)this.stopInWater.get()) {
                  this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12982));
                  return;
               }

               this.currentMode.handleFallMultiplier();
               this.currentMode.handleAutopilot();
               this.currentMode.handleAcceleration();
               this.currentMode.handleHorizontalSpeed(event);
               this.currentMode.handleVerticalSpeed(event);
            }

            int chunkX = (int)((this.mc.field_1724.method_23317() + this.currentMode.velX) / (double)16.0F);
            int chunkZ = (int)((this.mc.field_1724.method_23321() + this.currentMode.velZ) / (double)16.0F);
            if ((Boolean)this.dontGoIntoUnloadedChunks.get()) {
               if (this.mc.field_1687.method_2935().method_12123(chunkX, chunkZ)) {
                  if (this.flightMode.get() != ElytraFlightModes.Bounce) {
                     ((IVec3d)event.movement).meteor$set(this.currentMode.velX, this.currentMode.velY, this.currentMode.velZ);
                  }
               } else {
                  this.currentMode.zeroAcceleration();
                  ((IVec3d)event.movement).meteor$set((double)0.0F, this.currentMode.velY, (double)0.0F);
               }
            } else if (this.flightMode.get() != ElytraFlightModes.Bounce) {
               ((IVec3d)event.movement).meteor$set(this.currentMode.velX, this.currentMode.velY, this.currentMode.velZ);
            }

            if (this.flightMode.get() != ElytraFlightModes.Bounce) {
               this.currentMode.onPlayerMove();
            }
         } else if (this.currentMode.lastForwardPressed && this.flightMode.get() != ElytraFlightModes.Bounce) {
            this.mc.field_1690.field_1894.method_23481(false);
            this.currentMode.lastForwardPressed = false;
         }

         if ((Boolean)this.noCrash.get() && this.mc.field_1724.method_6128() && this.flightMode.get() != ElytraFlightModes.Bounce) {
            class_243 lookAheadPos = this.mc.field_1724.method_73189().method_1019(this.mc.field_1724.method_18798().method_1029().method_1021((double)(Integer)this.crashLookAhead.get()));
            class_3959 raycastContext = new class_3959(this.mc.field_1724.method_73189(), new class_243(lookAheadPos.method_10216(), this.mc.field_1724.method_23318(), lookAheadPos.method_10215()), class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
            class_3965 hitResult = this.mc.field_1687.method_17742(raycastContext);
            if (hitResult != null && hitResult.method_17783() == class_240.field_1332) {
               ((IVec3d)event.movement).meteor$set((double)0.0F, this.currentMode.velY, (double)0.0F);
            }
         }

         if ((Boolean)this.autoHover.get() && this.mc.field_1724.field_3913.field_54155.comp_3164() && !((Freecam)Modules.get().get(Freecam.class)).isActive() && this.mc.field_1724.method_6128() && this.flightMode.get() != ElytraFlightModes.Bounce) {
            class_2680 underState = this.mc.field_1687.method_8320(this.mc.field_1724.method_24515().method_10074());
            class_2248 under = underState.method_26204();
            class_2680 under2State = this.mc.field_1687.method_8320(this.mc.field_1724.method_24515().method_10074().method_10074());
            class_2248 under2 = under2State.method_26204();
            boolean underCollidable = ((AbstractBlockAccessor)under).meteor$isCollidable() || !underState.method_26227().method_15769();
            boolean under2Collidable = ((AbstractBlockAccessor)under2).meteor$isCollidable() || !under2State.method_26227().method_15769();
            if (!underCollidable && under2Collidable) {
               ((IVec3d)event.movement).meteor$set(event.movement.field_1352, (double)-0.1F, event.movement.field_1350);
               this.mc.field_1724.method_36457(class_3532.method_15363(this.mc.field_1724.method_5695(0.0F), -50.0F, 20.0F));
            }

            if (underCollidable) {
               ((IVec3d)event.movement).meteor$set(event.movement.field_1352, (double)-0.03F, event.movement.field_1350);
               this.mc.field_1724.method_36457(class_3532.method_15363(this.mc.field_1724.method_5695(0.0F), -50.0F, 20.0F));
               if (this.mc.field_1724.method_73189().field_1351 <= (double)((float)this.mc.field_1724.method_24515().method_10074().method_10264() + 1.34F)) {
                  ((IVec3d)event.movement).meteor$set(event.movement.field_1352, (double)0.0F, event.movement.field_1350);
                  this.mc.field_1724.method_5660(false);
               }
            }
         }

      }
   }

   public boolean canPacketEfly() {
      return this.isActive() && this.flightMode.get() == ElytraFlightModes.Packet && this.mc.field_1724.method_6118(class_1304.field_6174).method_57826(class_9334.field_54197) && !this.mc.field_1724.method_24828();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      this.currentMode.onTick();
   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      this.currentMode.onPreTick();
   }

   @EventHandler
   private void onPacketSend(PacketEvent.Send event) {
      this.currentMode.onPacketSend(event);
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if (event.packet instanceof class_2708) {
         this.currentMode.zeroAcceleration();
      }

      this.currentMode.onPacketReceive(event);
   }

   private void onModeChanged(ElytraFlightModes mode) {
      switch (mode) {
         case Vanilla:
            this.currentMode = new Vanilla();
            break;
         case Packet:
            this.currentMode = new Packet();
            break;
         case Pitch40:
            this.currentMode = new Pitch40();
            this.autoPilot.set(false);
            break;
         case Bounce:
            this.currentMode = new Bounce();
      }

   }

   protected void enableGroundListener() {
      MeteorClient.EVENT_BUS.subscribe(this.staticGroundListener);
   }

   protected void disableGroundListener() {
      MeteorClient.EVENT_BUS.unsubscribe(this.staticGroundListener);
   }

   protected void enableInstaDropListener() {
      MeteorClient.EVENT_BUS.subscribe(this.staticInstadropListener);
   }

   protected void disableInstaDropListener() {
      MeteorClient.EVENT_BUS.unsubscribe(this.staticInstadropListener);
   }

   public String getInfoString() {
      return this.currentMode.getHudString();
   }

   private class StaticGroundListener {
      @EventHandler
      private void chestSwapGroundListener(PlayerMoveEvent event) {
         if (ElytraFly.this.mc.field_1724 != null && ElytraFly.this.mc.field_1724.method_24828() && ElytraFly.this.mc.field_1724.method_6118(class_1304.field_6174).method_7909() == class_1802.field_8833) {
            ((ChestSwap)Modules.get().get(ChestSwap.class)).swap();
            ElytraFly.this.disableGroundListener();
         }

      }
   }

   private class StaticInstaDropListener {
      @EventHandler
      private void onInstadropTick(TickEvent.Post event) {
         if (ElytraFly.this.mc.field_1724 != null && ElytraFly.this.mc.field_1724.method_6128()) {
            ElytraFly.this.mc.field_1724.method_18800((double)0.0F, (double)0.0F, (double)0.0F);
            ElytraFly.this.mc.field_1724.field_3944.method_52787(new class_2828.class_5911(true, ElytraFly.this.mc.field_1724.field_5976));
         } else {
            ElytraFly.this.disableInstaDropListener();
         }

      }
   }

   public static enum ChestSwapMode {
      Always,
      Never,
      WaitForGround;

      // $FF: synthetic method
      private static ChestSwapMode[] $values() {
         return new ChestSwapMode[]{Always, Never, WaitForGround};
      }
   }

   public static enum AutoPilotMode {
      Vanilla,
      Pitch40;

      // $FF: synthetic method
      private static AutoPilotMode[] $values() {
         return new AutoPilotMode[]{Vanilla, Pitch40};
      }
   }
}
