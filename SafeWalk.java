package meteordevelopment.meteorclient.systems.modules.movement;

import java.util.Objects;
import meteordevelopment.meteorclient.events.entity.player.ClipAtLedgeEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_10185;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2902.class_2903;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class SafeWalk extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Integer> fallDistance;
   private final Setting<Boolean> sneak;
   private final Setting<Boolean> safeSneak;
   private final Setting<Boolean> sneakSprint;
   private final Setting<Double> edgeDistance;
   private final Setting<Boolean> renderEdgeDistance;
   private final Setting<Boolean> renderPlayerBox;

   public SafeWalk() {
      super(Categories.Movement, "safe-walk", "Prevents you from walking off blocks.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.fallDistance = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("minimum-fall-distance")).description("The minimum number of blocks you are expected to fall before the module activates.")).defaultValue(1)).min(1).build());
      this.sneak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sneak")).description("Sneak when approaching edge of block.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("safe-sneak")).description("Prevent you from falling if sneak doesn't trigger correctly.")).defaultValue(true);
      Setting var10003 = this.sneak;
      Objects.requireNonNull(var10003);
      this.safeSneak = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sneak-on-sprint")).description("Sneak even when sprinting at the block edge.")).defaultValue(true);
      var10003 = this.sneak;
      Objects.requireNonNull(var10003);
      this.sneakSprint = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      DoubleSetting.Builder var5 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("edge-distance")).description("Distance offset before reaching an edge.")).defaultValue(0.3).sliderRange((double)0.0F, 0.3).decimalPlaces(2);
      var10003 = this.sneak;
      Objects.requireNonNull(var10003);
      this.edgeDistance = var10001.add(((DoubleSetting.Builder)var5.visible(var10003::get)).build());
      var10001 = this.sgRender;
      BoolSetting.Builder var6 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Render edge distance helper.")).defaultValue(false);
      var10003 = this.sneak;
      Objects.requireNonNull(var10003);
      this.renderEdgeDistance = var10001.add(((BoolSetting.Builder)var6.visible(var10003::get)).build());
      this.renderPlayerBox = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-player-box")).description("Render player box helper.")).defaultValue(false)).visible(() -> (Boolean)this.sneak.get() && (Boolean)this.renderEdgeDistance.get())).build());
   }

   @EventHandler
   private void onClipAtLedge(ClipAtLedgeEvent event) {
      if ((Integer)this.fallDistance.get() > 1) {
         int surface = this.mc.field_1687.method_8500(this.mc.field_1724.method_24515()).method_12032(class_2903.field_13197).method_12603(this.mc.field_1724.method_31477() & 15, this.mc.field_1724.method_31479() & 15);
         if (this.mc.field_1724.method_31478() >= surface) {
            if (this.mc.field_1724.method_31478() - surface < (Integer)this.fallDistance.get()) {
               return;
            }
         } else {
            class_3965 raycastResult = this.mc.field_1687.method_17742(new class_3959(this.mc.field_1724.method_73189(), new class_243(this.mc.field_1724.method_23317(), (double)this.mc.field_1687.method_31607(), this.mc.field_1724.method_23321()), class_3960.field_17558, class_242.field_36338, this.mc.field_1724));
            if (raycastResult.method_17783() != class_240.field_1333 && (int)(this.mc.field_1724.method_23318() - (double)raycastResult.method_17777().method_10084().method_10264()) < (Integer)this.fallDistance.get()) {
               return;
            }
         }
      }

      if ((Boolean)this.sneak.get()) {
         boolean closeToEdge = false;
         boolean isSprinting = !(Boolean)this.sneakSprint.get() && this.mc.field_1690.field_1867.method_1434();
         class_238 playerBox = this.mc.field_1724.method_5829();
         class_238 adjustedBox = this.getAdjustedPlayerBox(playerBox);
         if (this.mc.field_1687.method_8587(this.mc.field_1724, adjustedBox) && this.mc.field_1724.method_24828()) {
            closeToEdge = true;
         }

         if (!isSprinting) {
            if (closeToEdge) {
               this.mc.field_1724.field_3913.field_54155 = new class_10185(this.mc.field_1724.field_3913.field_54155.comp_3159(), this.mc.field_1724.field_3913.field_54155.comp_3160(), this.mc.field_1724.field_3913.field_54155.comp_3161(), this.mc.field_1724.field_3913.field_54155.comp_3162(), this.mc.field_1724.field_3913.field_54155.comp_3163(), true, this.mc.field_1724.field_3913.field_54155.comp_3165());
            } else if ((Boolean)this.safeSneak.get()) {
               event.setClip(true);
            }
         }
      } else if (!this.mc.field_1724.method_5715()) {
         event.setClip(true);
      }

   }

   private class_238 getAdjustedPlayerBox(class_238 playerBox) {
      return playerBox.method_1012((double)0.0F, (double)(-this.mc.field_1724.method_49476()), (double)0.0F).method_1009(-(Double)this.edgeDistance.get(), (double)0.0F, -(Double)this.edgeDistance.get());
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.sneak.get() && (Boolean)this.renderEdgeDistance.get()) {
         class_238 playerBox = this.mc.field_1724.method_5829();
         class_238 adjustedBox = this.getAdjustedPlayerBox(playerBox);
         event.renderer.box((class_238)adjustedBox, Color.BLUE, Color.RED, ShapeMode.Lines, 0);
         if ((Boolean)this.renderPlayerBox.get()) {
            event.renderer.box((class_238)playerBox, Color.BLUE, Color.GREEN, ShapeMode.Lines, 0);
         }
      }

   }
}
