package meteordevelopment.meteorclient.utils.notebot.decoder;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.Notebot;
import meteordevelopment.meteorclient.utils.notebot.NotebotUtils;
import meteordevelopment.meteorclient.utils.notebot.song.Note;
import meteordevelopment.meteorclient.utils.notebot.song.Song;
import net.minecraft.class_2766;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

public class SongDecoders {
   private static final Map<String, SongDecoder> decoders = new HashMap();

   public static void registerDecoder(String extension, SongDecoder songDecoder) {
      decoders.put(extension, songDecoder);
   }

   public static SongDecoder getDecoder(File file) {
      return (SongDecoder)decoders.get(FilenameUtils.getExtension(file.getName()));
   }

   public static boolean hasDecoder(File file) {
      return decoders.containsKey(FilenameUtils.getExtension(file.getName()));
   }

   public static boolean hasDecoder(Path path) {
      return hasDecoder(path.toFile());
   }

   public static @NotNull Song parse(File file) throws Exception {
      if (!hasDecoder(file)) {
         throw new IllegalStateException("Decoder for this file does not exists!");
      } else {
         SongDecoder decoder = getDecoder(file);
         Song song = decoder.parse(file);
         fixSong(song);
         song.finishLoading();
         return song;
      }
   }

   private static void fixSong(Song song) {
      Notebot notebot = (Notebot)Modules.get().get(Notebot.class);
      Iterator<Map.Entry<Integer, Note>> iterator = song.getNotesMap().entries().iterator();

      while(iterator.hasNext()) {
         Map.Entry<Integer, Note> entry = (Map.Entry)iterator.next();
         int tick = (Integer)entry.getKey();
         Note note = (Note)entry.getValue();
         int n = note.getNoteLevel();
         if (n < 0 || n > 24) {
            if (!(Boolean)notebot.roundOutOfRange.get()) {
               notebot.warning("Note at tick %d out of range.", new Object[]{tick});
               iterator.remove();
               continue;
            }

            note.setNoteLevel(n < 0 ? 0 : 24);
         }

         if (notebot.mode.get() == NotebotUtils.NotebotMode.ExactInstruments) {
            class_2766 newInstrument = notebot.getMappedInstrument(note.getInstrument());
            if (newInstrument != null) {
               note.setInstrument(newInstrument);
            }
         } else {
            note.setInstrument((class_2766)null);
         }
      }

   }

   static {
      registerDecoder("nbs", new NBSSongDecoder());
      registerDecoder("txt", new TextSongDecoder());
   }
}
