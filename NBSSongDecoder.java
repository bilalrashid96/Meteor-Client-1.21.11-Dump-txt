package meteordevelopment.meteorclient.utils.notebot.decoder;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import meteordevelopment.meteorclient.utils.notebot.song.Note;
import meteordevelopment.meteorclient.utils.notebot.song.Song;
import net.minecraft.class_2766;
import org.jetbrains.annotations.NotNull;

public class NBSSongDecoder extends SongDecoder {
   public static final int NOTE_OFFSET = 33;

   public @NotNull Song parse(File songFile) throws Exception {
      return this.parse((InputStream)(new FileInputStream(songFile)));
   }

   private @NotNull Song parse(InputStream inputStream) throws Exception {
      Multimap<Integer, Note> notesMap = MultimapBuilder.linkedHashKeys().arrayListValues().build();
      DataInputStream dataInputStream = new DataInputStream(inputStream);
      short length = readShort(dataInputStream);
      int nbsversion = 0;
      if (length == 0) {
         nbsversion = dataInputStream.readByte();
         dataInputStream.readByte();
         if (nbsversion >= 3) {
            length = readShort(dataInputStream);
         }
      }

      readShort(dataInputStream);
      String title = readString(dataInputStream);
      String author = readString(dataInputStream);
      readString(dataInputStream);
      readString(dataInputStream);
      float speed = (float)readShort(dataInputStream) / 100.0F;
      dataInputStream.readBoolean();
      dataInputStream.readByte();
      dataInputStream.readByte();
      readInt(dataInputStream);
      readInt(dataInputStream);
      readInt(dataInputStream);
      readInt(dataInputStream);
      readInt(dataInputStream);
      readString(dataInputStream);
      if (nbsversion >= 4) {
         dataInputStream.readByte();
         dataInputStream.readByte();
         readShort(dataInputStream);
      }

      double tick = (double)-1.0F;

      while(true) {
         short jumpTicks = readShort(dataInputStream);
         if (jumpTicks == 0) {
            return new Song(notesMap, title, author);
         }

         tick += (double)((float)jumpTicks * (20.0F / speed));

         while(true) {
            short jumpLayers = readShort(dataInputStream);
            if (jumpLayers == 0) {
               break;
            }

            byte instrument = dataInputStream.readByte();
            byte key = dataInputStream.readByte();
            if (nbsversion >= 4) {
               dataInputStream.readUnsignedByte();
               dataInputStream.readUnsignedByte();
               readShort(dataInputStream);
            }

            class_2766 inst = fromNBSInstrument(instrument);
            if (inst != null) {
               Note note = new Note(inst, key - 33);
               setNote((int)Math.round(tick), note, notesMap);
            }
         }
      }
   }

   private static void setNote(int ticks, Note note, Multimap<Integer, Note> notesMap) {
      notesMap.put(ticks, note);
   }

   private static short readShort(DataInputStream dataInputStream) throws IOException {
      int byte1 = dataInputStream.readUnsignedByte();
      int byte2 = dataInputStream.readUnsignedByte();
      return (short)(byte1 + (byte2 << 8));
   }

   private static int readInt(DataInputStream dataInputStream) throws IOException {
      int byte1 = dataInputStream.readUnsignedByte();
      int byte2 = dataInputStream.readUnsignedByte();
      int byte3 = dataInputStream.readUnsignedByte();
      int byte4 = dataInputStream.readUnsignedByte();
      return byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24);
   }

   private static String readString(DataInputStream dataInputStream) throws IOException {
      int length = readInt(dataInputStream);
      if (length < 0) {
         throw new EOFException("Length can't be negative! Length: " + length);
      } else if (length > dataInputStream.available()) {
         throw new EOFException("Can't read string that is larger than a buffer! Length: " + length + " Readable Bytes Length: " + dataInputStream.available());
      } else {
         StringBuilder builder;
         for(builder = new StringBuilder(length); length > 0; --length) {
            char c = (char)dataInputStream.readByte();
            if (c == '\r') {
               c = ' ';
            }

            builder.append(c);
         }

         return builder.toString();
      }
   }

   private static class_2766 fromNBSInstrument(int instrument) {
      class_2766 var10000;
      switch (instrument) {
         case 0 -> var10000 = class_2766.field_12648;
         case 1 -> var10000 = class_2766.field_12651;
         case 2 -> var10000 = class_2766.field_12653;
         case 3 -> var10000 = class_2766.field_12643;
         case 4 -> var10000 = class_2766.field_12645;
         case 5 -> var10000 = class_2766.field_12654;
         case 6 -> var10000 = class_2766.field_12650;
         case 7 -> var10000 = class_2766.field_12644;
         case 8 -> var10000 = class_2766.field_12647;
         case 9 -> var10000 = class_2766.field_12655;
         case 10 -> var10000 = class_2766.field_18284;
         case 11 -> var10000 = class_2766.field_18285;
         case 12 -> var10000 = class_2766.field_18286;
         case 13 -> var10000 = class_2766.field_18287;
         case 14 -> var10000 = class_2766.field_18288;
         case 15 -> var10000 = class_2766.field_18289;
         default -> var10000 = null;
      }

      return var10000;
   }
}
