package meteordevelopment.meteorclient.systems.modules.render;

import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ParticleTypeListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2394;
import net.minecraft.class_2396;
import net.minecraft.class_2398;

public class Trail extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<class_2396<?>>> particles;
   private final Setting<Boolean> pause;

   public Trail() {
      super(Categories.Render, "trail", "Renders a customizable trail behind your player.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.particles = this.sgGeneral.add(((ParticleTypeListSetting.Builder)((ParticleTypeListSetting.Builder)(new ParticleTypeListSetting.Builder()).name("particles")).description("Particles to draw.")).defaultValue(class_2398.field_22446, class_2398.field_17430).build());
      this.pause = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-when-stationary")).description("Whether or not to add particles when you are not moving.")).defaultValue(true)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (!(Boolean)this.pause.get() || this.mc.field_1724.method_23317() != this.mc.field_1724.field_6014 || this.mc.field_1724.method_23318() != this.mc.field_1724.field_6036 || this.mc.field_1724.method_23321() != this.mc.field_1724.field_5969) {
         for(class_2396<?> particleType : this.particles.get()) {
            this.mc.field_1687.method_8406((class_2394)particleType, this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321(), (double)0.0F, (double)0.0F, (double)0.0F);
         }

      }
   }
}
