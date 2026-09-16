package meteordevelopment.meteorclient.gui.screens.settings;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WQuad;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WIntEdit;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_11909;
import net.minecraft.class_3532;
import net.minecraft.class_437;

public class ColorSettingScreen extends WindowScreen {
   private static final Color[] HUE_COLORS = new Color[]{new Color(255, 0, 0), new Color(255, 255, 0), new Color(0, 255, 0), new Color(0, 255, 255), new Color(0, 0, 255), new Color(255, 0, 255), new Color(255, 0, 0)};
   private static final Color WHITE = new Color(255, 255, 255);
   private static final Color BLACK = new Color(0, 0, 0);
   public Runnable action;
   private final Setting<SettingColor> setting;
   private WQuad displayQuad;
   private WBrightnessQuad brightnessQuad;
   private WHueQuad hueQuad;
   private WIntEdit rItb;
   private WIntEdit gItb;
   private WIntEdit bItb;
   private WIntEdit aItb;
   private WCheckbox rainbow;

   public ColorSettingScreen(GuiTheme theme, Setting<SettingColor> setting) {
      super(theme, "Select Color");
      this.setting = setting;
   }

   public void initWidgets() {
      this.displayQuad = (WQuad)this.add(this.theme.quad(this.setting.get())).expandX().widget();
      this.brightnessQuad = (WBrightnessQuad)this.add(new WBrightnessQuad()).expandX().widget();
      this.hueQuad = (WHueQuad)this.add(new WHueQuad()).expandX().widget();
      WTable rgbaTable = (WTable)this.add(this.theme.table()).expandX().widget();
      rgbaTable.add(this.theme.label("R:"));
      this.rItb = (WIntEdit)rgbaTable.add(this.theme.intEdit((this.setting.get()).r, 0, 255, 0, 255, false)).expandX().widget();
      this.rItb.action = this::rgbaChanged;
      rgbaTable.row();
      rgbaTable.add(this.theme.label("G:"));
      this.gItb = (WIntEdit)rgbaTable.add(this.theme.intEdit((this.setting.get()).g, 0, 255, 0, 255, false)).expandX().widget();
      this.gItb.action = this::rgbaChanged;
      rgbaTable.row();
      rgbaTable.add(this.theme.label("B:"));
      this.bItb = (WIntEdit)rgbaTable.add(this.theme.intEdit((this.setting.get()).b, 0, 255, 0, 255, false)).expandX().widget();
      this.bItb.action = this::rgbaChanged;
      rgbaTable.row();
      rgbaTable.add(this.theme.label("A:"));
      this.aItb = (WIntEdit)rgbaTable.add(this.theme.intEdit((this.setting.get()).a, 0, 255, 0, 255, false)).expandX().widget();
      this.aItb.action = this::rgbaChanged;
      WHorizontalList rainbowList = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
      rainbowList.add(this.theme.label("Rainbow: "));
      this.rainbow = this.theme.checkbox((this.setting.get()).rainbow);
      this.rainbow.action = () -> {
         (this.setting.get()).rainbow = this.rainbow.checked;
         this.setting.onChanged();
      };
      rainbowList.add(this.rainbow).expandCellX().right();
      WHorizontalList bottomList = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
      WButton backButton = (WButton)bottomList.add(this.theme.button("Back")).expandX().widget();
      backButton.action = this::method_25419;
      WButton copyButton = (WButton)bottomList.add(this.theme.button(GuiRenderer.COPY)).widget();
      copyButton.action = this::toClipboard;
      copyButton.tooltip = "Copy config";
      WButton pasteButton = (WButton)bottomList.add(this.theme.button(GuiRenderer.PASTE)).widget();
      pasteButton.action = this::fromClipboard;
      pasteButton.tooltip = "Paste config";
      WButton resetButton = (WButton)bottomList.add(this.theme.button(GuiRenderer.RESET)).widget();
      resetButton.action = () -> {
         this.setting.reset();
         this.setFromSetting();
         this.callAction();
      };
      resetButton.tooltip = "Reset";
      this.hueQuad.calculateFromSetting(false);
      this.brightnessQuad.calculateFromColor(this.setting.get(), false);
   }

   private void setFromSetting() {
      SettingColor c = this.setting.get();
      if (c.r != this.rItb.get()) {
         this.rItb.set(c.r);
      }

      if (c.g != this.gItb.get()) {
         this.gItb.set(c.g);
      }

      if (c.b != this.bItb.get()) {
         this.bItb.set(c.b);
      }

      if (c.a != this.aItb.get()) {
         this.aItb.set(c.a);
      }

      this.rainbow.checked = c.rainbow;
      this.displayQuad.color.set(this.setting.get());
      this.hueQuad.calculateFromSetting(true);
      this.brightnessQuad.calculateFromColor(this.setting.get(), true);
   }

   private void callAction() {
      if (this.action != null) {
         this.action.run();
      }

   }

   public void method_25393() {
      super.method_25393();
      if ((this.setting.get()).rainbow) {
         this.setFromSetting();
      }

   }

   private void rgbaChanged() {
      Color c = this.setting.get();
      c.r = this.rItb.get();
      c.g = this.gItb.get();
      c.b = this.bItb.get();
      c.a = this.aItb.get();
      c.validate();
      if (c.r != this.rItb.get()) {
         this.rItb.set(c.r);
      }

      if (c.g != this.gItb.get()) {
         this.gItb.set(c.g);
      }

      if (c.b != this.bItb.get()) {
         this.bItb.set(c.b);
      }

      if (c.a != this.aItb.get()) {
         this.aItb.set(c.a);
      }

      this.displayQuad.color.set(c);
      this.hueQuad.calculateFromSetting(true);
      this.brightnessQuad.calculateFromColor(this.setting.get(), true);
      this.setting.onChanged();
      this.callAction();
   }

   private void hsvChanged() {
      double r = (double)0.0F;
      double g = (double)0.0F;
      double b = (double)0.0F;
      boolean calculated = false;
      if (this.brightnessQuad.saturation <= (double)0.0F) {
         r = this.brightnessQuad.value;
         g = this.brightnessQuad.value;
         b = this.brightnessQuad.value;
         calculated = true;
      }

      if (!calculated) {
         double hh = this.hueQuad.hueAngle;
         if (hh >= (double)360.0F) {
            hh = (double)0.0F;
         }

         hh /= (double)60.0F;
         int i = (int)hh;
         double ff = hh - (double)i;
         double p = this.brightnessQuad.value * ((double)1.0F - this.brightnessQuad.saturation);
         double q = this.brightnessQuad.value * ((double)1.0F - this.brightnessQuad.saturation * ff);
         double t = this.brightnessQuad.value * ((double)1.0F - this.brightnessQuad.saturation * ((double)1.0F - ff));
         switch (i) {
            case 0:
               r = this.brightnessQuad.value;
               g = t;
               b = p;
               break;
            case 1:
               r = q;
               g = this.brightnessQuad.value;
               b = p;
               break;
            case 2:
               r = p;
               g = this.brightnessQuad.value;
               b = t;
               break;
            case 3:
               r = p;
               g = q;
               b = this.brightnessQuad.value;
               break;
            case 4:
               r = t;
               g = p;
               b = this.brightnessQuad.value;
               break;
            default:
               r = this.brightnessQuad.value;
               g = p;
               b = q;
         }
      }

      Color c = this.setting.get();
      c.r = (int)(r * (double)255.0F);
      c.g = (int)(g * (double)255.0F);
      c.b = (int)(b * (double)255.0F);
      c.validate();
      this.rItb.set(c.r);
      this.gItb.set(c.g);
      this.bItb.set(c.b);
      this.displayQuad.color.set(c);
      this.setting.onChanged();
      this.callAction();
   }

   public boolean toClipboard() {
      return NbtUtils.toClipboard(this.setting.get());
   }

   public boolean fromClipboard() {
      if (!NbtUtils.fromClipboard(this.setting.get())) {
         String clipboard = MeteorClient.mc.field_1774.method_1460().trim();
         SettingColor parsed = this.parseRGBA(clipboard);
         if (parsed == null) {
            parsed = this.parseHex(clipboard);
            if (parsed == null) {
               return false;
            }
         }

         this.setting.set(parsed);
      }

      ((SettingColor)this.setting.get()).validate();
      class_437 var4 = this.parent;
      if (var4 instanceof WidgetScreen p) {
         p.reload();
      }

      this.reload();
      return true;
   }

   private SettingColor parseRGBA(String string) {
      String[] rgba = string.replaceAll("[^0-9|,]", "").split(",");
      if (rgba.length >= 3 && rgba.length <= 4) {
         try {
            SettingColor color = new SettingColor(Integer.parseInt(rgba[0]), Integer.parseInt(rgba[1]), Integer.parseInt(rgba[2]));
            if (rgba.length == 4) {
               color.a = Integer.parseInt(rgba[3]);
            }

            return color;
         } catch (NumberFormatException var5) {
            return null;
         }
      } else {
         return null;
      }
   }

   private SettingColor parseHex(String string) {
      if (!string.startsWith("#")) {
         return null;
      } else {
         String hex = string.toLowerCase().replaceAll("[^0-9a-f]", "");
         if (hex.length() != 6 && hex.length() != 8) {
            return null;
         } else {
            try {
               SettingColor color = new SettingColor(Integer.parseInt(hex.substring(0, 2), 16), Integer.parseInt(hex.substring(2, 4), 16), Integer.parseInt(hex.substring(4, 6), 16));
               if (hex.length() == 8) {
                  color.a = Integer.parseInt(hex.substring(6, 8), 16);
               }

               return color;
            } catch (NumberFormatException var5) {
               return null;
            }
         }
      }
   }

   private class WBrightnessQuad extends WWidget {
      double saturation;
      double value;
      double handleX;
      double handleY;
      boolean dragging;
      double lastMouseX;
      double lastMouseY;
      double fixedHeight = (double)-1.0F;

      protected void onCalculateSize() {
         double s = this.theme.scale((double)75.0F);
         this.width = s;
         this.height = s;
         if (this.fixedHeight != (double)-1.0F) {
            this.height = this.fixedHeight;
            this.fixedHeight = (double)-1.0F;
         }

      }

      void calculateFromColor(Color c, boolean calculateNow) {
         double min = (double)Math.min(Math.min(c.r, c.g), c.b);
         double max = (double)Math.max(Math.max(c.r, c.g), c.b);
         double delta = max - min;
         this.value = max / (double)255.0F;
         if (delta == (double)0.0F) {
            this.saturation = (double)0.0F;
         } else {
            this.saturation = delta / max;
         }

         if (calculateNow) {
            this.handleX = this.saturation * this.width;
            this.handleY = ((double)1.0F - this.value) * this.height;
         }

      }

      public boolean onMouseClicked(class_11909 click, boolean doubled) {
         if (doubled) {
            return false;
         } else if (this.mouseOver) {
            this.dragging = true;
            this.setFocused(true);
            this.handleX = this.lastMouseX - this.x;
            this.handleY = this.lastMouseY - this.y;
            this.handleMoved();
            return true;
         } else {
            return false;
         }
      }

      public boolean onMouseReleased(class_11909 click) {
         if (this.dragging) {
            this.dragging = false;
            this.setFocused(false);
         }

         return false;
      }

      public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
         if (this.dragging) {
            if (mouseX >= this.x && mouseX <= this.x + this.width) {
               this.handleX += mouseX - lastMouseX;
            } else if (this.handleX > (double)0.0F && mouseX < this.x) {
               this.handleX = (double)0.0F;
            } else if (this.handleX < this.width && mouseX > this.x + this.width) {
               this.handleX = this.width;
            }

            if (mouseY >= this.y && mouseY <= this.y + this.height) {
               this.handleY += mouseY - lastMouseY;
            } else if (this.handleY > (double)0.0F && mouseY < this.y) {
               this.handleY = (double)0.0F;
            } else if (this.handleY < this.height && mouseY > this.y + this.height) {
               this.handleY = this.height;
            }

            this.handleMoved();
         }

         this.lastMouseX = mouseX;
         this.lastMouseY = mouseY;
      }

      void handleMoved() {
         double handleXPercentage = this.handleX / this.width;
         double handleYPercentage = this.handleY / this.height;
         this.saturation = handleXPercentage;
         this.value = (double)1.0F - handleYPercentage;
         ColorSettingScreen.this.hsvChanged();
      }

      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         if (this.height != this.width) {
            this.fixedHeight = this.width;
            this.invalidate();
            this.handleX = this.saturation * this.width;
            this.handleY = ((double)1.0F - this.value) * this.fixedHeight;
         }

         ColorSettingScreen.this.hueQuad.calculateColor();
         renderer.quad(this.x, this.y, this.width, this.height, ColorSettingScreen.WHITE, ColorSettingScreen.this.hueQuad.color, ColorSettingScreen.BLACK, ColorSettingScreen.BLACK);
         double s = this.theme.scale((double)2.0F);
         renderer.quad(this.x + this.handleX - s / (double)2.0F, this.y + this.handleY - s / (double)2.0F, s, s, ColorSettingScreen.WHITE);
      }
   }

   private class WHueQuad extends WWidget {
      private double hueAngle;
      private double handleX;
      private final Color color = new Color();
      private boolean dragging;
      private double lastMouseX;
      private boolean calculateHandleXOnLayout;

      protected void onCalculateSize() {
         this.width = this.theme.scale((double)75.0F);
         this.height = this.theme.scale((double)10.0F);
      }

      void calculateFromSetting(boolean calculateNow) {
         Color c = ColorSettingScreen.this.setting.get();
         boolean calculated = false;
         double min = (double)Math.min(c.r, c.g);
         min = min < (double)c.b ? min : (double)c.b;
         double max = (double)Math.max(c.r, c.g);
         max = max > (double)c.b ? max : (double)c.b;
         double delta = max - min;
         if (delta < 1.0E-5) {
            this.hueAngle = (double)0.0F;
            calculated = true;
         }

         if (!calculated) {
            if (max <= (double)0.0F) {
               this.hueAngle = (double)0.0F;
               calculated = true;
            }

            if (!calculated) {
               if ((double)c.r >= max) {
                  this.hueAngle = (double)(c.g - c.b) / delta;
               } else if ((double)c.g >= max) {
                  this.hueAngle = (double)2.0F + (double)(c.b - c.r) / delta;
               } else {
                  this.hueAngle = (double)4.0F + (double)(c.r - c.g) / delta;
               }

               this.hueAngle *= (double)60.0F;
               if (this.hueAngle < (double)0.0F) {
                  this.hueAngle += (double)360.0F;
               }
            }
         }

         if (calculateNow) {
            double huePercentage = this.hueAngle / (double)360.0F;
            this.handleX = huePercentage * this.width;
         } else {
            this.calculateHandleXOnLayout = true;
         }

      }

      protected void onCalculateWidgetPositions() {
         if (this.calculateHandleXOnLayout) {
            double huePercentage = this.hueAngle / (double)360.0F;
            this.handleX = huePercentage * this.width;
            this.calculateHandleXOnLayout = false;
         }

         super.onCalculateWidgetPositions();
      }

      void calculateColor() {
         double hh = this.hueAngle;
         if (hh >= (double)360.0F) {
            hh = (double)0.0F;
         }

         hh /= (double)60.0F;
         int i = (int)hh;
         double ff = hh - (double)i;
         double p = (double)0.0F;
         double q = (double)1.0F * ((double)1.0F - (double)1.0F * ff);
         double t = (double)1.0F * ((double)1.0F - (double)1.0F * ((double)1.0F - ff));
         double r;
         double g;
         double b;
         switch (i) {
            case 0:
               r = (double)1.0F;
               g = t;
               b = p;
               break;
            case 1:
               r = q;
               g = (double)1.0F;
               b = p;
               break;
            case 2:
               r = p;
               g = (double)1.0F;
               b = t;
               break;
            case 3:
               r = p;
               g = q;
               b = (double)1.0F;
               break;
            case 4:
               r = t;
               g = p;
               b = (double)1.0F;
               break;
            default:
               r = (double)1.0F;
               g = p;
               b = q;
         }

         this.color.r = (int)(r * (double)255.0F);
         this.color.g = (int)(g * (double)255.0F);
         this.color.b = (int)(b * (double)255.0F);
         this.color.validate();
      }

      public boolean onMouseClicked(class_11909 click, boolean doubled) {
         if (doubled) {
            return false;
         } else if (this.mouseOver) {
            this.dragging = true;
            this.setFocused(true);
            this.handleX = this.lastMouseX - this.x;
            this.calculateHueAngleFromHandleX();
            ColorSettingScreen.this.hsvChanged();
            return true;
         } else {
            return false;
         }
      }

      public boolean onMouseReleased(class_11909 click) {
         if (this.dragging) {
            this.dragging = false;
            this.setFocused(false);
         }

         return this.mouseOver;
      }

      public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
         if (this.dragging) {
            if (mouseX >= this.x && mouseX <= this.x + this.width) {
               this.handleX += mouseX - lastMouseX;
               this.handleX = class_3532.method_15350(this.handleX, (double)0.0F, this.width);
            } else if (this.handleX > (double)0.0F && mouseX < this.x) {
               this.handleX = (double)0.0F;
            } else if (this.handleX < this.width && mouseX > this.x + this.width) {
               this.handleX = this.width;
            }

            this.calculateHueAngleFromHandleX();
            ColorSettingScreen.this.hsvChanged();
         }

         this.lastMouseX = mouseX;
      }

      void calculateHueAngleFromHandleX() {
         double handleXPercentage = this.handleX / (this.width - (double)4.0F);
         this.hueAngle = handleXPercentage * (double)360.0F;
      }

      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         double sectionWidth = this.width / (double)(ColorSettingScreen.HUE_COLORS.length - 1);
         double sectionX = this.x;

         for(int i = 0; i < ColorSettingScreen.HUE_COLORS.length - 1; ++i) {
            renderer.quad(sectionX, this.y, sectionWidth, this.height, ColorSettingScreen.HUE_COLORS[i], ColorSettingScreen.HUE_COLORS[i + 1], ColorSettingScreen.HUE_COLORS[i + 1], ColorSettingScreen.HUE_COLORS[i]);
            sectionX += sectionWidth;
         }

         double s = this.theme.scale((double)2.0F);
         renderer.quad(this.x + this.handleX - s / (double)2.0F, this.y, s, this.height, ColorSettingScreen.WHITE);
      }
   }
}
