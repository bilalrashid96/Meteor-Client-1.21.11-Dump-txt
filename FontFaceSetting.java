package meteordevelopment.meteorclient.settings;

import java.util.List;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.renderer.text.FontFamily;
import meteordevelopment.meteorclient.renderer.text.FontInfo;
import net.minecraft.class_2487;

public class FontFaceSetting extends Setting<FontFace> {
   public FontFaceSetting(String name, String description, FontFace defaultValue, Consumer<FontFace> onChanged, Consumer<Setting<FontFace>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   protected FontFace parseImpl(String str) {
      String[] split = str.replace(" ", "").split("-");
      if (split.length != 2) {
         return null;
      } else {
         for(FontFamily family : Fonts.FONT_FAMILIES) {
            if (family.getName().replace(" ", "").equals(split[0])) {
               try {
                  return family.get(FontInfo.Type.valueOf(split[1]));
               } catch (IllegalArgumentException var6) {
                  return null;
               }
            }
         }

         return null;
      }
   }

   public List<String> getSuggestions() {
      return List.of("JetBrainsMono-Regular", "Arial-Bold");
   }

   protected boolean isValueValid(FontFace value) {
      if (value == null) {
         return false;
      } else {
         for(FontFamily fontFamily : Fonts.FONT_FAMILIES) {
            if (fontFamily.hasType(value.info.type())) {
               return true;
            }
         }

         return false;
      }
   }

   protected class_2487 save(class_2487 tag) {
      tag.method_10582("family", ((FontFace)this.get()).info.family());
      tag.method_10582("type", ((FontFace)this.get()).info.type().toString());
      return tag;
   }

   protected FontFace load(class_2487 tag) {
      String family = tag.method_68564("family", "");

      FontInfo.Type type;
      try {
         type = FontInfo.Type.valueOf(tag.method_68564("type", ""));
      } catch (IllegalArgumentException var7) {
         this.set(Fonts.DEFAULT_FONT);
         return (FontFace)this.get();
      }

      boolean changed = false;

      for(FontFamily fontFamily : Fonts.FONT_FAMILIES) {
         if (fontFamily.getName().equals(family)) {
            this.set(fontFamily.get(type));
            changed = true;
         }
      }

      if (!changed) {
         this.set(Fonts.DEFAULT_FONT);
      }

      return (FontFace)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, FontFace, FontFaceSetting> {
      public Builder() {
         super(Fonts.DEFAULT_FONT);
      }

      public FontFaceSetting build() {
         return new FontFaceSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
