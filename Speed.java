package meteordevelopment.meteorclient.systems.modules.movement.speed;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.speed.modes.Strafe;
import meteordevelopment.meteorclient.systems.modules.movement.speed.modes.Vanilla;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1313;
import net.minecraft.class_2708;

public class Speed extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<SpeedModes> speedMode;
   public final Setting<Double> vanillaSpeed;
   public final Setting<Double> ncpSpeed;
   public final Setting<Boolean> ncpSpeedLimit;
   public final Setting<Double> timer;
   public final Setting<Boolean> inLiquids;
   public final Setting<Boolean> whenSneaking;
   public final Setting<Boolean> vanillaOnGround;
   private SpeedMode currentMode;

   public Speed() {
      super(Categories.Movement, "speed", "Modifies your movement speed when moving on the ground.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.speedMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The method of applying speed.")).defaultValue(SpeedModes.Vanilla)).onModuleActivated((speedModesSetting) -> this.onSpeedModeChanged((SpeedModes)speedModesSetting.get()))).onChanged(this::onSpeedModeChanged)).build());
      this.vanillaSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("vanilla-speed")).description("The speed in blocks per second.")).defaultValue(5.6).min((double)0.0F).sliderMax((double)20.0F).visible(() -> this.speedMode.get() == SpeedModes.Vanilla)).build());
      this.ncpSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("strafe-speed")).description("The speed.")).visible(() -> this.speedMode.get() == SpeedModes.Strafe)).defaultValue(1.6).min((double)0.0F).sliderMax((double)3.0F).build());
      this.ncpSpeedLimit = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("speed-limit")).description("Limits your speed on servers with very strict anticheats.")).visible(() -> this.speedMode.get() == SpeedModes.Strafe)).defaultValue(false)).build());
      this.timer = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("timer")).description("Timer override.")).defaultValue((double)1.0F).min(0.01).sliderMin(0.01).sliderMax((double)10.0F).build());
      this.inLiquids = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("in-liquids")).description("Uses speed when in lava or water.")).defaultValue(false)).build());
      this.whenSneaking = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("when-sneaking")).description("Uses speed when sneaking.")).defaultValue(false)).build());
      this.vanillaOnGround = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Uses speed only when standing on a block.")).visible(() -> this.speedMode.get() == SpeedModes.Vanilla)).defaultValue(false)).build());
      this.onSpeedModeChanged(this.speedMode.get());
   }

   public void onActivate() {
      this.currentMode.onActivate();
   }

   public void onDeactivate() {
      ((Timer)Modules.get().get(Timer.class)).setOverride((double)1.0F);
      this.currentMode.onDeactivate();
   }

   @EventHandler
   private void onPlayerMove(PlayerMoveEvent event) {
      if (event.type == class_1313.field_6308 && !this.stopSpeed()) {
         if ((Double)this.timer.get() != (double)1.0F) {
            ((Timer)Modules.get().get(Timer.class)).setOverride(PlayerUtils.isMoving() ? (Double)this.timer.get() : (double)1.0F);
         }

         this.currentMode.onMove(event);
      }
   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      if (!this.stopSpeed()) {
         this.currentMode.onTick();
      }
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if (event.packet instanceof class_2708) {
         this.currentMode.onRubberband();
      }

   }

   private void onSpeedModeChanged(SpeedModes mode) {
      switch (mode) {
         case Vanilla -> this.currentMode = new Vanilla();
         case Strafe -> this.currentMode = new Strafe();
      }

   }

   private boolean stopSpeed() {
      if (!this.mc.field_1724.method_6128() && !this.mc.field_1724.method_6101() && this.mc.field_1724.method_5854() == null) {
         if (!(Boolean)this.whenSneaking.get() && this.mc.field_1724.method_5715()) {
            return true;
         } else if ((Boolean)this.vanillaOnGround.get() && !this.mc.field_1724.method_24828() && this.speedMode.get() == SpeedModes.Vanilla) {
            return true;
         } else {
            return !(Boolean)this.inLiquids.get() && (this.mc.field_1724.method_5799() || this.mc.field_1724.method_5771());
         }
      } else {
         return true;
      }
   }

   public String getInfoString() {
      return this.currentMode.getHudString();
   }
}
