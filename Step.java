package meteordevelopment.meteorclient.systems.modules.movement;

import com.google.common.collect.Streams;
import java.util.Objects;
import java.util.OptionalDouble;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_238;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_5134;

public class Step extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<Double> height;
   private final Setting<ActiveWhen> activeWhen;
   private final Setting<Boolean> safeStep;
   private final Setting<Integer> stepHealth;
   private float prevStepHeight;
   private boolean prevPathManagerStep;

   public Step() {
      super(Categories.Movement, "step", "Allows you to walk up full blocks instantly.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.height = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("height")).description("Step height.")).defaultValue((double)1.25F).min((double)0.0F).build());
      this.activeWhen = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("active-when")).description("Step is active when you meet these requirements.")).defaultValue(Step.ActiveWhen.Always)).build());
      this.safeStep = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("safe-step")).description("Doesn't let you step out of a hole if you are low on health or there is a crystal nearby.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("step-health")).description("The health you stop being able to step at.")).defaultValue(5)).range(1, 36).sliderRange(1, 36);
      Setting var10003 = this.safeStep;
      Objects.requireNonNull(var10003);
      this.stepHealth = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
   }

   public void onActivate() {
      this.prevStepHeight = this.mc.field_1724.method_49476();
      this.prevPathManagerStep = (Boolean)PathManagers.get().getSettings().getStep().get();
      PathManagers.get().getSettings().getStep().set(true);
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      boolean work = this.activeWhen.get() == Step.ActiveWhen.Always || this.activeWhen.get() == Step.ActiveWhen.Sneaking && this.mc.field_1724.method_5715() || this.activeWhen.get() == Step.ActiveWhen.NotSneaking && !this.mc.field_1724.method_5715();
      double height = this.getMaxSafeHeight();
      if (work && height > (double)0.0F) {
         this.mc.field_1724.method_5996(class_5134.field_47761).method_6192(height);
      } else {
         this.mc.field_1724.method_5996(class_5134.field_47761).method_6192((double)this.prevStepHeight);
      }

   }

   public void onDeactivate() {
      this.mc.field_1724.method_5996(class_5134.field_47761).method_6192((double)this.prevStepHeight);
      PathManagers.get().getSettings().getStep().set(this.prevPathManagerStep);
   }

   private float getHealth() {
      return this.mc.field_1724.method_6032() + this.mc.field_1724.method_6067();
   }

   private double getExplosionDamage() {
      OptionalDouble crystalDamage = Streams.stream(this.mc.field_1687.method_18112()).filter((entity) -> entity instanceof class_1511).filter(class_1297::method_5805).mapToDouble((entity) -> (double)DamageUtils.crystalDamage(this.mc.field_1724, entity.method_73189())).max();
      return crystalDamage.orElse((double)0.0F);
   }

   private boolean isSafe() {
      return this.getHealth() > (float)(Integer)this.stepHealth.get() && (double)this.getHealth() - this.getExplosionDamage() > (double)(Integer)this.stepHealth.get();
   }

   private boolean isSaferThanWith(double damage) {
      return this.isSafe() || this.getExplosionDamage() - damage <= (double)0.0F;
   }

   private double getMaxSafeHeight() {
      if (!(Boolean)this.safeStep.get()) {
         return (Double)this.height.get();
      } else {
         double max = (Double)this.height.get();
         double h = (double)0.0F;
         double currentDamage = this.getExplosionDamage();
         class_238 initial = this.mc.field_1724.method_5829();
         class_243 inputOffset = this.mc.field_1724.method_5720();
         class_241 input = this.mc.field_1724.field_3913.method_3128();
         ((IVec3d)inputOffset).meteor$setY((double)0.0F);
         inputOffset = inputOffset.method_1029().method_1021(1.2);
         double zdot = inputOffset.field_1350;
         double xdot = inputOffset.field_1352;
         inputOffset = new class_243((double)input.field_1342 * xdot + (double)input.field_1343 * zdot, (double)0.0F, (double)input.field_1343 * xdot + (double)input.field_1342 * zdot);

         for(int i = 1; (double)i < max; ++i) {
            this.mc.field_1724.method_5857(initial.method_989((double)0.0F, (double)i, (double)0.0F));
            if (!this.isSaferThanWith(currentDamage)) {
               this.mc.field_1724.method_5857(initial);
               return h;
            }

            this.mc.field_1724.method_5857(this.mc.field_1724.method_5829().method_997(inputOffset));
            if (!this.isSaferThanWith(currentDamage)) {
               this.mc.field_1724.method_5857(initial);
               return h;
            }

            ++h;
         }

         this.mc.field_1724.method_5857(initial.method_989((double)0.0F, max, (double)0.0F));
         if (this.isSaferThanWith(currentDamage)) {
            h = max;
         }

         this.mc.field_1724.method_5857(initial);
         return h;
      }
   }

   public static enum ActiveWhen {
      Always,
      Sneaking,
      NotSneaking;

      // $FF: synthetic method
      private static ActiveWhen[] $values() {
         return new ActiveWhen[]{Always, Sneaking, NotSneaking};
      }
   }
}
