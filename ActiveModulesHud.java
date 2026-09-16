package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ModuleListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Alignment;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class ActiveModulesHud extends HudElement {
   public static final HudElementInfo<ActiveModulesHud> INFO;
   private static final Color WHITE;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgColor;
   private final SettingGroup sgScale;
   private final SettingGroup sgBackground;
   private final Setting<Sort> sort;
   private final Setting<List<Module>> hiddenModules;
   private final Setting<Boolean> activeInfo;
   private final Setting<Boolean> showKeybind;
   private final Setting<Boolean> shadow;
   private final Setting<Boolean> outlines;
   private final Setting<Integer> outlineWidth;
   private final Setting<Alignment> alignment;
   private final Setting<ColorMode> colorMode;
   private final Setting<SettingColor> flatColor;
   private final Setting<Double> rainbowSpeed;
   private final Setting<Double> rainbowSpread;
   private final Setting<Double> rainbowSaturation;
   private final Setting<Double> rainbowBrightness;
   private final Setting<SettingColor> moduleInfoColor;
   private final Setting<Boolean> customScale;
   private final Setting<Double> scale;
   private final Setting<Boolean> background;
   private final Setting<SettingColor> backgroundColor;
   private final List<Module> modules;
   private final Color rainbow;
   private double rainbowHue1;
   private double rainbowHue2;
   private double lastX;
   private double emptySpace;
   private double prevTextLength;
   private Color prevColor;

   public ActiveModulesHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgColor = this.settings.createGroup("Color");
      this.sgScale = this.settings.createGroup("Scale");
      this.sgBackground = this.settings.createGroup("Background");
      this.sort = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("sort")).description("How to sort active modules.")).defaultValue(ActiveModulesHud.Sort.Biggest)).build());
      this.hiddenModules = this.sgGeneral.add(((ModuleListSetting.Builder)((ModuleListSetting.Builder)(new ModuleListSetting.Builder()).name("hidden-modules")).description("Which modules not to show in the list.")).build());
      this.activeInfo = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("module-info")).description("Shows info from the module next to the name in the active modules list.")).defaultValue(true)).build());
      this.showKeybind = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-keybind")).description("Shows the module's keybind next to its name.")).defaultValue(false)).build());
      this.shadow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("shadow")).description("Renders shadow behind text.")).defaultValue(true)).build());
      this.outlines = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("outlines")).description("Whether or not to render outlines")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("outline-width")).description("Outline width")).defaultValue(2)).min(1).sliderMin(1);
      Setting var10003 = this.outlines;
      Objects.requireNonNull(var10003);
      this.outlineWidth = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.alignment = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("alignment")).description("Horizontal alignment.")).defaultValue(Alignment.Auto)).build());
      this.colorMode = this.sgColor.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("color-mode")).description("What color to use for active modules.")).defaultValue(ActiveModulesHud.ColorMode.Rainbow)).build());
      this.flatColor = this.sgColor.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("flat-color")).description("Color for flat color mode.")).defaultValue(new SettingColor(225, 25, 25)).visible(() -> this.colorMode.get() == ActiveModulesHud.ColorMode.Flat)).build());
      this.rainbowSpeed = this.sgColor.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rainbow-speed")).description("Rainbow speed of rainbow color mode.")).defaultValue(0.05).sliderMin(0.01).sliderMax(0.2).decimalPlaces(4).visible(() -> this.colorMode.get() == ActiveModulesHud.ColorMode.Rainbow)).build());
      this.rainbowSpread = this.sgColor.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rainbow-spread")).description("Rainbow spread of rainbow color mode.")).defaultValue(0.01).sliderMin(0.001).sliderMax(0.05).decimalPlaces(4).visible(() -> this.colorMode.get() == ActiveModulesHud.ColorMode.Rainbow)).build());
      this.rainbowSaturation = this.sgColor.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rainbow-saturation")).defaultValue((double)1.0F).sliderRange((double)0.0F, (double)1.0F).visible(() -> this.colorMode.get() == ActiveModulesHud.ColorMode.Rainbow)).build());
      this.rainbowBrightness = this.sgColor.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rainbow-brightness")).defaultValue((double)1.0F).sliderRange((double)0.0F, (double)1.0F).visible(() -> this.colorMode.get() == ActiveModulesHud.ColorMode.Rainbow)).build());
      var10001 = this.sgColor;
      ColorSetting.Builder var4 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("module-info-color")).description("Color of module info text.")).defaultValue(new SettingColor(175, 175, 175));
      var10003 = this.activeInfo;
      Objects.requireNonNull(var10003);
      this.moduleInfoColor = var10001.add(((ColorSetting.Builder)var4.visible(var10003::get)).build());
      this.customScale = this.sgScale.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-scale")).description("Applies a custom scale to this hud element.")).defaultValue(false)).build());
      var10001 = this.sgScale;
      DoubleSetting.Builder var5 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Custom scale.");
      var10003 = this.customScale;
      Objects.requireNonNull(var10003);
      this.scale = var10001.add(((DoubleSetting.Builder)var5.visible(var10003::get)).defaultValue((double)1.0F).min((double)0.5F).sliderRange((double)0.5F, (double)3.0F).build());
      this.background = this.sgBackground.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      var10001 = this.sgBackground;
      ColorSetting.Builder var6 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var6.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
      this.modules = new ArrayList();
      this.rainbow = new Color(255, 255, 255);
      this.prevColor = new Color();
   }

   public void tick(HudRenderer renderer) {
      this.modules.clear();

      for(Module module : Modules.get().getActive()) {
         if (!((List)this.hiddenModules.get()).contains(module)) {
            this.modules.add(module);
         }
      }

      if (this.modules.isEmpty()) {
         if (this.isInEditor()) {
            this.setSize(renderer.textWidth("Active Modules", (Boolean)this.shadow.get(), this.getScale()), renderer.textHeight((Boolean)this.shadow.get(), this.getScale()));
         }

      } else {
         this.modules.sort((e1, e2) -> {
            int var10000;
            switch (((Sort)this.sort.get()).ordinal()) {
               case 0 -> var10000 = e1.title.compareTo(e2.title);
               case 1 -> var10000 = Double.compare(this.getModuleWidth(renderer, e2), this.getModuleWidth(renderer, e1));
               case 2 -> var10000 = Double.compare(this.getModuleWidth(renderer, e1), this.getModuleWidth(renderer, e2));
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            return var10000;
         });
         double width = (double)0.0F;
         double height = (double)0.0F;

         for(Module module : this.modules) {
            width = Math.max(width, this.getModuleWidth(renderer, module));
            height += renderer.textHeight((Boolean)this.shadow.get(), this.getScale());
         }

         this.setSize(width, height);
      }
   }

   public void render(HudRenderer renderer) {
      double x = (double)this.x;
      double y = (double)this.y;
      if (this.modules.isEmpty()) {
         if (this.isInEditor()) {
            renderer.text("Active Modules", x, y, WHITE, (Boolean)this.shadow.get(), this.getScale());
         }

      } else {
         this.rainbowHue1 += (Double)this.rainbowSpeed.get() * renderer.delta;
         if (this.rainbowHue1 > (double)1.0F) {
            --this.rainbowHue1;
         } else if (this.rainbowHue1 < (double)-1.0F) {
            ++this.rainbowHue1;
         }

         this.rainbowHue2 = this.rainbowHue1;
         this.lastX = x;
         this.emptySpace = renderer.textWidth(" ", (Boolean)this.shadow.get(), this.getScale());

         for(int i = 0; i < this.modules.size(); ++i) {
            double offset = this.alignX(this.getModuleWidth(renderer, (Module)this.modules.get(i)), this.alignment.get());
            this.renderModule(renderer, i, x + offset, y);
            this.lastX = x + offset;
            y += renderer.textHeight((Boolean)this.shadow.get(), this.getScale());
         }

      }
   }

   private void renderModule(HudRenderer renderer, int index, double x, double y) {
      Module module = (Module)this.modules.get(index);
      Color color = this.flatColor.get();
      switch (((ColorMode)this.colorMode.get()).ordinal()) {
         case 1:
            color = module.color;
            break;
         case 2:
            this.rainbowHue2 += (Double)this.rainbowSpread.get();
            int c = java.awt.Color.HSBtoRGB((float)this.rainbowHue2, ((Double)this.rainbowSaturation.get()).floatValue(), ((Double)this.rainbowBrightness.get()).floatValue());
            this.rainbow.r = Color.toRGBAR(c);
            this.rainbow.g = Color.toRGBAG(c);
            this.rainbow.b = Color.toRGBAB(c);
            color = this.rainbow;
      }

      renderer.text(module.title, x, y, color, (Boolean)this.shadow.get(), this.getScale());
      double textHeight = renderer.textHeight((Boolean)this.shadow.get(), this.getScale());
      double textLength = renderer.textWidth(module.title, (Boolean)this.shadow.get(), this.getScale());
      if ((Boolean)this.showKeybind.get() && module.keybind.isSet()) {
         String keybindStr = " [" + String.valueOf(module.keybind) + "]";
         renderer.text(keybindStr, x + textLength, y, this.moduleInfoColor.get(), (Boolean)this.shadow.get(), this.getScale());
         textLength += renderer.textWidth(keybindStr, (Boolean)this.shadow.get(), this.getScale());
      }

      if ((Boolean)this.activeInfo.get()) {
         String info = module.getInfoString();
         if (info != null) {
            renderer.text(info, x + textLength + this.emptySpace, y, this.moduleInfoColor.get(), (Boolean)this.shadow.get(), this.getScale());
            textLength += this.emptySpace + renderer.textWidth(info, (Boolean)this.shadow.get(), this.getScale());
         }
      }

      double lineStartY = y;
      double lineHeight = textHeight;
      if ((Boolean)this.outlines.get()) {
         if (index == 0) {
            lineStartY = y - (double)2.0F;
            lineHeight = textHeight + (double)2.0F;
            renderer.quad(x - (double)2.0F - (double)(Integer)this.outlineWidth.get(), lineStartY - (double)(Integer)this.outlineWidth.get(), textLength + (double)4.0F + (double)(2 * (Integer)this.outlineWidth.get()), (double)(Integer)this.outlineWidth.get(), this.prevColor, this.prevColor, color, color);
         } else {
            renderer.quad(Math.min(this.lastX, x) - (double)2.0F - (double)(Integer)this.outlineWidth.get(), Math.max(this.lastX, x) == x ? y : y - (double)(Integer)this.outlineWidth.get(), Math.max(this.lastX, x) - (double)2.0F - (Math.min(this.lastX, x) - (double)2.0F - (double)(Integer)this.outlineWidth.get()), (double)(Integer)this.outlineWidth.get(), this.prevColor, this.prevColor, color, color);
            renderer.quad(Math.min(this.lastX + this.prevTextLength, x + textLength) + (double)2.0F, Math.min(this.lastX + this.prevTextLength, x + textLength) == x + textLength ? y : y - (double)(Integer)this.outlineWidth.get(), Math.max(this.lastX + this.prevTextLength, x + textLength) + (double)2.0F + (double)(Integer)this.outlineWidth.get() - (Math.min(this.lastX + this.prevTextLength, x + textLength) + (double)2.0F), (double)(Integer)this.outlineWidth.get(), this.prevColor, this.prevColor, color, color);
         }

         if (index == this.modules.size() - 1) {
            lineHeight += (double)2.0F;
            renderer.quad(x - (double)2.0F - (double)(Integer)this.outlineWidth.get(), lineStartY + lineHeight, textLength + (double)4.0F + (double)(2 * (Integer)this.outlineWidth.get()), (double)(Integer)this.outlineWidth.get(), this.prevColor, this.prevColor, color, color);
         }

         renderer.quad(x - (double)2.0F - (double)(Integer)this.outlineWidth.get(), lineStartY, (double)(Integer)this.outlineWidth.get(), lineHeight, this.prevColor, this.prevColor, color, color);
         renderer.quad(x + textLength + (double)2.0F, lineStartY, (double)(Integer)this.outlineWidth.get(), lineHeight, this.prevColor, this.prevColor, color, color);
      }

      if ((Boolean)this.background.get()) {
         renderer.quad(x - (double)2.0F, lineStartY, textLength + (double)4.0F, lineHeight, this.backgroundColor.get());
      }

      this.prevTextLength = textLength;
      this.prevColor = color;
   }

   private double getModuleWidth(HudRenderer renderer, Module module) {
      double width = renderer.textWidth(module.title, (Boolean)this.shadow.get(), this.getScale());
      if ((Boolean)this.showKeybind.get() && module.keybind.isSet()) {
         width += renderer.textWidth(" [" + String.valueOf(module.keybind) + "]", (Boolean)this.shadow.get(), this.getScale());
      }

      if ((Boolean)this.activeInfo.get()) {
         String info = module.getInfoString();
         if (info != null) {
            width += renderer.textWidth(" ", (Boolean)this.shadow.get(), this.getScale()) + renderer.textWidth(info, (Boolean)this.shadow.get(), this.getScale());
         }
      }

      return width;
   }

   private double getScale() {
      return (Boolean)this.customScale.get() ? (Double)this.scale.get() : Hud.get().getTextScale();
   }

   static {
      INFO = new HudElementInfo<ActiveModulesHud>(Hud.GROUP, "active-modules", "Displays your active modules.", ActiveModulesHud::new);
      WHITE = new Color();
   }

   public static enum Sort {
      Alphabetical,
      Biggest,
      Smallest;

      // $FF: synthetic method
      private static Sort[] $values() {
         return new Sort[]{Alphabetical, Biggest, Smallest};
      }
   }

   public static enum ColorMode {
      Flat,
      Random,
      Rainbow;

      // $FF: synthetic method
      private static ColorMode[] $values() {
         return new ColorMode[]{Flat, Random, Rainbow};
      }
   }

   public static enum Background {
      None,
      Block,
      Text;

      // $FF: synthetic method
      private static Background[] $values() {
         return new Background[]{None, Block, Text};
      }
   }
}
