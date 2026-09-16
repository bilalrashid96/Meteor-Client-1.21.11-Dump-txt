package meteordevelopment.meteorclient.utils.render;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_10799;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_368;
import net.minecraft.class_374;
import net.minecraft.class_5251;
import net.minecraft.class_368.class_369;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MeteorToast implements class_368 {
   private static final int TITLE_COLOR = Color.fromRGBA(145, 61, 226, 255);
   private static final int TEXT_COLOR = Color.fromRGBA(220, 220, 220, 255);
   private static final class_2960 TEXTURE = class_2960.method_60654("toast/advancement");
   private static final long DEFAULT_DURATION = 6000L;
   private static final class_1113 DEFAULT_SOUND;
   private final @NotNull class_2561 title;
   private final @Nullable class_2561 text;
   private final @Nullable class_1799 icon;
   private final @Nullable class_1113 customSound;
   private final long duration;
   private boolean playedSound;
   private long start = -1L;
   private class_368.class_369 visibility;

   private MeteorToast(Builder builder) {
      this.visibility = class_369.field_2209;
      this.title = builder.title;
      this.text = builder.text;
      this.icon = builder.icon;
      this.customSound = builder.customSound;
      this.duration = builder.duration;
   }

   public class_368.class_369 method_61988() {
      return this.visibility;
   }

   public void method_61989(class_374 manager, long time) {
      if (this.start == -1L) {
         this.start = time;
      }

      this.visibility = time - this.start >= this.duration ? class_369.field_2209 : class_369.field_2210;
      if (!this.playedSound) {
         MeteorClient.mc.method_1483().method_4873(this.customSound);
         this.playedSound = true;
      }

   }

   public void method_1986(class_332 context, class_327 textRenderer, long startTime) {
      context.method_52706(class_10799.field_56883, TEXTURE, 0, 0, this.method_29049(), this.method_29050());
      int textX = this.icon != null ? 28 : 12;
      int titleY = 12;
      if (this.text != null) {
         context.method_51439(textRenderer, this.text, textX, 18, TEXT_COLOR, false);
         titleY = 7;
      }

      context.method_51439(textRenderer, this.title, textX, titleY, TITLE_COLOR, false);
      if (this.icon != null) {
         context.method_51427(this.icon, 8, 8);
      }

   }

   static {
      DEFAULT_SOUND = class_1109.method_4757((class_3414)class_3417.field_14725.comp_349(), 1.2F, 1.0F);
   }

   public static class Builder {
      private final @NotNull class_2561 title;
      private @Nullable class_2561 text;
      private @Nullable class_1799 icon;
      private @Nullable class_1113 customSound;
      private long duration;

      public Builder(@NotNull String title) {
         this.customSound = MeteorToast.DEFAULT_SOUND;
         this.duration = 6000L;
         this.title = class_2561.method_43470(title).method_10862(class_2583.field_24360.method_27703(class_5251.method_27717(MeteorToast.TITLE_COLOR)));
      }

      public Builder text(@Nullable String text) {
         this.text = text != null && !text.trim().isEmpty() ? class_2561.method_43470(text).method_10862(class_2583.field_24360.method_27703(class_5251.method_27717(MeteorToast.TEXT_COLOR))) : null;
         return this;
      }

      public Builder icon(@Nullable class_1792 item) {
         this.icon = item != null ? item.method_7854() : null;
         return this;
      }

      public Builder sound(@Nullable class_1113 sound) {
         this.customSound = sound;
         return this;
      }

      public Builder duration(long duration) {
         this.duration = Math.max(0L, duration);
         return this;
      }

      public MeteorToast build() {
         return new MeteorToast(this);
      }
   }
}
