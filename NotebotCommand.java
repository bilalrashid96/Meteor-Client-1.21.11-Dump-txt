package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.NotebotSongArgumentType;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.Notebot;
import meteordevelopment.meteorclient.utils.notebot.song.Note;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_156;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2766;
import net.minecraft.class_2767;
import net.minecraft.class_3414;

public class NotebotCommand extends Command {
   private static final SimpleCommandExceptionType INVALID_SONG = new SimpleCommandExceptionType(class_2561.method_43470("Invalid song."));
   private static final DynamicCommandExceptionType INVALID_PATH = new DynamicCommandExceptionType((object) -> class_2561.method_43470("'%s' is not a valid path.".formatted(object)));
   int ticks = -1;
   private final Map<Integer, List<Note>> song = new HashMap();

   public NotebotCommand() {
      super("notebot", "Allows you load notebot files");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("help").executes((ctx) -> {
         class_156.method_668().method_670("https://github.com/MeteorDevelopment/meteor-client/wiki/Notebot-Guide");
         return 1;
      }));
      builder.then(literal("status").executes((ctx) -> {
         Notebot notebot = (Notebot)Modules.get().get(Notebot.class);
         this.info(notebot.getStatus(), new Object[0]);
         return 1;
      }));
      builder.then(literal("pause").executes((ctx) -> {
         Notebot notebot = (Notebot)Modules.get().get(Notebot.class);
         notebot.pause();
         return 1;
      }));
      builder.then(literal("resume").executes((ctx) -> {
         Notebot notebot = (Notebot)Modules.get().get(Notebot.class);
         notebot.pause();
         return 1;
      }));
      builder.then(literal("stop").executes((ctx) -> {
         Notebot notebot = (Notebot)Modules.get().get(Notebot.class);
         notebot.stop();
         return 1;
      }));
      builder.then(literal("randomsong").executes((ctx) -> {
         Notebot notebot = (Notebot)Modules.get().get(Notebot.class);
         notebot.playRandomSong();
         return 1;
      }));
      builder.then(literal("play").then(argument("song", NotebotSongArgumentType.create()).executes((ctx) -> {
         Notebot notebot = (Notebot)Modules.get().get(Notebot.class);
         Path songPath = (Path)ctx.getArgument("song", Path.class);
         if (songPath != null && songPath.toFile().exists()) {
            notebot.loadSong(songPath.toFile());
            return 1;
         } else {
            throw INVALID_SONG.create();
         }
      })));
      builder.then(literal("preview").then(argument("song", NotebotSongArgumentType.create()).executes((ctx) -> {
         Notebot notebot = (Notebot)Modules.get().get(Notebot.class);
         Path songPath = (Path)ctx.getArgument("song", Path.class);
         if (songPath != null && songPath.toFile().exists()) {
            notebot.previewSong(songPath.toFile());
            return 1;
         } else {
            throw INVALID_SONG.create();
         }
      })));
      builder.then(literal("record").then(literal("start").executes((ctx) -> {
         this.ticks = -1;
         this.song.clear();
         MeteorClient.EVENT_BUS.subscribe(this);
         this.info("Recording started", new Object[0]);
         return 1;
      })));
      builder.then(literal("record").then(literal("cancel").executes((ctx) -> {
         MeteorClient.EVENT_BUS.unsubscribe(this);
         this.info("Recording cancelled", new Object[0]);
         return 1;
      })));
      builder.then(literal("record").then(literal("save").then(argument("name", StringArgumentType.greedyString()).executes((ctx) -> {
         String name = (String)ctx.getArgument("name", String.class);
         if (name != null && !name.isEmpty()) {
            Path notebotFolder = MeteorClient.FOLDER.toPath().resolve("notebot");
            Path path = notebotFolder.resolve(String.format("%s.txt", name)).normalize();
            if (!path.startsWith(notebotFolder)) {
               throw INVALID_PATH.create(path);
            } else {
               this.saveRecording(path);
               return 1;
            }
         } else {
            throw INVALID_PATH.create(name);
         }
      }))));
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.ticks != -1) {
         ++this.ticks;
      }
   }

   @EventHandler
   private void onReadPacket(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2767 sound) {
         if (((class_3414)sound.method_11894().comp_349()).comp_3319().method_12832().contains("note_block")) {
            if (this.ticks == -1) {
               this.ticks = 0;
            }

            List<Note> notes = (List)this.song.computeIfAbsent(this.ticks, (tick) -> new ArrayList());
            Note note = this.getNote(sound);
            if (note != null) {
               notes.add(note);
            }
         }
      }

   }

   private void saveRecording(Path path) {
      if (this.song.isEmpty()) {
         MeteorClient.EVENT_BUS.unsubscribe(this);
      } else {
         try {
            MeteorClient.EVENT_BUS.unsubscribe(this);
            FileWriter file = new FileWriter(path.toFile());

            for(Map.Entry<Integer, List<Note>> entry : this.song.entrySet()) {
               int tick = (Integer)entry.getKey();

               for(Note note : (List)entry.getValue()) {
                  class_2766 instrument = note.getInstrument();
                  int noteLevel = note.getNoteLevel();
                  file.write(String.format("%d:%d:%d\n", tick, noteLevel, instrument.ordinal()));
               }
            }

            file.close();
            this.info("Song saved.", new Object[0]);
         } catch (IOException var11) {
            this.info("Couldn't create the file.", new Object[0]);
            MeteorClient.EVENT_BUS.unsubscribe(this);
         }

      }
   }

   private Note getNote(class_2767 soundPacket) {
      float pitch = soundPacket.method_11892();
      int noteLevel = -1;

      for(int n = 0; n < 25; ++n) {
         if ((double)((float)Math.pow((double)2.0F, (double)(n - 12) / (double)12.0F)) - 0.01 < (double)pitch && (double)((float)Math.pow((double)2.0F, (double)(n - 12) / (double)12.0F)) + 0.01 > (double)pitch) {
            noteLevel = n;
            break;
         }
      }

      if (noteLevel == -1) {
         this.error("Error while bruteforcing a note level! Sound: " + String.valueOf(soundPacket.method_11894().comp_349()) + " Pitch: " + pitch, new Object[0]);
         return null;
      } else {
         class_2766 instrument = this.getInstrumentFromSound((class_3414)soundPacket.method_11894().comp_349());
         if (instrument == null) {
            this.error("Can't find the instrument from sound! Sound: " + String.valueOf(soundPacket.method_11894().comp_349()), new Object[0]);
            return null;
         } else {
            return new Note(instrument, noteLevel);
         }
      }
   }

   private class_2766 getInstrumentFromSound(class_3414 sound) {
      String path = sound.comp_3319().method_12832();
      if (path.contains("harp")) {
         return class_2766.field_12648;
      } else if (path.contains("basedrum")) {
         return class_2766.field_12653;
      } else if (path.contains("snare")) {
         return class_2766.field_12643;
      } else if (path.contains("hat")) {
         return class_2766.field_12645;
      } else if (path.contains("bass")) {
         return class_2766.field_12651;
      } else if (path.contains("flute")) {
         return class_2766.field_12650;
      } else if (path.contains("bell")) {
         return class_2766.field_12644;
      } else if (path.contains("guitar")) {
         return class_2766.field_12654;
      } else if (path.contains("chime")) {
         return class_2766.field_12647;
      } else if (path.contains("xylophone")) {
         return class_2766.field_12655;
      } else if (path.contains("iron_xylophone")) {
         return class_2766.field_18284;
      } else if (path.contains("cow_bell")) {
         return class_2766.field_18285;
      } else if (path.contains("didgeridoo")) {
         return class_2766.field_18286;
      } else if (path.contains("bit")) {
         return class_2766.field_18287;
      } else if (path.contains("banjo")) {
         return class_2766.field_18288;
      } else {
         return path.contains("pling") ? class_2766.field_18289 : null;
      }
   }
}
