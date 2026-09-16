package meteordevelopment.meteorclient.renderer.text;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import meteordevelopment.meteorclient.utils.files.ByteBufferUtils;
import org.jspecify.annotations.NullMarked;
import org.lwjgl.BufferUtils;

@NullMarked
public abstract sealed class FontFace permits BuiltinFontFace, SystemFontFace {
   public final FontInfo info;

   protected FontFace(FontInfo info) {
      this.info = info;
   }

   public abstract ReadableByteChannel byteChannelForRead() throws IOException;

   public final ByteBuffer readToDirectByteBuffer() throws IOException {
      ReadableByteChannel channel = this.byteChannelForRead();

      ByteBuffer var2;
      try {
         var2 = ByteBufferUtils.readFully(channel, BufferUtils::createByteBuffer);
      } catch (Throwable var5) {
         if (channel != null) {
            try {
               channel.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (channel != null) {
         channel.close();
      }

      return var2;
   }

   public String toString() {
      return this.info.toString();
   }
}
