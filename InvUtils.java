package meteordevelopment.meteorclient.utils.player;

import java.util.function.Predicate;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.IClientPlayerInteractionManager;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2680;
import org.jetbrains.annotations.Range;

public class InvUtils {
   private static final Action ACTION = new Action();
   public static int previousSlot = -1;

   private InvUtils() {
   }

   public static boolean testInMainHand(Predicate<class_1799> predicate) {
      return predicate.test(MeteorClient.mc.field_1724.method_6047());
   }

   public static boolean testInMainHand(class_1792... items) {
      return testInMainHand((Predicate)((itemStack) -> {
         for(class_1792 item : items) {
            if (itemStack.method_31574(item)) {
               return true;
            }
         }

         return false;
      }));
   }

   public static boolean testInOffHand(Predicate<class_1799> predicate) {
      return predicate.test(MeteorClient.mc.field_1724.method_6079());
   }

   public static boolean testInOffHand(class_1792... items) {
      return testInOffHand((Predicate)((itemStack) -> {
         for(class_1792 item : items) {
            if (itemStack.method_31574(item)) {
               return true;
            }
         }

         return false;
      }));
   }

   public static boolean testInHands(Predicate<class_1799> predicate) {
      return testInMainHand(predicate) || testInOffHand(predicate);
   }

   public static boolean testInHands(class_1792... items) {
      return testInMainHand(items) || testInOffHand(items);
   }

   public static boolean testInHotbar(Predicate<class_1799> predicate) {
      if (testInHands(predicate)) {
         return true;
      } else {
         for(int i = 0; i <= 8; ++i) {
            class_1799 stack = MeteorClient.mc.field_1724.method_31548().method_5438(i);
            if (predicate.test(stack)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean testInHotbar(class_1792... items) {
      return testInHotbar((Predicate)((itemStack) -> {
         for(class_1792 item : items) {
            if (itemStack.method_31574(item)) {
               return true;
            }
         }

         return false;
      }));
   }

   public static FindItemResult findEmpty() {
      return find(class_1799::method_7960);
   }

   public static FindItemResult findInHotbar(class_1792... items) {
      return findInHotbar((Predicate)((itemStack) -> {
         for(class_1792 item : items) {
            if (itemStack.method_7909() == item) {
               return true;
            }
         }

         return false;
      }));
   }

   public static FindItemResult findInHotbar(Predicate<class_1799> isGood) {
      if (testInOffHand(isGood)) {
         return new FindItemResult(40, MeteorClient.mc.field_1724.method_6079().method_7947());
      } else {
         return testInMainHand(isGood) ? new FindItemResult(MeteorClient.mc.field_1724.method_31548().method_67532(), MeteorClient.mc.field_1724.method_6047().method_7947()) : find(isGood, 0, 8);
      }
   }

   public static FindItemResult find(class_1792... items) {
      return find((Predicate)((itemStack) -> {
         for(class_1792 item : items) {
            if (itemStack.method_7909() == item) {
               return true;
            }
         }

         return false;
      }));
   }

   public static FindItemResult find(Predicate<class_1799> isGood) {
      return MeteorClient.mc.field_1724 == null ? new FindItemResult(0, 0) : find(isGood, 0, MeteorClient.mc.field_1724.method_31548().method_5439());
   }

   public static FindItemResult find(Predicate<class_1799> isGood, int start, int end) {
      if (MeteorClient.mc.field_1724 == null) {
         return new FindItemResult(0, 0);
      } else {
         int slot = -1;
         int count = 0;

         for(int i = start; i <= end; ++i) {
            class_1799 stack = MeteorClient.mc.field_1724.method_31548().method_5438(i);
            if (isGood.test(stack)) {
               if (slot == -1) {
                  slot = i;
               }

               count += stack.method_7947();
            }
         }

         return new FindItemResult(slot, count);
      }
   }

   public static FindItemResult findFastestTool(class_2680 state) {
      float bestScore = 1.0F;
      int slot = -1;

      for(int i = 0; i < 9; ++i) {
         class_1799 stack = MeteorClient.mc.field_1724.method_31548().method_5438(i);
         if (stack.method_7951(state)) {
            float score = stack.method_7924(state);
            if (score > bestScore) {
               bestScore = score;
               slot = i;
            }
         }
      }

      return new FindItemResult(slot, 1);
   }

   public static boolean swap(int slot, boolean swapBack) {
      if (slot == 40) {
         return true;
      } else if (slot >= 0 && slot <= 8) {
         if (swapBack && previousSlot == -1) {
            previousSlot = MeteorClient.mc.field_1724.method_31548().method_67532();
         } else if (!swapBack) {
            previousSlot = -1;
         }

         MeteorClient.mc.field_1724.method_31548().method_61496(slot);
         ((IClientPlayerInteractionManager)MeteorClient.mc.field_1761).meteor$syncSelected();
         return true;
      } else {
         return false;
      }
   }

   public static boolean swapBack() {
      if (previousSlot == -1) {
         return false;
      } else {
         boolean return_ = swap(previousSlot, false);
         previousSlot = -1;
         return return_;
      }
   }

   public static Action move() {
      ACTION.type = class_1713.field_7790;
      ACTION.two = true;
      return ACTION;
   }

   public static Action click() {
      ACTION.type = class_1713.field_7790;
      return ACTION;
   }

   public static Action quickSwap() {
      ACTION.type = class_1713.field_7791;
      return ACTION;
   }

   public static Action shiftClick() {
      ACTION.type = class_1713.field_7794;
      return ACTION;
   }

   public static Action drop() {
      ACTION.type = class_1713.field_7795;
      ACTION.data = 1;
      return ACTION;
   }

   public static Action dropOne() {
      ACTION.type = class_1713.field_7795;
      ACTION.data = 0;
      return ACTION;
   }

   public static void dropHand() {
      if (!MeteorClient.mc.field_1724.field_7512.method_34255().method_7960()) {
         MeteorClient.mc.field_1761.method_2906(MeteorClient.mc.field_1724.field_7512.field_7763, -999, 0, class_1713.field_7790, MeteorClient.mc.field_1724);
      }

   }

   public static class Action {
      private class_1713 type = null;
      private boolean two = false;
      private int from = -1;
      private int to = -1;
      private int data = 0;
      private boolean isRecursive = false;

      private Action() {
      }

      public Action fromId(int id) {
         this.from = id;
         return this;
      }

      public Action from(int index) {
         return this.fromId(SlotUtils.indexToId(index));
      }

      public Action fromHotbar(@Range(
   from = 0L,
   to = 8L
) int i) {
         return this.from(0 + i);
      }

      public Action fromOffhand() {
         return this.from(40);
      }

      public Action fromMain(@Range(
   from = 0L,
   to = 26L
) int i) {
         return this.from(9 + i);
      }

      public Action fromArmor(int i) {
         return this.from(36 + (3 - i));
      }

      public void toId(int id) {
         this.to = id;
         this.run();
      }

      public void to(int index) {
         this.toId(SlotUtils.indexToId(index));
      }

      public void toHotbar(@Range(
   from = 0L,
   to = 8L
) int i) {
         this.to(0 + i);
      }

      public void toOffhand() {
         this.to(40);
      }

      public void toMain(@Range(
   from = 0L,
   to = 26L
) int i) {
         this.to(9 + i);
      }

      public void toArmor(int i) {
         this.to(36 + (3 - i));
      }

      public void slotId(int id) {
         this.from = this.to = id;
         this.run();
      }

      public void slot(int index) {
         this.slotId(SlotUtils.indexToId(index));
      }

      public void slotHotbar(@Range(
   from = 0L,
   to = 8L
) int i) {
         this.slot(0 + i);
      }

      public void slotOffhand() {
         this.slot(40);
      }

      public void slotMain(@Range(
   from = 0L,
   to = 26L
) int i) {
         this.slot(9 + i);
      }

      public void slotArmor(int i) {
         this.slot(36 + (3 - i));
      }

      private void run() {
         boolean hadEmptyCursor = MeteorClient.mc.field_1724.field_7512.method_34255().method_7960();
         if (this.type == class_1713.field_7791) {
            this.data = this.from;
            this.from = this.to;
         }

         if (this.type != null && this.from != -1 && this.to != -1) {
            this.click(this.from);
            if (this.two) {
               this.click(this.to);
            }
         }

         class_1713 preType = this.type;
         boolean preTwo = this.two;
         int preFrom = this.from;
         int preTo = this.to;
         this.type = null;
         this.two = false;
         this.from = -1;
         this.to = -1;
         this.data = 0;
         if (!this.isRecursive && hadEmptyCursor && preType == class_1713.field_7790 && preTwo && preFrom != -1 && preTo != -1 && !MeteorClient.mc.field_1724.field_7512.method_34255().method_7960()) {
            this.isRecursive = true;
            InvUtils.click().slotId(preFrom);
            this.isRecursive = false;
         }

      }

      private void click(int id) {
         MeteorClient.mc.field_1761.method_2906(MeteorClient.mc.field_1724.field_7512.field_7763, id, this.data, this.type, MeteorClient.mc.field_1724);
      }
   }
}
