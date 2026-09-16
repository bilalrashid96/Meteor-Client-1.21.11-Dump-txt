package meteordevelopment.meteorclient.systems.modules.world;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.events.entity.player.BlockBreakingCooldownEvent;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
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
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.Names;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2846;
import net.minecraft.class_2879;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2846.class_2847;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class Nuker extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgWhitelist;
   private final SettingGroup sgRender;
   private final Setting<Shape> shape;
   private final Setting<Mode> mode;
   private final Setting<Double> range;
   private final Setting<Integer> range_up;
   private final Setting<Integer> range_down;
   private final Setting<Integer> range_left;
   private final Setting<Integer> range_right;
   private final Setting<Integer> range_forward;
   private final Setting<Integer> range_back;
   private final Setting<Double> wallsRange;
   private final Setting<Integer> delay;
   private final Setting<Integer> maxBlocksPerTick;
   private final Setting<SortMode> sortMode;
   private final Setting<Boolean> packetMine;
   private final Setting<Boolean> suitableTools;
   private final Setting<Boolean> interact;
   private final Setting<Boolean> rotate;
   private final Setting<ListMode> listMode;
   private final Setting<List<class_2248>> blacklist;
   private final Setting<List<class_2248>> whitelist;
   private final Setting<Keybind> selectBlockBind;
   private final Setting<Boolean> swing;
   private final Setting<Boolean> enableRenderBounding;
   private final Setting<ShapeMode> shapeModeBox;
   private final Setting<SettingColor> sideColorBox;
   private final Setting<SettingColor> lineColorBox;
   private final Setting<Boolean> enableRenderBreaking;
   private final Setting<ShapeMode> shapeModeBreak;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final List<class_2338> blocks;
   private final Set<class_2338> interacted;
   private boolean firstBlock;
   private final class_2338.class_2339 lastBlockPos;
   private int timer;
   private int noBlockTimer;
   private final class_2338.class_2339 pos1;
   private final class_2338.class_2339 pos2;
   int maxh;
   int maxv;

   public Nuker() {
      super(Categories.World, "nuker", "Breaks blocks around you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgWhitelist = this.settings.createGroup("Whitelist");
      this.sgRender = this.settings.createGroup("Render");
      this.shape = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape")).description("The shape of nuking algorithm.")).defaultValue(Nuker.Shape.Sphere)).build());
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The way the blocks are broken.")).defaultValue(Nuker.Mode.Flatten)).build());
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("The break range.")).defaultValue((double)4.0F).min((double)0.0F).visible(() -> this.shape.get() != Nuker.Shape.Cube)).build());
      this.range_up = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("up")).description("The break range.")).defaultValue(1)).min(0).visible(() -> this.shape.get() == Nuker.Shape.Cube)).build());
      this.range_down = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("down")).description("The break range.")).defaultValue(1)).min(0).visible(() -> this.shape.get() == Nuker.Shape.Cube)).build());
      this.range_left = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("left")).description("The break range.")).defaultValue(1)).min(0).visible(() -> this.shape.get() == Nuker.Shape.Cube)).build());
      this.range_right = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("right")).description("The break range.")).defaultValue(1)).min(0).visible(() -> this.shape.get() == Nuker.Shape.Cube)).build());
      this.range_forward = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("forward")).description("The break range.")).defaultValue(1)).min(0).visible(() -> this.shape.get() == Nuker.Shape.Cube)).build());
      this.range_back = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("back")).description("The break range.")).defaultValue(1)).min(0).visible(() -> this.shape.get() == Nuker.Shape.Cube)).build());
      this.wallsRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("walls-range")).description("Range in which to break when behind blocks.")).defaultValue((double)4.0F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("Delay in ticks between breaking blocks.")).defaultValue(0)).build());
      this.maxBlocksPerTick = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-blocks-per-tick")).description("Maximum blocks to try to break per tick. Useful when insta mining.")).defaultValue(1)).min(1).build());
      this.sortMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("sort-mode")).description("The blocks you want to mine first.")).defaultValue(Nuker.SortMode.Closest)).build());
      this.packetMine = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("packet-mine")).description("Attempt to instamine everything at once.")).defaultValue(false)).build());
      this.suitableTools = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-suitable-tools")).description("Only mines when using an appropriate for the block.")).defaultValue(false)).build());
      this.interact = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("interact")).description("Interacts with the block instead of mining.")).defaultValue(false)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates server-side to the block being mined.")).defaultValue(true)).build());
      this.listMode = this.sgWhitelist.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("list-mode")).description("Selection mode.")).defaultValue(Nuker.ListMode.Blacklist)).build());
      this.blacklist = this.sgWhitelist.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blacklist")).description("The blocks you don't want to mine.")).visible(() -> this.listMode.get() == Nuker.ListMode.Blacklist)).build());
      this.whitelist = this.sgWhitelist.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("whitelist")).description("The blocks you want to mine.")).visible(() -> this.listMode.get() == Nuker.ListMode.Whitelist)).build());
      this.selectBlockBind = this.sgWhitelist.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("select-block-bind")).description("Adds targeted block to list when this button is pressed.")).defaultValue(Keybind.none())).build());
      this.swing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing")).description("Whether to swing hand client-side.")).defaultValue(true)).build());
      this.enableRenderBounding = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("bounding-box")).description("Enable rendering bounding box for Cube and Uniform Cube.")).defaultValue(true)).build());
      this.shapeModeBox = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("nuke-box-mode")).description("How the shape for the bounding box is rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColorBox = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the bounding box.")).defaultValue(new SettingColor(16, 106, 144, 100)).build());
      this.lineColorBox = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the bounding box.")).defaultValue(new SettingColor(16, 106, 144, 255)).build());
      this.enableRenderBreaking = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("broken-blocks")).description("Enable rendering bounding box for Cube and Uniform Cube.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("nuke-block-mode")).description("How the shapes for broken blocks are rendered.")).defaultValue(ShapeMode.Both);
      Setting var10003 = this.enableRenderBreaking;
      Objects.requireNonNull(var10003);
      this.shapeModeBreak = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgRender;
      ColorSetting.Builder var3 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(255, 0, 0, 80));
      var10003 = this.enableRenderBreaking;
      Objects.requireNonNull(var10003);
      this.sideColor = var10001.add(((ColorSetting.Builder)var3.visible(var10003::get)).build());
      var10001 = this.sgRender;
      var3 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(255, 0, 0, 255));
      var10003 = this.enableRenderBreaking;
      Objects.requireNonNull(var10003);
      this.lineColor = var10001.add(((ColorSetting.Builder)var3.visible(var10003::get)).build());
      this.blocks = new ArrayList();
      this.interacted = new ObjectOpenHashSet();
      this.lastBlockPos = new class_2338.class_2339();
      this.pos1 = new class_2338.class_2339();
      this.pos2 = new class_2338.class_2339();
      this.maxh = 0;
      this.maxv = 0;
   }

   public void onActivate() {
      this.firstBlock = true;
      this.timer = 0;
      this.noBlockTimer = 0;
      this.interacted.clear();
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.enableRenderBounding.get() && this.shape.get() != Nuker.Shape.Sphere && this.mode.get() != Nuker.Mode.Smash) {
         int minX = Math.min(this.pos1.method_10263(), this.pos2.method_10263());
         int minY = Math.min(this.pos1.method_10264(), this.pos2.method_10264());
         int minZ = Math.min(this.pos1.method_10260(), this.pos2.method_10260());
         int maxX = Math.max(this.pos1.method_10263(), this.pos2.method_10263());
         int maxY = Math.max(this.pos1.method_10264(), this.pos2.method_10264());
         int maxZ = Math.max(this.pos1.method_10260(), this.pos2.method_10260());
         event.renderer.box((double)minX, (double)minY, (double)minZ, (double)maxX, (double)maxY, (double)maxZ, this.sideColorBox.get(), this.lineColorBox.get(), this.shapeModeBox.get(), 0);
      }

   }

   @EventHandler
   private void onMouseClick(MouseClickEvent event) {
      if (event.action == KeyAction.Press) {
         this.addTargetedBlockToList();
      }

   }

   @EventHandler
   private void onKey(KeyEvent event) {
      if (event.action == KeyAction.Press) {
         this.addTargetedBlockToList();
      }

   }

   @EventHandler
   private void onTickPre(TickEvent.Pre event) {
      if (this.timer > 0) {
         --this.timer;
      } else {
         double pX = this.mc.field_1724.method_23317();
         double pY = this.mc.field_1724.method_23318();
         double pZ = this.mc.field_1724.method_23321();
         double rangeSq = Math.pow((Double)this.range.get(), (double)2.0F);
         class_2338 playerBlockPos = this.mc.field_1724.method_24515();
         if (this.shape.get() == Nuker.Shape.UniformCube) {
            this.range.set((double)Math.round((Double)this.range.get()));
         }

         int r = (int)Math.round((Double)this.range.get());
         if (this.shape.get() == Nuker.Shape.UniformCube) {
            double pX_ = pX + (double)1.0F;
            this.pos1.method_10102(pX_ - (double)r, pY - (double)r + (double)1.0F, pZ - (double)r + (double)1.0F);
            this.pos2.method_10102(pX_ + (double)r - (double)1.0F, pY + (double)r, pZ + (double)r);
            this.maxh = 0;
            this.maxv = 0;
         } else {
            class_2350 direction = this.mc.field_1724.method_5735();
            switch (direction) {
               case field_11035:
                  double var20 = pZ + (double)1.0F;
                  double var19 = pX + (double)1.0F;
                  this.pos1.method_10102(var19 - (double)((Integer)this.range_right.get() + 1), Math.ceil(pY) - (double)(Integer)this.range_down.get(), var20 - (double)((Integer)this.range_back.get() + 1));
                  this.pos2.method_10102(var19 + (double)(Integer)this.range_left.get(), Math.ceil(pY + (double)(Integer)this.range_up.get() + (double)1.0F), var20 + (double)(Integer)this.range_forward.get());
                  break;
               case field_11039:
                  this.pos1.method_10102(pX - (double)(Integer)this.range_forward.get(), Math.ceil(pY) - (double)(Integer)this.range_down.get(), pZ - (double)(Integer)this.range_right.get());
                  this.pos2.method_10102(pX + (double)(Integer)this.range_back.get() + (double)1.0F, Math.ceil(pY + (double)(Integer)this.range_up.get() + (double)1.0F), pZ + (double)(Integer)this.range_left.get() + (double)1.0F);
                  break;
               case field_11043:
                  double var18 = pX + (double)1.0F;
                  double pZ_ = pZ + (double)1.0F;
                  this.pos1.method_10102(var18 - (double)((Integer)this.range_left.get() + 1), Math.ceil(pY) - (double)(Integer)this.range_down.get(), pZ_ - (double)((Integer)this.range_forward.get() + 1));
                  this.pos2.method_10102(var18 + (double)(Integer)this.range_right.get(), Math.ceil(pY + (double)(Integer)this.range_up.get() + (double)1.0F), pZ_ + (double)(Integer)this.range_back.get());
                  break;
               case field_11034:
                  double var17 = pX + (double)1.0F;
                  this.pos1.method_10102(var17 - (double)((Integer)this.range_back.get() + 1), Math.ceil(pY) - (double)(Integer)this.range_down.get(), pZ - (double)(Integer)this.range_left.get());
                  this.pos2.method_10102(var17 + (double)(Integer)this.range_forward.get(), Math.ceil(pY + (double)(Integer)this.range_up.get() + (double)1.0F), pZ + (double)(Integer)this.range_right.get() + (double)1.0F);
            }

            this.maxh = 1 + Math.max(Math.max(Math.max((Integer)this.range_back.get(), (Integer)this.range_right.get()), (Integer)this.range_forward.get()), (Integer)this.range_left.get());
            this.maxv = 1 + Math.max((Integer)this.range_up.get(), (Integer)this.range_down.get());
         }

         if (this.mode.get() == Nuker.Mode.Flatten) {
            this.pos1.method_33098((int)Math.floor(pY + (double)0.5F));
         }

         class_238 box = new class_238(this.pos1.method_46558(), this.pos2.method_46558());
         BlockIterator.register(Math.max((int)Math.ceil((Double)this.range.get() + (double)1.0F), this.maxh), Math.max((int)Math.ceil((Double)this.range.get()), this.maxv), (blockPos, blockState) -> {
            class_243 center = blockPos.method_46558();
            switch (((Shape)this.shape.get()).ordinal()) {
               case 0:
                  if (!box.method_1006(center)) {
                     return;
                  }
                  break;
               case 1:
                  if ((double)chebyshevDist(playerBlockPos.method_10263(), playerBlockPos.method_10264(), playerBlockPos.method_10260(), blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260()) >= (Double)this.range.get()) {
                     return;
                  }
                  break;
               case 2:
                  if (Utils.squaredDistance(pX, pY, pZ, center.method_10216(), center.method_10214(), center.method_10215()) > rangeSq) {
                     return;
                  }
            }

            if (this.mode.get() != Nuker.Mode.Flatten || !((double)blockPos.method_10264() + (double)0.5F < pY)) {
               if (this.mode.get() != Nuker.Mode.Smash || blockState.method_26214(this.mc.field_1687, blockPos) == 0.0F) {
                  if (!(Boolean)this.suitableTools.get() || (Boolean)this.interact.get() || this.mc.field_1724.method_6047().method_7951(blockState)) {
                     if (BlockUtils.canBreak(blockPos, blockState) || (Boolean)this.interact.get()) {
                        if (!this.isOutOfRange(blockPos)) {
                           if (this.listMode.get() != Nuker.ListMode.Whitelist || ((List)this.whitelist.get()).contains(blockState.method_26204())) {
                              if (this.listMode.get() != Nuker.ListMode.Blacklist || !((List)this.blacklist.get()).contains(blockState.method_26204())) {
                                 if (!(Boolean)this.interact.get() || !this.interacted.contains(blockPos)) {
                                    this.blocks.add(blockPos.method_10062());
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         });
         BlockIterator.after(() -> {
            if (this.sortMode.get() == Nuker.SortMode.TopDown) {
               this.blocks.sort(Comparator.comparingDouble((value) -> (double)(-value.method_10264())));
            } else if (this.sortMode.get() != Nuker.SortMode.None) {
               this.blocks.sort(Comparator.comparingDouble((value) -> Utils.squaredDistance(pX, pY, pZ, (double)value.method_10263() + (double)0.5F, (double)value.method_10264() + (double)0.5F, (double)value.method_10260() + (double)0.5F) * (double)(this.sortMode.get() == Nuker.SortMode.Closest ? 1 : -1)));
            }

            if (this.blocks.isEmpty()) {
               this.interacted.clear();
               if (this.noBlockTimer++ >= (Integer)this.delay.get()) {
                  this.firstBlock = true;
               }

            } else {
               this.noBlockTimer = 0;
               if (!this.firstBlock && !this.lastBlockPos.equals(this.blocks.getFirst())) {
                  this.timer = (Integer)this.delay.get();
                  this.firstBlock = false;
                  this.lastBlockPos.method_10101((class_2382)this.blocks.getFirst());
                  if (this.timer > 0) {
                     return;
                  }
               }

               int count = 0;

               for(class_2338 block : this.blocks) {
                  if (count >= (Integer)this.maxBlocksPerTick.get()) {
                     break;
                  }

                  boolean canInstaMine = BlockUtils.canInstaBreak(block);
                  if ((Boolean)this.rotate.get()) {
                     Rotations.rotate(Rotations.getYaw(block), Rotations.getPitch(block), () -> this.breakBlock(block));
                  } else {
                     this.breakBlock(block);
                  }

                  if ((Boolean)this.enableRenderBreaking.get()) {
                     RenderUtils.renderTickingBlock(block, this.sideColor.get(), this.lineColor.get(), this.shapeModeBreak.get(), 0, 8, true, false);
                  }

                  this.lastBlockPos.method_10101(block);
                  ++count;
                  if (!canInstaMine && !(Boolean)this.packetMine.get()) {
                     break;
                  }
               }

               this.firstBlock = false;
               this.blocks.clear();
            }
         });
      }
   }

   private void breakBlock(class_2338 blockPos) {
      if ((Boolean)this.interact.get()) {
         BlockUtils.interact(new class_3965(blockPos.method_46558(), BlockUtils.getDirection(blockPos), blockPos, true), class_1268.field_5808, (Boolean)this.swing.get());
         this.interacted.add(blockPos);
      } else if ((Boolean)this.packetMine.get()) {
         this.mc.field_1761.method_41931(this.mc.field_1687, (sequence) -> new class_2846(class_2847.field_12968, blockPos, BlockUtils.getDirection(blockPos), sequence));
         if ((Boolean)this.swing.get()) {
            this.mc.field_1724.method_6104(class_1268.field_5808);
         } else {
            this.mc.method_1562().method_52787(new class_2879(class_1268.field_5808));
         }

         this.mc.field_1761.method_41931(this.mc.field_1687, (sequence) -> new class_2846(class_2847.field_12973, blockPos, BlockUtils.getDirection(blockPos), sequence));
      } else {
         BlockUtils.breakBlock(blockPos, (Boolean)this.swing.get());
      }

   }

   private boolean isOutOfRange(class_2338 blockPos) {
      class_243 pos = blockPos.method_46558();
      class_3959 raycastContext = new class_3959(this.mc.field_1724.method_33571(), pos, class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
      class_3965 result = this.mc.field_1687.method_17742(raycastContext);
      if (result != null && result.method_17777().equals(blockPos)) {
         return false;
      } else {
         return !PlayerUtils.isWithin(pos, (Double)this.wallsRange.get());
      }
   }

   private void addTargetedBlockToList() {
      if (((Keybind)this.selectBlockBind.get()).isPressed() && this.mc.field_1755 == null) {
         class_239 hitResult = this.mc.field_1765;
         if (hitResult != null && hitResult.method_17783() == class_240.field_1332) {
            class_2338 pos = ((class_3965)hitResult).method_17777();
            class_2248 targetBlock = this.mc.field_1687.method_8320(pos).method_26204();
            List<class_2248> list = this.listMode.get() == Nuker.ListMode.Whitelist ? (List)this.whitelist.get() : (List)this.blacklist.get();
            String modeName = ((ListMode)this.listMode.get()).name();
            if (list.contains(targetBlock)) {
               list.remove(targetBlock);
               this.info("Removed " + Names.get(targetBlock) + " from " + modeName, new Object[0]);
            } else {
               list.add(targetBlock);
               this.info("Added " + Names.get(targetBlock) + " to " + modeName, new Object[0]);
            }

         }
      }
   }

   @EventHandler(
      priority = 200
   )
   private void onBlockBreakingCooldown(BlockBreakingCooldownEvent event) {
      event.cooldown = 0;
   }

   public static int chebyshevDist(int x1, int y1, int z1, int x2, int y2, int z2) {
      int dX = Math.abs(x2 - x1);
      int dY = Math.abs(y2 - y1);
      int dZ = Math.abs(z2 - z1);
      return Math.max(Math.max(dX, dY), dZ);
   }

   public static enum ListMode {
      Whitelist,
      Blacklist;

      // $FF: synthetic method
      private static ListMode[] $values() {
         return new ListMode[]{Whitelist, Blacklist};
      }
   }

   public static enum Mode {
      All,
      Flatten,
      Smash;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{All, Flatten, Smash};
      }
   }

   public static enum SortMode {
      None,
      Closest,
      Furthest,
      TopDown;

      // $FF: synthetic method
      private static SortMode[] $values() {
         return new SortMode[]{None, Closest, Furthest, TopDown};
      }
   }

   public static enum Shape {
      Cube,
      UniformCube,
      Sphere;

      // $FF: synthetic method
      private static Shape[] $values() {
         return new Shape[]{Cube, UniformCube, Sphere};
      }
   }
}
