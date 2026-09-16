package meteordevelopment.meteorclient.systems.modules.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BreakIndicators;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_2350.class_2351;
import net.minecraft.class_2846.class_2847;

public class PacketMine extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Integer> delay;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> autoSwitch;
   private final Setting<Boolean> notOnUse;
   private final Setting<Boolean> obscureBreakingProgress;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> readySideColor;
   private final Setting<SettingColor> readyLineColor;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Pool<MyBlock> blockPool;
   public final List<MyBlock> blocks;
   private boolean swapped;
   private boolean shouldUpdateSlot;

   public PacketMine() {
      super(Categories.World, "packet-mine", "Sends packets to mine blocks without the mining animation.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("Delay between mining blocks in ticks.")).defaultValue(1)).min(0).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Sends rotation packets to the server when mining.")).defaultValue(true)).build());
      this.autoSwitch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-switch")).description("Automatically switches to the best tool when the block is ready to be mined instantly.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("not-on-use")).description("Won't auto switch if you're using an item.")).defaultValue(true);
      Setting var10003 = this.autoSwitch;
      Objects.requireNonNull(var10003);
      this.notOnUse = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.obscureBreakingProgress = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("obscure-breaking-progress")).description("Spams abort breaking packets to obscure the block mining progress from other players. Does not hide it perfectly.")).defaultValue(false)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Whether or not to render the block being mined.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.readySideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ready-side-color")).description("The color of the sides of the blocks that can be broken.")).defaultValue(new SettingColor(0, 204, 0, 10)).build());
      this.readyLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ready-line-color")).description("The color of the lines of the blocks that can be broken.")).defaultValue(new SettingColor(0, 204, 0, 255)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The color of the sides of the blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 10)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The color of the lines of the blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 255)).build());
      this.blockPool = new Pool<MyBlock>(() -> new MyBlock());
      this.blocks = new ArrayList();
   }

   public void onActivate() {
      this.swapped = false;
   }

   public void onDeactivate() {
      this.blockPool.freeAll(this.blocks);
      this.blocks.clear();
      if (this.shouldUpdateSlot) {
         this.mc.field_1724.field_3944.method_52787(new class_2868(this.mc.field_1724.method_31548().method_67532()));
         this.shouldUpdateSlot = false;
      }

   }

   @EventHandler
   private void onStartBreakingBlock(StartBreakingBlockEvent event) {
      if (BlockUtils.canBreak(event.blockPos)) {
         event.cancel();
         this.swapped = false;
         if (!this.isMiningBlock(event.blockPos)) {
            this.blocks.add(((MyBlock)this.blockPool.get()).set(event));
         }

      }
   }

   public boolean isMiningBlock(class_2338 pos) {
      for(MyBlock block : this.blocks) {
         if (block.blockPos.equals(pos)) {
            return true;
         }
      }

      return false;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.blocks.removeIf(MyBlock::shouldRemove);
      if (this.shouldUpdateSlot) {
         this.mc.field_1724.field_3944.method_52787(new class_2868(this.mc.field_1724.method_31548().method_67532()));
         this.shouldUpdateSlot = false;
         this.swapped = false;
      }

      if (!this.blocks.isEmpty()) {
         MyBlock block = (MyBlock)this.blocks.getFirst();
         block.mine();
         if (block.isReady() && !this.swapped && (Boolean)this.autoSwitch.get() && (!this.mc.field_1724.method_6115() || !(Boolean)this.notOnUse.get())) {
            FindItemResult slot = InvUtils.findFastestTool(block.blockState);
            if (!slot.found() || this.mc.field_1724.method_31548().method_67532() == slot.slot()) {
               return;
            }

            this.mc.field_1724.field_3944.method_52787(new class_2868(slot.slot()));
            this.swapped = true;
            this.shouldUpdateSlot = true;
         }
      }

   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         for(MyBlock block : this.blocks) {
            if (!((BreakIndicators)Modules.get().get(BreakIndicators.class)).isActive() || !(Boolean)((BreakIndicators)Modules.get().get(BreakIndicators.class)).packetMine.get() || !block.mining) {
               block.render(event);
            }
         }

      }
   }

   public class MyBlock {
      public class_2338 blockPos;
      public class_2680 blockState;
      public class_2248 block;
      public class_2350 direction;
      public int timer;
      public int startTime;
      public boolean mining;

      public MyBlock set(StartBreakingBlockEvent event) {
         this.blockPos = event.blockPos;
         this.direction = event.direction;
         this.blockState = PacketMine.this.mc.field_1687.method_8320(this.blockPos);
         this.block = this.blockState.method_26204();
         this.timer = (Integer)PacketMine.this.delay.get();
         this.mining = false;
         return this;
      }

      public boolean shouldRemove() {
         boolean broken = PacketMine.this.mc.field_1687.method_8320(this.blockPos).method_26204() != this.block;
         boolean timeout = this.progress() > (double)2.0F && PacketMine.this.mc.field_1724.field_6012 - this.startTime > 50;
         boolean distance = Utils.distance(PacketMine.this.mc.field_1724.method_33571().field_1352, PacketMine.this.mc.field_1724.method_33571().field_1351, PacketMine.this.mc.field_1724.method_33571().field_1350, (double)(this.blockPos.method_10263() + this.direction.method_10148()), (double)(this.blockPos.method_10264() + this.direction.method_10164()), (double)(this.blockPos.method_10260() + this.direction.method_10165())) > PacketMine.this.mc.field_1724.method_55754();
         return broken || timeout || distance;
      }

      public boolean isReady() {
         return this.progress() >= (double)1.0F;
      }

      public double progress() {
         if (!this.mining) {
            return (double)0.0F;
         } else {
            FindItemResult fir = InvUtils.findFastestTool(this.blockState);
            return BlockUtils.getBreakDelta(fir.found() ? fir.slot() : PacketMine.this.mc.field_1724.method_31548().method_67532(), this.blockState) * (double)(PacketMine.this.mc.field_1724.field_6012 - this.startTime + 1);
         }
      }

      public void mine() {
         if ((Boolean)PacketMine.this.rotate.get()) {
            Rotations.rotate(Rotations.getYaw(this.blockPos), Rotations.getPitch(this.blockPos), 50, this::sendMinePackets);
         } else {
            this.sendMinePackets();
         }

      }

      private void sendMinePackets() {
         if (this.timer <= 0) {
            if (!this.mining) {
               PacketMine.this.mc.field_1761.method_41931(PacketMine.this.mc.field_1687, (sequence) -> new class_2846(class_2847.field_12968, this.blockPos, this.direction, sequence));
               PacketMine.this.mc.field_1761.method_41931(PacketMine.this.mc.field_1687, (sequence) -> new class_2846(class_2847.field_12973, this.blockPos, this.direction, sequence));
               this.mining = true;
               this.startTime = PacketMine.this.mc.field_1724.field_6012;
            }
         } else {
            --this.timer;
         }

         if (this.mining && (Boolean)PacketMine.this.obscureBreakingProgress.get()) {
            PacketMine.this.mc.method_1562().method_52787(new class_2846(class_2847.field_12971, this.blockPos, this.direction));
         }

      }

      public void render(Render3DEvent event) {
         class_265 shape = PacketMine.this.mc.field_1687.method_8320(this.blockPos).method_26218(PacketMine.this.mc.field_1687, this.blockPos);
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

         if (this.isReady()) {
            event.renderer.box(x1, y1, z1, x2, y2, z2, PacketMine.this.readySideColor.get(), PacketMine.this.readyLineColor.get(), PacketMine.this.shapeMode.get(), 0);
         } else {
            event.renderer.box(x1, y1, z1, x2, y2, z2, PacketMine.this.sideColor.get(), PacketMine.this.lineColor.get(), PacketMine.this.shapeMode.get(), 0);
         }

      }
   }
}
