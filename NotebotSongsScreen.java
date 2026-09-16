package meteordevelopment.meteorclient.gui.screens;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.Notebot;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.notebot.decoder.SongDecoders;
import org.apache.commons.io.FilenameUtils;

public class NotebotSongsScreen extends WindowScreen {
   private static final Notebot notebot = (Notebot)Modules.get().get(Notebot.class);
   private WTextBox filter;
   private String filterText = "";
   private WTable table;

   public NotebotSongsScreen(GuiTheme theme) {
      super(theme, "Notebot Songs");
   }

   public void initWidgets() {
      WButton randomSong = (WButton)this.add(this.theme.button("Random Song")).minWidth((double)400.0F).expandX().widget();
      Notebot var10001 = notebot;
      Objects.requireNonNull(var10001);
      randomSong.action = var10001::playRandomSong;
      this.filter = (WTextBox)this.add(this.theme.textBox("", "Search for the songs...")).minWidth((double)400.0F).expandX().widget();
      this.filter.setFocused(true);
      this.filter.action = () -> {
         this.filterText = this.filter.get().trim();
         this.table.clear();
         this.initSongsTable();
      };
      this.table = (WTable)this.add(this.theme.table()).widget();
      this.initSongsTable();
   }

   private void initSongsTable() {
      AtomicBoolean noSongsFound = new AtomicBoolean(true);

      try {
         Files.list(MeteorClient.FOLDER.toPath().resolve("notebot")).forEach((path) -> {
            if (SongDecoders.hasDecoder(path)) {
               String name = path.getFileName().toString();
               if (Utils.searchTextDefault(name, this.filterText, false)) {
                  this.addPath(path);
                  noSongsFound.set(false);
               }
            }

         });
      } catch (IOException var3) {
         this.table.add(this.theme.label("Missing meteor-client/notebot folder.")).expandCellX();
         this.table.row();
      }

      if (noSongsFound.get()) {
         this.table.add(this.theme.label("No songs found.")).expandCellX().center();
      }

   }

   private void addPath(Path path) {
      this.table.add(this.theme.horizontalSeparator()).expandX().minWidth((double)400.0F);
      this.table.row();
      this.table.add(this.theme.label(FilenameUtils.getBaseName(path.getFileName().toString()))).expandCellX();
      WButton load = (WButton)this.table.add(this.theme.button("Load")).right().widget();
      load.action = () -> notebot.loadSong(path.toFile());
      WButton preview = (WButton)this.table.add(this.theme.button("Preview")).right().widget();
      preview.action = () -> notebot.previewSong(path.toFile());
      this.table.row();
   }
}
