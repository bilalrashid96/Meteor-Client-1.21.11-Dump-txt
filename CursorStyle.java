package meteordevelopment.meteorclient.utils.misc;

import org.lwjgl.glfw.GLFW;

public enum CursorStyle {
   Default,
   Click,
   Type;

   private boolean created;
   private long cursor;

   public long getGlfwCursor() {
      if (!this.created) {
         switch (this.ordinal()) {
            case 1 -> this.cursor = GLFW.glfwCreateStandardCursor(221188);
            case 2 -> this.cursor = GLFW.glfwCreateStandardCursor(221186);
         }

         this.created = true;
      }

      return this.cursor;
   }

   // $FF: synthetic method
   private static CursorStyle[] $values() {
      return new CursorStyle[]{Default, Click, Type};
   }
}
