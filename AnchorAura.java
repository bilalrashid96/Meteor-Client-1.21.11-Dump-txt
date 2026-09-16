package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class AnchorAura extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPlace;
   private final SettingGroup sgBreak;
   private final SettingGroup sgPause;
   private final SettingGroup sgRender;
   private final Setting<Double> targetRange;
   private final Setting<SortPriority> targetPriority;
   private final Setting<Double> minDamage;
   private final Setting<Double> maxSelfDamage;
   private final Setting<Boolean> antiSuicide;
   private final Setting<Boolean> swapBack;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> place;
   private final Setting<Integer> placeDelay;
   private final Setting<Double> placeRange;
   private final Setting<Double> placeWallsRange;
   private final Setting<Boolean> airPlace;
   private final Setting<Integer> chargeDelay;
   private final Setting<Integer> breakDelay;
   private final Setting<Double> breakRange;
   private final Setting<Double> breakWallsRange;
   private final Setting<Boolean> pauseOnUse;
   private final Setting<Boolean> pauseOnMine;
   private final Setting<Boolean> pauseOnCA;
   private final Setting<Boolean> swing;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private double bestPlaceDamage;
   private final class_2338.class_2339 bestPlacePos;
   private double bestBreakDamage;
   private final class_2338.class_2339 bestBreakPos;
   private class_2338 renderBlockPos;
   private int placeDelayLeft;
   private int chargeDelayLeft;
   private int breakDelayLeft;
   private class_1657 target;

   public AnchorAura() {
      super(Categories.Combat, "anchor-aura", "Automatically places and breaks Respawn Anchors to harm entities.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPlace = this.settings.createGroup("Place");
      this.sgBreak = this.settings.createGroup("Break");
      this.sgPause = this.settings.createGroup("Pause");
      this.sgRender = this.settings.createGroup("Render");
      this.targetRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("Range in which to target players.")).defaultValue((double)10.0F).min((double)0.0F).sliderMax((double)16.0F).build());
      this.targetPriority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-priority")).description("How to select the player to target.")).defaultValue(SortPriority.LowestHealth)).build());
      this.minDamage = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-damage")).description("The minimum damage to inflict on your target.")).defaultValue((double)7.0F).min((double)0.0F).sliderMax((double)36.0F).build());
      this.maxSelfDamage = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-self-damage")).description("The maximum damage to inflict on yourself.")).defaultValue((double)7.0F).min((double)0.0F).sliderMax((double)36.0F).build());
      this.antiSuicide = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-suicide")).description("Will not place and break anchors if they will kill you.")).defaultValue(true)).build());
      this.swapBack = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swap-back")).description("Switches to your previous slot after using anchors.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates server-side towards the anchors being placed/broken.")).defaultValue(true)).build());
      this.place = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("place")).description("Allows Anchor Aura to place anchors.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgPlace;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("The tick delay between placing anchors.")).defaultValue(5)).range(0, 10);
      Setting var10003 = this.place;
      Objects.requireNonNull(var10003);
      this.placeDelay = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgPlace;
      DoubleSetting.Builder var5 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("The range at which anchors can be placed.")).defaultValue((double)4.0F).range((double)0.0F, (double)6.0F);
      var10003 = this.place;
      Objects.requireNonNull(var10003);
      this.placeRange = var10001.add(((DoubleSetting.Builder)var5.visible(var10003::get)).build());
      var10001 = this.sgPlace;
      var5 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("walls-range")).description("Range in which to place anchors when behind blocks.")).defaultValue((double)4.0F).range((double)0.0F, (double)6.0F);
      var10003 = this.place;
      Objects.requireNonNull(var10003);
      this.placeWallsRange = var10001.add(((DoubleSetting.Builder)var5.visible(var10003::get)).build());
      var10001 = this.sgPlace;
      BoolSetting.Builder var7 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("air-place")).description("Allows Anchor Aura to place anchors in the air.")).defaultValue(true);
      var10003 = this.place;
      Objects.requireNonNull(var10003);
      this.airPlace = var10001.add(((BoolSetting.Builder)var7.visible(var10003::get)).build());
      this.chargeDelay = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("charge-delay")).description("The tick delay it takes to charge anchors.")).defaultValue(1)).range(0, 10).build());
      this.breakDelay = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-delay")).description("The tick delay it takes to break anchors.")).defaultValue(1)).range(0, 10).build());
      this.breakRange = this.sgBreak.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-range")).description("Range in which to break anchors.")).defaultValue((double)4.5F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.breakWallsRange = this.sgBreak.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("walls-range")).description("Range in which to break anchors when behind blocks.")).defaultValue((double)4.5F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.pauseOnUse = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-use")).description("Pauses while using an item.")).defaultValue(true)).build());
      this.pauseOnMine = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-mine")).description("Pauses while mining blocks.")).defaultValue(true)).build());
      this.pauseOnCA = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-CA")).description("Pauses while Crystal Aura is placing.")).defaultValue(true)).build());
      this.swing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing")).description("Whether to swing your hand client-side.")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders the block where it is placing an anchor.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var8 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both);
      var10003 = this.render;
      Objects.requireNonNull(var10003);
      this.shapeMode = var10001.add(((EnumSetting.Builder)var8.visible(var10003::get)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color for positions to be placed.")).defaultValue(new SettingColor(15, 255, 211, 41)).visible(() -> (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).sides())).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color for positions to be placed.")).defaultValue(new SettingColor(15, 255, 211)).visible(() -> (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).lines())).build());
      this.bestPlacePos = new class_2338.class_2339();
      this.bestBreakPos = new class_2338.class_2339();
   }

   public void onActivate() {
      this.renderBlockPos = null;
      this.placeDelayLeft = (Integer)this.placeDelay.get();
      this.chargeDelayLeft = 0;
      this.breakDelayLeft = 0;
      this.target = null;
   }

   public void onDeactivate() {
      this.renderBlockPos = null;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.mc.field_1687.method_27983() == class_1937.field_25180) {
         this.error("You can't blow up respawn anchors in this dimension, disabling.", new Object[0]);
         this.toggle();
      } else if (this.shouldPause()) {
         this.renderBlockPos = null;
      } else {
         if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
            this.renderBlockPos = null;
            this.target = TargetUtils.getPlayerTarget((Double)this.targetRange.get(), this.targetPriority.get());
            if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
               return;
            }
         }

         this.doAnchorAura();
      }
   }

   private void doAnchorAura() {
      this.bestPlaceDamage = (double)0.0F;
      this.bestBreakDamage = (double)0.0F;
      int iteratorRange = (int)Math.ceil(Math.max((Double)this.placeRange.get(), (Double)this.breakRange.get()));
      BlockIterator.register(iteratorRange, iteratorRange, (blockPos, blockState) -> {
         boolean isPlacing = blockState.method_26204() != class_2246.field_23152;
         double baseRange = isPlacing ? (Double)this.placeRange.get() : (Double)this.breakRange.get();
         double wallsRange = isPlacing ? (Double)this.placeWallsRange.get() : (Double)this.breakWallsRange.get();
         if (!this.isOutOfRange(blockPos, baseRange, wallsRange)) {
            if (isPlacing) {
               if (!BlockUtils.canPlace(blockPos)) {
                  return;
               }

               if (!(Boolean)this.airPlace.get() && this.isAirPlace(blockPos)) {
                  return;
               }
            }

            float bestDamage = isPlacing ? (float)this.bestPlaceDamage : (float)this.bestBreakDamage;
            float selfDamage = DamageUtils.anchorDamage(this.mc.field_1724, blockPos.method_46558());
            float targetDamage = DamageUtils.anchorDamage(this.target, blockPos.method_46558());
            if ((double)targetDamage >= (Double)this.minDamage.get() && targetDamage > bestDamage && (!(Boolean)this.antiSuicide.get() || (double)selfDamage <= (Double)this.maxSelfDamage.get()) && (!(Boolean)this.antiSuicide.get() || PlayerUtils.getTotalHealth() - selfDamage > 0.0F)) {
               if (isPlacing) {
                  this.bestPlaceDamage = (double)targetDamage;
                  this.bestPlacePos.method_10101(blockPos);
               } else {
                  this.bestBreakDamage = (double)targetDamage;
                  this.bestBreakPos.method_10101(blockPos);
               }
            }

         }
      });
      BlockIterator.after(() -> {
         this.renderBlockPos = null;
         FindItemResult anchor = InvUtils.findInHotbar(class_1802.field_23141);
         FindItemResult glowStone = InvUtils.findInHotbar(class_1802.field_8801);
         if (this.bestBreakDamage > (double)0.0F) {
            this.doBreak(glowStone);
         } else if (this.bestPlaceDamage > (double)0.0F && (Boolean)this.place.get() && anchor.found() && glowStone.found()) {
            this.doPlace(anchor);
         }

      });
   }

   private void doPlace(FindItemResult anchor) {
      this.renderBlockPos = this.bestPlacePos;
      if (this.placeDelayLeft++ >= (Integer)this.placeDelay.get()) {
         BlockUtils.place(this.bestPlacePos, anchor, (Boolean)this.rotate.get(), 50, (Boolean)this.swing.get(), false, (Boolean)this.swapBack.get());
         this.placeDelayLeft = 0;
      }
   }

   private void doBreak(FindItemResult glowStone) {
      this.renderBlockPos = this.bestBreakPos;
      if ((Boolean)this.rotate.get()) {
         Rotations.rotate(Rotations.getYaw((class_2338)this.bestBreakPos), Rotations.getPitch((class_2338)this.bestBreakPos), 40, () -> this.doInteract(glowStone));
      } else {
         this.doInteract(glowStone);
      }

   }

   private void doInteract(FindItemResult glowStone) {
      class_2680 blockState = this.mc.field_1687.method_8320(this.bestBreakPos);
      if (blockState.method_26204() == class_2246.field_23152) {
         class_243 center = this.bestBreakPos.method_46558();
         int charges = (Integer)blockState.method_11654(class_2741.field_23187);
         if (charges == 0 && this.chargeDelayLeft++ >= (Integer)this.chargeDelay.get()) {
            if (!glowStone.found()) {
               return;
            }

            InvUtils.swap(glowStone.slot(), (Boolean)this.swapBack.get());
            BlockUtils.interact(new class_3965(center, BlockUtils.getDirection(this.bestBreakPos), this.bestBreakPos, true), class_1268.field_5808, (Boolean)this.swing.get());
            this.chargeDelayLeft = 0;
            ++charges;
         }

         if (charges > 0 && this.breakDelayLeft++ >= (Integer)this.breakDelay.get()) {
            FindItemResult fir = InvUtils.findInHotbar((Predicate)((item) -> !item.method_7909().equals(class_1802.field_8801)));
            if (!fir.found()) {
               return;
            }

            InvUtils.swap(fir.slot(), (Boolean)this.swapBack.get());
            BlockUtils.interact(new class_3965(center, BlockUtils.getDirection(this.bestBreakPos), this.bestBreakPos, true), class_1268.field_5808, (Boolean)this.swing.get());
            this.breakDelayLeft = 0;
            this.mc.field_1687.method_8652(this.bestBreakPos, this.mc.field_1687.method_8316(this.bestBreakPos).method_15759(), 0);
         }

         if ((Boolean)this.swapBack.get()) {
            InvUtils.swapBack();
         }

      }
   }

   private boolean isOutOfRange(class_2338 blockPos, double baseRange, double wallsRange) {
      class_243 pos = blockPos.method_46558();
      if (!PlayerUtils.isWithin(pos, baseRange)) {
         return true;
      } else {
         class_3959 raycastContext = new class_3959(this.mc.field_1724.method_33571(), pos, class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
         class_3965 result = this.mc.field_1687.method_17742(raycastContext);
         if (result != null && result.method_17777().equals(blockPos)) {
            return false;
         } else {
            return !PlayerUtils.isWithin(pos, wallsRange);
         }
      }
   }

   private boolean isAirPlace(class_2338 blockPos) {
      for(class_2350 direction : class_2350.values()) {
         if (!this.mc.field_1687.method_8320(blockPos.method_10093(direction)).method_45474()) {
            return false;
         }
      }

      return true;
   }

   private boolean shouldPause() {
      if ((Boolean)this.pauseOnUse.get() && this.mc.field_1724.method_6115()) {
         return true;
      } else if ((Boolean)this.pauseOnMine.get() && this.mc.field_1761.method_2923()) {
         return true;
      } else {
         CrystalAura CA = (CrystalAura)Modules.get().get(CrystalAura.class);
         return (Boolean)this.pauseOnCA.get() && CA.isActive() && CA.kaTimer > 0;
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get() && this.renderBlockPos != null) {
         event.renderer.box((class_2338)this.renderBlockPos, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
      }
   }

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }
}
