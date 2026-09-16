package meteordevelopment.meteorclient.gui.themes.meteor.widgets;

import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.WAccount;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorAccount extends WAccount implements MeteorWidget {
   public WMeteorAccount(WidgetScreen screen, Account<?> account) {
      super(screen, account);
   }

   protected Color loggedInColor() {
      return this.theme().loggedInColor.get();
   }

   protected Color accountTypeColor() {
      return this.theme().textSecondaryColor.get();
   }
}
