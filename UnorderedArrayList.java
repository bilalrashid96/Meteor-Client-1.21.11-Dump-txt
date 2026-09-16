package meteordevelopment.meteorclient.utils.misc;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class UnorderedArrayList<T> extends AbstractList<T> {
   private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = new Object[0];
   private static final int DEFAULT_CAPACITY = 10;
   private static final int MAX_ARRAY_SIZE = 2147483639;
   private transient T[] items;
   private int size;

   public UnorderedArrayList() {
      this.items = (T[])DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
   }

   public boolean add(T t) {
      if (this.size == this.items.length) {
         this.grow(this.size + 1);
      }

      this.items[this.size++] = t;
      ++this.modCount;
      return true;
   }

   public T set(int index, T element) {
      T old = (T)this.items[index];
      this.items[index] = element;
      return old;
   }

   public T get(int index) {
      return (T)this.items[index];
   }

   public void clear() {
      ++this.modCount;

      for(int i = 0; i < this.size; ++i) {
         this.items[i] = null;
      }

      this.size = 0;
   }

   public int indexOf(Object o) {
      for(int i = 0; i < this.size; ++i) {
         if (Objects.equals(this.items[i], o)) {
            return i;
         }
      }

      return -1;
   }

   public int lastIndexOf(Object o) {
      T[] elements = this.items;

      for(int i = this.size - 1; i >= 0; --i) {
         if (Objects.equals(elements[i], o)) {
            return i;
         }
      }

      return -1;
   }

   public boolean remove(Object o) {
      int i = this.indexOf(o);
      if (i == -1) {
         return false;
      } else {
         this.items[i] = null;
         this.items[i] = this.items[--this.size];
         ++this.modCount;
         return true;
      }
   }

   public T remove(int index) {
      T old = (T)this.items[index];
      this.items[index] = null;
      this.items[index] = this.items[--this.size];
      ++this.modCount;
      return old;
   }

   public boolean removeIf(Predicate<? super T> filter) {
      int preSize = this.size;
      int j = 0;

      for(int i = 0; i < this.size; ++i) {
         T item = (T)this.items[i];
         if (!filter.test(item)) {
            if (j < i) {
               this.items[j] = item;
            }

            ++j;
         }
      }

      this.size = j;
      return this.size != preSize;
   }

   public int size() {
      return this.size;
   }

   public void ensureCapacity(int minCapacity) {
      if (minCapacity > this.items.length && (this.items != DEFAULTCAPACITY_EMPTY_ELEMENTDATA || minCapacity > 10)) {
         ++this.modCount;
         this.grow(minCapacity);
      }

   }

   private void grow(int minCapacity) {
      this.items = (T[])Arrays.copyOf(this.items, this.newCapacity(minCapacity));
   }

   private int newCapacity(int minCapacity) {
      int oldCapacity = this.items.length;
      int newCapacity = oldCapacity + (oldCapacity >> 1);
      if (newCapacity - minCapacity <= 0) {
         if (this.items == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            return Math.max(10, minCapacity);
         } else if (minCapacity < 0) {
            throw new OutOfMemoryError();
         } else {
            return minCapacity;
         }
      } else {
         return newCapacity - 2147483639 <= 0 ? newCapacity : hugeCapacity(minCapacity);
      }
   }

   private static int hugeCapacity(int minCapacity) {
      if (minCapacity < 0) {
         throw new OutOfMemoryError();
      } else {
         return minCapacity > 2147483639 ? Integer.MAX_VALUE : 2147483639;
      }
   }
}
