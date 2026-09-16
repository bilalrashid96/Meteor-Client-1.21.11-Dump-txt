package meteordevelopment.meteorclient.gui.screens.accounts;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.WAccount;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.AccountCache;
import meteordevelopment.meteorclient.systems.accounts.AccountType;
import meteordevelopment.meteorclient.systems.accounts.Accounts;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import org.jetbrains.annotations.Nullable;

public class AccountsScreen extends WindowScreen {
   public AccountsScreen(GuiTheme theme) {
      super(theme, "Accounts");
   }

   public void initWidgets() {
      for(Account<?> account : Accounts.get()) {
         WAccount wAccount = (WAccount)this.add(this.theme.account(this, account)).expandX().widget();
         wAccount.refreshScreenAction = this::reload;
      }

      WHorizontalList l = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
      this.addButton(l, "Cracked", () -> MeteorClient.mc.method_1507(new AddCrackedAccountScreen(this.theme, this)));
      this.addButton(l, "Altening", () -> MeteorClient.mc.method_1507(new AddAlteningAccountScreen(this.theme, this)));
      this.addButton(l, "Session", () -> MeteorClient.mc.method_1507(new AddSessionAccountScreen(this.theme, this)));
      this.addButton(l, "Microsoft", () -> MeteorClient.mc.method_1507(new AddMicrosoftAccountScreen(this.theme, this)));
   }

   private void addButton(WContainer c, String text, Runnable action) {
      WButton button = (WButton)c.add(this.theme.button(text)).expandX().widget();
      button.action = action;
   }

   public static void addAccount(@Nullable AddAccountScreen screen, AccountsScreen parent, Account<?> account) {
      if (screen != null) {
         screen.locked = true;
      }

      MeteorExecutor.execute(() -> {
         if (!account.fetchInfo()) {
            MeteorClient.mc.execute(() -> {
               if (screen != null) {
                  screen.locked = false;
               }

            });
         } else {
            Accounts.get().add(account);
            if (account.login()) {
               if (account.getType() != AccountType.Cracked) {
                  AccountCache var10000 = account.getCache();
                  Objects.requireNonNull(parent);
                  var10000.loadHead(parent::reload);
               }

               Accounts.get().save();
            }

            MeteorClient.mc.execute(() -> {
               if (screen != null) {
                  screen.locked = false;
                  screen.method_25419();
               }

               parent.reload();
            });
         }
      });
   }

   public boolean toClipboard() {
      return NbtUtils.toClipboard(Accounts.get());
   }

   public boolean fromClipboard() {
      return NbtUtils.fromClipboard(Accounts.get());
   }
}
