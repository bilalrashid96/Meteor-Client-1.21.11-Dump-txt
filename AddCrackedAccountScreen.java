package meteordevelopment.meteorclient.gui.screens.accounts;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.accounts.Accounts;
import meteordevelopment.meteorclient.systems.accounts.types.CrackedAccount;

public class AddCrackedAccountScreen extends AddAccountScreen {
   public AddCrackedAccountScreen(GuiTheme theme, AccountsScreen parent) {
      super(theme, "Add Cracked Account", parent);
   }

   public void initWidgets() {
      WTable t = (WTable)this.add(this.theme.table()).widget();
      t.add(this.theme.label("Name: "));
      WTextBox name = (WTextBox)t.add(this.theme.textBox("", (String)"seasnail8169", (CharFilter)((text, c) -> c > ' ' && c < 127))).minWidth((double)400.0F).expandX().widget();
      name.setFocused(true);
      t.row();
      this.add = (WButton)t.add(this.theme.button("Add")).expandX().widget();
      this.add.action = () -> {
         String username = name.get().trim();
         if (username.length() <= 16) {
            CrackedAccount account = new CrackedAccount(username);
            if (!Accounts.get().exists(account)) {
               AccountsScreen.addAccount(this, this.parent, account);
            }

         }
      };
      this.enterAction = this.add.action;
   }
}
