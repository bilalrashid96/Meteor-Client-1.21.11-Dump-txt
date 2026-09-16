package meteordevelopment.meteorclient.systems.hud.elements;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StatusEffectListSetting;
import meteordevelopment.meteorclient.systems.hud.Alignment;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.misc.Names;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1291;
import net.minecraft.class_1292;
import net.minecraft.class_1293;

public class PotionTimersHud extends HudElement {
   public static final HudElementInfo<PotionTimersHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgScale;
   private final SettingGroup sgBackground;
   private final Setting<List<class_1291>> hiddenEffects;
   private final Setting<Boolean> showAmbient;
   private final Setting<ColorMode> colorMode;
   private final Setting<SettingColor> flatColor;
   private final Setting<Double> rainbowSpeed;
   private final Setting<Double> rainbowSpread;
   private final Setting<Double> rainbowSaturation;
   private final Setting<Double> rainbowBrightness;
   private final Setting<Boolean> shadow;
   private final Setting<Alignment> alignment;
   private final Setting<Integer> border;
   private final Setting<Boolean> customScale;
   private final Setting<Double> scale;
   private final Setting<Boolean> background;
   private final Setting<SettingColor> backgroundColor;
   private final List<Pair<class_1293, String>> texts;
   private double rainbowHue;

   public PotionTimersHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgScale = this.settings.createGroup("Scale");
      this.sgBackground = this.settings.createGroup("Background");
      this.hiddenEffects = this.sgGeneral.add(((StatusEffectListSetting.Builder)((StatusEffectListSetting.Builder)(new StatusEffectListSetting.Builder()).name("hidden-effects")).description("Which effects not to show in the list.")).build());
      this.showAmbient = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-ambient")).description("Whether to show ambient effects like from beacons and conduits.")).defaultValue(true)).build());
      this.colorMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("color-mode")).description("What color to use for effects.")).defaultValue(PotionTimersHud.ColorMode.Effect)).build());
      this.flatColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("flat-color")).description("Color for flat color mode.")).defaultValue(new SettingColor(225, 25, 25)).visible(() -> this.colorMode.get() == PotionTimersHud.ColorMode.Flat)).build());
      this.rainbowSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rainbow-speed")).description("Rainbow speed of rainbow color mode.")).defaultValue(0.05).sliderMin(0.01).sliderMax(0.2).decimalPlaces(4).visible(() -> this.colorMode.get() == PotionTimersHud.ColorMode.Rainbow)).build());
      this.rainbowSpread = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rainbow-spread")).description("Rainbow spread of rainbow color mode.")).defaultValue(0.01).sliderMin(0.001).sliderMax(0.05).decimalPlaces(4).visible(() -> this.colorMode.get() == PotionTimersHud.ColorMode.Rainbow)).build());
      this.rainbowSaturation = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rainbow-saturation")).description("Saturation of rainbow color mode.")).defaultValue((double)1.0F).sliderRange((double)0.0F, (double)1.0F).visible(() -> this.colorMode.get() == PotionTimersHud.ColorMode.Rainbow)).build());
      this.rainbowBrightness = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rainbow-brightness")).description("Brightness of rainbow color mode.")).defaultValue((double)1.0F).sliderRange((double)0.0F, (double)1.0F).visible(() -> this.colorMode.get() == PotionTimersHud.ColorMode.Rainbow)).build());
      this.shadow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("shadow")).description("Renders shadow behind text.")).defaultValue(true)).build());
      this.alignment = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("alignment")).description("Horizontal alignment.")).defaultValue(Alignment.Auto)).build());
      this.border = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("border")).description("How much space to add around the element.")).defaultValue(0)).build());
      this.customScale = this.sgScale.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-scale")).description("Applies a custom scale to this hud element.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgScale;
      DoubleSetting.Builder var10002 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Custom scale.");
      Setting var10003 = this.customScale;
      Objects.requireNonNull(var10003);
      this.scale = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).defaultValue((double)1.0F).min((double)0.5F).sliderRange((double)0.5F, (double)3.0F).build());
      this.background = this.sgBackground.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      var10001 = this.sgBackground;
      ColorSetting.Builder var2 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var2.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
      this.texts = new ArrayList();
   }

   public void setSize(double width, double height) {
      super.setSize(width + (double)((Integer)this.border.get() * 2), height + (double)((Integer)this.border.get() * 2));
   }

   protected double alignX(double width, Alignment alignment) {
      return this.box.alignX((double)(this.getWidth() - (Integer)this.border.get() * 2), width, alignment);
   }

   public void tick(HudRenderer renderer) {
      if (MeteorClient.mc.field_1724 != null && (!this.isInEditor() || !this.hasNoVisibleEffects())) {
         double width = (double)0.0F;
         double height = (double)0.0F;
         this.texts.clear();

         for(class_1293 statusEffectInstance : MeteorClient.mc.field_1724.method_6026()) {
            if (!((List)this.hiddenEffects.get()).contains(statusEffectInstance.method_5579().comp_349()) && ((Boolean)this.showAmbient.get() || !statusEffectInstance.method_5591())) {
               String text = this.getString(statusEffectInstance);
               this.texts.add(new ObjectObjectImmutablePair(statusEffectInstance, text));
               width = Math.max(width, renderer.textWidth(text, (Boolean)this.shadow.get(), this.getScale()));
               height += renderer.textHeight((Boolean)this.shadow.get(), this.getScale());
            }
         }

         this.setSize(width, height);
      } else {
         this.setSize(renderer.textWidth("Potion Timers 0:00", (Boolean)this.shadow.get(), this.getScale()), renderer.textHeight((Boolean)this.shadow.get(), this.getScale()));
      }
   }

   public void render(HudRenderer renderer) {
      double x = (double)(this.x + (Integer)this.border.get());
      double y = (double)(this.y + (Integer)this.border.get());
      if ((Boolean)this.background.get()) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), this.backgroundColor.get());
      }

      if (MeteorClient.mc.field_1724 != null && (!this.isInEditor() || !this.hasNoVisibleEffects())) {
         this.rainbowHue += (Double)this.rainbowSpeed.get() * renderer.delta;
         if (this.rainbowHue > (double)1.0F) {
            --this.rainbowHue;
         } else if (this.rainbowHue < (double)-1.0F) {
            ++this.rainbowHue;
         }

         double localRainbowHue = this.rainbowHue;

         for(Pair<class_1293, String> potionEffectEntry : this.texts) {
            Object var10000;
            switch (((ColorMode)this.colorMode.get()).ordinal()) {
               case 0:
                  int c = ((class_1291)((class_1293)potionEffectEntry.left()).method_5579().comp_349()).method_5556();
                  var10000 = (new Color(c)).a(255);
                  break;
               case 1:
                  ((SettingColor)this.flatColor.get()).update();
                  var10000 = this.flatColor.get();
                  break;
               case 2:
                  localRainbowHue += (Double)this.rainbowSpread.get();
                  int c = java.awt.Color.HSBtoRGB((float)localRainbowHue, ((Double)this.rainbowSaturation.get()).floatValue(), ((Double)this.rainbowBrightness.get()).floatValue());
                  var10000 = new Color(c);
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            Color color = (Color)var10000;
            String text = (String)potionEffectEntry.right();
            renderer.text(text, x + this.alignX(renderer.textWidth(text, (Boolean)this.shadow.get(), this.getScale()), this.alignment.get()), y, color, (Boolean)this.shadow.get(), this.getScale());
            y += renderer.textHeight((Boolean)this.shadow.get(), this.getScale());
         }

      } else {
         renderer.text("Potion Timers 0:00", x, y, Color.WHITE, (Boolean)this.shadow.get(), this.getScale());
      }
   }

   private String getString(class_1293 statusEffectInstance) {
      return String.format("%s %d (%s)", Names.get((class_1291)statusEffectInstance.method_5579().comp_349()), statusEffectInstance.method_5578() + 1, class_1292.method_5577(statusEffectInstance, 1.0F, MeteorClient.mc.field_1687.method_54719().method_54748()).getString());
   }

   private double getScale() {
      return (Boolean)this.customScale.get() ? (Double)this.scale.get() : Hud.get().getTextScale();
   }

   private boolean hasNoVisibleEffects() {
      for(class_1293 statusEffectInstance : MeteorClient.mc.field_1724.method_6026()) {
         if (!((List)this.hiddenEffects.get()).contains(statusEffectInstance.method_5579().comp_349()) && ((Boolean)this.showAmbient.get() || !statusEffectInstance.method_5591())) {
            return false;
         }
      }

      return true;
   }

   static {
      INFO = new HudElementInfo<PotionTimersHud>(Hud.GROUP, "potion-timers", "Displays active potion effects with timers.", PotionTimersHud::new);
   }

   public static enum ColorMode {
      Effect,
      Flat,
      Rainbow;

      // $FF: synthetic method
      private static ColorMode[] $values() {
         return new ColorMode[]{Effect, Flat, Rainbow};
      }
   }
}
