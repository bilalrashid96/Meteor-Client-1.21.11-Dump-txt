package meteordevelopment.meteorclient.utils.player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import meteordevelopment.meteorclient.mixininterface.ISlot;
import meteordevelopment.meteorclient.utils.render.PeekScreen;
import net.minecraft.class_1277;
import net.minecraft.class_1661;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_3545;
import net.minecraft.class_465;
import net.minecraft.class_476;
import net.minecraft.class_481;
import net.minecraft.class_495;
import net.minecraft.class_7923;

public class InventorySorter {
   private final class_465<?> screen;
   private final InvPart originInvPart;
   private boolean invalid;
   private List<Action> actions;
   private int timer;
   private int currentActionI;

   public InventorySorter(class_465<?> screen, class_1735 originSlot) {
      this.screen = screen;
      this.originInvPart = this.getInvPart(originSlot);
      if (this.originInvPart != InventorySorter.InvPart.Invalid && this.originInvPart != InventorySorter.InvPart.Hotbar && !(screen instanceof PeekScreen)) {
         this.actions = new ArrayList();
         this.generateActions();
      } else {
         this.invalid = true;
      }
   }

   public boolean tick(int delay) {
      if (this.invalid) {
         return true;
      } else if (this.currentActionI >= this.actions.size()) {
         return true;
      } else if (this.timer >= delay) {
         this.timer = 0;
         Action action = (Action)this.actions.get(this.currentActionI);
         InvUtils.move().fromId(action.from).toId(action.to);
         ++this.currentActionI;
         return false;
      } else {
         ++this.timer;
         return false;
      }
   }

   private void generateActions() {
      List<MySlot> slots = new ArrayList();

      for(class_1735 slot : this.screen.method_17577().field_7761) {
         if (this.getInvPart(slot) == this.originInvPart) {
            slots.add(new MySlot(((ISlot)slot).meteor$getId(), slot.method_7677()));
         }
      }

      slots.sort(Comparator.comparingInt((value) -> value.id));
      this.generateStackingActions(slots);
      this.generateSortingActions(slots);
   }

   private void generateStackingActions(List<MySlot> slots) {
      SlotMap slotMap = new SlotMap();

      for(MySlot slot : slots) {
         if (!slot.itemStack.method_7960() && slot.itemStack.method_7946() && slot.itemStack.method_7947() < slot.itemStack.method_7914()) {
            slotMap.get(slot.itemStack).add(slot);
         }
      }

      for(class_3545<class_1799, List<MySlot>> entry : slotMap.map) {
         List<MySlot> slotsToStack = (List)entry.method_15441();
         MySlot slotToStackTo = null;

         for(int i = 0; i < slotsToStack.size(); ++i) {
            MySlot slot = (MySlot)slotsToStack.get(i);
            if (slotToStackTo == null) {
               slotToStackTo = slot;
            } else {
               this.actions.add(new Action(slot.id, slotToStackTo.id));
               if (slotToStackTo.itemStack.method_7947() + slot.itemStack.method_7947() <= slotToStackTo.itemStack.method_7914()) {
                  slotToStackTo.itemStack = new class_1799(slotToStackTo.itemStack.method_7909(), slotToStackTo.itemStack.method_7947() + slot.itemStack.method_7947());
                  slot.itemStack = class_1799.field_8037;
                  if (slotToStackTo.itemStack.method_7947() >= slotToStackTo.itemStack.method_7914()) {
                     slotToStackTo = null;
                  }
               } else {
                  int needed = slotToStackTo.itemStack.method_7914() - slotToStackTo.itemStack.method_7947();
                  slotToStackTo.itemStack = new class_1799(slotToStackTo.itemStack.method_7909(), slotToStackTo.itemStack.method_7914());
                  slot.itemStack = new class_1799(slot.itemStack.method_7909(), slot.itemStack.method_7947() - needed);
                  slotToStackTo = null;
                  --i;
               }
            }
         }
      }

   }

   private void generateSortingActions(List<MySlot> slots) {
      for(int i = 0; i < slots.size(); ++i) {
         MySlot bestSlot = null;

         for(int j = i; j < slots.size(); ++j) {
            MySlot slot = (MySlot)slots.get(j);
            if (bestSlot == null) {
               bestSlot = slot;
            } else if (this.isSlotBetter(bestSlot, slot)) {
               bestSlot = slot;
            }
         }

         if (!bestSlot.itemStack.method_7960()) {
            MySlot toSlot = (MySlot)slots.get(i);
            int from = bestSlot.id;
            int to = toSlot.id;
            if (from != to) {
               class_1799 temp = bestSlot.itemStack;
               bestSlot.itemStack = toSlot.itemStack;
               toSlot.itemStack = temp;
               this.actions.add(new Action(from, to));
            }
         }
      }

   }

   private boolean isSlotBetter(MySlot best, MySlot slot) {
      class_1799 bestI = best.itemStack;
      class_1799 slotI = slot.itemStack;
      if (bestI.method_7960() && !slotI.method_7960()) {
         return true;
      } else if (!bestI.method_7960() && slotI.method_7960()) {
         return false;
      } else {
         int c = class_7923.field_41178.method_10221(bestI.method_7909()).method_12833(class_7923.field_41178.method_10221(slotI.method_7909()));
         if (c == 0) {
            if (slotI.method_7947() != bestI.method_7947()) {
               return slotI.method_7947() > bestI.method_7947();
            }

            if (slotI.method_7919() != bestI.method_7919()) {
               return slotI.method_7919() > bestI.method_7919();
            }
         }

         return c > 0;
      }
   }

   private InvPart getInvPart(class_1735 slot) {
      int i = ((ISlot)slot).meteor$getIndex();
      if (!(slot.field_7871 instanceof class_1661) || this.screen instanceof class_481 && ((ISlot)slot).meteor$getId() <= 8) {
         if ((this.screen instanceof class_476 || this.screen instanceof class_495) && slot.field_7871 instanceof class_1277) {
            return InventorySorter.InvPart.Main;
         }
      } else {
         if (SlotUtils.isHotbar(i)) {
            return InventorySorter.InvPart.Hotbar;
         }

         if (SlotUtils.isMain(i)) {
            return InventorySorter.InvPart.Player;
         }
      }

      return InventorySorter.InvPart.Invalid;
   }

   private static enum InvPart {
      Hotbar,
      Player,
      Main,
      Invalid;

      // $FF: synthetic method
      private static InvPart[] $values() {
         return new InvPart[]{Hotbar, Player, Main, Invalid};
      }
   }

   private static class MySlot {
      public final int id;
      public class_1799 itemStack;

      public MySlot(int id, class_1799 itemStack) {
         this.id = id;
         this.itemStack = itemStack;
      }
   }

   private static class SlotMap {
      private final List<class_3545<class_1799, List<MySlot>>> map = new ArrayList();

      public List<MySlot> get(class_1799 itemStack) {
         for(class_3545<class_1799, List<MySlot>> entry : this.map) {
            if (class_1799.method_31577(itemStack, (class_1799)entry.method_15442())) {
               return (List)entry.method_15441();
            }
         }

         List<MySlot> list = new ArrayList();
         this.map.add(new class_3545(itemStack, list));
         return list;
      }
   }

   private static record Action(int from, int to) {
   }
}
