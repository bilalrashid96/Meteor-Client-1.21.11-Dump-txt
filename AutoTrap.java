package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class AutoTrap extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Double> placeRange;
   private final Setting<Double> placeWallsRange;
   private final Setting<SortPriority> priority;
   private final Setting<Double> targetRange;
   private final Setting<Integer> delay;
   private final Setting<Integer> blocksPerTick;
   private final Setting<TopMode> topPlacement;
   private final Setting<BottomMode> bottomPlacement;
   private final Setting<Boolean> selfToggle;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<SettingColor> nextSideColor;
   private final Setting<SettingColor> nextLineColor;
   private final List<class_2338> placePositions;
   private class_1657 target;
   private boolean placed;
   private int timer;

   public AutoTrap() {
      super(Categories.Combat, "auto-trap", "Traps people in a box to prevent them from moving.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("whitelist")).description("Which blocks to use.")).defaultValue(class_2246.field_10540, class_2246.field_22423).build());
      this.placeRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("The range at which blocks can be placed.")).defaultValue((double)4.0F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.placeWallsRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("walls-range")).description("Range in which to place when behind blocks.")).defaultValue((double)4.0F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-priority")).description("How to select the player to target.")).defaultValue(SortPriority.LowestHealth)).build());
      this.targetRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("The maximum distance to target players.")).defaultValue((double)3.0F).min((double)0.0F).sliderMax((double)10.0F).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("How many ticks between block placements.")).defaultValue(1)).build());
      this.blocksPerTick = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("blocks-per-tick")).description("How many blocks to place in one tick.")).defaultValue(1)).min(1).build());
      this.topPlacement = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("top-blocks")).description("Which blocks to place on the top half of the target.")).defaultValue(AutoTrap.TopMode.Full)).build());
      this.bottomPlacement = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("bottom-blocks")).description("Which blocks to place on the bottom half of the target.")).defaultValue(AutoTrap.BottomMode.Platform)).build());
      this.selfToggle = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("self-toggle")).description("Turns off after placing all blocks.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates towards blocks when placing.")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both);
      Setting var10003 = this.render;
      Objects.requireNonNull(var10003);
      this.shapeMode = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232, 10)).visible(() -> (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).sides())).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232)).visible(() -> (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).lines())).build());
      this.nextSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-side-color")).description("The side color of the next block to be placed.")).defaultValue(new SettingColor(227, 196, 245, 10)).visible(() -> (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).sides())).build());
      this.nextLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-line-color")).description("The line color of the next block to be placed.")).defaultValue(new SettingColor(5, 139, 221)).visible(() -> (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).lines())).build());
      this.placePositions = new ArrayList();
   }

   public void onActivate() {
      this.target = null;
      this.placePositions.clear();
      this.timer = 0;
      this.placed = false;
   }

   public void onDeactivate() {
      this.placePositions.clear();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if ((Boolean)this.selfToggle.get() && this.placed && this.placePositions.isEmpty()) {
         this.placed = false;
         this.toggle();
      } else {
         FindItemResult block = InvUtils.findInHotbar((Predicate)((itemStack) -> ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()))));
         if (block.found()) {
            if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
               this.target = TargetUtils.getPlayerTarget((Double)this.targetRange.get(), this.priority.get());
               if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
                  return;
               }
            }

            this.fillPlaceArray(this.target);
            if (this.timer >= (Integer)this.delay.get() && !this.placePositions.isEmpty()) {
               int placedCount = 0;

               for(class_2338 placePosition : this.placePositions) {
                  if (placedCount < (Integer)this.blocksPerTick.get() && BlockUtils.place(placePosition, block, (Boolean)this.rotate.get(), 50, true)) {
                     this.placed = true;
                     ++placedCount;
                  }
               }

               this.timer = 0;
            } else {
               ++this.timer;
            }

         }
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get() && !this.placePositions.isEmpty()) {
         for(int i = 0; i < this.placePositions.size(); ++i) {
            boolean isNext = i < (Integer)this.blocksPerTick.get();
            Color side = isNext ? (Color)this.nextSideColor.get() : (Color)this.sideColor.get();
            Color line = isNext ? (Color)this.nextLineColor.get() : (Color)this.lineColor.get();
            event.renderer.box((class_2338)((class_2338)this.placePositions.get(i)), side, line, this.shapeMode.get(), 0);
         }

      }
   }

   private void fillPlaceArray(class_1657 target) {
      this.placePositions.clear();
      double epsilon = 1.0E-5;
      class_238 box = target.method_5829();
      List<class_2338> corners = new ArrayList();
      corners.add(class_2338.method_49637(box.field_1323, box.field_1322, box.field_1321));
      corners.add(class_2338.method_49637(box.field_1323, box.field_1322, box.field_1324 - epsilon));
      corners.add(class_2338.method_49637(box.field_1320 - epsilon, box.field_1322, box.field_1321));
      corners.add(class_2338.method_49637(box.field_1320 - epsilon, box.field_1322, box.field_1324 - epsilon));

      for(class_2338 targetPos : new LinkedHashSet(corners)) {
         switch (((TopMode)this.topPlacement.get()).ordinal()) {
            case 0:
               this.add(targetPos.method_10069(0, 2, 0));
               this.add(targetPos.method_10069(1, 1, 0));
               this.add(targetPos.method_10069(-1, 1, 0));
               this.add(targetPos.method_10069(0, 1, 1));
               this.add(targetPos.method_10069(0, 1, -1));
               break;
            case 1:
               this.add(targetPos.method_10069(0, 2, 0));
               break;
            case 2:
               this.add(targetPos.method_10069(1, 1, 0));
               this.add(targetPos.method_10069(-1, 1, 0));
               this.add(targetPos.method_10069(0, 1, 1));
               this.add(targetPos.method_10069(0, 1, -1));
         }

         switch (((BottomMode)this.bottomPlacement.get()).ordinal()) {
            case 0:
               this.add(targetPos.method_10069(0, -1, 0));
               break;
            case 1:
               this.add(targetPos.method_10069(0, -1, 0));
               this.add(targetPos.method_10069(1, -1, 0));
               this.add(targetPos.method_10069(-1, -1, 0));
               this.add(targetPos.method_10069(0, -1, 1));
               this.add(targetPos.method_10069(0, -1, -1));
               break;
            case 2:
               this.add(targetPos.method_10069(0, -1, 0));
               this.add(targetPos.method_10069(1, 0, 0));
               this.add(targetPos.method_10069(-1, 0, 0));
               this.add(targetPos.method_10069(0, 0, -1));
               this.add(targetPos.method_10069(0, 0, 1));
         }
      }

      double pX = this.mc.field_1724.method_23317();
      double pY = this.mc.field_1724.method_23318();
      double pZ = this.mc.field_1724.method_23321();
      this.placePositions.sort(Comparator.comparingDouble((value) -> Utils.squaredDistance(pX, pY, pZ, (double)value.method_10263() + (double)0.5F, (double)value.method_10264() + (double)0.5F, (double)value.method_10260() + (double)0.5F) * (double)-1.0F));
   }

   private void add(class_2338 blockPos) {
      if (!this.placePositions.contains(blockPos)) {
         if (BlockUtils.canPlace(blockPos)) {
            if (!this.isOutOfRange(blockPos)) {
               this.placePositions.add(blockPos);
            }
         }
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

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }

   public static enum TopMode {
      Full,
      Top,
      Face,
      None;

      // $FF: synthetic method
      private static TopMode[] $values() {
         return new TopMode[]{Full, Top, Face, None};
      }
   }

   public static enum BottomMode {
      Single,
      Platform,
      Full,
      None;

      // $FF: synthetic method
      private static BottomMode[] $values() {
         return new BottomMode[]{Single, Platform, Full, None};
      }
   }
}
