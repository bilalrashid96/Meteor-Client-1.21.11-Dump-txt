package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;
import net.minecraft.class_2487;

public class DoubleSetting extends Setting<Double> {
   public final double min;
   public final double max;
   public final double sliderMin;
   public final double sliderMax;
   public final boolean onSliderRelease;
   public final int decimalPlaces;
   public final boolean noSlider;

   private DoubleSetting(String name, String description, double defaultValue, Consumer<Double> onChanged, Consumer<Setting<Double>> onModuleActivated, IVisible visible, double min, double max, double sliderMin, double sliderMax, boolean onSliderRelease, int decimalPlaces, boolean noSlider) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.min = min;
      this.max = max;
      this.sliderMin = sliderMin;
      this.sliderMax = sliderMax;
      this.decimalPlaces = decimalPlaces;
      this.onSliderRelease = onSliderRelease;
      this.noSlider = noSlider;
   }

   protected Double parseImpl(String str) {
      try {
         return Double.parseDouble(str.trim());
      } catch (NumberFormatException var3) {
         return null;
      }
   }

   protected boolean isValueValid(Double value) {
      return value >= this.min && value <= this.max;
   }

   protected class_2487 save(class_2487 tag) {
      tag.method_10549("value", (Double)this.get());
      return tag;
   }

   public Double load(class_2487 tag) {
      this.set(tag.method_68563("value", (double)0.0F));
      return (Double)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, Double, DoubleSetting> {
      public double min = Double.NEGATIVE_INFINITY;
      public double max = Double.POSITIVE_INFINITY;
      public double sliderMin = (double)0.0F;
      public double sliderMax = (double)10.0F;
      public boolean onSliderRelease = false;
      public int decimalPlaces = 3;
      public boolean noSlider = false;

      public Builder() {
         super((double)0.0F);
      }

      public Builder defaultValue(double defaultValue) {
         this.defaultValue = defaultValue;
         return this;
      }

      public Builder min(double min) {
         this.min = min;
         return this;
      }

      public Builder max(double max) {
         this.max = max;
         return this;
      }

      public Builder range(double min, double max) {
         this.min = Math.min(min, max);
         this.max = Math.max(min, max);
         return this;
      }

      public Builder sliderMin(double min) {
         this.sliderMin = min;
         return this;
      }

      public Builder sliderMax(double max) {
         this.sliderMax = max;
         return this;
      }

      public Builder sliderRange(double min, double max) {
         this.sliderMin = min;
         this.sliderMax = max;
         return this;
      }

      public Builder onSliderRelease() {
         this.onSliderRelease = true;
         return this;
      }

      public Builder decimalPlaces(int decimalPlaces) {
         this.decimalPlaces = decimalPlaces;
         return this;
      }

      public Builder noSlider() {
         this.noSlider = true;
         return this;
      }

      public DoubleSetting build() {
         return new DoubleSetting(this.name, this.description, (Double)this.defaultValue, this.onChanged, this.onModuleActivated, this.visible, this.min, this.max, Math.max(this.sliderMin, this.min), Math.min(this.sliderMax, this.max), this.onSliderRelease, this.decimalPlaces, this.noSlider);
      }
   }
}
