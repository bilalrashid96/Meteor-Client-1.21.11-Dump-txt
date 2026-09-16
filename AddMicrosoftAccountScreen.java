package meteordevelopment.meteorclient.gui.screens.accounts;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.accounts.MicrosoftLogin;
import meteordevelopment.meteorclient.systems.accounts.types.MicrosoftAccount;

public class AddMicrosoftAccountScreen extends AddAccountScreen {
   public AddMicrosoftAccountScreen(GuiTheme theme, AccountsScreen parent) {
      super(theme, "Add Microsoft Account", parent);
   }

   public void initWidgets() {
      String url = MicrosoftLogin.getRefreshToken((refreshToken) -> {
         if (refreshToken != null) {
            MicrosoftAccount account = new MicrosoftAccount(refreshToken);
            AccountsScreen.addAccount((AddAccountScreen)null, this.parent, account);
         }

         this.method_25419();
      });
      this.add(this.theme.label("Please select the account to log into in your browser."));
      this.add(this.theme.label("If the link does not automatically open in a few seconds, copy it into your browser."));
      WHorizontalList l = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
      WButton copy = (WButton)l.add(this.theme.button("Copy link")).expandX().widget();
      copy.action = () -> MeteorClient.mc.field_1774.method_1455(url);
      WButton cancel = (WButton)l.add(this.theme.button("Cancel")).expandX().widget();
      cancel.action = () -> {
         MicrosoftLogin.cancelLogin();
         this.method_25419();
      };
   }

   public void method_25393() {
   }

   public boolean method_25422() {
      return false;
   }
}
