package meteordevelopment.meteorclient.systems.modules.render.blockesp;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockDataSetting;
import meteordevelopment.meteorclient.settings.GenericSetting;
import meteordevelopment.meteorclient.settings.IBlockData;
import meteordevelopment.meteorclient.settings.IGeneric;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_2248;
import net.minecraft.class_2487;

public class ESPBlockData implements IGeneric<ESPBlockData>, IChangeable, IBlockData<ESPBlockData> {
   public ShapeMode shapeMode;
   public SettingColor lineColor;
   public SettingColor sideColor;
   public boolean tracer;
   public SettingColor tracerColor;
   private boolean changed;

   public ESPBlockData(ShapeMode shapeMode, SettingColor lineColor, SettingColor sideColor, boolean tracer, SettingColor tracerColor) {
      this.shapeMode = shapeMode;
      this.lineColor = lineColor;
      this.sideColor = sideColor;
      this.tracer = tracer;
      this.tracerColor = tracerColor;
   }

   public WidgetScreen createScreen(GuiTheme theme, class_2248 block, BlockDataSetting<ESPBlockData> setting) {
      return new ESPBlockDataScreen(theme, this, block, setting);
   }

   public WidgetScreen createScreen(GuiTheme theme, GenericSetting<ESPBlockData> setting) {
      return new ESPBlockDataScreen(theme, this, setting);
   }

   public boolean isChanged() {
      return this.changed;
   }

   public void changed() {
      this.changed = true;
   }

   public void tickRainbow() {
      this.lineColor.update();
      this.sideColor.update();
      this.tracerColor.update();
   }

   public ESPBlockData set(ESPBlockData value) {
      this.shapeMode = value.shapeMode;
      this.lineColor.set((Color)value.lineColor);
      this.sideColor.set((Color)value.sideColor);
      this.tracer = value.tracer;
      this.tracerColor.set((Color)value.tracerColor);
      this.changed = value.changed;
      return this;
   }

   public ESPBlockData copy() {
      return new ESPBlockData(this.shapeMode, new SettingColor(this.lineColor), new SettingColor(this.sideColor), this.tracer, new SettingColor(this.tracerColor));
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("shapeMode", this.shapeMode.name());
      tag.method_10566("lineColor", this.lineColor.toTag());
      tag.method_10566("sideColor", this.sideColor.toTag());
      tag.method_10556("tracer", this.tracer);
      tag.method_10566("tracerColor", this.tracerColor.toTag());
      tag.method_10556("changed", this.changed);
      return tag;
   }

   public ESPBlockData fromTag(class_2487 tag) {
      this.shapeMode = ShapeMode.valueOf(tag.method_68564("shapeMode", ""));
      this.lineColor.fromTag(tag.method_68568("lineColor"));
      this.sideColor.fromTag(tag.method_68568("sideColor"));
      this.tracer = tag.method_68566("tracer", false);
      this.tracerColor.fromTag(tag.method_68568("tracerColor"));
      this.changed = tag.method_68566("changed", false);
      return this;
   }
}
