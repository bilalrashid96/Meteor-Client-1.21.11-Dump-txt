package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_265;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;

public class BlockSelection extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> advanced;
   private final Setting<Boolean> oneSide;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<Boolean> hideInside;

   public BlockSelection() {
      super(Categories.Render, "block-selection", "Modifies how your block selection is rendered.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.advanced = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("advanced")).description("Shows a more advanced outline on different types of shape blocks.")).defaultValue(true)).build());
      this.oneSide = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("single-side")).description("Only renders the side you are looking at.")).defaultValue(false)).build());
      this.shapeMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(255, 255, 255, 50)).build());
      this.lineColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.hideInside = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hide-when-inside-block")).description("Hide selection when inside target block.")).defaultValue(true)).build());
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.mc.field_1765 != null) {
         class_239 var3 = this.mc.field_1765;
         if (var3 instanceof class_3965) {
            class_3965 result = (class_3965)var3;
            if (result.method_17783() != class_240.field_1333) {
               if ((Boolean)this.hideInside.get() && result.method_17781()) {
                  return;
               }

               class_2338 bp = result.method_17777();
               class_2350 side = result.method_17780();
               class_265 shape = this.mc.field_1687.method_8320(bp).method_26218(this.mc.field_1687, bp);
               if (shape.method_1110()) {
                  return;
               }

               class_238 box = shape.method_1107();
               if ((Boolean)this.oneSide.get()) {
                  if (side != class_2350.field_11036 && side != class_2350.field_11033) {
                     if (side != class_2350.field_11035 && side != class_2350.field_11043) {
                        double x = side == class_2350.field_11039 ? box.field_1323 : box.field_1320;
                        event.renderer.sideVertical((double)bp.method_10263() + x, (double)bp.method_10264() + box.field_1322, (double)bp.method_10260() + box.field_1321, (double)bp.method_10263() + x, (double)bp.method_10264() + box.field_1325, (double)bp.method_10260() + box.field_1324, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get());
                     } else {
                        double z = side == class_2350.field_11043 ? box.field_1321 : box.field_1324;
                        event.renderer.sideVertical((double)bp.method_10263() + box.field_1323, (double)bp.method_10264() + box.field_1322, (double)bp.method_10260() + z, (double)bp.method_10263() + box.field_1320, (double)bp.method_10264() + box.field_1325, (double)bp.method_10260() + z, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get());
                     }
                  } else {
                     event.renderer.sideHorizontal((double)bp.method_10263() + box.field_1323, (double)bp.method_10264() + (side == class_2350.field_11033 ? box.field_1322 : box.field_1325), (double)bp.method_10260() + box.field_1321, (double)bp.method_10263() + box.field_1320, (double)bp.method_10260() + box.field_1324, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get());
                  }
               } else if ((Boolean)this.advanced.get()) {
                  if (this.shapeMode.get() == ShapeMode.Both || this.shapeMode.get() == ShapeMode.Lines) {
                     shape.method_1104((minX, minY, minZ, maxX, maxY, maxZ) -> event.renderer.line((double)bp.method_10263() + minX, (double)bp.method_10264() + minY, (double)bp.method_10260() + minZ, (double)bp.method_10263() + maxX, (double)bp.method_10264() + maxY, (double)bp.method_10260() + maxZ, this.lineColor.get()));
                  }

                  if (this.shapeMode.get() == ShapeMode.Both || this.shapeMode.get() == ShapeMode.Sides) {
                     for(class_238 b : shape.method_1090()) {
                        this.render(event, bp, b);
                     }
                  }
               } else {
                  this.render(event, bp, box);
               }

               return;
            }
         }
      }

   }

   private void render(Render3DEvent event, class_2338 bp, class_238 box) {
      event.renderer.box((double)bp.method_10263() + box.field_1323, (double)bp.method_10264() + box.field_1322, (double)bp.method_10260() + box.field_1321, (double)bp.method_10263() + box.field_1320, (double)bp.method_10264() + box.field_1325, (double)bp.method_10260() + box.field_1324, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
   }
}
