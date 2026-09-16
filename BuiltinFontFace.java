package meteordevelopment.meteorclient.renderer.text;

import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import meteordevelopment.meteorclient.utils.render.FontUtils;
import org.jspecify.annotations.NullMarked;

@NullMarked
public non-sealed class BuiltinFontFace extends FontFace {
   private final String name;

   public BuiltinFontFace(FontInfo info, String name) {
      super(info);
      this.name = name;
   }

   public ReadableByteChannel byteChannelForRead() {
      InputStream inputStream = FontUtils.builtinFontStream(this.name);
      if (inputStream == null) {
         throw new IllegalArgumentException("Builtin font '" + this.name + "' not found");
      } else {
         return Channels.newChannel(inputStream);
      }
   }

   public String toString() {
      return super.toString() + " (builtin)";
   }
}
