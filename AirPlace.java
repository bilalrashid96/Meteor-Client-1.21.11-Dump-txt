package meteordevelopment.meteorclient.systems.modules.player;

import java.util.Objects;
import meteordevelopment.meteorclient.events.entity.player.InteractItemEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1269;
import net.minecraft.class_1742;
import net.minecraft.class_1747;
import net.minecraft.class_1781;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1826;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;

public class AirPlace extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRange;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<Boolean> customRange;
   private final Setting<Double> range;
   private class_239 hitResult;

   public AirPlace() {
      super(Categories.Player, "air-place", "Places a block where your crosshair is pointing at.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRange = this.settings.createGroup("Range");
      this.render = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders a block overlay where the obsidian will be placed.")).defaultValue(true)).build());
      this.shapeMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The color of the sides of the blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 10)).build());
      this.lineColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The color of the lines of the blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 255)).build());
      this.customRange = this.sgRange.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-range")).description("Use custom range for air place.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgRange;
      DoubleSetting.Builder var10002 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("Custom range to place at.");
      Setting var10003 = this.customRange;
      Objects.requireNonNull(var10003);
      this.range = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).defaultValue((double)5.0F).min((double)0.0F).sliderMax((double)6.0F).build());
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (InvUtils.testInHands(this::placeable)) {
         if (this.mc.field_1765 == null || this.mc.field_1765.method_17783() == class_240.field_1333) {
            double r = (Boolean)this.customRange.get() ? (Double)this.range.get() : this.mc.field_1724.method_55754();
            this.hitResult = this.mc.method_1560().method_5745(r, 0.0F, false);
         }
      }
   }

   @EventHandler
   private void onInteractItem(InteractItemEvent event) {
      class_239 var3 = this.hitResult;
      if (var3 instanceof class_3965 bhr) {
         if (this.placeable(this.mc.field_1724.method_5998(event.hand))) {
            class_2248 toPlace = class_2246.field_10540;
            class_1792 i = this.mc.field_1724.method_5998(event.hand).method_7909();
            if (i instanceof class_1747) {
               class_1747 blockItem = (class_1747)i;
               toPlace = blockItem.method_7711();
            }

            if (!BlockUtils.canPlaceBlock(bhr.method_17777(), i instanceof class_1742 || i instanceof class_1747, toPlace)) {
               return;
            }

            class_243 hitPos = class_243.method_24953(bhr.method_17777());
            class_3965 b = new class_3965(hitPos, this.mc.field_1724.method_5755().method_10153(), bhr.method_17777(), false);
            BlockUtils.interact(b, event.hand, true);
            event.toReturn = class_1269.field_5812;
            return;
         }
      }

   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      class_239 var3 = this.hitResult;
      if (var3 instanceof class_3965 bhr) {
         if ((this.mc.field_1765 == null || this.mc.field_1765.method_17783() == class_240.field_1333) && this.mc.field_1687.method_8320(bhr.method_17777()).method_45474() && InvUtils.testInHands(this::placeable) && (Boolean)this.render.get()) {
            event.renderer.box((class_2338)bhr.method_17777(), this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
            return;
         }
      }

   }

   private boolean placeable(class_1799 stack) {
      class_1792 i = stack.method_7909();
      return i instanceof class_1747 || i instanceof class_1826 || i instanceof class_1781 || i instanceof class_1742;
   }
}
