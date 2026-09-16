package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;
import net.minecraft.class_2487;

public class IntSetting extends Setting<Integer> {
   public final int min;
   public final int max;
   public final int sliderMin;
   public final int sliderMax;
   public final boolean noSlider;

   private IntSetting(String name, String description, int defaultValue, Consumer<Integer> onChanged, Consumer<Setting<Integer>> onModuleActivated, IVisible visible, int min, int max, int sliderMin, int sliderMax, boolean noSlider) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.min = min;
      this.max = max;
      this.sliderMin = sliderMin;
      this.sliderMax = sliderMax;
      this.noSlider = noSlider;
   }

   protected Integer parseImpl(String str) {
      try {
         return Integer.parseInt(str.trim());
      } catch (NumberFormatException var3) {
         return null;
      }
   }

   protected boolean isValueValid(Integer value) {
      return value >= this.min && value <= this.max;
   }

   public class_2487 save(class_2487 tag) {
      tag.method_10569("value", (Integer)this.get());
      return tag;
   }

   public Integer load(class_2487 tag) {
      this.set(tag.method_68083("value", 0));
      return (Integer)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, Integer, IntSetting> {
      private int min = Integer.MIN_VALUE;
      private int max = Integer.MAX_VALUE;
      private int sliderMin = 0;
      private int sliderMax = 10;
      private boolean noSlider = false;

      public Builder() {
         super(0);
      }

      public Builder min(int min) {
         this.min = min;
         return this;
      }

      public Builder max(int max) {
         this.max = max;
         return this;
      }

      public Builder range(int min, int max) {
         this.min = Math.min(min, max);
         this.max = Math.max(min, max);
         return this;
      }

      public Builder sliderMin(int min) {
         this.sliderMin = min;
         return this;
      }

      public Builder sliderMax(int max) {
         this.sliderMax = max;
         return this;
      }

      public Builder sliderRange(int min, int max) {
         this.sliderMin = min;
         this.sliderMax = max;
         return this;
      }

      public Builder noSlider() {
         this.noSlider = true;
         return this;
      }

      public IntSetting build() {
         return new IntSetting(this.name, this.description, (Integer)this.defaultValue, this.onChanged, this.onModuleActivated, this.visible, this.min, this.max, Math.max(this.sliderMin, this.min), Math.min(this.sliderMax, this.max), this.noSlider);
      }
   }
}
