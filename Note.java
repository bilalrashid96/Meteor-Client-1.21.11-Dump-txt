package meteordevelopment.meteorclient.utils.notebot.song;

import java.util.Objects;
import net.minecraft.class_2766;

public class Note {
   private class_2766 instrument;
   private int noteLevel;

   public Note(class_2766 instrument, int noteLevel) {
      this.instrument = instrument;
      this.noteLevel = noteLevel;
   }

   public class_2766 getInstrument() {
      return this.instrument;
   }

   public void setInstrument(class_2766 instrument) {
      this.instrument = instrument;
   }

   public int getNoteLevel() {
      return this.noteLevel;
   }

   public void setNoteLevel(int noteLevel) {
      this.noteLevel = noteLevel;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Note note = (Note)o;
         return this.instrument == note.instrument && this.noteLevel == note.noteLevel;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.instrument, this.noteLevel});
   }

   public String toString() {
      String var10000 = String.valueOf(this.getInstrument());
      return "Note{instrument=" + var10000 + ", noteLevel=" + this.getNoteLevel() + "}";
   }
}
