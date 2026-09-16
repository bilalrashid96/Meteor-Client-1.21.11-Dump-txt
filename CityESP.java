package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_2338;

public class CityESP extends Module {
   private final SettingGroup sgRender;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private class_2338 target;

   public CityESP() {
      super(Categories.Render, "city-esp", "Displays blocks that can be broken in order to city another player.");
      this.sgRender = this.settings.createGroup("Render");
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the rendering.")).defaultValue(new SettingColor(225, 0, 0, 75)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the rendering.")).defaultValue(new SettingColor(225, 0, 0, 255)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      class_1657 targetEntity = TargetUtils.getPlayerTarget(this.mc.field_1724.method_55754() + (double)2.0F, SortPriority.LowestDistance);
      if (TargetUtils.isBadTarget(targetEntity, this.mc.field_1724.method_55754() + (double)2.0F)) {
         this.target = null;
      } else {
         this.target = EntityUtils.getCityBlock(targetEntity);
      }

   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.target != null) {
         event.renderer.box((class_2338)this.target, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
      }
   }
}
