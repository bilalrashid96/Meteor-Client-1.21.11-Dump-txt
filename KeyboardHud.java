package meteordevelopment.meteorclient.systems.hud.elements.keyboard;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.mixin.KeyBindingAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.misc.input.KeyBinds;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2520;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3675;
import net.minecraft.class_3675.class_307;
import org.lwjgl.glfw.GLFW;

public class KeyboardHud extends HudElement {
   public static final HudElementInfo<KeyboardHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgColor;
   private final SettingGroup sgBorder;
   private final SettingGroup sgBackground;
   private final Setting<Preset> preset;
   private final Setting<List<Key>> customKeys;
   private final Setting<Double> scale;
   private final Setting<Double> spacing;
   private final Setting<KeyboardLayout> keyboardLayout;
   private final Setting<Boolean> showCps;
   private final Setting<Alignment> alignment;
   private final Setting<SettingColor> pressedColor;
   private final Setting<SettingColor> unpressedColor;
   private final Setting<Boolean> colorFade;
   private final Setting<Double> fadeTime;
   private final Setting<SettingColor> textColor;
   private final Setting<Boolean> border;
   private final Setting<SettingColor> borderColor;
   private final Setting<Double> borderWidth;
   private final Setting<Double> opacity;
   private final Setting<Boolean> background;
   private final Setting<SettingColor> backgroundColor;
   private final List<Key> keys;
   private double minX;
   private double minY;

   private Color getColor(SettingColor color, SettingColor out) {
      out.set((Color)color);
      out.a = (int)((double)out.a * (Double)this.opacity.get());
      return out;
   }

   private Color getKeyColor(Key key, SettingColor out) {
      if ((Boolean)this.colorFade.get()) {
         boolean pressed = key.isPressed;
         float target = pressed ? 1.0F : 0.0F;
         float tickDelta = class_310.method_1551().method_61966().method_60636();
         float frameDelta = (float)((double)(tickDelta / 20.0F) / (Double)this.fadeTime.get()) * (float)(pressed ? 1 : -1);
         key.delta = Math.clamp(key.delta + frameDelta, 0.0F, 1.0F);
         if (key.delta == target) {
            out.set(target == 1.0F ? (Color)this.pressedColor.get() : (Color)this.unpressedColor.get());
         } else {
            Color c1 = this.pressedColor.get();
            Color c2 = this.unpressedColor.get();
            float[] hsb1 = new float[3];
            float[] hsb2 = new float[3];
            java.awt.Color.RGBtoHSB(c1.r, c1.g, c1.b, hsb2);
            java.awt.Color.RGBtoHSB(c2.r, c2.g, c2.b, hsb1);
            int rgb = java.awt.Color.HSBtoRGB(class_3532.method_16439(key.delta, hsb1[0], hsb2[0]), class_3532.method_16439(key.delta, hsb1[1], hsb2[1]), class_3532.method_16439(key.delta, hsb1[2], hsb2[2]));
            out.r = Color.toRGBAR(rgb);
            out.g = Color.toRGBAG(rgb);
            out.b = Color.toRGBAB(rgb);
            out.a = class_3532.method_48781(key.delta, (this.pressedColor.get()).a, (this.unpressedColor.get()).a);
         }
      } else {
         out.set(key.isPressed ? (Color)this.pressedColor.get() : (Color)this.unpressedColor.get());
      }

      out.a = (int)((double)out.a * (Double)this.opacity.get());
      return out;
   }

   @EventHandler(
      priority = 100
   )
   private void onKey(KeyEvent event) {
      for(Key key : this.keys) {
         if (key.matches(event.input.comp_4795(), event.input.comp_4796(), true)) {
            key.update(event.action);
         }
      }

   }

   @EventHandler(
      priority = 100
   )
   private void onMouseClick(MouseClickEvent event) {
      for(Key key : this.keys) {
         if (key.matches(event.input.comp_4801(), -1, false)) {
            key.update(event.action);
         }
      }

   }

   public KeyboardHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgColor = this.settings.createGroup("Color");
      this.sgBorder = this.settings.createGroup("Border");
      this.sgBackground = this.settings.createGroup("Background");
      this.preset = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("preset")).description("Which keys to display.")).defaultValue(KeyboardHud.Preset.Movement)).onChanged(this::onPresetChanged)).build());
      this.customKeys = this.sgGeneral.add(new CustomKeyListSetting());
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Scale of the keyboard.")).defaultValue((double)1.5F).min((double)0.5F).sliderRange((double)0.5F, (double)5.0F).decimalPlaces(1).onChanged((s) -> this.calculateSize())).build());
      this.spacing = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("spacing")).description("Spacing between keys.")).defaultValue((double)1.0F).min((double)0.0F).sliderRange((double)0.0F, (double)10.0F).decimalPlaces(1).visible(() -> this.preset.get() != KeyboardHud.Preset.Custom)).onChanged((s) -> this.onPresetChanged(this.preset.get()))).build());
      this.keyboardLayout = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("keyboard-layout")).description("Physical keyboard layout (ANSI or ISO).")).defaultValue(KeyboardHud.KeyboardLayout.ANSI)).visible(() -> this.preset.get() == KeyboardHud.Preset.Keyboard)).onChanged((layout) -> this.onPresetChanged(this.preset.get()))).build());
      this.showCps = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Show CPS")).description("Shows clicks per second on keys.")).defaultValue(false)).visible(() -> this.preset.get() != KeyboardHud.Preset.Custom)).onChanged((b) -> this.onPresetChanged(this.preset.get()))).build());
      this.alignment = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("alignment")).description("Horizontal alignment of the text.")).defaultValue(KeyboardHud.Alignment.Center)).build());
      this.pressedColor = this.sgColor.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("pressed-color")).description("Color of pressed keys.")).defaultValue(new SettingColor(200, 200, 200, 100)).build());
      this.unpressedColor = this.sgColor.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("unpressed-color")).description("Color of unpressed keys.")).defaultValue(new SettingColor(0, 0, 0, 100)).build());
      this.colorFade = this.sgColor.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("color-fade")).description("Whether to fade the key color when pressing/unpressing.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgColor;
      DoubleSetting.Builder var10002 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fade-time")).description("How long to fade the color for, in seconds.");
      Setting var10003 = this.colorFade;
      Objects.requireNonNull(var10003);
      this.fadeTime = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).defaultValue(0.1).min(0.01).sliderRange(0.01, (double)0.5F).decimalPlaces(2).build());
      this.textColor = this.sgColor.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("text-color")).description("Color of the key name.")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.border = this.sgBorder.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("border")).description("Draw a border around keys.")).defaultValue(false)).build());
      var10001 = this.sgBorder;
      ColorSetting.Builder var4 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("border-color")).description("Color of the key border.");
      var10003 = this.border;
      Objects.requireNonNull(var10003);
      this.borderColor = var10001.add(((ColorSetting.Builder)var4.visible(var10003::get)).defaultValue(new SettingColor(255, 255, 255, 200)).build());
      var10001 = this.sgBorder;
      DoubleSetting.Builder var5 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("border-width")).description("Width of the key border.");
      var10003 = this.border;
      Objects.requireNonNull(var10003);
      this.borderWidth = var10001.add(((DoubleSetting.Builder)var5.visible(var10003::get)).defaultValue((double)1.0F).min((double)0.5F).sliderRange((double)0.5F, (double)5.0F).build());
      this.opacity = this.sgColor.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("opacity")).description("Opacity of the whole element.")).defaultValue((double)1.0F).min((double)0.0F).max((double)1.0F).sliderMax((double)1.0F).build());
      this.background = this.sgBackground.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      var10001 = this.sgBackground;
      ColorSetting.Builder var6 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var6.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
      this.keys = new ArrayList();
      if (MeteorClient.mc.field_1690 != null) {
         this.onPresetChanged(this.preset.get());
      }

      MeteorClient.EVENT_BUS.subscribe(this);
   }

   private void onPresetChanged(Preset preset) {
      if (MeteorClient.mc.field_1690 != null) {
         this.keys.clear();
         double u = (double)35.0F;
         double g = (Double)this.spacing.get() * (double)2.0F;
         LayoutContext l = new LayoutContext(u, g, (double)15.0F);
         switch (preset.ordinal()) {
            case 0:
               this.keys.add(l.key(MeteorClient.mc.field_1690.field_1894, l.ux((double)1.0F), (double)0.0F).setShowCps((Boolean)this.showCps.get()));
               this.keys.add(l.key(MeteorClient.mc.field_1690.field_1913, (double)0.0F, l.y((double)1.0F)).setShowCps((Boolean)this.showCps.get()));
               this.keys.add(l.key(MeteorClient.mc.field_1690.field_1881, l.ux((double)1.0F), l.y((double)1.0F)).setShowCps((Boolean)this.showCps.get()));
               this.keys.add(l.key(MeteorClient.mc.field_1690.field_1849, l.ux((double)2.0F), l.y((double)1.0F)).setShowCps((Boolean)this.showCps.get()));
               this.keys.add(l.key(MeteorClient.mc.field_1690.field_1832, (double)0.0F, l.y((double)2.0F)).setShowCps((Boolean)this.showCps.get()));
               this.keys.add(l.key(MeteorClient.mc.field_1690.field_1903, l.ux((double)1.0F), l.y((double)2.0F), KeyDimensions.UNIT_2U).setShowCps((Boolean)this.showCps.get()));
               break;
            case 1:
               this.keys.add(l.key(MeteorClient.mc.field_1690.field_1886, "LMB", (double)0.0F, (double)0.0F).setShowCps((Boolean)this.showCps.get()));
               this.keys.add(l.key(MeteorClient.mc.field_1690.field_1904, "RMB", l.ux((double)1.0F), (double)0.0F).setShowCps((Boolean)this.showCps.get()));
               break;
            case 2:
               this.keys.add(l.key(MeteorClient.mc.field_1690.field_1869, (double)0.0F, (double)0.0F).setShowCps((Boolean)this.showCps.get()));
               this.keys.add(l.key(MeteorClient.mc.field_1690.field_1831, l.ux((double)1.0F), (double)0.0F).setShowCps((Boolean)this.showCps.get()));
               this.keys.add(l.key(MeteorClient.mc.field_1690.field_1822, l.ux((double)2.0F), (double)0.0F).setShowCps((Boolean)this.showCps.get()));
               break;
            case 3:
               for(int i = 0; i < 9; ++i) {
                  this.keys.add(l.key(MeteorClient.mc.field_1690.field_1852[i], l.ux((double)i), (double)0.0F).setShowCps((Boolean)this.showCps.get()));
               }
               break;
            case 4:
               if (this.keyboardLayout.get() == KeyboardHud.KeyboardLayout.ANSI) {
                  this.buildAnsiLayout(l);
               } else {
                  this.buildIsoLayout(l);
               }

               for(Key key : this.keys) {
                  key.setShowCps((Boolean)this.showCps.get());
               }
               break;
            case 5:
               this.keys.addAll((Collection)this.customKeys.get());
         }

         this.calculateSize();
      }
   }

   private void buildAnsiLayout(LayoutContext l) {
      double row0 = l.uy((double)0.0F);
      double row1 = l.uy((double)1.0F);
      double row2 = l.uy((double)2.0F);
      double row3 = l.uy((double)3.0F);
      double row4 = l.uy((double)4.0F);
      double row5 = l.uy((double)5.0F);
      this.keys.add(l.key(Keybind.fromKey(256), (double)0.0F, row0));

      for(int i = 0; i < 4; ++i) {
         this.keys.add(l.key(Keybind.fromKey(290 + i), l.ux((double)2.0F + (double)i), row0));
      }

      for(int i = 0; i < 4; ++i) {
         this.keys.add(l.key(Keybind.fromKey(294 + i), l.ux((double)6.5F + (double)i), row0));
      }

      for(int i = 0; i < 4; ++i) {
         this.keys.add(l.key(Keybind.fromKey(298 + i), l.ux((double)11.0F + (double)i), row0));
      }

      this.keys.add(l.key(Keybind.fromKey(283), l.ux((double)15.5F), row0));
      this.keys.add(l.key(Keybind.fromKey(281), l.ux((double)16.5F), row0));
      this.keys.add(l.key(Keybind.fromKey(284), l.ux((double)17.5F), row0));
      int[] row1Keys = new int[]{96, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 45, 61};

      for(int i = 0; i < row1Keys.length; ++i) {
         this.keys.add(l.key(Keybind.fromKey(row1Keys[i]), l.ux((double)i), row1));
      }

      this.keys.add(l.key(Keybind.fromKey(259), l.ux((double)13.0F), row1, KeyDimensions.BACKSPACE));
      this.keys.add(l.key(Keybind.fromKey(260), l.ux((double)15.5F), row1));
      this.keys.add(l.key(Keybind.fromKey(268), l.ux((double)16.5F), row1));
      this.keys.add(l.key(Keybind.fromKey(266), l.ux((double)17.5F), row1));
      this.keys.add(l.key(Keybind.fromKey(258), (double)0.0F, row2, KeyDimensions.TAB));
      int[] row2Keys = new int[]{81, 87, 69, 82, 84, 89, 85, 73, 79, 80, 91, 93};
      double tabEnd = l.px(KeyDimensions.TAB) + l.keyGap;

      for(int i = 0; i < row2Keys.length; ++i) {
         this.keys.add(l.key(Keybind.fromKey(row2Keys[i]), tabEnd + l.ux((double)i), row2));
      }

      this.keys.add(l.key(Keybind.fromKey(92), tabEnd + l.ux((double)12.0F), row2, KeyDimensions.TAB));
      this.keys.add(l.key(Keybind.fromKey(261), l.ux((double)15.5F), row2));
      this.keys.add(l.key(Keybind.fromKey(269), l.ux((double)16.5F), row2));
      this.keys.add(l.key(Keybind.fromKey(267), l.ux((double)17.5F), row2));
      this.keys.add(l.key(Keybind.fromKey(280), (double)0.0F, row3, KeyDimensions.CAPS_LOCK));
      int[] row3Keys = new int[]{65, 83, 68, 70, 71, 72, 74, 75, 76, 59, 39};
      double capsEnd = l.px(KeyDimensions.CAPS_LOCK) + l.keyGap;

      for(int i = 0; i < row3Keys.length; ++i) {
         this.keys.add(l.key(Keybind.fromKey(row3Keys[i]), capsEnd + l.ux((double)i), row3));
      }

      this.keys.add(l.key(Keybind.fromKey(257), capsEnd + l.ux((double)11.0F), row3, KeyDimensions.ENTER_ANSI));
      this.keys.add(l.key(Keybind.fromKey(340), (double)0.0F, row4, KeyDimensions.LEFT_SHIFT_ANSI));
      int[] row4Keys = new int[]{90, 88, 67, 86, 66, 78, 77, 44, 46, 47};
      double lShiftEnd = l.px(KeyDimensions.LEFT_SHIFT_ANSI) + l.keyGap;

      for(int i = 0; i < row4Keys.length; ++i) {
         this.keys.add(l.key(Keybind.fromKey(row4Keys[i]), lShiftEnd + l.ux((double)i), row4));
      }

      this.keys.add(l.key(Keybind.fromKey(344), lShiftEnd + l.ux((double)10.0F), row4, KeyDimensions.RIGHT_SHIFT));
      this.keys.add(l.key(Keybind.fromKey(265), l.ux((double)16.5F), row4));
      double xPos = (double)0.0F;
      this.keys.add(l.key(Keybind.fromKey(341), xPos, row5, KeyDimensions.CTRL));
      xPos += l.px(KeyDimensions.CTRL) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(343), xPos, row5, KeyDimensions.GUI));
      xPos += l.px(KeyDimensions.GUI) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(342), xPos, row5, KeyDimensions.ALT));
      xPos += l.px(KeyDimensions.ALT) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(32), xPos, row5, KeyDimensions.SPACEBAR));
      xPos += l.px(KeyDimensions.SPACEBAR) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(346), xPos, row5, KeyDimensions.ALT));
      xPos += l.px(KeyDimensions.ALT) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(347), xPos, row5, KeyDimensions.GUI));
      xPos += l.px(KeyDimensions.GUI) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(348), xPos, row5, KeyDimensions.MENU));
      xPos += l.px(KeyDimensions.MENU) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(345), xPos, row5, KeyDimensions.CTRL));
      this.keys.add(l.key(Keybind.fromKey(263), l.ux((double)15.5F), row5));
      this.keys.add(l.key(Keybind.fromKey(264), l.ux((double)16.5F), row5));
      this.keys.add(l.key(Keybind.fromKey(262), l.ux((double)17.5F), row5));
   }

   private void buildIsoLayout(LayoutContext l) {
      double row0 = l.uy((double)0.0F);
      double row1 = l.uy((double)1.0F);
      double row2 = l.uy((double)2.0F);
      double row3 = l.uy((double)3.0F);
      double row4 = l.uy((double)4.0F);
      double row5 = l.uy((double)5.0F);
      this.keys.add(l.key(Keybind.fromKey(256), (double)0.0F, row0));

      for(int i = 0; i < 4; ++i) {
         this.keys.add(l.key(Keybind.fromKey(290 + i), l.ux((double)2.0F + (double)i), row0));
      }

      for(int i = 0; i < 4; ++i) {
         this.keys.add(l.key(Keybind.fromKey(294 + i), l.ux((double)6.5F + (double)i), row0));
      }

      for(int i = 0; i < 4; ++i) {
         this.keys.add(l.key(Keybind.fromKey(298 + i), l.ux((double)11.0F + (double)i), row0));
      }

      this.keys.add(l.key(Keybind.fromKey(283), l.ux((double)15.5F), row0));
      this.keys.add(l.key(Keybind.fromKey(281), l.ux((double)16.5F), row0));
      this.keys.add(l.key(Keybind.fromKey(284), l.ux((double)17.5F), row0));
      int[] row1Keys = new int[]{96, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 45, 61};

      for(int i = 0; i < row1Keys.length; ++i) {
         this.keys.add(l.key(Keybind.fromKey(row1Keys[i]), l.ux((double)i), row1));
      }

      this.keys.add(l.key(Keybind.fromKey(259), l.ux((double)13.0F), row1, KeyDimensions.BACKSPACE));
      this.keys.add(l.key(Keybind.fromKey(260), l.ux((double)15.5F), row1));
      this.keys.add(l.key(Keybind.fromKey(268), l.ux((double)16.5F), row1));
      this.keys.add(l.key(Keybind.fromKey(266), l.ux((double)17.5F), row1));
      this.keys.add(l.key(Keybind.fromKey(258), (double)0.0F, row2, KeyDimensions.TAB));
      int[] row2Keys = new int[]{81, 87, 69, 82, 84, 89, 85, 73, 79, 80, 91, 93};
      double tabEnd = l.px(KeyDimensions.TAB) + l.keyGap;

      for(int i = 0; i < row2Keys.length; ++i) {
         this.keys.add(l.key(Keybind.fromKey(row2Keys[i]), tabEnd + l.ux((double)i), row2));
      }

      this.keys.add(l.key(Keybind.fromKey(261), l.ux((double)15.5F), row2));
      this.keys.add(l.key(Keybind.fromKey(269), l.ux((double)16.5F), row2));
      this.keys.add(l.key(Keybind.fromKey(267), l.ux((double)17.5F), row2));
      this.keys.add(l.key(Keybind.fromKey(280), (double)0.0F, row3, KeyDimensions.CAPS_LOCK));
      int[] row3Keys = new int[]{65, 83, 68, 70, 71, 72, 74, 75, 76, 59, 39};
      double capsEnd = l.px(KeyDimensions.CAPS_LOCK) + l.keyGap;

      for(int i = 0; i < row3Keys.length; ++i) {
         this.keys.add(l.key(Keybind.fromKey(row3Keys[i]), capsEnd + l.ux((double)i), row3));
      }

      this.keys.add(l.key(Keybind.fromKey(92), capsEnd + l.ux((double)11.0F), row3));
      double topBarStartX = tabEnd + l.ux((double)12.0F);
      double mainBlockRightEdge = tabEnd + l.ux((double)12.0F) + l.px(KeyDimensions.TAB);
      double enterStemWidth = l.px(KeyDimensions.ENTER_ISO_WIDTH);
      double enterStemHeight = l.px(KeyDimensions.ENTER_ISO_HEIGHT);
      double enterStemX = mainBlockRightEdge - enterStemWidth;
      this.keys.add(new IsoEnterKey(Keybind.fromKey(257), enterStemX, row2, enterStemWidth, enterStemHeight, topBarStartX));
      this.keys.add(l.key(Keybind.fromKey(340), (double)0.0F, row4, KeyDimensions.LEFT_SHIFT_ISO));
      double lShiftEnd = l.px(KeyDimensions.LEFT_SHIFT_ISO) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(162), lShiftEnd, row4));
      int[] row4Keys = new int[]{90, 88, 67, 86, 66, 78, 77, 44, 46, 47};

      for(int i = 0; i < row4Keys.length; ++i) {
         this.keys.add(l.key(Keybind.fromKey(row4Keys[i]), lShiftEnd + l.ux((double)1.0F + (double)i), row4));
      }

      double rShiftX = lShiftEnd + l.ux((double)11.0F);
      this.keys.add(l.key(Keybind.fromKey(344), rShiftX, row4, KeyDimensions.RIGHT_SHIFT));
      this.keys.add(l.key(Keybind.fromKey(265), l.ux((double)16.5F), row4));
      double xPos = (double)0.0F;
      this.keys.add(l.key(Keybind.fromKey(341), xPos, row5, KeyDimensions.CTRL));
      xPos += l.px(KeyDimensions.CTRL) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(343), xPos, row5, KeyDimensions.GUI));
      xPos += l.px(KeyDimensions.GUI) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(342), xPos, row5, KeyDimensions.ALT));
      xPos += l.px(KeyDimensions.ALT) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(32), xPos, row5, KeyDimensions.SPACEBAR));
      xPos += l.px(KeyDimensions.SPACEBAR) + l.keyGap;
      this.keys.add(l.keyNamed(Keybind.fromKey(346), "AltGr", xPos, row5, KeyDimensions.ALT));
      xPos += l.px(KeyDimensions.ALT) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(347), xPos, row5, KeyDimensions.GUI));
      xPos += l.px(KeyDimensions.GUI) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(348), xPos, row5, KeyDimensions.MENU));
      xPos += l.px(KeyDimensions.MENU) + l.keyGap;
      this.keys.add(l.key(Keybind.fromKey(345), xPos, row5, KeyDimensions.CTRL));
      this.keys.add(l.key(Keybind.fromKey(263), l.ux((double)15.5F), row5));
      this.keys.add(l.key(Keybind.fromKey(264), l.ux((double)16.5F), row5));
      this.keys.add(l.key(Keybind.fromKey(262), l.ux((double)17.5F), row5));
   }

   private void calculateSize() {
      if (this.keys.isEmpty()) {
         this.setSize((double)0.0F, (double)0.0F);
      } else {
         this.minX = this.minY = (double)0.0F;
         double maxX = (double)0.0F;
         double maxY = (double)0.0F;

         for(Key key : this.keys) {
            this.minX = Math.min(this.minX, key.x);
            this.minY = Math.min(this.minY, key.y);
            maxX = Math.max(maxX, key.x + key.width);
            maxY = Math.max(maxY, key.y + key.height);
         }

         this.setSize((maxX - this.minX) * (Double)this.scale.get(), (maxY - this.minY) * (Double)this.scale.get());
      }
   }

   private static String getShortName(String name) {
      String var10000;
      switch (name.toUpperCase(Locale.ROOT)) {
         case "LEFT SHIFT":
         case "LSHIFT":
            var10000 = "LSh";
            break;
         case "RIGHT SHIFT":
         case "RSHIFT":
            var10000 = "RSh";
            break;
         case "LEFT CONTROL":
         case "LCTRL":
            var10000 = "LCtrl";
            break;
         case "RIGHT CONTROL":
         case "RCTRL":
            var10000 = "RCtrl";
            break;
         case "LEFT ALT":
         case "LALT":
            var10000 = "LAlt";
            break;
         case "RIGHT ALT":
         case "RALT":
            var10000 = "RAlt";
            break;
         case "LEFT SUPER":
            var10000 = "LSup";
            break;
         case "RIGHT SUPER":
            var10000 = "RSup";
            break;
         case "GRAVE ACCENT":
            var10000 = "`";
            break;
         case "COMMA":
            var10000 = ",";
            break;
         case "DOT":
         case "PERIOD":
            var10000 = ".";
            break;
         case "SLASH":
            var10000 = "/";
            break;
         case "APOSTROPHE":
            var10000 = "'";
            break;
         case "BACKSPACE":
            var10000 = "BS";
            break;
         case "ENTER":
            var10000 = "Ent";
            break;
         case "SCROLL":
         case "SCROLL LOCK":
            var10000 = "ScrL";
            break;
         case "PRINT":
         case "PRTSC":
         case "PRINT SCREEN":
            var10000 = "PrtS";
            break;
         case "PAUSE":
            var10000 = "Paus";
            break;
         case "PAGEUP":
         case "PAGE UP":
         case "PGUP":
            var10000 = "PgUp";
            break;
         case "PAGEDOWN":
         case "PAGE DOWN":
         case "PGDN":
            var10000 = "PgDn";
            break;
         case "INSERT":
         case "INS":
            var10000 = "Ins";
            break;
         case "DELETE":
         case "DEL":
            var10000 = "Del";
            break;
         case "HOME":
            var10000 = "Home";
            break;
         case "END":
            var10000 = "End";
            break;
         case "ARROW UP":
         case "UP":
            var10000 = "Up";
            break;
         case "ARROW DOWN":
         case "DOWN":
            var10000 = "Dn";
            break;
         case "ARROW LEFT":
         case "LEFT":
            var10000 = "Lt";
            break;
         case "ARROW RIGHT":
         case "RIGHT":
            var10000 = "Rt";
            break;
         case "WORLD 1":
            var10000 = "#";
            break;
         case "WORLD 2":
            var10000 = "\\";
            break;
         case "UNKNOWN":
            var10000 = "?";
            break;
         default:
            var10000 = name;
      }

      return var10000;
   }

   public void render(HudRenderer renderer) {
      if (this.keys.isEmpty()) {
         if (MeteorClient.mc.field_1690 != null) {
            this.onPresetChanged(this.preset.get());
         }

      } else {
         SettingColor mutableColor = new SettingColor();
         ((SettingColor)this.pressedColor.get()).update();
         ((SettingColor)this.unpressedColor.get()).update();
         ((SettingColor)this.textColor.get()).update();
         ((SettingColor)this.borderColor.get()).update();
         ((SettingColor)this.backgroundColor.get()).update();
         if ((Boolean)this.background.get()) {
            renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), this.getColor(this.backgroundColor.get(), mutableColor));
         }

         double s = (Double)this.scale.get();
         class_3675.class_306 guiKey = ((KeyBindingAccessor)KeyBinds.OPEN_GUI).meteor$getKey();

         for(Key key : this.keys) {
            if (key.matches(guiKey.method_1444(), guiKey.method_1444(), guiKey.method_1442() != class_307.field_1672) && key.isPressed != key.isNativelyPressed()) {
               key.update(key.isPressed ? KeyAction.Release : KeyAction.Press);
            }

            if (key instanceof IsoEnterKey) {
               IsoEnterKey isoEnter = (IsoEnterKey)key;
               isoEnter.render(this, renderer, s, mutableColor);
            } else {
               Color color = this.getKeyColor(key, mutableColor);
               double kX = (double)this.x + (key.x - this.minX) * s;
               double kY = (double)this.y + (key.y - this.minY) * s;
               double kW = key.width * s;
               double kH = key.height * s;
               renderer.quad(kX, kY, kW, kH, color);
               if ((Boolean)this.border.get()) {
                  Color bColor = this.getColor(this.borderColor.get(), mutableColor);
                  double bw = (Double)this.borderWidth.get();
                  renderer.quad(kX, kY, kW, bw, bColor);
                  renderer.quad(kX, kY + kH - bw, kW, bw, bColor);
                  renderer.quad(kX, kY, bw, kH, bColor);
                  renderer.quad(kX + kW - bw, kY, bw, kH, bColor);
               }

               String text = key.getName();
               Color txtColor = this.getColor(this.textColor.get(), mutableColor);
               double padding = (double)2.0F * s;
               double availableWidth = kW - padding;
               if (!key.showCps) {
                  double textScale = Math.min((double)1.0F, availableWidth / renderer.textWidth(text, (double)1.0F));
                  double textWidth = renderer.textWidth(text, textScale);
                  double yText = kY + (kH - renderer.textHeight(false, textScale)) / (double)2.0F;
                  this.drawTextLine(renderer, text, textWidth, kX, yText, kW, textScale, txtColor);
               } else {
                  double topScale = Math.min((double)1.0F, availableWidth / renderer.textWidth(text, (double)1.0F));
                  double topWidth = renderer.textWidth(text, topScale);
                  double topHeight = renderer.textHeight(false, topScale);
                  String cpsText = key.getCps() + " CPS";
                  double botScale = Math.min((double)1.0F, availableWidth / renderer.textWidth(cpsText, (double)1.0F));
                  double botWidth = renderer.textWidth(cpsText, botScale);
                  double botHeight = renderer.textHeight(false, botScale);
                  double totalHeight = topHeight + botHeight;
                  double startY = kY + (kH - totalHeight) / (double)2.0F;
                  this.drawTextLine(renderer, text, topWidth, kX, startY, kW, topScale, txtColor);
                  this.drawTextLine(renderer, cpsText, botWidth, kX, startY + topHeight, kW, botScale, txtColor);
               }
            }
         }

      }
   }

   private void drawTextLine(HudRenderer renderer, String text, double textWidth, double x, double y, double w, double textScale, Color color) {
      double s = (Double)this.scale.get();
      double padding = (double)2.0F * s;
      double xText = x + (w - textWidth) / (double)2.0F;
      if (this.alignment.get() == KeyboardHud.Alignment.Left) {
         xText = x + padding;
      } else if (this.alignment.get() == KeyboardHud.Alignment.Right) {
         xText = x + w - padding - textWidth;
      }

      renderer.text(text, xText, y, color, false, textScale);
   }

   public static void fillTable(GuiTheme theme, WTable table, CustomKeyListSetting setting) {
      table.clear();
      Iterator<Key> it = ((List)setting.get()).iterator();

      while(it.hasNext()) {
         Key key = (Key)it.next();
         ((WLabel)table.add(theme.label("Key")).expandWidgetX().widget()).color(theme.textSecondaryColor());
         table.add(theme.label(String.format("(%s)", key.keybind))).expandWidgetX();
         WButton edit = (WButton)table.add(theme.button(GuiRenderer.EDIT)).expandCellX().widget();
         edit.action = () -> {
            WidgetScreen screen = (WidgetScreen)MeteorClient.mc.field_1755;
            MeteorClient.mc.method_1507(new CustomKeySettingScreen(theme, setting, key, screen));
         };
         WMinus delete = (WMinus)table.add(theme.minus()).right().widget();
         delete.action = () -> {
            it.remove();
            setting.onChanged();
            fillTable(theme, table, setting);
         };
         table.row();
      }

      if (!((List)setting.get()).isEmpty()) {
         table.add(theme.horizontalSeparator()).expandX();
         table.row();
      }

      WButton add = (WButton)table.add(theme.button("Add")).expandX().widget();
      add.action = () -> {
         Key newKey = new Key();
         if (!((List)setting.get()).isEmpty()) {
            Key lastKey = (Key)((List)setting.get()).getLast();
            newKey.x = lastKey.x + lastKey.width + (double)10.0F;
            newKey.y = lastKey.y;
         }

         ((List)setting.get()).add(newKey);
         setting.onChanged();
         fillTable(theme, table, setting);
      };
      WButton reset = (WButton)table.add(theme.button(GuiRenderer.RESET)).widget();
      reset.action = () -> {
         setting.reset();
         fillTable(theme, table, setting);
      };
      reset.tooltip = "Reset";
   }

   static {
      INFO = new HudElementInfo<KeyboardHud>(Hud.GROUP, "keyboard", "Displays pressed keys.", KeyboardHud::new);
   }

   public static enum Alignment {
      Left,
      Center,
      Right;

      // $FF: synthetic method
      private static Alignment[] $values() {
         return new Alignment[]{Left, Center, Right};
      }
   }

   public static enum Preset {
      Movement,
      Clicks,
      Actions,
      Hotbar,
      Keyboard,
      Custom;

      // $FF: synthetic method
      private static Preset[] $values() {
         return new Preset[]{Movement, Clicks, Actions, Hotbar, Keyboard, Custom};
      }
   }

   public static enum KeyboardLayout {
      ANSI,
      ISO;

      // $FF: synthetic method
      private static KeyboardLayout[] $values() {
         return new KeyboardLayout[]{ANSI, ISO};
      }
   }

   public static class Key {
      public String name = "";
      public class_304 binding;
      public Keybind keybind;
      public double x;
      public double y;
      public double width;
      public double height;
      public boolean showCps = false;
      private final RollingCps rollingCps = new RollingCps();
      private boolean isPressed;
      private float delta;

      public Key() {
         this.keybind = Keybind.fromKey(32);
         this.width = (double)60.0F;
         this.height = (double)40.0F;
      }

      public Key(class_2487 compound) {
         this.keybind = Keybind.none().fromTag(compound.method_68568("key"));
         this.name = compound.method_68564("name", "");
         this.x = compound.method_68563("x", (double)0.0F);
         this.y = compound.method_68563("y", (double)0.0F);
         this.width = compound.method_68563("width", (double)60.0F);
         this.height = compound.method_68563("height", (double)60.0F);
         this.showCps = compound.method_68566("showCps", false);
      }

      Key(class_304 binding, String name, double x, double y, double width, double height) {
         this.binding = binding;
         this.name = name;
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }

      Key(Keybind keybind, String name, double x, double y, double width, double height) {
         this.keybind = keybind;
         this.name = name;
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }

      public Key setShowCps(boolean show) {
         this.showCps = show;
         return this;
      }

      public String getName() {
         if (this.name != null && !this.name.isEmpty()) {
            return this.name;
         } else if (this.keybind != null) {
            return KeyboardHud.getShortName(this.keybind.toString());
         } else {
            return this.binding != null ? KeyboardHud.getShortName(this.binding.method_16007().getString()) : "?";
         }
      }

      public boolean matches(int input, int scancode, boolean key) {
         if (this.keybind != null) {
            return this.keybind.isKey() == key && this.keybind.getValue() == input;
         } else {
            class_3675.class_306 inputKey = ((KeyBindingAccessor)this.binding).meteor$getKey();
            boolean isKey = inputKey.method_1442() != class_307.field_1672;
            return isKey == key && inputKey.method_1442() == class_307.field_1671 ? scancode == inputKey.method_1444() : input == inputKey.method_1444();
         }
      }

      public void update(KeyAction action) {
         if (action != KeyAction.Release) {
            this.isPressed = true;
            if (this.showCps && action == KeyAction.Press) {
               this.rollingCps.add();
            }
         } else {
            this.isPressed = false;
         }

      }

      public boolean isNativelyPressed() {
         long window = MeteorClient.mc.method_22683().method_4490();
         if (this.keybind != null) {
            if (!this.keybind.isSet()) {
               return false;
            } else {
               return this.keybind.isKey() ? GLFW.glfwGetKey(window, this.keybind.getValue()) != 0 : GLFW.glfwGetMouseButton(window, this.keybind.getValue()) != 0;
            }
         } else {
            int key = ((KeyBindingAccessor)this.binding).meteor$getKey().method_1444();
            return key >= 0 && key < 8 ? GLFW.glfwGetMouseButton(window, key) != 0 : GLFW.glfwGetKey(window, key) != 0;
         }
      }

      public int getCps() {
         return this.rollingCps.get();
      }

      public class_2487 serialize() {
         class_2487 compound = new class_2487();
         compound.method_10566("key", this.keybind.toTag());
         compound.method_10582("name", this.name);
         compound.method_10549("x", this.x);
         compound.method_10549("y", this.y);
         compound.method_10549("width", this.width);
         compound.method_10549("height", this.height);
         compound.method_10556("showCps", this.showCps);
         return compound;
      }
   }

   public static class IsoEnterKey extends Key {
      private final double topBarStartX;

      public IsoEnterKey(Keybind keybind, double x, double y, double width, double height, double topBarStartX) {
         super((Keybind)keybind, (String)null, x, y, width, height);
         this.topBarStartX = topBarStartX;
      }

      public void render(KeyboardHud hud, HudRenderer renderer, double s, SettingColor mutableColor) {
         double kX = (double)hud.x + (this.x - hud.minX) * s;
         double kY = (double)hud.y + (this.y - hud.minY) * s;
         double kW = this.width * s;
         double kH = this.height * s;
         double u = (double)35.0F * s;
         Color color = hud.getKeyColor(this, mutableColor);
         double stemRight = kX + kW;
         double topBarX = (double)hud.x + (this.topBarStartX - hud.minX) * s;
         double topBarLeftWidth = stemRight - topBarX - kW;
         if (topBarLeftWidth > (double)0.0F) {
            renderer.quad(topBarX, kY, topBarLeftWidth, u, color);
         }

         renderer.quad(kX, kY, kW, u, color);
         renderer.quad(kX, kY + u, kW, kH - u, color);
         if ((Boolean)hud.border.get()) {
            Color bColor = hud.getColor(hud.borderColor.get(), mutableColor);
            double bw = (Double)hud.borderWidth.get();
            double fullTopBarWidth = topBarLeftWidth + kW;
            renderer.quad(topBarX, kY, fullTopBarWidth, bw, bColor);
            renderer.quad(topBarX, kY, bw, u, bColor);
            renderer.quad(topBarX + fullTopBarWidth - bw, kY, bw, kH, bColor);
            renderer.quad(kX, kY + kH - bw, kW, bw, bColor);
            renderer.quad(kX, kY + u, bw, kH - u, bColor);
            if (topBarLeftWidth > (double)0.0F) {
               renderer.quad(topBarX, kY + u - bw, topBarLeftWidth, bw, bColor);
            }
         }

         String text = this.getName();
         Color txtColor = hud.getColor(hud.textColor.get(), mutableColor);
         double padding = (double)2.0F * s;
         double availableWidth = kW - padding * (double)2.0F;
         double availableHeight = kH - padding * (double)2.0F;
         double tH = renderer.textHeight();
         double tW = renderer.textWidth(text);
         double widthScale = tW > availableWidth ? availableWidth / tW : (double)1.0F;
         double heightScale = tH > availableHeight * 0.6 ? availableHeight * 0.6 / tH : (double)1.0F;
         double textScale = Math.min(widthScale, heightScale);
         double yText = kY + (kH - tH * textScale) / (double)2.0F;
         hud.drawTextLine(renderer, text, tW, kX, yText, kW, textScale, txtColor);
      }
   }

   private static class RollingCps {
      private final LongList clicks = new LongArrayList();

      public void add() {
         this.clicks.add(System.currentTimeMillis());
      }

      public int get() {
         long time = System.currentTimeMillis();
         this.clicks.removeIf((val) -> val + 1000L < time);
         return this.clicks.size();
      }
   }

   public class CustomKeyListSetting extends Setting<List<Key>> {
      public CustomKeyListSetting() {
         super("custom-keys", "Configure the custom keys display.", List.of(), (k) -> KeyboardHud.this.onPresetChanged(KeyboardHud.this.preset.get()), (s) -> {
         }, () -> KeyboardHud.this.preset.get() == KeyboardHud.Preset.Custom);
      }

      protected void resetImpl() {
         this.value = new ObjectArrayList();
         ((List)this.value).add(new Key());
      }

      protected List<Key> parseImpl(String str) {
         return List.of();
      }

      protected boolean isValueValid(List<Key> value) {
         return true;
      }

      protected class_2487 save(class_2487 tag) {
         class_2499 valueTag = new class_2499();

         for(Key key : (List)this.get()) {
            valueTag.add(key.serialize());
         }

         tag.method_10566("value", valueTag);
         return tag;
      }

      protected List<Key> load(class_2487 tag) {
         ((List)this.get()).clear();

         for(class_2520 tagI : tag.method_68569("value")) {
            tagI.method_68571().ifPresent((nbtCompound) -> ((List)this.get()).add(new Key(nbtCompound)));
         }

         return (List)this.get();
      }
   }

   public static class CustomKeySettingScreen extends WindowScreen {
      private final CustomKeyListSetting setting;
      private final Key key;
      private final WidgetScreen screen;

      public CustomKeySettingScreen(GuiTheme theme, CustomKeyListSetting setting, Key key, WidgetScreen screen) {
         super(theme, "Select Key");
         this.setting = setting;
         this.key = key;
         this.screen = screen;
      }

      public void initWidgets() {
         Settings settings = new Settings();
         SettingGroup sgGeneral = settings.getDefaultGroup();
         sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("custom-key")).description("The key to display.")).defaultValue(Keybind.fromKey(32))).onChanged((k) -> {
            this.key.keybind = k;
            this.screen.reload();
         })).onModuleActivated((setting) -> setting.set(this.key.keybind))).build());
         sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("custom-label")).description("Replace the Key name with custom text.")).defaultValue("")).onChanged((s) -> this.key.name = s)).onModuleActivated((setting) -> setting.set(this.key.name))).build());
         sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("key-width")).description("Width of the key.")).defaultValue((double)60.0F).min((double)20.0F).sliderRange((double)20.0F, (double)200.0F).decimalPlaces(1).onChanged((d) -> {
            this.key.width = d;
            this.setting.onChanged();
         })).onModuleActivated((setting) -> setting.set(this.key.width))).build());
         sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("key-height")).description("Height of the key.")).defaultValue((double)40.0F).min((double)20.0F).sliderRange((double)20.0F, (double)200.0F).decimalPlaces(1).onChanged((d) -> {
            this.key.height = d;
            this.setting.onChanged();
         })).onModuleActivated((setting) -> setting.set(this.key.height))).build());
         sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("key-x")).description("X position offset of the key.")).defaultValue((double)0.0F).sliderRange((double)-200.0F, (double)200.0F).decimalPlaces(1).onChanged((d) -> {
            this.key.x = d;
            this.setting.onChanged();
         })).onModuleActivated((setting) -> setting.set(this.key.x))).build());
         sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("key-y")).description("Y position offset of the key.")).defaultValue((double)0.0F).sliderRange((double)-200.0F, (double)200.0F).decimalPlaces(1).onChanged((d) -> {
            this.key.y = d;
            this.setting.onChanged();
         })).onModuleActivated((setting) -> setting.set(this.key.y))).build());
         sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-cps")).description("Show CPS for this key.")).defaultValue(false)).onChanged((b) -> this.key.showCps = b)).onModuleActivated((setting) -> setting.set(this.key.showCps))).build());
         settings.onActivated();
         this.add(this.theme.settings(settings)).expandX();
      }
   }
}
