package meteordevelopment.meteorclient.systems.modules.render.blockesp;

import java.util.Map;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockDataSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.GenericSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_2248;
import org.jetbrains.annotations.Nullable;

public class ESPBlockDataScreen extends WindowScreen {
   private final ESPBlockData blockData;
   private final Setting<?> setting;
   private final @Nullable Runnable firstChangeConsumer;

   public ESPBlockDataScreen(GuiTheme theme, ESPBlockData blockData, class_2248 block, BlockDataSetting<ESPBlockData> setting) {
      this(theme, blockData, (Setting)setting, (Runnable)(() -> ((Map)setting.get()).put(block, blockData)));
   }

   public ESPBlockDataScreen(GuiTheme theme, ESPBlockData blockData, GenericSetting<ESPBlockData> setting) {
      this(theme, blockData, (Setting)setting, (Runnable)null);
   }

   private ESPBlockDataScreen(GuiTheme theme, ESPBlockData blockData, Setting<?> setting, @Nullable Runnable firstChangeConsumer) {
      super(theme, "Configure Block");
      this.blockData = blockData;
      this.setting = setting;
      this.firstChangeConsumer = firstChangeConsumer;
   }

   public void initWidgets() {
      Settings settings = new Settings();
      SettingGroup sgGeneral = settings.getDefaultGroup();
      SettingGroup sgTracer = settings.createGroup("Tracer");
      sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shape is rendered.")).defaultValue(ShapeMode.Lines)).onModuleActivated((shapeModeSetting) -> shapeModeSetting.set(this.blockData.shapeMode))).onChanged((shapeMode) -> {
         if (this.blockData.shapeMode != shapeMode) {
            this.blockData.shapeMode = shapeMode;
            this.onChanged();
         }

      })).build());
      sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("Color of lines.")).defaultValue(new SettingColor(0, 255, 200)).onModuleActivated((settingColorSetting) -> ((SettingColor)settingColorSetting.get()).set((Color)this.blockData.lineColor))).onChanged((settingColor) -> {
         if (!this.blockData.lineColor.equals(settingColor)) {
            this.blockData.lineColor.set((Color)settingColor);
            this.onChanged();
         }

      })).build());
      sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("Color of sides.")).defaultValue(new SettingColor(0, 255, 200, 25)).onModuleActivated((settingColorSetting) -> ((SettingColor)settingColorSetting.get()).set((Color)this.blockData.sideColor))).onChanged((settingColor) -> {
         if (!this.blockData.sideColor.equals(settingColor)) {
            this.blockData.sideColor.set((Color)settingColor);
            this.onChanged();
         }

      })).build());
      sgTracer.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("tracer")).description("If tracer line is allowed to this block.")).defaultValue(true)).onModuleActivated((booleanSetting) -> booleanSetting.set(this.blockData.tracer))).onChanged((aBoolean) -> {
         if (this.blockData.tracer != aBoolean) {
            this.blockData.tracer = aBoolean;
            this.onChanged();
         }

      })).build());
      sgTracer.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("tracer-color")).description("Color of tracer line.")).defaultValue(new SettingColor(0, 255, 200, 125)).onModuleActivated((settingColorSetting) -> ((SettingColor)settingColorSetting.get()).set((Color)this.blockData.tracerColor))).onChanged((settingColor) -> {
         if (!this.blockData.tracerColor.equals(settingColor)) {
            this.blockData.tracerColor.set((Color)settingColor);
            this.onChanged();
         }

      })).build());
      settings.onActivated();
      this.add(this.theme.settings(settings)).expandX();
   }

   private void onChanged() {
      if (!this.blockData.isChanged() && this.firstChangeConsumer != null) {
         this.firstChangeConsumer.run();
      }

      this.setting.onChanged();
      this.blockData.changed();
   }
}
