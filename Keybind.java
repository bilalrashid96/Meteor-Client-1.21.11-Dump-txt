package meteordevelopment.meteorclient.utils.misc;

import java.util.Objects;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import net.minecraft.class_11908;
import net.minecraft.class_11910;
import net.minecraft.class_2487;

public class Keybind implements ISerializable<Keybind>, ICopyable<Keybind> {
   private boolean isKey;
   private int value;
   private int modifiers;

   private Keybind(boolean isKey, int value, int modifiers) {
      this.set(isKey, value, modifiers);
   }

   public static Keybind none() {
      return new Keybind(true, -1, 0);
   }

   public static Keybind fromKey(int key) {
      return new Keybind(true, key, 0);
   }

   public static Keybind fromKeys(int key, int modifiers) {
      return new Keybind(true, key, modifiers);
   }

   public static Keybind fromButton(int button) {
      return new Keybind(false, button, 0);
   }

   public int getValue() {
      return this.value;
   }

   public boolean isSet() {
      return this.value != -1;
   }

   public boolean isKey() {
      return this.isKey;
   }

   public boolean hasMods() {
      return this.isKey && this.modifiers != 0;
   }

   public void set(boolean isKey, int value, int modifiers) {
      this.isKey = isKey;
      this.value = value;
      this.modifiers = modifiers;
   }

   public Keybind set(Keybind value) {
      this.isKey = value.isKey;
      this.value = value.value;
      this.modifiers = value.modifiers;
      return this;
   }

   public void reset() {
      this.set(true, -1, 0);
   }

   public boolean canBindTo(boolean isKey, int value, int modifiers) {
      if (isKey) {
         if (modifiers != 0 && this.isKeyMod(value)) {
            return false;
         } else {
            return value != -1 && value != 256;
         }
      } else {
         return value != 0 && value != 1;
      }
   }

   public boolean matches(boolean isKey, int value, int modifiers) {
      if (this.isSet() && this.isKey == isKey) {
         if (!this.hasMods()) {
            return this.value == value;
         } else {
            return this.value == value && this.modifiers == modifiers;
         }
      } else {
         return false;
      }
   }

   public boolean matches(class_11908 input) {
      return this.matches(true, input.comp_4795(), input.comp_4797());
   }

   public boolean matches(class_11910 input) {
      return this.matches(false, input.comp_4801(), 0);
   }

   public boolean isPressed() {
      return this.isKey ? this.modifiersPressed() && Input.isKeyPressed(this.value) : Input.isButtonPressed(this.value);
   }

   private boolean modifiersPressed() {
      if (!this.hasMods()) {
         return true;
      } else if (!this.isModPressed(2, 341, 345)) {
         return false;
      } else if (!this.isModPressed(8, 343, 347)) {
         return false;
      } else if (!this.isModPressed(4, 342, 346)) {
         return false;
      } else {
         return this.isModPressed(1, 340, 344);
      }
   }

   private boolean isModPressed(int value, int... keys) {
      if ((this.modifiers & value) == 0) {
         return true;
      } else {
         for(int key : keys) {
            if (Input.isKeyPressed(key)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean isKeyMod(int key) {
      return key >= 340 && key <= 347;
   }

   public Keybind copy() {
      return new Keybind(this.isKey, this.value, this.modifiers);
   }

   public String toString() {
      if (!this.isSet()) {
         return "None";
      } else if (!this.isKey) {
         return Utils.getButtonName(this.value);
      } else if (this.modifiers == 0) {
         return Utils.getKeyName(this.value);
      } else {
         StringBuilder label = new StringBuilder();
         if ((this.modifiers & 2) != 0) {
            label.append("Ctrl + ");
         }

         if ((this.modifiers & 8) != 0) {
            label.append("Cmd + ");
         }

         if ((this.modifiers & 4) != 0) {
            label.append("Alt + ");
         }

         if ((this.modifiers & 1) != 0) {
            label.append("Shift + ");
         }

         if ((this.modifiers & 16) != 0) {
            label.append("Caps Lock + ");
         }

         if ((this.modifiers & 32) != 0) {
            label.append("Num Lock + ");
         }

         label.append(Utils.getKeyName(this.value));
         return label.toString();
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Keybind keybind = (Keybind)o;
         return this.isKey == keybind.isKey && this.value == keybind.value && this.modifiers == keybind.modifiers;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.isKey, this.value, this.modifiers});
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10556("isKey", this.isKey);
      tag.method_10569("value", this.value);
      tag.method_10569("modifiers", this.modifiers);
      return tag;
   }

   public Keybind fromTag(class_2487 tag) {
      this.isKey = tag.method_68566("isKey", false);
      this.value = tag.method_68083("value", 0);
      this.modifiers = tag.method_68083("modifiers", 0);
      return this;
   }
}
