package meteordevelopment.meteorclient.utils.misc;

import java.io.DataOutput;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class ByteCountDataOutput implements DataOutput {
   public static final ByteCountDataOutput INSTANCE = new ByteCountDataOutput();
   private int count;

   public int getCount() {
      return this.count;
   }

   public void reset() {
      this.count = 0;
   }

   public void write(int b) throws IOException {
      ++this.count;
   }

   public void write(byte[] b) throws IOException {
      this.count += b.length;
   }

   public void write(byte @NotNull [] b, int off, int len) throws IOException {
      this.count += len;
   }

   public void writeBoolean(boolean v) {
      ++this.count;
   }

   public void writeByte(int v) {
      ++this.count;
   }

   public void writeShort(int v) {
      this.count += 2;
   }

   public void writeChar(int v) {
      this.count += 2;
   }

   public void writeInt(int v) {
      this.count += 4;
   }

   public void writeLong(long v) {
      this.count += 8;
   }

   public void writeFloat(float v) {
      this.count += 4;
   }

   public void writeDouble(double v) {
      this.count += 8;
   }

   public void writeBytes(String s) {
      this.count += s.length();
   }

   public void writeChars(String s) {
      this.count += s.length() * 2;
   }

   public void writeUTF(@NotNull String s) {
      this.count = (int)((long)this.count + 2L + this.getUTFLength(s));
   }

   long getUTFLength(String s) {
      long utflen = 0L;

      for(int cpos = 0; cpos < s.length(); ++cpos) {
         char c = s.charAt(cpos);
         if (c >= 1 && c <= 127) {
            ++utflen;
         } else if (c > 2047) {
            utflen += 3L;
         } else {
            utflen += 2L;
         }
      }

      return utflen;
   }
}
