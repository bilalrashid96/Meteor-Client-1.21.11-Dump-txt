package meteordevelopment.meteorclient.gui.widgets.input;

import java.util.Locale;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;

public class WDoubleEdit extends WHorizontalList {
   private double value;
   private final double min;
   private final double max;
   private final double sliderMin;
   private final double sliderMax;
   public int decimalPlaces;
   public boolean noSlider = false;
   public boolean small = false;
   public Runnable action;
   public Runnable actionOnRelease;
   private WTextBox textBox;
   private WSlider slider;

   public WDoubleEdit(double value, double min, double max, double sliderMin, double sliderMax, int decimalPlaces, boolean noSlider) {
      this.value = value;
      this.min = min;
      this.max = max;
      this.decimalPlaces = decimalPlaces;
      this.sliderMin = sliderMin;
      this.sliderMax = sliderMax;
      if (noSlider || sliderMin == (double)0.0F && sliderMax == (double)0.0F) {
         this.noSlider = true;
      }

   }

   public void init() {
      this.textBox = (WTextBox)this.add(this.theme.textBox(this.valueString(), this::filter)).minWidth((double)75.0F).widget();
      if (this.noSlider) {
         ((WButton)this.add(this.theme.button("+")).widget()).action = () -> this.setButton(this.get() + (double)1.0F);
         ((WButton)this.add(this.theme.button("-")).widget()).action = () -> this.setButton(this.get() - (double)1.0F);
      } else {
         this.slider = (WSlider)this.add(this.theme.slider(this.value, this.sliderMin, this.sliderMax)).minWidth(this.small ? (double)125.0F - this.spacing : (double)200.0F).centerY().expandX().widget();
      }

      this.textBox.actionOnUnfocused = () -> {
         double lastValue = this.value;
         if (this.textBox.get().isEmpty()) {
            this.value = (double)0.0F;
         } else if (this.textBox.get().equals("-")) {
            this.value = (double)0.0F;
         } else if (this.textBox.get().equals(".")) {
            this.value = (double)0.0F;
         } else if (this.textBox.get().equals("-.")) {
            this.value = (double)0.0F;
         } else {
            try {
               this.value = Double.parseDouble(this.textBox.get());
            } catch (NumberFormatException var5) {
            }
         }

         double preValidationValue = this.value;
         if (this.value < this.min) {
            this.value = this.min;
         } else if (this.value > this.max) {
            this.value = this.max;
         }

         if (this.value != preValidationValue) {
            this.textBox.set(this.valueString());
         }

         if (this.slider != null) {
            this.slider.set(this.value);
         }

         if (this.value != lastValue) {
            if (this.action != null) {
               this.action.run();
            }

            if (this.actionOnRelease != null) {
               this.actionOnRelease.run();
            }
         }

      };
      if (this.slider != null) {
         this.slider.action = () -> {
            double lastValue = this.value;
            this.value = this.slider.get();
            this.textBox.set(this.valueString());
            if (this.action != null && this.value != lastValue) {
               this.action.run();
            }

         };
         this.slider.actionOnRelease = () -> {
            if (this.actionOnRelease != null) {
               this.actionOnRelease.run();
            }

         };
      }

   }

   private boolean filter(String text, char c) {
      boolean validate = true;
      boolean good;
      if (c == '-' && !text.contains("-") && this.textBox.cursor == 0) {
         good = true;
         validate = false;
      } else if (c == '.' && !text.contains(".")) {
         good = true;
         if (text.isEmpty()) {
            validate = false;
         }
      } else {
         good = Character.isDigit(c);
      }

      if (good && validate) {
         try {
            Double.parseDouble(text + c);
         } catch (NumberFormatException var6) {
            good = false;
         }
      }

      return good;
   }

   private void setButton(double v) {
      if (this.value != v) {
         if (v < this.min) {
            this.value = this.min;
         } else {
            this.value = Math.min(v, this.max);
         }

         if (this.value == v) {
            this.textBox.set(this.valueString());
            if (this.slider != null) {
               this.slider.set(this.value);
            }

            if (this.action != null) {
               this.action.run();
            }

            if (this.actionOnRelease != null) {
               this.actionOnRelease.run();
            }
         }

      }
   }

   public double get() {
      return this.value;
   }

   public void set(double value) {
      this.value = value;
      this.textBox.set(this.valueString());
      if (this.slider != null) {
         this.slider.set(value);
      }

   }

   private String valueString() {
      return String.format(Locale.US, "%." + this.decimalPlaces + "f", this.value);
   }
}
