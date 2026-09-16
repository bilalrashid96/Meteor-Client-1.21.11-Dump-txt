package meteordevelopment.meteorclient.systems.modules.combat;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1802;
import net.minecraft.class_2596;
import net.minecraft.class_2663;

public class AutoTotem extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Mode> mode;
   private final Setting<Integer> delay;
   private final Setting<Integer> health;
   private final Setting<Boolean> elytra;
   private final Setting<Boolean> fall;
   private final Setting<Boolean> explosion;
   public boolean locked;
   private int totems;
   private int ticks;

   public AutoTotem() {
      super(Categories.Combat, "auto-totem", "Automatically equips a totem in your offhand.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Determines when to hold a totem, strict will always hold.")).defaultValue(AutoTotem.Mode.Smart)).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("The ticks between slot movements.")).defaultValue(0)).min(0).build());
      this.health = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("health")).description("The health to hold a totem at.")).defaultValue(10)).range(0, 36).sliderMax(36).visible(() -> this.mode.get() == AutoTotem.Mode.Smart)).build());
      this.elytra = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("elytra")).description("Will always hold a totem when flying with elytra.")).defaultValue(true)).visible(() -> this.mode.get() == AutoTotem.Mode.Smart)).build());
      this.fall = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fall")).description("Will hold a totem when fall damage could kill you.")).defaultValue(true)).visible(() -> this.mode.get() == AutoTotem.Mode.Smart)).build());
      this.explosion = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("explosion")).description("Will hold a totem when explosion damage could kill you.")).defaultValue(true)).visible(() -> this.mode.get() == AutoTotem.Mode.Smart)).build());
   }

   @EventHandler(
      priority = 200
   )
   private void onTick(TickEvent.Pre event) {
      FindItemResult result = InvUtils.find(class_1802.field_8288);
      this.totems = result.count();
      if (this.totems <= 0) {
         this.locked = false;
      } else if (this.ticks >= (Integer)this.delay.get()) {
         boolean low = this.mc.field_1724.method_6032() + this.mc.field_1724.method_6067() - PlayerUtils.possibleHealthReductions((Boolean)this.explosion.get(), (Boolean)this.fall.get()) <= (float)(Integer)this.health.get();
         boolean ely = (Boolean)this.elytra.get() && this.mc.field_1724.method_6118(class_1304.field_6174).method_7909() == class_1802.field_8833 && this.mc.field_1724.method_6128();
         this.locked = this.mode.get() == AutoTotem.Mode.Strict || this.mode.get() == AutoTotem.Mode.Smart && (low || ely);
         if (this.locked && this.mc.field_1724.method_6079().method_7909() != class_1802.field_8288) {
            InvUtils.move().from(result.slot()).toOffhand();
         }

         this.ticks = 0;
         return;
      }

      ++this.ticks;
   }

   @EventHandler(
      priority = 100
   )
   private void onReceivePacket(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2663 p) {
         if (p.method_11470() == 35) {
            class_1297 entity = p.method_11469(this.mc.field_1687);
            if (entity != null && entity.equals(this.mc.field_1724)) {
               this.ticks = 0;
            }
         }
      }
   }

   public boolean isLocked() {
      return this.isActive() && this.locked;
   }

   public String getInfoString() {
      return String.valueOf(this.totems);
   }

   public static enum Mode {
      Smart,
      Strict;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Smart, Strict};
      }
   }
}
