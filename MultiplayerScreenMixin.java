package meteordevelopment.meteorclient.mixin;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.NameProtect;
import meteordevelopment.meteorclient.systems.proxies.Proxies;
import meteordevelopment.meteorclient.systems.proxies.Proxy;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_500;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_500.class})
public abstract class MultiplayerScreenMixin extends class_437 {
   @Unique
   private int textColor1;
   @Unique
   private int textColor2;
   @Unique
   private String loggedInAs;
   @Unique
   private int loggedInAsLength;
   @Unique
   private class_4185 accounts;
   @Unique
   private class_4185 proxies;
   @Unique
   private static final int BUTTON_WIDTH = 75;
   @Unique
   private static final int BUTTON_HEIGHT = 20;
   @Unique
   private static final int MARGIN = 3;
   @Unique
   private static final int GAP = 2;

   public MultiplayerScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_48640"},
      at = {@At("TAIL")}
   )
   private void onInit(CallbackInfo info) {
      this.textColor1 = Color.fromRGBA(255, 255, 255, 255);
      this.textColor2 = Color.fromRGBA(175, 175, 175, 255);
      this.loggedInAs = "Logged in as ";
      this.loggedInAsLength = this.field_22793.method_1727(this.loggedInAs);
      if (this.accounts == null) {
         this.accounts = (class_4185)this.method_37063((new class_4185.class_7840(class_2561.method_43470("Accounts"), (button) -> this.field_22787.method_1507(GuiThemes.get().accountsScreen()))).method_46437(75, 20).method_46431());
      }

      if (this.proxies == null) {
         this.proxies = (class_4185)this.method_37063((new class_4185.class_7840(class_2561.method_43470("Proxies"), (button) -> this.field_22787.method_1507(GuiThemes.get().proxiesScreen()))).method_46437(75, 20).method_46431());
      }

      Config config = Config.get();
      Config.ButtonPosition accountPos = config.accountButtonAnchor.get();
      Config.ButtonPosition proxiesPos = config.proxiesButtonAnchor.get();
      boolean accountsVisible = accountPos != Config.ButtonPosition.Hidden;
      boolean proxiesVisible = proxiesPos != Config.ButtonPosition.Hidden;
      this.accounts.field_22764 = accountsVisible;
      this.proxies.field_22764 = proxiesVisible;
      this.positionButton(this.accounts, accountPos, proxiesVisible && proxiesPos == accountPos, true);
      this.positionButton(this.proxies, proxiesPos, accountsVisible && accountPos == proxiesPos, false);
   }

   @Unique
   private void positionButton(class_4185 button, Config.ButtonPosition anchor, boolean sharingCorner, boolean isAccounts) {
      int leftOffset = sharingCorner && isAccounts ? 77 : 0;
      int rightOffset = sharingCorner && !isAccounts ? 77 : 0;
      switch (anchor) {
         case TopRight -> button.method_48229(this.field_22789 - 3 - 75 - rightOffset, 3);
         case TopLeft -> button.method_48229(3 + leftOffset, 3);
         case BottomLeft -> button.method_48229(3 + leftOffset, this.field_22790 - 3 - 20);
         case BottomRight -> button.method_48229(this.field_22789 - 3 - 75 - rightOffset, this.field_22790 - 3 - 20);
         default -> button.method_48229(this.field_22789 - 3 - 75 - rightOffset, 3);
      }

   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float deltaTicks) {
      super.method_25394(context, mouseX, mouseY, deltaTicks);
      Config config = Config.get();
      if ((Boolean)config.showAccountStatus.get() || (Boolean)config.showProxiesStatus.get()) {
         int x = 3;
         if (config.proxiesButtonAnchor.get() != Config.ButtonPosition.Hidden && config.proxiesButtonAnchor.get() == Config.ButtonPosition.TopLeft) {
            x += 77;
         }

         if (config.accountButtonAnchor.get() != Config.ButtonPosition.Hidden && config.accountButtonAnchor.get() == Config.ButtonPosition.TopLeft) {
            x += 77;
         }

         int y = 3;
         if ((Boolean)config.showAccountStatus.get()) {
            context.method_25303(MeteorClient.mc.field_1772, this.loggedInAs, x, y, this.textColor1);
            context.method_25303(MeteorClient.mc.field_1772, ((NameProtect)Modules.get().get(NameProtect.class)).getName(this.field_22787.method_1548().method_1676()), x + this.loggedInAsLength, y, this.textColor2);
            Objects.requireNonNull(this.field_22793);
            y += 9 + 2;
         }

         if ((Boolean)config.showProxiesStatus.get()) {
            Proxy proxy = Proxies.get().getEnabled();
            String left = proxy != null ? "Using proxy " : "Not using a proxy";
            String right = proxy != null ? (proxy.name.get() != null && !((String)proxy.name.get()).isEmpty() ? "(" + (String)proxy.name.get() + ") " : "") + (String)proxy.address.get() + ":" + String.valueOf(proxy.port.get()) : null;
            context.method_25303(MeteorClient.mc.field_1772, left, x, y, this.textColor1);
            if (right != null) {
               context.method_25303(MeteorClient.mc.field_1772, right, x + this.field_22793.method_1727(left), y, this.textColor2);
            }

         }
      }
   }
}
