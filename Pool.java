package meteordevelopment.meteorclient.utils.misc;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Supplier;

public final class Pool<T> {
   private final Queue<T> items = new ArrayDeque();
   private final Supplier<T> producer;

   public Pool(Supplier<T> producer) {
      this.producer = producer;
   }

   public synchronized T get() {
      return (T)(!this.items.isEmpty() ? this.items.poll() : this.producer.get());
   }

   public synchronized void free(T obj) {
      this.items.offer(obj);
   }

   public synchronized void freeAll(Iterable<T> objects) {
      if (objects instanceof Collection<T> collection) {
         this.items.addAll(collection);
      } else {
         Queue var10001 = this.items;
         Objects.requireNonNull(var10001);
         objects.forEach(var10001::add);
      }

   }
}
