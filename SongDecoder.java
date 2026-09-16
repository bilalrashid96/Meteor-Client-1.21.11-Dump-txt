package meteordevelopment.meteorclient.utils.notebot.decoder;

import java.io.File;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.Notebot;
import meteordevelopment.meteorclient.utils.notebot.song.Song;

public abstract class SongDecoder {
   protected Notebot notebot = (Notebot)Modules.get().get(Notebot.class);

   public abstract Song parse(File var1) throws Exception;
}
