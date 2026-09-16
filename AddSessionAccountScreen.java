package meteordevelopment.meteorclient.gui.screens.accounts;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.accounts.types.SessionAccount;

public class AddSessionAccountScreen extends AddAccountScreen {
   public AddSessionAccountScreen(GuiTheme theme, AccountsScreen parent) {
      super(theme, "Add Session Account", parent);
   }

   public void initWidgets() {
      WTable t = (WTable)this.add(this.theme.table()).widget();
      t.add(this.theme.label("Access Token: "));
      WTextBox token = (WTextBox)t.add(this.theme.textBox("")).minWidth((double)400.0F).expandX().widget();
      token.setFocused(true);
      t.row();
      this.add = (WButton)t.add(this.theme.button("Add")).expandX().widget();
      this.add.action = () -> {
         if (!token.get().isEmpty()) {
            SessionAccount account = new SessionAccount(token.get());
            AccountsScreen.addAccount(this, this.parent, account);
         }

      };
      this.enterAction = this.add.action;
   }
}
