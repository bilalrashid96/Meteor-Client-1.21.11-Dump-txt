package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.List;
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
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_3726;

public class SelfTrap extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<List<class_2248>> blocks;
   private final Setting<TopMode> topPlacement;
   private final Setting<BottomMode> bottomPlacement;
   private final Setting<Integer> delaySetting;
   private final Setting<Boolean> center;
   private final Setting<Boolean> turnOff;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final List<class_2338> placePositions;
   private boolean placed;
   private int delay;

   public SelfTrap() {
      super(Categories.Combat, "self-trap", "Places blocks above your head.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("whitelist")).description("Which blocks to use.")).defaultValue(class_2246.field_10540, class_2246.field_22108).build());
      this.topPlacement = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("top-mode")).description("Which positions to place on your top half.")).defaultValue(SelfTrap.TopMode.Top)).build());
      this.bottomPlacement = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("bottom-mode")).description("Which positions to place on your bottom half.")).defaultValue(SelfTrap.BottomMode.None)).build());
      this.delaySetting = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("How many ticks between block placements.")).defaultValue(1)).build());
      this.center = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("center")).description("Centers you on the block you are standing on before placing.")).defaultValue(true)).build());
      this.turnOff = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("turn-off")).description("Turns off after placing.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Sends rotation packets to the server when placing.")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders a block overlay where the blocks will be placed.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The color of the sides of the blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 10)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The color of the lines of the blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 255)).build());
      this.placePositions = new ArrayList();
   }

   public void onActivate() {
      if (!this.placePositions.isEmpty()) {
         this.placePositions.clear();
      }

      this.delay = 0;
      this.placed = false;
      if ((Boolean)this.center.get()) {
         PlayerUtils.centerPlayer();
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      for(class_2248 currentBlock : this.blocks.get()) {
         FindItemResult itemResult = InvUtils.findInHotbar(currentBlock.method_8389());
         if (!(Boolean)this.turnOff.get() || (!this.placed || !this.placePositions.isEmpty()) && itemResult.found()) {
            if (itemResult.found()) {
               this.findPlacePos(currentBlock);
               if (this.delay >= (Integer)this.delaySetting.get() && !this.placePositions.isEmpty()) {
                  class_2338 blockPos = (class_2338)this.placePositions.getLast();
                  if (BlockUtils.place(blockPos, itemResult, (Boolean)this.rotate.get(), 50)) {
                     this.placePositions.remove(blockPos);
                     this.placed = true;
                  }

                  this.delay = 0;
               } else {
                  ++this.delay;
               }

               return;
            }

            this.placePositions.clear();
         } else {
            this.toggle();
         }
      }

   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get() && !this.placePositions.isEmpty()) {
         for(class_2338 pos : this.placePositions) {
            event.renderer.box((class_2338)pos, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
         }

      }
   }

   private void findPlacePos(class_2248 block) {
      this.placePositions.clear();
      class_2338 pos = this.mc.field_1724.method_24515();
      switch (((TopMode)this.topPlacement.get()).ordinal()) {
         case 0:
            this.add(pos.method_10069(1, 1, 0), block);
            this.add(pos.method_10069(-1, 1, 0), block);
            this.add(pos.method_10069(0, 1, 1), block);
            this.add(pos.method_10069(0, 1, -1), block);
            break;
         case 1:
            this.add(pos.method_10069(0, 2, 0), block);
            this.add(pos.method_10069(1, 1, 0), block);
            this.add(pos.method_10069(-1, 1, 0), block);
            this.add(pos.method_10069(0, 1, 1), block);
            this.add(pos.method_10069(0, 1, -1), block);
            break;
         case 2:
            this.add(pos.method_10069(0, 2, 0), block);
      }

      if (this.bottomPlacement.get() == SelfTrap.BottomMode.Single) {
         this.add(pos.method_10069(0, -1, 0), block);
      }

   }

   private void add(class_2338 blockPos, class_2248 block) {
      if (!this.placePositions.contains(blockPos) && this.mc.field_1687.method_8320(blockPos).method_45474() && this.mc.field_1687.method_8628(block.method_9564(), blockPos, class_3726.method_16194())) {
         this.placePositions.add(blockPos);
      }

   }

   public static enum TopMode {
      AntiFacePlace,
      Full,
      Top,
      None;

      // $FF: synthetic method
      private static TopMode[] $values() {
         return new TopMode[]{AntiFacePlace, Full, Top, None};
      }
   }

   public static enum BottomMode {
      Single,
      None;

      // $FF: synthetic method
      private static BottomMode[] $values() {
         return new BottomMode[]{Single, None};
      }
   }
}
