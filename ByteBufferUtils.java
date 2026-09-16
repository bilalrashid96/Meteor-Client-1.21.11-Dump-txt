package meteordevelopment.meteorclient.utils.files;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.IntFunction;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ByteBufferUtils {
   private ByteBufferUtils() {
   }

   public static ByteBuffer readFully(Path path, IntFunction<ByteBuffer> allocator) throws IOException {
      FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);

      ByteBuffer var9;
      try {
         long size = Files.size(path);
         if (size > 2147483647L) {
            throw new IOException("File too large to read into ByteBuffer: " + String.valueOf(path));
         }

         ByteBuffer buffer = (ByteBuffer)allocator.apply((int)size);

         while(buffer.hasRemaining()) {
            int bytesRead = channel.read(buffer);
            if (bytesRead == -1) {
               break;
            }
         }

         buffer.flip();
         var9 = buffer;
      } catch (Throwable var8) {
         if (channel != null) {
            try {
               channel.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (channel != null) {
         channel.close();
      }

      return var9;
   }

   public static ByteBuffer readFully(ReadableByteChannel channel, IntFunction<ByteBuffer> allocator) throws IOException {
      ByteBuffer buffer = requireCapacity((ByteBuffer)allocator.apply(8192), 8192);

      while(true) {
         int bytesRead = channel.read(buffer);
         if (bytesRead == -1) {
            break;
         }

         if (bytesRead == 0) {
            if (buffer.hasRemaining()) {
               break;
            }

            buffer = grow(buffer, allocator);
         } else if (!buffer.hasRemaining()) {
            buffer = grow(buffer, allocator);
         }
      }

      buffer.flip();
      return buffer;
   }

   private static ByteBuffer grow(ByteBuffer buffer, IntFunction<ByteBuffer> allocator) {
      int oldCap = buffer.capacity();
      int newCap = oldCap << 1;
      if (newCap <= 0) {
         throw new OutOfMemoryError("Buffer too large (overflow): " + oldCap);
      } else {
         ByteBuffer newBuffer = requireCapacity((ByteBuffer)allocator.apply(newCap), newCap);
         buffer.flip();
         newBuffer.put(buffer);
         return newBuffer;
      }
   }

   private static ByteBuffer requireCapacity(ByteBuffer buf, int minCap) {
      if (buf.capacity() < minCap) {
         int var10002 = buf.capacity();
         throw new IllegalArgumentException("Allocator returned capacity " + var10002 + " < " + minCap);
      } else {
         return buf;
      }
   }
}
