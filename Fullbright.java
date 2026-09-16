package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.StatusEffectInstanceAccessor;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1944;
import net.minecraft.class_7923;

public class Fullbright extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<Mode> mode;
   public final Setting<class_1944> lightType;
   private final Setting<Integer> minimumLightLevel;

   public Fullbright() {
      super(Categories.Render, "fullbright", "Lights up your world!");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The mode to use for Fullbright.")).defaultValue(Fullbright.Mode.Gamma)).onChanged((mode) -> {
         if (this.isActive()) {
            if (mode != Fullbright.Mode.Potion) {
               this.disableNightVision();
            }

            if (this.mc.field_1769 != null) {
               this.mc.field_1769.method_3279();
            }
         }

      })).build());
      this.lightType = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("light-type")).description("Which type of light to use for Luminance mode.")).defaultValue(class_1944.field_9282)).visible(() -> this.mode.get() == Fullbright.Mode.Luminance)).onChanged((integer) -> {
         if (this.mc.field_1769 != null && this.isActive()) {
            this.mc.field_1769.method_3279();
         }

      })).build());
      this.minimumLightLevel = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("minimum-light-level")).description("Minimum light level when using Luminance mode.")).visible(() -> this.mode.get() == Fullbright.Mode.Luminance)).defaultValue(8)).range(0, 15).sliderMax(15).onChanged((integer) -> {
         if (this.mc.field_1769 != null && this.isActive()) {
            this.mc.field_1769.method_3279();
         }

      })).build());
   }

   public void onActivate() {
      if (this.mode.get() == Fullbright.Mode.Luminance) {
         this.mc.field_1769.method_3279();
      }

   }

   public void onDeactivate() {
      if (this.mode.get() == Fullbright.Mode.Luminance) {
         this.mc.field_1769.method_3279();
      } else if (this.mode.get() == Fullbright.Mode.Potion) {
         this.disableNightVision();
      }

   }

   public int getLuminance(class_1944 type) {
      return this.isActive() && this.mode.get() == Fullbright.Mode.Luminance && type == this.lightType.get() ? (Integer)this.minimumLightLevel.get() : 0;
   }

   public boolean getGamma() {
      return this.isActive() && this.mode.get() == Fullbright.Mode.Gamma;
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.field_1724 != null && ((Mode)this.mode.get()).equals(Fullbright.Mode.Potion)) {
         if (this.mc.field_1724.method_6059(class_7923.field_41174.method_47983((class_1291)class_1294.field_5925.comp_349()))) {
            class_1293 instance = this.mc.field_1724.method_6112(class_7923.field_41174.method_47983((class_1291)class_1294.field_5925.comp_349()));
            if (instance != null && instance.method_5584() < 420) {
               ((StatusEffectInstanceAccessor)instance).meteor$setDuration(420);
            }
         } else {
            this.mc.field_1724.method_6092(new class_1293(class_7923.field_41174.method_47983((class_1291)class_1294.field_5925.comp_349()), 420, 0));
         }

      }
   }

   private void disableNightVision() {
      if (this.mc.field_1724 != null) {
         if (this.mc.field_1724.method_6059(class_7923.field_41174.method_47983((class_1291)class_1294.field_5925.comp_349()))) {
            this.mc.field_1724.method_6016(class_7923.field_41174.method_47983((class_1291)class_1294.field_5925.comp_349()));
         }

      }
   }

   public static enum Mode {
      Gamma,
      Potion,
      Luminance;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Gamma, Potion, Luminance};
      }
   }
}
