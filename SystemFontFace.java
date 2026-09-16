package meteordevelopment.meteorclient.renderer.text;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SystemFontFace extends FontFace {
   private final Path path;

   public SystemFontFace(FontInfo info, Path path) {
      super(info);
      this.path = path;
   }

   public ReadableByteChannel byteChannelForRead() throws IOException {
      return FileChannel.open(this.path, StandardOpenOption.READ);
   }

   public String toString() {
      String var10000 = super.toString();
      return var10000 + " (" + this.path.toString() + ")";
   }
}
