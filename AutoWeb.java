package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class AutoWeb extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Double> placeRange;
   private final Setting<Double> placeWallsRange;
   private final Setting<SortPriority> priority;
   private final Setting<Double> targetRange;
   private final Setting<Boolean> predictMovement;
   private final Setting<Double> ticksToPredict;
   private final Setting<Boolean> doubles;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final List<class_2338> placePositions;
   private class_1657 target;

   public AutoWeb() {
      super(Categories.Combat, "auto-web", "Automatically places webs on other players.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.placeRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("The range at which webs can be placed.")).defaultValue((double)4.0F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.placeWallsRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("walls-range")).description("Range in which to place webs when behind blocks.")).defaultValue((double)4.0F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-priority")).description("How to filter targets within range.")).defaultValue(SortPriority.LowestDistance)).build());
      this.targetRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("The maximum distance to target players.")).defaultValue((double)10.0F).min((double)0.0F).sliderMax((double)30.0F).build());
      this.predictMovement = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("predict-movement")).description("Predict target movement to account for ping.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("ticks-to-predict")).description("How many ticks ahead we should predict for.")).defaultValue((double)10.0F).min((double)1.0F).sliderMax((double)30.0F);
      Setting var10003 = this.predictMovement;
      Objects.requireNonNull(var10003);
      this.ticksToPredict = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.doubles = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("doubles")).description("Places webs in the target's upper hitbox as well as the lower hitbox.")).defaultValue(false)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates towards the webs when placing.")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where webs are placed.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var2 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both);
      var10003 = this.render;
      Objects.requireNonNull(var10003);
      this.shapeMode = var10001.add(((EnumSetting.Builder)var2.visible(var10003::get)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the placed web rendering.")).defaultValue(new SettingColor(239, 231, 244, 31)).visible(() -> (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).sides())).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the placed web rendering.")).defaultValue(new SettingColor(255, 255, 255)).visible(() -> (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).lines())).build());
      this.placePositions = new ArrayList();
      this.target = null;
   }

   public void onActivate() {
      this.target = null;
   }

   public void onDeactivate() {
      this.placePositions.clear();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.placePositions.clear();
      if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
         this.target = TargetUtils.getPlayerTarget((Double)this.targetRange.get(), this.priority.get());
         if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
            return;
         }
      }

      FindItemResult webs = InvUtils.findInHotbar(class_1802.field_8786);
      if (webs.found()) {
         class_243 pos = this.target.method_73189();
         if ((Boolean)this.predictMovement.get()) {
            double dx = this.target.method_23317() - this.target.field_6014;
            double dy = this.target.method_23318() - this.target.field_6036;
            double dz = this.target.method_23321() - this.target.field_5969;
            pos = pos.method_1031(dx * (Double)this.ticksToPredict.get(), dy * (Double)this.ticksToPredict.get(), dz * (Double)this.ticksToPredict.get());
         }

         class_2338 blockPos = class_2338.method_49638(pos);
         if (this.canPlaceWebAt(blockPos)) {
            BlockUtils.place(blockPos, webs, (Boolean)this.rotate.get(), 0, false);
            this.placePositions.add(blockPos);
         }

         if ((Boolean)this.doubles.get() && this.canPlaceWebAt(blockPos.method_10084())) {
            BlockUtils.place(blockPos.method_10084(), webs, (Boolean)this.rotate.get(), 0, false);
            this.placePositions.add(blockPos.method_10084());
         }

      }
   }

   private boolean canPlaceWebAt(class_2338 blockPos) {
      if (!this.mc.field_1687.method_8320(blockPos).method_45474()) {
         return false;
      } else {
         return !this.isOutOfRange(blockPos);
      }
   }

   private boolean isOutOfRange(class_2338 blockPos) {
      class_243 pos = blockPos.method_46558();
      if (!PlayerUtils.isWithin(pos, (Double)this.placeRange.get())) {
         return true;
      } else {
         class_3959 raycastContext = new class_3959(this.mc.field_1724.method_33571(), pos, class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
         class_3965 result = this.mc.field_1687.method_17742(raycastContext);
         if (result != null && result.method_17777().equals(blockPos)) {
            return false;
         } else {
            return !PlayerUtils.isWithin(pos, (Double)this.placeWallsRange.get());
         }
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get() && !this.placePositions.isEmpty()) {
         for(class_2338 placePosition : this.placePositions) {
            event.renderer.box((class_2338)placePosition, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
         }

      }
   }
}
