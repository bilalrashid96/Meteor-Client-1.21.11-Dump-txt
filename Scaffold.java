package meteordevelopment.meteorclient.systems.modules.movement;

import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
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
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1747;
import net.minecraft.class_1799;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2346;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_243;

public class Scaffold extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<List<class_2248>> blocks;
   private final Setting<ListMode> blocksFilter;
   private final Setting<Boolean> fastTower;
   private final Setting<Double> towerSpeed;
   private final Setting<Boolean> whileMoving;
   private final Setting<Boolean> onlyOnClick;
   private final Setting<Boolean> renderSwing;
   private final Setting<Boolean> autoSwitch;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> airPlace;
   private final Setting<Double> aheadDistance;
   private final Setting<Double> placeRange;
   private final Setting<Double> radius;
   private final Setting<Integer> blocksPerTick;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final class_2338.class_2339 bp;

   public Scaffold() {
      super(Categories.Movement, "scaffold", "Automatically places blocks under you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks")).description("Selected blocks.")).build());
      this.blocksFilter = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("blocks-filter")).description("How to use the block list setting")).defaultValue(Scaffold.ListMode.Blacklist)).build());
      this.fastTower = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fast-tower")).description("Whether or not to scaffold upwards faster.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("tower-speed")).description("The speed at which to tower.")).defaultValue((double)0.5F).min((double)0.0F).sliderMax((double)1.0F);
      Setting var10003 = this.fastTower;
      Objects.requireNonNull(var10003);
      this.towerSpeed = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      BoolSetting.Builder var7 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("while-moving")).description("Allows you to tower while moving.")).defaultValue(false);
      var10003 = this.fastTower;
      Objects.requireNonNull(var10003);
      this.whileMoving = var10001.add(((BoolSetting.Builder)var7.visible(var10003::get)).build());
      this.onlyOnClick = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-click")).description("Only places blocks when holding right click.")).defaultValue(false)).build());
      this.renderSwing = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing")).description("Renders your client-side swing.")).defaultValue(false)).build());
      this.autoSwitch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-switch")).description("Automatically swaps to a block before placing.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates towards the blocks being placed.")).defaultValue(true)).build());
      this.airPlace = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("air-place")).description("Allow air place. This also allows you to modify scaffold radius.")).defaultValue(false)).build());
      this.aheadDistance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("ahead-distance")).description("How far ahead to place blocks.")).defaultValue((double)0.0F).min((double)0.0F).sliderMax((double)1.0F).visible(() -> !(Boolean)this.airPlace.get())).build());
      this.placeRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("closest-block-range")).description("How far can scaffold place blocks when you are in air.")).defaultValue((double)4.0F).min((double)0.0F).sliderMax((double)8.0F).visible(() -> !(Boolean)this.airPlace.get())).build());
      var10001 = this.sgGeneral;
      DoubleSetting.Builder var8 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("radius")).description("Scaffold radius.")).defaultValue((double)0.0F).min((double)0.0F).max((double)6.0F);
      var10003 = this.airPlace;
      Objects.requireNonNull(var10003);
      this.radius = var10001.add(((DoubleSetting.Builder)var8.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      IntSetting.Builder var9 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("blocks-per-tick")).description("How many blocks to place in one tick.")).defaultValue(3)).min(1);
      var10003 = this.airPlace;
      Objects.requireNonNull(var10003);
      this.blocksPerTick = var10001.add(((IntSetting.Builder)var9.visible(var10003::get)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Whether to render blocks that have been placed.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var10 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both);
      var10003 = this.render;
      Objects.requireNonNull(var10003);
      this.shapeMode = var10001.add(((EnumSetting.Builder)var10.visible(var10003::get)).build());
      var10001 = this.sgRender;
      ColorSetting.Builder var11 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232, 10));
      var10003 = this.render;
      Objects.requireNonNull(var10003);
      this.sideColor = var10001.add(((ColorSetting.Builder)var11.visible(var10003::get)).build());
      var10001 = this.sgRender;
      var11 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232));
      var10003 = this.render;
      Objects.requireNonNull(var10003);
      this.lineColor = var10001.add(((ColorSetting.Builder)var11.visible(var10003::get)).build());
      this.bp = new class_2338.class_2339();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (!(Boolean)this.onlyOnClick.get() || this.mc.field_1690.field_1904.method_1434()) {
         class_243 vec = this.mc.field_1724.method_73189().method_1019(this.mc.field_1724.method_18798()).method_1031((double)0.0F, (double)-0.75F, (double)0.0F);
         if ((Boolean)this.airPlace.get()) {
            this.bp.method_10102(vec.method_10216(), vec.method_10214(), vec.method_10215());
         } else {
            class_243 pos = this.mc.field_1724.method_73189();
            if ((Double)this.aheadDistance.get() != (double)0.0F && !this.towering() && !this.mc.field_1687.method_8320(this.mc.field_1724.method_24515().method_10074()).method_26220(this.mc.field_1687, this.mc.field_1724.method_24515()).method_1110()) {
               class_243 dir = class_243.method_1030(0.0F, this.mc.field_1724.method_36454()).method_18805((Double)this.aheadDistance.get(), (double)0.0F, (Double)this.aheadDistance.get());
               if (this.mc.field_1690.field_1894.method_1434()) {
                  pos = pos.method_1031(dir.field_1352, (double)0.0F, dir.field_1350);
               }

               if (this.mc.field_1690.field_1881.method_1434()) {
                  pos = pos.method_1031(-dir.field_1352, (double)0.0F, -dir.field_1350);
               }

               if (this.mc.field_1690.field_1913.method_1434()) {
                  pos = pos.method_1031(dir.field_1350, (double)0.0F, -dir.field_1352);
               }

               if (this.mc.field_1690.field_1849.method_1434()) {
                  pos = pos.method_1031(-dir.field_1350, (double)0.0F, dir.field_1352);
               }
            }

            this.bp.method_10102(pos.field_1352, vec.field_1351, pos.field_1350);
         }

         if (this.mc.field_1690.field_1832.method_1434() && !this.mc.field_1690.field_1903.method_1434() && this.mc.field_1724.method_23318() + vec.field_1351 > (double)-1.0F) {
            this.bp.method_33098(this.bp.method_10264() - 1);
         }

         if (this.bp.method_10264() >= this.mc.field_1724.method_24515().method_10264()) {
            this.bp.method_33098(this.mc.field_1724.method_24515().method_10264() - 1);
         }

         class_2338 targetBlock = this.bp.method_10062();
         if (!(Boolean)this.airPlace.get() && BlockUtils.getPlaceSide(this.bp) == null) {
            class_243 pos = this.mc.field_1724.method_73189();
            pos = pos.method_1031((double)0.0F, (double)-0.98F, (double)0.0F);
            pos.method_1019(this.mc.field_1724.method_18798());
            List<class_2338> blockPosArray = new ArrayList();

            for(int x = (int)(this.mc.field_1724.method_23317() - (Double)this.placeRange.get()); (double)x < this.mc.field_1724.method_23317() + (Double)this.placeRange.get(); ++x) {
               for(int z = (int)(this.mc.field_1724.method_23321() - (Double)this.placeRange.get()); (double)z < this.mc.field_1724.method_23321() + (Double)this.placeRange.get(); ++z) {
                  for(int y = (int)Math.max((double)this.mc.field_1687.method_31607(), this.mc.field_1724.method_23318() - (Double)this.placeRange.get()); (double)y < Math.min((double)this.mc.field_1687.method_31605(), this.mc.field_1724.method_23318() + (Double)this.placeRange.get()); ++y) {
                     this.bp.method_10103(x, y, z);
                     if (BlockUtils.getPlaceSide(this.bp) != null && BlockUtils.canPlace(this.bp) && !(this.mc.field_1724.method_33571().method_1025(class_243.method_24953(this.bp.method_10093(BlockUtils.getClosestPlaceSide(this.bp)))) > (double)36.0F)) {
                        blockPosArray.add(new class_2338(this.bp));
                     }
                  }
               }
            }

            if (blockPosArray.isEmpty()) {
               return;
            }

            blockPosArray.sort(Comparator.comparingDouble((blockPos) -> blockPos.method_10262(targetBlock)));
            this.bp.method_10101((class_2382)blockPosArray.getFirst());
         }

         if ((Boolean)this.airPlace.get()) {
            List<class_2338> blocks = new ArrayList();

            for(int x = (int)((double)this.bp.method_10263() - (Double)this.radius.get()); (double)x <= (double)this.bp.method_10263() + (Double)this.radius.get(); ++x) {
               for(int z = (int)((double)this.bp.method_10260() - (Double)this.radius.get()); (double)z <= (double)this.bp.method_10260() + (Double)this.radius.get(); ++z) {
                  class_2338 blockPos = class_2338.method_49637((double)x, (double)this.bp.method_10264(), (double)z);
                  if (this.mc.field_1724.method_73189().method_1022(class_243.method_24953(blockPos)) <= (Double)this.radius.get() || x == this.bp.method_10263() && z == this.bp.method_10260()) {
                     blocks.add(blockPos);
                  }
               }
            }

            if (!blocks.isEmpty()) {
               blocks.sort(Comparator.comparingDouble(PlayerUtils::squaredDistanceTo));
               int counter = 0;

               for(class_2338 block : blocks) {
                  if (this.place(block)) {
                     ++counter;
                  }

                  if (counter >= (Integer)this.blocksPerTick.get()) {
                     break;
                  }
               }
            }
         } else {
            this.place(this.bp);
         }

         FindItemResult result = InvUtils.findInHotbar((Predicate)((itemStack) -> this.validItem(itemStack, this.bp)));
         if ((Boolean)this.fastTower.get() && this.mc.field_1690.field_1903.method_1434() && !this.mc.field_1690.field_1832.method_1434() && result.found() && ((Boolean)this.autoSwitch.get() || result.getHand() != null)) {
            class_243 velocity = this.mc.field_1724.method_18798();
            class_238 playerBox = this.mc.field_1724.method_5829();
            if (Streams.stream(this.mc.field_1687.method_20812(this.mc.field_1724, playerBox.method_989((double)0.0F, (double)1.0F, (double)0.0F))).toList().isEmpty()) {
               if ((Boolean)this.whileMoving.get() || !PlayerUtils.isMoving()) {
                  velocity = new class_243(velocity.field_1352, (Double)this.towerSpeed.get(), velocity.field_1350);
               }

               this.mc.field_1724.method_18799(velocity);
            } else {
               this.mc.field_1724.method_18800(velocity.field_1352, Math.ceil(this.mc.field_1724.method_23318()) - this.mc.field_1724.method_23318(), velocity.field_1350);
               this.mc.field_1724.method_24830(true);
            }
         }

      }
   }

   public boolean scaffolding() {
      return this.isActive() && (!(Boolean)this.onlyOnClick.get() || (Boolean)this.onlyOnClick.get() && this.mc.field_1690.field_1904.method_1434());
   }

   public boolean towering() {
      FindItemResult result = InvUtils.findInHotbar((Predicate)((itemStack) -> this.validItem(itemStack, this.bp)));
      return this.scaffolding() && (Boolean)this.fastTower.get() && this.mc.field_1690.field_1903.method_1434() && !this.mc.field_1690.field_1832.method_1434() && ((Boolean)this.whileMoving.get() || !PlayerUtils.isMoving()) && result.found() && ((Boolean)this.autoSwitch.get() || result.getHand() != null);
   }

   private boolean validItem(class_1799 itemStack, class_2338 pos) {
      if (!(itemStack.method_7909() instanceof class_1747)) {
         return false;
      } else {
         class_2248 block = ((class_1747)itemStack.method_7909()).method_7711();
         if (this.blocksFilter.get() == Scaffold.ListMode.Blacklist && ((List)this.blocks.get()).contains(block)) {
            return false;
         } else if (this.blocksFilter.get() == Scaffold.ListMode.Whitelist && !((List)this.blocks.get()).contains(block)) {
            return false;
         } else if (!class_2248.method_9614(block.method_9564().method_26220(this.mc.field_1687, pos))) {
            return false;
         } else {
            return !(block instanceof class_2346) || !class_2346.method_10128(this.mc.field_1687.method_8320(pos));
         }
      }
   }

   private boolean place(class_2338 bp) {
      FindItemResult item = InvUtils.findInHotbar((Predicate)((itemStack) -> this.validItem(itemStack, bp)));
      if (!item.found()) {
         return false;
      } else if (item.getHand() == null && !(Boolean)this.autoSwitch.get()) {
         return false;
      } else if (BlockUtils.place(bp, item, (Boolean)this.rotate.get(), 50, (Boolean)this.renderSwing.get(), true)) {
         if ((Boolean)this.render.get()) {
            RenderUtils.renderTickingBlock(bp.method_10062(), this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0, 8, true, false);
         }

         return true;
      } else {
         return false;
      }
   }

   public static enum ListMode {
      Whitelist,
      Blacklist;

      // $FF: synthetic method
      private static ListMode[] $values() {
         return new ListMode[]{Whitelist, Blacklist};
      }
   }
}
