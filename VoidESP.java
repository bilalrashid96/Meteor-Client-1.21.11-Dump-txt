package meteordevelopment.meteorclient.systems.modules.render;

import java.util.ArrayList;
import java.util.List;
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
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dir;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_7134;

public class VoidESP extends Module {
   private static final class_2350[] SIDES;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Boolean> airOnly;
   private final Setting<Integer> horizontalRadius;
   private final Setting<Integer> holeHeight;
   private final Setting<Boolean> netherRoof;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final class_2338.class_2339 blockPos;
   private final Pool<Void> voidHolePool;
   private final List<Void> voidHoles;

   public VoidESP() {
      super(Categories.Render, "void-esp", "Renders holes in bedrock layers that lead to the void.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.airOnly = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("air-only")).description("Checks bedrock only for air blocks.")).defaultValue(false)).build());
      this.horizontalRadius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("horizontal-radius")).description("Horizontal radius in which to search for holes.")).defaultValue(64)).min(0).sliderMax(256).build());
      this.holeHeight = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("hole-height")).description("The minimum hole height to be rendered.")).defaultValue(1)).min(1).sliderRange(1, 5).build());
      this.netherRoof = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("nether-roof")).description("Check for holes in nether roof.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("fill-color")).description("The color that fills holes in the void.")).defaultValue(new SettingColor(225, 25, 25, 50)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The color to draw lines of holes to the void.")).defaultValue(new SettingColor(225, 25, 255)).build());
      this.blockPos = new class_2338.class_2339();
      this.voidHolePool = new Pool<Void>(() -> new Void());
      this.voidHoles = new ArrayList();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      this.voidHoles.clear();
      if (this.mc.field_1687.method_40134() != class_7134.field_37668) {
         int px = this.mc.field_1724.method_24515().method_10263();
         int pz = this.mc.field_1724.method_24515().method_10260();
         int radius = (Integer)this.horizontalRadius.get();

         for(int x = px - radius; x <= px + radius; ++x) {
            for(int z = pz - radius; z <= pz + radius; ++z) {
               this.blockPos.method_10103(x, this.mc.field_1687.method_31607(), z);
               if (this.isHole(this.blockPos, false)) {
                  this.voidHoles.add(((Void)this.voidHolePool.get()).set(this.blockPos.method_10103(x, this.mc.field_1687.method_31607(), z), false));
               }

               if ((Boolean)this.netherRoof.get() && this.mc.field_1687.method_40134() == class_7134.field_37667) {
                  this.blockPos.method_10103(x, 127, z);
                  if (this.isHole(this.blockPos, true)) {
                     this.voidHoles.add(((Void)this.voidHolePool.get()).set(this.blockPos.method_10103(x, 127, z), true));
                  }
               }
            }
         }

      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      for(Void voidHole : this.voidHoles) {
         voidHole.render(event);
      }

   }

   private boolean isBlockWrong(class_2338 blockPos) {
      class_2791 chunk = this.mc.field_1687.method_8402(blockPos.method_10263() >> 4, blockPos.method_10260() >> 4, class_2806.field_12803, false);
      if (chunk == null) {
         return true;
      } else {
         class_2248 block = chunk.method_8320(blockPos).method_26204();
         if ((Boolean)this.airOnly.get()) {
            return block != class_2246.field_10124;
         } else {
            return block == class_2246.field_9987;
         }
      }
   }

   private boolean isHole(class_2338.class_2339 blockPos, boolean nether) {
      for(int i = 0; i < (Integer)this.holeHeight.get(); ++i) {
         blockPos.method_33098(nether ? 127 - i : this.mc.field_1687.method_31607() + i);
         if (this.isBlockWrong(blockPos)) {
            return false;
         }
      }

      return true;
   }

   static {
      SIDES = new class_2350[]{class_2350.field_11034, class_2350.field_11043, class_2350.field_11035, class_2350.field_11039};
   }

   private class Void {
      private int x;
      private int y;
      private int z;
      private int excludeDir;

      public Void set(class_2338.class_2339 blockPos, boolean nether) {
         this.x = blockPos.method_10263();
         this.y = blockPos.method_10264();
         this.z = blockPos.method_10260();
         this.excludeDir = 0;

         for(class_2350 side : VoidESP.SIDES) {
            blockPos.method_10103(this.x + side.method_10148(), this.y, this.z + side.method_10165());
            if (VoidESP.this.isHole(blockPos, nether)) {
               this.excludeDir |= Dir.get(side);
            }
         }

         return this;
      }

      public void render(Render3DEvent event) {
         event.renderer.box((double)this.x, (double)this.y, (double)this.z, (double)(this.x + 1), (double)(this.y + 1), (double)(this.z + 1), VoidESP.this.sideColor.get(), VoidESP.this.lineColor.get(), VoidESP.this.shapeMode.get(), this.excludeDir);
      }
   }
}
