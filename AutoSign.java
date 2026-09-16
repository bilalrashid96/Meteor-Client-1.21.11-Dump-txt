package meteordevelopment.meteorclient.systems.modules.world;

import java.util.ArrayDeque;
import java.util.Queue;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.AbstractSignEditScreenAccessor;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2596;
import net.minecraft.class_2625;
import net.minecraft.class_2877;
import net.minecraft.class_7743;

public class AutoSign extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Integer> delay;
   private String[] text;
   private final Queue<class_2877> queue;
   private int timer;

   public AutoSign() {
      super(Categories.World, "auto-sign", "Automatically writes signs. The first sign's text will be used.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("The tick delay between sign update packets.")).defaultValue(10)).range(0, 100).sliderRange(0, 100).build());
      this.queue = new ArrayDeque();
      this.timer = 0;
   }

   public void onDeactivate() {
      this.text = null;
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.field_1724 != null && this.queue.peek() != null) {
         if (this.timer < (Integer)this.delay.get()) {
            ++this.timer;
         } else {
            this.mc.field_1724.field_3944.method_52787((class_2596)this.queue.poll());
            this.timer = 0;
         }
      } else {
         this.timer = 0;
      }
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      if (event.packet instanceof class_2877) {
         this.text = ((class_2877)event.packet).method_12508();
      }
   }

   @EventHandler
   private void onOpenScreen(OpenScreenEvent event) {
      if (event.screen instanceof class_7743 && this.text != null) {
         class_2625 sign = ((AbstractSignEditScreenAccessor)event.screen).meteor$getSign();
         this.queue.add(new class_2877(sign.method_11016(), true, this.text[0], this.text[1], this.text[2], this.text[3]));
         event.cancel();
      }
   }
}
