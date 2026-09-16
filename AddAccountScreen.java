package meteordevelopment.meteorclient.gui.screens.accounts;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;

public abstract class AddAccountScreen extends WindowScreen {
   public final AccountsScreen parent;
   public WButton add;
   private int timer;

   protected AddAccountScreen(GuiTheme theme, String title, AccountsScreen parent) {
      super(theme, title);
      this.parent = parent;
   }

   public void method_25393() {
      if (this.locked) {
         if (this.timer > 2) {
            this.add.set(this.getNext(this.add));
            this.timer = 0;
         } else {
            ++this.timer;
         }
      } else if (!this.add.getText().equals("Add")) {
         this.add.set("Add");
      }

   }

   private String getNext(WButton add) {
      String var10000;
      switch (add.getText()) {
         case "Add":
         case "oo0":
            var10000 = "ooo";
            break;
         case "ooo":
            var10000 = "0oo";
            break;
         case "0oo":
            var10000 = "o0o";
            break;
         case "o0o":
            var10000 = "oo0";
            break;
         default:
            var10000 = "Add";
      }

      return var10000;
   }
}
