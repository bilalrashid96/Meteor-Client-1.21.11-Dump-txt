package meteordevelopment.meteorclient.utils.misc.input;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiKeyEvents;
import meteordevelopment.meteorclient.mixin.KeyBindingAccessor;
import meteordevelopment.meteorclient.utils.misc.CursorStyle;
import net.minecraft.class_304;
import org.lwjgl.glfw.GLFW;

public class Input {
   private static final boolean[] keys = new boolean[512];
   private static final boolean[] buttons = new boolean[16];
   private static CursorStyle lastCursorStyle;

   private Input() {
   }

   public static void setKeyState(int key, boolean pressed) {
      if (key >= 0 && key < keys.length) {
         keys[key] = pressed;
      }

   }

   public static void setButtonState(int button, boolean pressed) {
      if (button >= 0 && button < buttons.length) {
         buttons[button] = pressed;
      }

   }

   public static int getKey(class_304 bind) {
      return ((KeyBindingAccessor)bind).meteor$getKey().method_1444();
   }

   public static void setKeyState(class_304 bind, boolean pressed) {
      setKeyState(getKey(bind), pressed);
   }

   public static boolean isPressed(class_304 bind) {
      return isKeyPressed(getKey(bind)) || isButtonPressed(getKey(bind));
   }

   public static boolean isKeyPressed(int key) {
      if (!GuiKeyEvents.canUseKeys) {
         return false;
      } else if (key == -1) {
         return false;
      } else {
         return key < keys.length && keys[key];
      }
   }

   public static boolean isButtonPressed(int button) {
      if (button == -1) {
         return false;
      } else {
         return button < buttons.length && buttons[button];
      }
   }

   public static void setCursorStyle(CursorStyle style) {
      if (lastCursorStyle != style) {
         GLFW.glfwSetCursor(MeteorClient.mc.method_22683().method_4490(), style.getGlfwCursor());
         lastCursorStyle = style;
      }

   }

   public static int getModifier(int key) {
      byte var10000;
      switch (key) {
         case 340:
         case 344:
            var10000 = 1;
            break;
         case 341:
         case 345:
            var10000 = 2;
            break;
         case 342:
         case 346:
            var10000 = 4;
            break;
         case 343:
         case 347:
            var10000 = 8;
            break;
         default:
            var10000 = 0;
      }

      return var10000;
   }

   static {
      lastCursorStyle = CursorStyle.Default;
   }
}
