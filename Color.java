package meteordevelopment.meteorclient.utils.render.color;

import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.class_124;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2583;
import net.minecraft.class_5251;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Color implements ICopyable<Color>, ISerializable<Color> {
   public static final Color CLEAR = new Color(0, 0, 0, 0);
   public static final Color WHITE;
   public static final Color LIGHT_GRAY;
   public static final Color GRAY;
   public static final Color DARK_GRAY;
   public static final Color BLACK;
   public static final Color RED;
   public static final Color PINK;
   public static final Color ORANGE;
   public static final Color YELLOW;
   public static final Color GREEN;
   public static final Color MAGENTA;
   public static final Color CYAN;
   public static final Color BLUE;
   public int r;
   public int g;
   public int b;
   public int a;

   public Color() {
      this(255, 255, 255, 255);
   }

   public Color(int r, int g, int b) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = 255;
      this.validate();
   }

   public Color(int r, int g, int b, int a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
      this.validate();
   }

   public Color(float r, float g, float b, float a) {
      this.r = (int)(r * 255.0F);
      this.g = (int)(g * 255.0F);
      this.b = (int)(b * 255.0F);
      this.a = (int)(a * 255.0F);
      this.validate();
   }

   public Color(int packed) {
      this.r = toRGBAR(packed);
      this.g = toRGBAG(packed);
      this.b = toRGBAB(packed);
      this.a = toRGBAA(packed);
   }

   public Color(Color color) {
      this.r = color.r;
      this.g = color.g;
      this.b = color.b;
      this.a = color.a;
   }

   public Color(java.awt.Color color) {
      this.r = color.getRed();
      this.g = color.getGreen();
      this.b = color.getBlue();
      this.a = color.getAlpha();
   }

   public Color(class_124 formatting) {
      if (formatting.method_543()) {
         this.r = toRGBAR(formatting.method_532());
         this.g = toRGBAG(formatting.method_532());
         this.b = toRGBAB(formatting.method_532());
         this.a = toRGBAA(formatting.method_532());
      } else {
         this.r = 255;
         this.g = 255;
         this.b = 255;
         this.a = 255;
      }

   }

   public Color(class_5251 textColor) {
      this.r = toRGBAR(textColor.method_27716());
      this.g = toRGBAG(textColor.method_27716());
      this.b = toRGBAB(textColor.method_27716());
      this.a = toRGBAA(textColor.method_27716());
   }

   public Color(class_2583 style) {
      class_5251 textColor = style.method_10973();
      if (textColor == null) {
         this.r = 255;
         this.g = 255;
         this.b = 255;
         this.a = 255;
      } else {
         this.r = toRGBAR(textColor.method_27716());
         this.g = toRGBAG(textColor.method_27716());
         this.b = toRGBAB(textColor.method_27716());
         this.a = toRGBAA(textColor.method_27716());
      }

   }

   public static int fromRGBA(int r, int g, int b, int a) {
      return (r << 16) + (g << 8) + b + (a << 24);
   }

   public static int toRGBAR(int color) {
      return color >> 16 & 255;
   }

   public static int toRGBAG(int color) {
      return color >> 8 & 255;
   }

   public static int toRGBAB(int color) {
      return color & 255;
   }

   public static int toRGBAA(int color) {
      return color >> 24 & 255;
   }

   public static Color fromHsv(double h, double s, double v) {
      if (s <= (double)0.0F) {
         return new Color((int)(v * (double)255.0F), (int)(v * (double)255.0F), (int)(v * (double)255.0F), 255);
      } else {
         double hh = h;
         if (h >= (double)360.0F) {
            hh = (double)0.0F;
         }

         hh /= (double)60.0F;
         int i = (int)hh;
         double ff = hh - (double)i;
         double p = v * ((double)1.0F - s);
         double q = v * ((double)1.0F - s * ff);
         double t = v * ((double)1.0F - s * ((double)1.0F - ff));
         double r;
         double g;
         double b;
         switch (i) {
            case 0:
               r = v;
               g = t;
               b = p;
               break;
            case 1:
               r = q;
               g = v;
               b = p;
               break;
            case 2:
               r = p;
               g = v;
               b = t;
               break;
            case 3:
               r = p;
               g = q;
               b = v;
               break;
            case 4:
               r = t;
               g = p;
               b = v;
               break;
            case 5:
            default:
               r = v;
               g = p;
               b = q;
         }

         return new Color((int)(r * (double)255.0F), (int)(g * (double)255.0F), (int)(b * (double)255.0F), 255);
      }
   }

   public Color set(int r, int g, int b, int a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
      this.validate();
      return this;
   }

   public Color r(int r) {
      this.r = r;
      this.validate();
      return this;
   }

   public Color g(int g) {
      this.g = g;
      this.validate();
      return this;
   }

   public Color b(int b) {
      this.b = b;
      this.validate();
      return this;
   }

   public Color a(int a) {
      this.a = a;
      this.validate();
      return this;
   }

   public Color set(Color value) {
      this.r = value.r;
      this.g = value.g;
      this.b = value.b;
      this.a = value.a;
      this.validate();
      return this;
   }

   public boolean parse(String text) {
      String[] split = text.split(",");
      if (split.length != 3 && split.length != 4) {
         return false;
      } else {
         try {
            int r = Integer.parseInt(split[0]);
            int g = Integer.parseInt(split[1]);
            int b = Integer.parseInt(split[2]);
            int a = split.length == 4 ? Integer.parseInt(split[3]) : this.a;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            return true;
         } catch (NumberFormatException var7) {
            return false;
         }
      }
   }

   public Color copy() {
      return new Color(this.r, this.g, this.b, this.a);
   }

   public SettingColor toSetting() {
      return new SettingColor(this.r, this.g, this.b, this.a);
   }

   public class_5251 toTextColor() {
      return class_5251.method_27717(this.getPacked());
   }

   public class_2583 toStyle() {
      return class_2583.field_24360.method_27703(this.toTextColor());
   }

   public class_2583 styleWith(class_2583 style) {
      return style.method_27703(this.toTextColor());
   }

   public void validate() {
      if (this.r < 0) {
         this.r = 0;
      } else if (this.r > 255) {
         this.r = 255;
      }

      if (this.g < 0) {
         this.g = 0;
      } else if (this.g > 255) {
         this.g = 255;
      }

      if (this.b < 0) {
         this.b = 0;
      } else if (this.b > 255) {
         this.b = 255;
      }

      if (this.a < 0) {
         this.a = 0;
      } else if (this.a > 255) {
         this.a = 255;
      }

   }

   public class_243 getVec3d() {
      return new class_243((double)this.r / (double)255.0F, (double)this.g / (double)255.0F, (double)this.b / (double)255.0F);
   }

   public Vector3f getVec3f() {
      return new Vector3f((float)this.r / 255.0F, (float)this.g / 255.0F, (float)this.b / 255.0F);
   }

   public Vector4f getVec4f() {
      return new Vector4f((float)this.r / 255.0F, (float)this.g / 255.0F, (float)this.b / 255.0F, (float)this.a / 255.0F);
   }

   public int getPacked() {
      return fromRGBA(this.r, this.g, this.b, this.a);
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10569("r", this.r);
      tag.method_10569("g", this.g);
      tag.method_10569("b", this.b);
      tag.method_10569("a", this.a);
      return tag;
   }

   public Color fromTag(class_2487 tag) {
      this.r = tag.method_68083("r", 0);
      this.g = tag.method_68083("g", 0);
      this.b = tag.method_68083("b", 0);
      this.a = tag.method_68083("a", 0);
      this.validate();
      return this;
   }

   public String toString() {
      return this.r + " " + this.g + " " + this.b + " " + this.a;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Color color = (Color)o;
         return this.r == color.r && this.g == color.g && this.b == color.b && this.a == color.a;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.r;
      result = 31 * result + this.g;
      result = 31 * result + this.b;
      result = 31 * result + this.a;
      return result;
   }

   static {
      WHITE = new Color(java.awt.Color.WHITE);
      LIGHT_GRAY = new Color(java.awt.Color.LIGHT_GRAY);
      GRAY = new Color(java.awt.Color.GRAY);
      DARK_GRAY = new Color(java.awt.Color.DARK_GRAY);
      BLACK = new Color(java.awt.Color.BLACK);
      RED = new Color(java.awt.Color.RED);
      PINK = new Color(java.awt.Color.PINK);
      ORANGE = new Color(java.awt.Color.ORANGE);
      YELLOW = new Color(java.awt.Color.YELLOW);
      GREEN = new Color(java.awt.Color.GREEN);
      MAGENTA = new Color(java.awt.Color.MAGENTA);
      CYAN = new Color(java.awt.Color.CYAN);
      BLUE = new Color(java.awt.Color.BLUE);
   }
}
