package meteordevelopment.meteorclient.systems.modules.render;

import java.util.List;
import java.util.Map;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.mixin.ClientPlayerInteractionManagerAccessor;
import meteordevelopment.meteorclient.mixin.WorldRendererAccessor;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.HighwayBuilder;
import meteordevelopment.meteorclient.systems.modules.world.PacketMine;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3191;

public class BreakIndicators extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<ShapeMode> shapeMode;
   public final Setting<Boolean> packetMine;
   private final Setting<SettingColor> startSideColor;
   private final Setting<SettingColor> startLineColor;
   private final Setting<SettingColor> endSideColor;
   private final Setting<SettingColor> endLineColor;
   private final Color cSides;
   private final Color cLines;

   public BreakIndicators() {
      super(Categories.Render, "break-indicators", "Renders the progress of a block being broken.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.shapeMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.packetMine = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("packet-mine")).description("Whether or not to render blocks being packet mined.")).defaultValue(true)).build());
      this.startSideColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("start-side-color")).description("The side color for the non-broken block.")).defaultValue(new SettingColor(25, 252, 25, 150)).visible(() -> ((ShapeMode)this.shapeMode.get()).sides())).build());
      this.startLineColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("start-line-color")).description("The line color for the non-broken block.")).defaultValue(new SettingColor(25, 252, 25, 150)).visible(() -> ((ShapeMode)this.shapeMode.get()).lines())).build());
      this.endSideColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("end-side-color")).description("The side color for the fully-broken block.")).defaultValue(new SettingColor(255, 25, 25, 150)).visible(() -> ((ShapeMode)this.shapeMode.get()).sides())).build());
      this.endLineColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("end-line-color")).description("The line color for the fully-broken block.")).defaultValue(new SettingColor(255, 25, 25, 150)).visible(() -> ((ShapeMode)this.shapeMode.get()).lines())).build());
      this.cSides = new Color();
      this.cLines = new Color();
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      this.renderNormal(event);
      if ((Boolean)this.packetMine.get() && !((PacketMine)Modules.get().get(PacketMine.class)).blocks.isEmpty()) {
         this.renderPacket(event, ((PacketMine)Modules.get().get(PacketMine.class)).blocks);
      }

      HighwayBuilder b = (HighwayBuilder)Modules.get().get(HighwayBuilder.class);
      if (b.isActive()) {
         if (b.normalMining != null) {
            class_265 voxelShape = b.normalMining.blockState.method_26218(this.mc.field_1687, b.normalMining.blockPos);
            if (voxelShape.method_1110()) {
               return;
            }

            double normalised = Math.min((double)1.0F, b.normalMining.progress());
            this.renderBlock(event, voxelShape.method_1107(), b.normalMining.blockPos, (double)1.0F - normalised, normalised);
         }

         if (b.packetMining != null) {
            class_265 voxelShape = b.packetMining.blockState.method_26218(this.mc.field_1687, b.packetMining.blockPos);
            if (voxelShape.method_1110()) {
               return;
            }

            double normalised = Math.min((double)1.0F, b.packetMining.progress());
            this.renderBlock(event, voxelShape.method_1107(), b.packetMining.blockPos, (double)1.0F - normalised, normalised);
         }

      }
   }

   private void renderNormal(Render3DEvent event) {
      Map<Integer, class_3191> blocks = ((WorldRendererAccessor)this.mc.field_1769).meteor$getBlockBreakingInfos();
      float ownBreakingStage = ((ClientPlayerInteractionManagerAccessor)this.mc.field_1761).meteor$getBreakingProgress();
      class_2338 ownBreakingPos = ((ClientPlayerInteractionManagerAccessor)this.mc.field_1761).meteor$getCurrentBreakingBlockPos();
      if (ownBreakingPos != null && ownBreakingStage > 0.0F) {
         class_2680 state = this.mc.field_1687.method_8320(ownBreakingPos);
         class_265 shape = state.method_26218(this.mc.field_1687, ownBreakingPos);
         if (shape == null || shape.method_1110()) {
            return;
         }

         class_238 orig = shape.method_1107();
         double shrinkFactor = (double)1.0F - (double)ownBreakingStage;
         this.renderBlock(event, orig, ownBreakingPos, shrinkFactor, (double)ownBreakingStage);
      }

      blocks.values().forEach((info) -> {
         class_2338 pos = info.method_13991();
         int stage = info.method_13988();
         if (!pos.equals(ownBreakingPos)) {
            class_2680 state = this.mc.field_1687.method_8320(pos);
            class_265 shape = state.method_26218(this.mc.field_1687, pos);
            if (shape != null && !shape.method_1110()) {
               class_238 orig = shape.method_1107();
               double shrinkFactor = (double)(9 - (stage + 1)) / (double)9.0F;
               double progress = (double)1.0F - shrinkFactor;
               this.renderBlock(event, orig, pos, shrinkFactor, progress);
            }
         }
      });
   }

   private void renderPacket(Render3DEvent event, List<PacketMine.MyBlock> blocks) {
      for(PacketMine.MyBlock block : blocks) {
         if (block.mining && block.progress() != Double.POSITIVE_INFINITY) {
            class_265 shape = block.blockState.method_26218(this.mc.field_1687, block.blockPos);
            if (shape == null || shape.method_1110()) {
               return;
            }

            class_238 orig = shape.method_1107();
            double progressNormalised = Math.min((double)1.0F, block.progress());
            double shrinkFactor = (double)1.0F - progressNormalised;
            class_2338 pos = block.blockPos;
            this.renderBlock(event, orig, pos, shrinkFactor, progressNormalised);
         }
      }

   }

   private void renderBlock(Render3DEvent event, class_238 orig, class_2338 pos, double shrinkFactor, double progress) {
      class_238 box = orig.method_1002(orig.method_17939() * shrinkFactor, orig.method_17940() * shrinkFactor, orig.method_17941() * shrinkFactor);
      double xShrink = orig.method_17939() * shrinkFactor / (double)2.0F;
      double yShrink = orig.method_17940() * shrinkFactor / (double)2.0F;
      double zShrink = orig.method_17941() * shrinkFactor / (double)2.0F;
      double x1 = (double)pos.method_10263() + box.field_1323 + xShrink;
      double y1 = (double)pos.method_10264() + box.field_1322 + yShrink;
      double z1 = (double)pos.method_10260() + box.field_1321 + zShrink;
      double x2 = (double)pos.method_10263() + box.field_1320 + xShrink;
      double y2 = (double)pos.method_10264() + box.field_1325 + yShrink;
      double z2 = (double)pos.method_10260() + box.field_1324 + zShrink;
      Color c1Sides = ((SettingColor)this.startSideColor.get()).copy().a((this.startSideColor.get()).a / 2);
      Color c2Sides = ((SettingColor)this.endSideColor.get()).copy().a((this.endSideColor.get()).a / 2);
      this.cSides.set((int)Math.round((double)c1Sides.r + (double)(c2Sides.r - c1Sides.r) * progress), (int)Math.round((double)c1Sides.g + (double)(c2Sides.g - c1Sides.g) * progress), (int)Math.round((double)c1Sides.b + (double)(c2Sides.b - c1Sides.b) * progress), (int)Math.round((double)c1Sides.a + (double)(c2Sides.a - c1Sides.a) * progress));
      Color c1Lines = this.startLineColor.get();
      Color c2Lines = this.endLineColor.get();
      this.cLines.set((int)Math.round((double)c1Lines.r + (double)(c2Lines.r - c1Lines.r) * progress), (int)Math.round((double)c1Lines.g + (double)(c2Lines.g - c1Lines.g) * progress), (int)Math.round((double)c1Lines.b + (double)(c2Lines.b - c1Lines.b) * progress), (int)Math.round((double)c1Lines.a + (double)(c2Lines.a - c1Lines.a) * progress));
      event.renderer.box(x1, y1, z1, x2, y2, z2, this.cSides, this.cLines, this.shapeMode.get(), 0);
   }
}
