package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_2848;
import net.minecraft.class_2848.class_2849;

public class AntiHunger extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> sprint;
   private final Setting<Boolean> onGround;
   private boolean lastOnGround;
   private boolean ignorePacket;

   public AntiHunger() {
      super(Categories.Player, "anti-hunger", "Reduces (does NOT remove) hunger consumption.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sprint = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sprint")).description("Spoofs sprinting packets.")).defaultValue(true)).build());
      this.onGround = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("on-ground")).description("Spoofs the onGround flag.")).defaultValue(true)).build());
   }

   public void onActivate() {
      this.lastOnGround = this.mc.field_1724.method_24828();
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      if (this.ignorePacket && event.packet instanceof class_2828) {
         this.ignorePacket = false;
      } else if (!this.mc.field_1724.method_5765() && !this.mc.field_1724.method_5799() && !this.mc.field_1724.method_5869()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2848) {
            class_2848 packet = (class_2848)var3;
            if ((Boolean)this.sprint.get() && packet.method_12365() == class_2849.field_12981) {
               event.cancel();
            }
         }

         var3 = event.packet;
         if (var3 instanceof class_2828) {
            class_2828 packet = (class_2828)var3;
            if ((Boolean)this.onGround.get() && this.mc.field_1724.method_24828() && this.mc.field_1724.field_6017 <= (double)0.0F && !this.mc.field_1761.method_2923()) {
               ((PlayerMoveC2SPacketAccessor)packet).meteor$setOnGround(false);
            }
         }

      }
   }

   @EventHandler
   private void onTick(SendMovementPacketsEvent.Pre event) {
      if (this.mc.field_1724.method_24828() && !this.lastOnGround && (Boolean)this.onGround.get()) {
         this.ignorePacket = true;
      }

      this.lastOnGround = this.mc.field_1724.method_24828();
   }
}
