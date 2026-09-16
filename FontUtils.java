package meteordevelopment.meteorclient.utils.render;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.text.BuiltinFontFace;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.renderer.text.FontFamily;
import meteordevelopment.meteorclient.renderer.text.FontInfo;
import meteordevelopment.meteorclient.renderer.text.SystemFontFace;
import meteordevelopment.meteorclient.utils.files.ByteBufferUtils;
import net.minecraft.class_156;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

@NullMarked
public final class FontUtils {
   private FontUtils() {
   }

   public static @Nullable FontInfo getSysFontInfo(File file) {
      return getFontInfo(file);
   }

   public static @Nullable FontInfo getBuiltinFontInfo(String builtin) {
      return getFontInfo(builtinFontStream(builtin));
   }

   private static @Nullable FontInfo getFontInfo(@Nullable File file) {
      if (file != null && file.isFile()) {
         try {
            return getFontInfo(ByteBufferUtils.readFully(file.toPath(), BufferUtils::createByteBuffer));
         } catch (Exception e) {
            MeteorClient.LOG.warn("Failed to read font file: {}", file, e);
            return null;
         }
      } else {
         return null;
      }
   }

   public static @Nullable FontInfo getFontInfo(@Nullable InputStream stream) {
      if (stream == null) {
         return null;
      } else {
         try {
            ReadableByteChannel ch = Channels.newChannel(stream);

            FontInfo var3;
            try {
               ByteBuffer buf = ByteBufferUtils.readFully(ch, BufferUtils::createByteBuffer);
               var3 = getFontInfo(buf);
            } catch (Throwable var5) {
               if (ch != null) {
                  try {
                     ch.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (ch != null) {
               ch.close();
            }

            return var3;
         } catch (Exception e) {
            MeteorClient.LOG.warn("Failed to read font stream.", e);
            return null;
         }
      }
   }

   private static @Nullable FontInfo getFontInfo(ByteBuffer buffer) {
      if (buffer.remaining() < 5) {
         return null;
      } else if (buffer.get(0) == 0 && buffer.get(1) == 1 && buffer.get(2) == 0 && buffer.get(3) == 0 && buffer.get(4) == 0) {
         STBTTFontinfo fontInfo = STBTTFontinfo.create();
         if (!STBTruetype.stbtt_InitFont(fontInfo, buffer)) {
            return null;
         } else {
            ByteBuffer nameBuffer = STBTruetype.stbtt_GetFontNameString(fontInfo, 3, 1, 1033, 1);
            ByteBuffer typeBuffer = STBTruetype.stbtt_GetFontNameString(fontInfo, 3, 1, 1033, 2);
            return typeBuffer != null && nameBuffer != null ? new FontInfo(StandardCharsets.UTF_16.decode(nameBuffer).toString(), FontInfo.Type.fromString(StandardCharsets.UTF_16.decode(typeBuffer).toString())) : null;
         }
      } else {
         return null;
      }
   }

   public static Set<String> getSearchPaths() {
      Set<String> paths = new ObjectOpenHashSet();
      paths.add(System.getProperty("java.home") + "/lib/fonts");

      for(File dir : getUFontDirs()) {
         if (dir.exists()) {
            paths.add(dir.getAbsolutePath());
         }
      }

      for(File dir : getSFontDirs()) {
         if (dir.exists()) {
            paths.add(dir.getAbsolutePath());
         }
      }

      return paths;
   }

   public static List<File> getUFontDirs() {
      List var10000;
      switch (class_156.method_668()) {
         case field_1133 -> var10000 = List.of(new File(System.getProperty("user.home") + "\\AppData\\Local\\Microsoft\\Windows\\Fonts"));
         case field_1137 -> var10000 = List.of(new File(System.getProperty("user.home") + "/Library/Fonts/"));
         default -> var10000 = List.of(new File(System.getProperty("user.home") + "/.local/share/fonts"), new File(System.getProperty("user.home") + "/.fonts"));
      }

      return var10000;
   }

   public static List<File> getSFontDirs() {
      List var10000;
      switch (class_156.method_668()) {
         case field_1133 -> var10000 = List.of(new File(System.getenv("SystemRoot") + "\\Fonts"));
         case field_1137 -> var10000 = List.of(new File("/System/Library/Fonts/"));
         default -> var10000 = List.of(new File("/usr/share/fonts/"));
      }

      return var10000;
   }

   public static void loadBuiltin(List<FontFamily> fontList, String builtin) {
      FontInfo fontInfo = getBuiltinFontInfo(builtin);
      if (fontInfo != null) {
         FontFace fontFace = new BuiltinFontFace(fontInfo, builtin);
         if (!addFont(fontList, fontFace)) {
            MeteorClient.LOG.warn("Failed to load builtin font {}", fontFace);
         }

      }
   }

   public static void loadSystem(List<FontFamily> fontList, File dir) {
      if (dir.exists() && dir.isDirectory()) {
         File[] files = dir.listFiles((filex) -> filex.isFile() && filex.getName().endsWith(".ttf") || filex.isDirectory());
         if (files != null) {
            for(File file : files) {
               if (file.isDirectory()) {
                  loadSystem(fontList, file);
               } else {
                  FontInfo fontInfo = getSysFontInfo(file);
                  if (fontInfo != null) {
                     boolean isBuiltin = false;

                     for(String builtinFont : Fonts.BUILTIN_FONTS) {
                        if (builtinFont.equals(fontInfo.family())) {
                           isBuiltin = true;
                           break;
                        }
                     }

                     if (!isBuiltin) {
                        FontFace fontFace = new SystemFontFace(fontInfo, file.toPath());
                        if (!addFont(fontList, fontFace)) {
                           MeteorClient.LOG.warn("Failed to load system font {}", fontFace);
                        }
                     }
                  }
               }
            }

         }
      }
   }

   private static boolean addFont(List<FontFamily> fontList, @Nullable FontFace font) {
      if (font == null) {
         return false;
      } else {
         FontInfo info = font.info;
         FontFamily family = Fonts.getFamily(info.family());
         if (family == null) {
            family = new FontFamily(info.family());
            fontList.add(family);
         }

         return family.hasType(info.type()) ? false : family.addFont(font);
      }
   }

   public static @Nullable InputStream builtinFontStream(String name) {
      return FontUtils.class.getResourceAsStream("/assets/meteor-client/fonts/" + name + ".ttf");
   }
}
