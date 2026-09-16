package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.DirectionAccessor;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.Dir;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class HoleFiller extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgSmart;
   private final SettingGroup sgRender;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Integer> searchRadius;
   private final Setting<Double> placeRange;
   private final Setting<Double> placeWallsRange;
   private final Setting<Boolean> doubles;
   private final Setting<Boolean> rotate;
   private final Setting<Integer> placeDelay;
   private final Setting<Integer> blocksPerTick;
   private final Setting<Boolean> smart;
   public final Setting<Keybind> forceFill;
   private final Setting<Boolean> predictMovement;
   private final Setting<Double> ticksToPredict;
   private final Setting<Boolean> ignoreSafe;
   private final Setting<Boolean> onlyMoving;
   private final Setting<Double> targetRange;
   private final Setting<Double> feetRange;
   private final Setting<Boolean> swing;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<SettingColor> nextSideColor;
   private final Setting<SettingColor> nextLineColor;
   private final List<class_1657> targets;
   private final List<Hole> holes;
   private int timer;

   public HoleFiller() {
      super(Categories.Combat, "hole-filler", "Fills holes with specified blocks.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgSmart = this.settings.createGroup("Smart");
      this.sgRender = this.settings.createGroup("Render");
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks")).description("Which blocks can be used to fill holes.")).defaultValue(class_2246.field_10540, class_2246.field_22423, class_2246.field_22108, class_2246.field_23152, class_2246.field_10343).build());
      this.searchRadius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("search-radius")).description("Horizontal radius in which to search for holes.")).defaultValue(5)).min(0).sliderMax(6).build());
      this.placeRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("How far away from the player you can place a block.")).defaultValue((double)4.5F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.placeWallsRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("walls-range")).description("How far away from the player you can place a block behind walls.")).defaultValue((double)4.5F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.doubles = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("doubles")).description("Fills double holes.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically rotates towards the holes being filled.")).defaultValue(false)).build());
      this.placeDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("The ticks delay between placement.")).defaultValue(1)).min(0).build());
      this.blocksPerTick = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("blocks-per-tick")).description("How many blocks to place in one tick.")).defaultValue(3)).min(1).build());
      this.smart = this.sgSmart.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smart")).description("Take more factors into account before filling a hole.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgSmart;
      KeybindSetting.Builder var10002 = (KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("force-fill")).description("Fills all holes around you regardless of target checks.")).defaultValue(Keybind.none());
      Setting var10003 = this.smart;
      Objects.requireNonNull(var10003);
      this.forceFill = var10001.add(((KeybindSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgSmart;
      BoolSetting.Builder var7 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("predict-movement")).description("Predict target movement to account for ping.")).defaultValue(true);
      var10003 = this.smart;
      Objects.requireNonNull(var10003);
      this.predictMovement = var10001.add(((BoolSetting.Builder)var7.visible(var10003::get)).build());
      this.ticksToPredict = this.sgSmart.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("ticks-to-predict")).description("How many ticks ahead we should predict for.")).defaultValue((double)10.0F).min((double)1.0F).sliderMax((double)30.0F).visible(() -> (Boolean)this.smart.get() && (Boolean)this.predictMovement.get())).build());
      var10001 = this.sgSmart;
      var7 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-safe")).description("Ignore players in safe holes.")).defaultValue(true);
      var10003 = this.smart;
      Objects.requireNonNull(var10003);
      this.ignoreSafe = var10001.add(((BoolSetting.Builder)var7.visible(var10003::get)).build());
      var10001 = this.sgSmart;
      var7 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-moving")).description("Ignore players if they're standing still.")).defaultValue(true);
      var10003 = this.smart;
      Objects.requireNonNull(var10003);
      this.onlyMoving = var10001.add(((BoolSetting.Builder)var7.visible(var10003::get)).build());
      var10001 = this.sgSmart;
      DoubleSetting.Builder var10 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("How far away to target players.")).defaultValue((double)7.0F).min((double)0.0F).sliderMin((double)1.0F).sliderMax((double)10.0F);
      var10003 = this.smart;
      Objects.requireNonNull(var10003);
      this.targetRange = var10001.add(((DoubleSetting.Builder)var10.visible(var10003::get)).build());
      var10001 = this.sgSmart;
      var10 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("feet-range")).description("How far from a hole a player's feet must be to fill it.")).defaultValue((double)1.5F).min((double)0.0F).sliderMax((double)4.0F);
      var10003 = this.smart;
      Objects.requireNonNull(var10003);
      this.feetRange = var10001.add(((DoubleSetting.Builder)var10.visible(var10003::get)).build());
      this.swing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing")).description("Swing the player's hand when placing.")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var12 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both);
      var10003 = this.render;
      Objects.requireNonNull(var10003);
      this.shapeMode = var10001.add(((EnumSetting.Builder)var12.visible(var10003::get)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232, 10)).visible(() -> (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).sides())).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232)).visible(() -> (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).lines())).build());
      this.nextSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-side-color")).description("The side color of the next block to be placed.")).defaultValue(new SettingColor(227, 196, 245, 10)).visible(() -> (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).sides())).build());
      this.nextLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-line-color")).description("The line color of the next block to be placed.")).defaultValue(new SettingColor(5, 139, 221)).visible(() -> (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).lines())).build());
      this.targets = new ArrayList();
      this.holes = new ArrayList();
   }

   public void onActivate() {
      this.timer = 0;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if ((Boolean)this.smart.get()) {
         this.setTargets();
      }

      this.holes.clear();
      FindItemResult block = InvUtils.findInHotbar((Predicate)((itemStack) -> ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()))));
      if (block.found()) {
         BlockIterator.register((Integer)this.searchRadius.get(), (Integer)this.searchRadius.get(), (blockPos, blockState) -> {
            if (this.validHole(blockPos)) {
               int surroundBlocks = 0;
               class_2350 air = null;

               for(class_2350 direction : class_2350.values()) {
                  if (direction != class_2350.field_11036) {
                     class_2680 state = this.mc.field_1687.method_8320(blockPos.method_10093(direction));
                     if (state.method_26204().method_9520() >= 600.0F) {
                        ++surroundBlocks;
                     } else {
                        if (direction == class_2350.field_11033) {
                           return;
                        }

                        if (this.validHole(blockPos.method_10093(direction)) && air == null) {
                           for(class_2350 dir : class_2350.values()) {
                              if (dir != direction.method_10153() && dir != class_2350.field_11036) {
                                 class_2680 state1 = this.mc.field_1687.method_8320(blockPos.method_10093(direction).method_10093(dir));
                                 if (!(state1.method_26204().method_9520() >= 600.0F)) {
                                    return;
                                 }

                                 ++surroundBlocks;
                              }
                           }

                           air = direction;
                        }
                     }

                     if (surroundBlocks == 5 && air == null) {
                        this.holes.add(new Hole(blockPos, (byte)0));
                     } else if (surroundBlocks == 8 && (Boolean)this.doubles.get() && air != null) {
                        this.holes.add(new Hole(blockPos, Dir.get(air)));
                     }
                  }
               }

            }
         });
         BlockIterator.after(() -> {
            if (this.timer <= 0 && !this.holes.isEmpty()) {
               int placedCount = 0;

               for(Hole hole : this.holes) {
                  if (placedCount < (Integer)this.blocksPerTick.get() && BlockUtils.place(hole.blockPos, block, (Boolean)this.rotate.get(), 10, (Boolean)this.swing.get(), true)) {
                     ++placedCount;
                  }
               }

               this.timer = (Integer)this.placeDelay.get();
            }
         });
         --this.timer;
      }
   }

   @EventHandler(
      priority = 100
   )
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get() && !this.holes.isEmpty()) {
         for(int i = 0; i < this.holes.size(); ++i) {
            boolean isNext = i < (Integer)this.blocksPerTick.get();
            Color side = isNext ? (Color)this.nextSideColor.get() : (Color)this.sideColor.get();
            Color line = isNext ? (Color)this.nextLineColor.get() : (Color)this.lineColor.get();
            Hole hole = (Hole)this.holes.get(i);
            event.renderer.box((class_2338)hole.blockPos, side, line, this.shapeMode.get(), hole.exclude);
         }

      }
   }

   private boolean validHole(class_2338 blockPos) {
      if (!BlockUtils.canPlace(blockPos)) {
         return false;
      } else if (!this.mc.field_1687.method_8320(blockPos.method_10084()).method_45474()) {
         return false;
      } else if (this.isOutOfRange(blockPos)) {
         return false;
      } else {
         return (Boolean)this.smart.get() && !((Keybind)this.forceFill.get()).isPressed() ? this.targets.stream().anyMatch((target) -> target.method_23318() > (double)blockPos.method_10264() && this.isCloseToHolePos(target, blockPos)) : true;
      }
   }

   private void setTargets() {
      this.targets.clear();

      for(class_1657 player : this.mc.field_1687.method_18456()) {
         if (!(player.method_5858(this.mc.field_1724) > Math.pow((Double)this.targetRange.get(), (double)2.0F)) && !player.method_68878() && player != this.mc.field_1724 && !player.method_29504() && Friends.get().shouldAttack(player) && (!(Boolean)this.ignoreSafe.get() || !this.isSurrounded(player)) && (!(Boolean)this.onlyMoving.get() || player.method_23317() - player.field_6014 == (double)0.0F && player.method_23318() - player.field_6036 == (double)0.0F && player.method_23321() - player.field_5969 == (double)0.0F)) {
            this.targets.add(player);
         }
      }

   }

   private boolean isSurrounded(class_1657 target) {
      for(class_2350 dir : DirectionAccessor.meteor$getHorizontal()) {
         class_2338 blockPos = target.method_24515().method_10093(dir);
         class_2248 block = this.mc.field_1687.method_8320(blockPos).method_26204();
         if (block.method_9520() < 600.0F) {
            return false;
         }
      }

      return true;
   }

   private boolean isOutOfRange(class_2338 blockPos) {
      class_243 pos = blockPos.method_46558().method_1031((double)0.0F, 0.499, (double)0.0F);
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

   private boolean isCloseToHolePos(class_1657 target, class_2338 blockPos) {
      class_243 pos = target.method_73189();
      if ((Boolean)this.predictMovement.get()) {
         double dx = target.method_23317() - target.field_6014;
         double dy = target.method_23318() - target.field_6036;
         double dz = target.method_23321() - target.field_5969;
         pos = pos.method_1031(dx * (Double)this.ticksToPredict.get(), dy * (Double)this.ticksToPredict.get(), dz * (Double)this.ticksToPredict.get());
      }

      double i = pos.field_1352 - ((double)blockPos.method_10263() + (double)0.5F);
      double j = pos.field_1351 - ((double)blockPos.method_10264() + (double)1.0F);
      double k = pos.field_1350 - ((double)blockPos.method_10260() + (double)0.5F);
      double distance = Math.sqrt(i * i + j * j + k * k);
      return distance < (Double)this.feetRange.get();
   }

   private static class Hole {
      private final class_2338.class_2339 blockPos = new class_2338.class_2339();
      private final byte exclude;

      public Hole(class_2338 blockPos, byte exclude) {
         this.blockPos.method_10101(blockPos);
         this.exclude = exclude;
      }
   }
}
