package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Objects;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerInteractEntityC2SPacket;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_2596;
import net.minecraft.class_2824;
import net.minecraft.class_2828;
import net.minecraft.class_2879;
import net.minecraft.class_9362;
import net.minecraft.class_2824.class_5907;

public class Criticals extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgMace;
   private final Setting<Mode> mode;
   private final Setting<Boolean> ka;
   private final Setting<Boolean> mace;
   private final Setting<Double> extraHeight;
   private class_2824 attackPacket;
   private class_2879 swingPacket;
   private boolean sendPackets;
   private int sendTimer;
   private double lastY;
   private boolean waitingForPeak;

   public Criticals() {
      super(Categories.Combat, "criticals", "Performs critical attacks when you hit your target.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgMace = this.settings.createGroup("Mace");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The mode on how Criticals will function.")).defaultValue(Criticals.Mode.Packet)).build());
      this.ka = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-killaura")).description("Only performs crits when using killaura.")).defaultValue(false)).visible(() -> this.mode.get() != Criticals.Mode.None)).build());
      this.mace = this.sgMace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smash-attack")).description("Will always perform smash attacks when using a mace.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgMace;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("additional-height")).description("The amount of additional height to spoof. More height means more damage.")).defaultValue((double)0.0F).min((double)0.0F).sliderRange((double)0.0F, (double)100.0F);
      Setting var10003 = this.mace;
      Objects.requireNonNull(var10003);
      this.extraHeight = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
   }

   public void onActivate() {
      this.attackPacket = null;
      this.swingPacket = null;
      this.sendPackets = false;
      this.sendTimer = 0;
      this.lastY = (double)0.0F;
      this.waitingForPeak = false;
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof IPlayerInteractEntityC2SPacket packet) {
         if (packet.meteor$getType() == class_5907.field_29172) {
            if ((Boolean)this.mace.get() && this.mc.field_1724.method_6047().method_7909() instanceof class_9362) {
               if (this.mc.field_1724.method_6128()) {
                  return;
               }

               this.sendPacket((double)0.0F);
               this.sendPacket(1.501 + (Double)this.extraHeight.get());
               this.sendPacket((double)0.0F);
               return;
            }

            if (this.skipCrit()) {
               return;
            }

            class_1297 entity = packet.meteor$getEntity();
            if (!(entity instanceof class_1309) || entity != ((KillAura)Modules.get().get(KillAura.class)).getTarget() && (Boolean)this.ka.get()) {
               return;
            }

            switch (((Mode)this.mode.get()).ordinal()) {
               case 1:
                  this.sendPacket((double)0.0625F);
                  this.sendPacket((double)0.0F);
                  return;
               case 2:
                  this.sendPacket(8.0E-7);
                  this.sendPacket((double)0.0F);
                  return;
               case 3:
                  this.sendPacket(0.11);
                  this.sendPacket(0.1100013579);
                  this.sendPacket(1.3579E-6);
                  return;
               case 4:
               case 5:
                  if (!this.sendPackets) {
                     this.sendPackets = true;
                     this.attackPacket = (class_2824)event.packet;
                     if (this.mode.get() == Criticals.Mode.Jump) {
                        this.mc.field_1724.method_6043();
                        this.waitingForPeak = true;
                        this.lastY = this.mc.field_1724.method_23318();
                     } else {
                        ((IVec3d)this.mc.field_1724.method_18798()).meteor$setY((double)0.25F);
                        this.sendTimer = 4;
                     }

                     event.cancel();
                  }

                  return;
               default:
                  return;
            }
         }
      }

      if (event.packet instanceof class_2879 && this.mode.get() != Criticals.Mode.Packet) {
         if (this.skipCrit()) {
            return;
         }

         if (this.sendPackets && this.swingPacket == null) {
            this.swingPacket = (class_2879)event.packet;
            event.cancel();
         }
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.sendPackets) {
         if (this.mode.get() == Criticals.Mode.Jump && this.waitingForPeak) {
            double currentY = this.mc.field_1724.method_23318();
            if (currentY <= this.lastY) {
               this.waitingForPeak = false;
               this.sendTimer = 0;
            }

            this.lastY = currentY;
            return;
         }

         if (this.sendTimer <= 0) {
            if (this.attackPacket == null || this.swingPacket == null) {
               this.sendPackets = false;
               return;
            }

            this.mc.method_1562().method_52787(this.attackPacket);
            this.mc.method_1562().method_52787(this.swingPacket);
            this.attackPacket = null;
            this.swingPacket = null;
            this.sendPackets = false;
         } else {
            --this.sendTimer;
         }
      }

   }

   private void sendPacket(double height) {
      double x = this.mc.field_1724.method_23317();
      double y = this.mc.field_1724.method_23318();
      double z = this.mc.field_1724.method_23321();
      class_2828 packet = new class_2828.class_2829(x, y + height, z, false, false);
      ((IPlayerMoveC2SPacket)packet).meteor$setTag(1337);
      this.mc.field_1724.field_3944.method_52787(packet);
   }

   private boolean skipCrit() {
      if (!EntityUtils.isInCobweb(this.mc.field_1724) || this.mode.get() != Criticals.Mode.Jump && this.mode.get() != Criticals.Mode.MiniJump) {
         return !this.mc.field_1724.method_24828() || this.mc.field_1724.method_5869() || this.mc.field_1724.method_5771() || this.mc.field_1724.method_6101();
      } else {
         return true;
      }
   }

   public String getInfoString() {
      return ((Mode)this.mode.get()).name();
   }

   public static enum Mode {
      None,
      Packet,
      UpdatedNCP,
      OldNCP,
      Jump,
      MiniJump;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{None, Packet, UpdatedNCP, OldNCP, Jump, MiniJump};
      }
   }
}
