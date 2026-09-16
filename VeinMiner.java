package meteordevelopment.meteorclient.systems.modules.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2382;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2350.class_2351;

public class VeinMiner extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Set<class_2382> blockNeighbours;
   private final Setting<List<class_2248>> selectedBlocks;
   private final Setting<ListMode> mode;
   private final Setting<Integer> depth;
   private final Setting<Integer> delay;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> swingHand;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Pool<MyBlock> blockPool;
   private final List<MyBlock> blocks;
   private final List<class_2338> foundBlockPositions;
   private int tick;

   public VeinMiner() {
      super(Categories.World, "vein-miner", "Mines all nearby blocks with this type");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.blockNeighbours = Set.of(new class_2382(1, -1, 1), new class_2382(0, -1, 1), new class_2382(-1, -1, 1), new class_2382(1, -1, 0), new class_2382(0, -1, 0), new class_2382(-1, -1, 0), new class_2382(1, -1, -1), new class_2382(0, -1, -1), new class_2382(-1, -1, -1), new class_2382(1, 0, 1), new class_2382(0, 0, 1), new class_2382(-1, 0, 1), new class_2382(1, 0, 0), new class_2382(-1, 0, 0), new class_2382(1, 0, -1), new class_2382(0, 0, -1), new class_2382(-1, 0, -1), new class_2382(1, 1, 1), new class_2382(0, 1, 1), new class_2382(-1, 1, 1), new class_2382(1, 1, 0), new class_2382(0, 1, 0), new class_2382(-1, 1, 0), new class_2382(1, 1, -1), new class_2382(0, 1, -1), new class_2382(-1, 1, -1));
      this.selectedBlocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks")).description("Which blocks to select.")).defaultValue(class_2246.field_10340, class_2246.field_10566, class_2246.field_10219).build());
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Selection mode.")).defaultValue(VeinMiner.ListMode.Blacklist)).build());
      this.depth = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("depth")).description("Amount of iterations used to scan for similar blocks.")).defaultValue(3)).min(1).sliderRange(1, 15).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("Delay between mining blocks.")).defaultValue(0)).min(0).sliderRange(0, 20).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Sends rotation packets to the server when mining.")).defaultValue(true)).build());
      this.swingHand = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing-hand")).description("Swing hand client-side.")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Whether or not to render the block being mined.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The color of the sides of the blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 10)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The color of the lines of the blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 255)).build());
      this.blockPool = new Pool<MyBlock>(() -> new MyBlock());
      this.blocks = new ArrayList();
      this.foundBlockPositions = new ArrayList();
      this.tick = 0;
   }

   public void onDeactivate() {
      this.blockPool.freeAll(this.blocks);
      this.blocks.clear();
      this.foundBlockPositions.clear();
   }

   private boolean isMiningBlock(class_2338 pos) {
      for(MyBlock block : this.blocks) {
         if (block.blockPos.equals(pos)) {
            return true;
         }
      }

      return false;
   }

   @EventHandler
   private void onStartBreakingBlock(StartBreakingBlockEvent event) {
      class_2680 state = this.mc.field_1687.method_8320(event.blockPos);
      if (!(state.method_26214(this.mc.field_1687, event.blockPos) < 0.0F)) {
         if (this.mode.get() != VeinMiner.ListMode.Whitelist || ((List)this.selectedBlocks.get()).contains(state.method_26204())) {
            if (this.mode.get() != VeinMiner.ListMode.Blacklist || !((List)this.selectedBlocks.get()).contains(state.method_26204())) {
               this.foundBlockPositions.clear();
               if (!this.isMiningBlock(event.blockPos)) {
                  MyBlock block = this.blockPool.get();
                  block.set(event);
                  this.blocks.add(block);
                  this.mineNearbyBlocks(block.originalBlock.method_8389(), event.blockPos, event.direction, (Integer)this.depth.get());
               }

            }
         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.blocks.removeIf(MyBlock::shouldRemove);
      if (!this.blocks.isEmpty()) {
         if (this.tick < (Integer)this.delay.get() && !((MyBlock)this.blocks.getFirst()).mining) {
            ++this.tick;
            return;
         }

         this.tick = 0;
         ((MyBlock)this.blocks.getFirst()).mine();
      }

   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         for(MyBlock block : this.blocks) {
            block.render(event);
         }
      }

   }

   private void mineNearbyBlocks(class_1792 item, class_2338 pos, class_2350 dir, int depth) {
      if (depth > 0) {
         if (!this.foundBlockPositions.contains(pos)) {
            this.foundBlockPositions.add(pos);
            if (!(Utils.distance(this.mc.field_1724.method_23317() - (double)0.5F, this.mc.field_1724.method_23318() + (double)this.mc.field_1724.method_18381(this.mc.field_1724.method_18376()), this.mc.field_1724.method_23321() - (double)0.5F, (double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260()) > this.mc.field_1724.method_55754())) {
               for(class_2382 neighbourOffset : this.blockNeighbours) {
                  class_2338 neighbour = pos.method_10081(neighbourOffset);
                  if (this.mc.field_1687.method_8320(neighbour).method_26204().method_8389() == item) {
                     MyBlock block = this.blockPool.get();
                     block.set(neighbour, dir);
                     this.blocks.add(block);
                     this.mineNearbyBlocks(item, neighbour, dir, depth - 1);
                  }
               }

            }
         }
      }
   }

   public String getInfoString() {
      String var10000 = ((ListMode)this.mode.get()).toString();
      return var10000 + " (" + ((List)this.selectedBlocks.get()).size() + ")";
   }

   private class MyBlock {
      public class_2338 blockPos;
      public class_2350 direction;
      public class_2248 originalBlock;
      public boolean mining;

      public void set(StartBreakingBlockEvent event) {
         this.blockPos = event.blockPos;
         this.direction = event.direction;
         this.originalBlock = VeinMiner.this.mc.field_1687.method_8320(this.blockPos).method_26204();
         this.mining = false;
      }

      public void set(class_2338 pos, class_2350 dir) {
         this.blockPos = pos;
         this.direction = dir;
         this.originalBlock = VeinMiner.this.mc.field_1687.method_8320(pos).method_26204();
         this.mining = false;
      }

      public boolean shouldRemove() {
         return VeinMiner.this.mc.field_1687.method_8320(this.blockPos).method_26204() != this.originalBlock || Utils.distance(VeinMiner.this.mc.field_1724.method_23317() - (double)0.5F, VeinMiner.this.mc.field_1724.method_23318() + (double)VeinMiner.this.mc.field_1724.method_18381(VeinMiner.this.mc.field_1724.method_18376()), VeinMiner.this.mc.field_1724.method_23321() - (double)0.5F, (double)(this.blockPos.method_10263() + this.direction.method_10148()), (double)(this.blockPos.method_10264() + this.direction.method_10164()), (double)(this.blockPos.method_10260() + this.direction.method_10165())) > VeinMiner.this.mc.field_1724.method_55754();
      }

      public void mine() {
         if (!this.mining) {
            VeinMiner.this.mc.field_1724.method_6104(class_1268.field_5808);
            this.mining = true;
         }

         if ((Boolean)VeinMiner.this.rotate.get()) {
            Rotations.rotate(Rotations.getYaw(this.blockPos), Rotations.getPitch(this.blockPos), 50, this::updateBlockBreakingProgress);
         } else {
            this.updateBlockBreakingProgress();
         }

      }

      private void updateBlockBreakingProgress() {
         BlockUtils.breakBlock(this.blockPos, (Boolean)VeinMiner.this.swingHand.get());
      }

      public void render(Render3DEvent event) {
         class_265 shape = VeinMiner.this.mc.field_1687.method_8320(this.blockPos).method_26218(VeinMiner.this.mc.field_1687, this.blockPos);
         double x1 = (double)this.blockPos.method_10263();
         double y1 = (double)this.blockPos.method_10264();
         double z1 = (double)this.blockPos.method_10260();
         double x2 = (double)(this.blockPos.method_10263() + 1);
         double y2 = (double)(this.blockPos.method_10264() + 1);
         double z2 = (double)(this.blockPos.method_10260() + 1);
         if (!shape.method_1110()) {
            x1 = (double)this.blockPos.method_10263() + shape.method_1091(class_2351.field_11048);
            y1 = (double)this.blockPos.method_10264() + shape.method_1091(class_2351.field_11052);
            z1 = (double)this.blockPos.method_10260() + shape.method_1091(class_2351.field_11051);
            x2 = (double)this.blockPos.method_10263() + shape.method_1105(class_2351.field_11048);
            y2 = (double)this.blockPos.method_10264() + shape.method_1105(class_2351.field_11052);
            z2 = (double)this.blockPos.method_10260() + shape.method_1105(class_2351.field_11051);
         }

         event.renderer.box(x1, y1, z1, x2, y2, z2, VeinMiner.this.sideColor.get(), VeinMiner.this.lineColor.get(), VeinMiner.this.shapeMode.get(), 0);
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
