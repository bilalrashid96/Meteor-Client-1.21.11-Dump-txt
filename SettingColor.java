package meteordevelopment.meteorclient.utils.render.color;

import net.minecraft.class_124;
import net.minecraft.class_2487;
import net.minecraft.class_2583;
import net.minecraft.class_5251;

public class SettingColor extends Color {
   public boolean rainbow;

   public SettingColor() {
   }

   public SettingColor(int packed) {
      super(packed);
   }

   public SettingColor(int r, int g, int b) {
      super(r, g, b);
   }

   public SettingColor(int r, int g, int b, boolean rainbow) {
      this(r, g, b, 255, rainbow);
   }

   public SettingColor(int r, int g, int b, int a) {
      super(r, g, b, a);
   }

   public SettingColor(float r, float g, float b, float a) {
      super(r, g, b, a);
   }

   public SettingColor(int r, int g, int b, int a, boolean rainbow) {
      super(r, g, b, a);
      this.rainbow = rainbow;
   }

   public SettingColor(SettingColor color) {
      super((Color)color);
      this.rainbow = color.rainbow;
   }

   public SettingColor(java.awt.Color color) {
      super(color);
   }

   public SettingColor(class_124 formatting) {
      super(formatting);
   }

   public SettingColor(class_5251 textColor) {
      super(textColor);
   }

   public SettingColor(class_2583 style) {
      super(style);
   }

   public SettingColor rainbow(boolean rainbow) {
      this.rainbow = rainbow;
      return this;
   }

   public void update() {
      if (this.rainbow) {
         this.set(RainbowColors.GLOBAL.r, RainbowColors.GLOBAL.g, RainbowColors.GLOBAL.b, this.a);
      }

   }

   public SettingColor set(Color value) {
      super.set(value);
      if (value instanceof SettingColor) {
         this.rainbow = ((SettingColor)value).rainbow;
      }

      return this;
   }

   public Color copy() {
      return new SettingColor(this.r, this.g, this.b, this.a, this.rainbow);
   }

   public class_2487 toTag() {
      class_2487 tag = super.toTag();
      tag.method_10556("rainbow", this.rainbow);
      return tag;
   }

   public SettingColor fromTag(class_2487 tag) {
      super.fromTag(tag);
      this.rainbow = tag.method_68566("rainbow", false);
      return this;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         if (!super.equals(o)) {
            return false;
         } else {
            return this.rainbow == ((SettingColor)o).rainbow;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (this.rainbow ? 1 : 0);
      return result;
   }
}
