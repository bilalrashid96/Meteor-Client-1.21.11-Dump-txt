package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2761;

public class TimeChanger extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> time;
   long oldTime;

   public TimeChanger() {
      super(Categories.Render, "time-changer", "Makes you able to set a custom time.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.time = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("time")).description("The specified time to be set.")).defaultValue((double)0.0F).sliderRange((double)-20000.0F, (double)20000.0F).build());
   }

   public void onActivate() {
      this.oldTime = this.mc.field_1687.method_75260();
   }

   public void onDeactivate() {
      this.mc.field_1687.method_28104().method_165(this.oldTime);
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if (event.packet instanceof class_2761) {
         this.oldTime = ((class_2761)event.packet).comp_3220();
         event.cancel();
      }

   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      this.mc.field_1687.method_28104().method_165(((Double)this.time.get()).longValue());
   }
}
