package meteordevelopment.meteorclient.systems.modules.movement;

import java.util.Objects;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.EntityVelocityUpdateS2CPacketAccessor;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2743;

public class Velocity extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<Boolean> knockback;
   public final Setting<Double> knockbackHorizontal;
   public final Setting<Double> knockbackVertical;
   public final Setting<Boolean> explosions;
   public final Setting<Double> explosionsHorizontal;
   public final Setting<Double> explosionsVertical;
   public final Setting<Boolean> liquids;
   public final Setting<Double> liquidsHorizontal;
   public final Setting<Double> liquidsVertical;
   public final Setting<Boolean> entityPush;
   public final Setting<Double> entityPushAmount;
   public final Setting<Boolean> blocks;
   public final Setting<Boolean> sinking;
   public final Setting<Boolean> fishing;

   public Velocity() {
      super(Categories.Movement, "velocity", "Prevents you from being moved by external forces.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.knockback = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("knockback")).description("Modifies the amount of knockback you take from attacks.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("knockback-horizontal")).description("How much horizontal knockback you will take.")).defaultValue((double)0.0F).sliderMax((double)1.0F);
      Setting var10003 = this.knockback;
      Objects.requireNonNull(var10003);
      this.knockbackHorizontal = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("knockback-vertical")).description("How much vertical knockback you will take.")).defaultValue((double)0.0F).sliderMax((double)1.0F);
      var10003 = this.knockback;
      Objects.requireNonNull(var10003);
      this.knockbackVertical = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.explosions = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("explosions")).description("Modifies your knockback from explosions.")).defaultValue(true)).build());
      var10001 = this.sgGeneral;
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("explosions-horizontal")).description("How much velocity you will take from explosions horizontally.")).defaultValue((double)0.0F).sliderMax((double)1.0F);
      var10003 = this.explosions;
      Objects.requireNonNull(var10003);
      this.explosionsHorizontal = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("explosions-vertical")).description("How much velocity you will take from explosions vertically.")).defaultValue((double)0.0F).sliderMax((double)1.0F);
      var10003 = this.explosions;
      Objects.requireNonNull(var10003);
      this.explosionsVertical = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.liquids = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("liquids")).description("Modifies the amount you are pushed by flowing liquids.")).defaultValue(true)).build());
      var10001 = this.sgGeneral;
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("liquids-horizontal")).description("How much velocity you will take from liquids horizontally.")).defaultValue((double)0.0F).sliderMax((double)1.0F);
      var10003 = this.liquids;
      Objects.requireNonNull(var10003);
      this.liquidsHorizontal = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("liquids-vertical")).description("How much velocity you will take from liquids vertically.")).defaultValue((double)0.0F).sliderMax((double)1.0F);
      var10003 = this.liquids;
      Objects.requireNonNull(var10003);
      this.liquidsVertical = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.entityPush = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("entity-push")).description("Modifies the amount you are pushed by entities.")).defaultValue(true)).build());
      var10001 = this.sgGeneral;
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("entity-push-amount")).description("How much you will be pushed.")).defaultValue((double)0.0F).sliderMax((double)1.0F);
      var10003 = this.entityPush;
      Objects.requireNonNull(var10003);
      this.entityPushAmount = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.blocks = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("blocks")).description("Prevents you from being pushed out of blocks.")).defaultValue(true)).build());
      this.sinking = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sinking")).description("Prevents you from sinking in liquids.")).defaultValue(false)).build());
      this.fishing = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fishing")).description("Prevents you from being pulled by fishing rods.")).defaultValue(false)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if ((Boolean)this.sinking.get()) {
         if (!this.mc.field_1690.field_1903.method_1434() && !this.mc.field_1690.field_1832.method_1434()) {
            if ((this.mc.field_1724.method_5799() || this.mc.field_1724.method_5771()) && this.mc.field_1724.method_18798().field_1351 < (double)0.0F) {
               ((IVec3d)this.mc.field_1724.method_18798()).meteor$setY((double)0.0F);
            }

         }
      }
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if ((Boolean)this.knockback.get()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2743) {
            class_2743 packet = (class_2743)var3;
            if (packet.method_11818() == this.mc.field_1724.method_5628()) {
               double velX = (packet.method_73085().method_10216() - this.mc.field_1724.method_18798().field_1352) * (Double)this.knockbackHorizontal.get();
               double velY = (packet.method_73085().method_10214() - this.mc.field_1724.method_18798().field_1351) * (Double)this.knockbackVertical.get();
               double velZ = (packet.method_73085().method_10215() - this.mc.field_1724.method_18798().field_1350) * (Double)this.knockbackHorizontal.get();
               ((EntityVelocityUpdateS2CPacketAccessor)packet).meteor$setVelocity(new class_243(velX + this.mc.field_1724.method_18798().field_1352, velY + this.mc.field_1724.method_18798().field_1351, velZ + this.mc.field_1724.method_18798().field_1350));
            }
         }
      }

   }

   public double getHorizontal(Setting<Double> setting) {
      return this.isActive() ? (Double)setting.get() : (double)1.0F;
   }

   public double getVertical(Setting<Double> setting) {
      return this.isActive() ? (Double)setting.get() : (double)1.0F;
   }
}
