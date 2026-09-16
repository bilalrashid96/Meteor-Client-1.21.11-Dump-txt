package meteordevelopment.meteorclient.utils.notebot.song;

import com.google.common.collect.Multimap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class Song {
   private final Multimap<Integer, Note> notesMap;
   private int lastTick;
   private final String title;
   private final String author;
   private final Set<Note> requirements = new HashSet();
   private boolean finishedLoading = false;

   public Song(Multimap<Integer, Note> notesMap, String title, String author) {
      this.notesMap = notesMap;
      this.title = title;
      this.author = author;
   }

   public void finishLoading() {
      if (this.finishedLoading) {
         throw new IllegalStateException("Song has already finished loading!");
      } else {
         this.lastTick = (Integer)Collections.max(this.notesMap.keySet());
         Stream var10000 = this.notesMap.values().stream().distinct();
         Set var10001 = this.requirements;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::add);
         this.finishedLoading = true;
      }
   }

   public Multimap<Integer, Note> getNotesMap() {
      return this.notesMap;
   }

   public Set<Note> getRequirements() {
      if (!this.finishedLoading) {
         throw new IllegalStateException("Song is still loading!");
      } else {
         return this.requirements;
      }
   }

   public int getLastTick() {
      if (!this.finishedLoading) {
         throw new IllegalStateException("Song is still loading!");
      } else {
         return this.lastTick;
      }
   }

   public String getTitle() {
      return this.title;
   }

   public String getAuthor() {
      return this.author;
   }
}
