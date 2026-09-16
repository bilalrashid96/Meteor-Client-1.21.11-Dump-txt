package meteordevelopment.meteorclient.gui.widgets;

import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.Keybind;

public class WKeybind extends WHorizontalList {
   public Runnable action;
   public Runnable actionOnSet;
   private WButton button;
   private final Keybind keybind;
   private final Keybind defaultValue;
   private boolean listening;

   public WKeybind(Keybind keybind, Keybind defaultValue) {
      this.keybind = keybind;
      this.defaultValue = defaultValue;
   }

   public void init() {
      this.button = (WButton)this.add(this.theme.button("")).widget();
      this.button.action = () -> {
         this.listening = true;
         this.button.set("...");
         if (this.actionOnSet != null) {
            this.actionOnSet.run();
         }

      };
      this.refreshLabel();
   }

   public boolean onClear() {
      if (this.listening) {
         this.keybind.reset();
         this.reset();
         return true;
      } else {
         return false;
      }
   }

   public boolean onAction(boolean isKey, int value, int modifiers) {
      if (this.listening && this.keybind.canBindTo(isKey, value, modifiers)) {
         this.keybind.set(isKey, value, modifiers);
         this.reset();
         return true;
      } else {
         return false;
      }
   }

   public void resetBind() {
      this.keybind.set(this.defaultValue);
      this.reset();
   }

   public void reset() {
      this.listening = false;
      this.refreshLabel();
      if (Modules.get().isBinding()) {
         Modules.get().setModuleToBind((Module)null);
      }

      if (this.action != null) {
         this.action.run();
      }

   }

   private void refreshLabel() {
      this.button.set(this.keybind.toString());
   }
}
