package meteordevelopment.meteorclient.events.game;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_2561;

public class ItemStackTooltipEvent {
   private final class_1799 itemStack;
   private List<class_2561> list;

   public ItemStackTooltipEvent(class_1799 itemStack, List<class_2561> list) {
      this.itemStack = itemStack;
      this.list = list;
   }

   public List<class_2561> list() {
      return this.list;
   }

   public class_1799 itemStack() {
      return this.itemStack;
   }

   public void appendStart(class_2561 text) {
      this.copyIfImmutable();
      int index = this.list.isEmpty() ? 0 : 1;
      this.list.add(index, text);
   }

   public void appendEnd(class_2561 text) {
      this.copyIfImmutable();
      this.list.add(text);
   }

   public void append(int index, class_2561 text) {
      this.copyIfImmutable();
      this.list.add(index, text);
   }

   public void set(int index, class_2561 text) {
      this.copyIfImmutable();
      this.list.set(index, text);
   }

   private void copyIfImmutable() {
      if (List.of().getClass().getSuperclass().isInstance(this.list)) {
         this.list = new ObjectArrayList(this.list);
      }

   }
}
