package meteordevelopment.meteorclient.gui.screens.settings.base;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_2359;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SortingHelper {
   private static final Comparator<Entry<?>> FILTER_COMPARATOR = Comparator.comparingInt(Entry::distance);

   private SortingHelper() {
   }

   public static <T> Iterable<T> sort(Iterable<T> registry, Predicate<T> filter, Function<T, String[]> nameFunction, String filterText) {
      return sortInternal(registry, filter, nameFunction, filterText, (Comparator)null);
   }

   public static <T> Iterable<T> sortWithPriority(Iterable<T> registry, Predicate<T> filter, Function<T, String[]> nameFunction, String filterText, Comparator<T> comparator) {
      return sortInternal(registry, filter, nameFunction, filterText, comparator);
   }

   private static <T> Iterable<T> sortInternal(Iterable<T> registry, Predicate<T> filter, Function<T, String[]> nameFunction, String filterText, @Nullable Comparator<T> comparator) {
      if (filterText.isBlank()) {
         if (comparator == null) {
            return filtering(registry, filter);
         } else {
            List<T> list = createList(registry);

            for(T value : registry) {
               if (filter.test(value)) {
                  list.add(value);
               }
            }

            list.sort(comparator);
            return list;
         }
      } else {
         List<Entry<T>> list = createList(registry);

         for(T value : registry) {
            if (filter.test(value)) {
               String[] names = (String[])nameFunction.apply(value);
               int bestWords = 0;
               int bestDistance = Integer.MAX_VALUE;
               float relevancy = 0.0F;

               for(String name : names) {
                  int words = Utils.searchInWords(name, filterText);
                  int distance = Utils.searchLevenshteinDefault(name, filterText, false);
                  bestWords = Math.max(bestWords, words);
                  bestDistance = Math.min(bestDistance, distance);
                  relevancy = Math.max(relevancy, 1.0F - (float)distance / (float)name.length());
               }

               if (bestWords > 0 || relevancy >= 0.5F) {
                  list.add(new Entry(value, bestDistance));
               }
            }
         }

         Comparator<Entry<T>> entryComparator = comparator != null ? Comparator.comparing(Entry::value, comparator).thenComparing(filterComparator()) : filterComparator();
         list.sort(entryComparator);
         return iterate(list);
      }
   }

   private static <T> List<T> createList(Iterable<?> iterable) {
      if (iterable instanceof class_2359<?> indexed) {
         return new ObjectArrayList(indexed.method_10204());
      } else if (iterable instanceof Collection<?> collection) {
         return new ObjectArrayList(collection.size());
      } else {
         return new ObjectArrayList();
      }
   }

   private static <T> Comparator<Entry<T>> filterComparator() {
      return FILTER_COMPARATOR;
   }

   private static <T> Iterable<T> iterate(final List<Entry<T>> sortedList) {
      return new Iterable<T>() {
         public @NotNull Iterator<T> iterator() {
            return new Iterator<T>() {
               private final Iterator<SortingHelper.Entry<T>> it;
               // $FF: synthetic field
               final <undefinedtype> this$0;

               {
                  this.this$0 = this$0;
                  this.it = this.this$0.val$sortedList.iterator();
               }

               public boolean hasNext() {
                  return this.it.hasNext();
               }

               public T next() {
                  return (T)((SortingHelper.Entry)this.it.next()).value();
               }
            };
         }
      };
   }

   private static <T> Iterable<T> filtering(final Iterable<T> iterable, final Predicate<T> filter) {
      return new Iterable<T>() {
         public @NotNull Iterator<T> iterator() {
            throw new UnsupportedOperationException("iterator() not supported by this Iterable, use forEach() instead.");
         }

         public void forEach(Consumer<? super T> action) {
            for(T value : iterable) {
               if (filter.test(value)) {
                  action.accept(value);
               }
            }

         }
      };
   }

   public static record Entry<T>(T value, int distance) {
   }
}
