package meteordevelopment.meteorclient.systems.modules.movement;

import java.util.Objects;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.LivingEntityAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_243;
import net.minecraft.class_5635;

public class FastClimb extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> timerMode;
   private final Setting<Double> speed;
   private final Setting<Double> timer;
   private boolean resetTimer;

   public FastClimb() {
      super(Categories.Movement, "fast-climb", "Allows you to climb faster.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.timerMode = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("timer-mode")).description("Use timer.")).defaultValue(false)).build());
      this.speed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("climb-speed")).description("Your climb speed.")).defaultValue(0.2872).min((double)0.0F).visible(() -> !(Boolean)this.timerMode.get())).build());
      SettingGroup var10001 = this.sgGeneral;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("timer")).description("The timer value for Timer.")).defaultValue(1.436).min((double)1.0F).sliderMin((double)1.0F);
      Setting var10003 = this.timerMode;
      Objects.requireNonNull(var10003);
      this.timer = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
   }

   public void onActivate() {
      this.resetTimer = false;
   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      if ((Boolean)this.timerMode.get()) {
         if (this.climbing()) {
            this.resetTimer = false;
            ((Timer)Modules.get().get(Timer.class)).setOverride((Double)this.timer.get());
         } else if (!this.resetTimer) {
            ((Timer)Modules.get().get(Timer.class)).setOverride((double)1.0F);
            this.resetTimer = true;
         }
      }

   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (!(Boolean)this.timerMode.get() && this.climbing()) {
         class_243 velocity = this.mc.field_1724.method_18798();
         this.mc.field_1724.method_18800(velocity.field_1352, (Double)this.speed.get(), velocity.field_1350);
      }

   }

   private boolean climbing() {
      return (this.mc.field_1724.field_5976 || ((LivingEntityAccessor)this.mc.field_1724).meteor$isJumping()) && (this.mc.field_1724.method_6101() || this.mc.field_1724.method_55667().method_27852(class_2246.field_27879) && class_5635.method_32355(this.mc.field_1724));
   }
}
