package meteordevelopment.meteorclient.systems.accounts.types;

import com.mojang.util.UndashedUuid;
import java.util.Optional;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.AccountType;
import meteordevelopment.meteorclient.systems.accounts.MicrosoftLogin;
import net.minecraft.class_320;
import org.jetbrains.annotations.Nullable;

public class MicrosoftAccount extends Account<MicrosoftAccount> {
   private @Nullable String token;

   public MicrosoftAccount(String refreshToken) {
      super(AccountType.Microsoft, refreshToken);
   }

   public boolean fetchInfo() {
      this.token = this.auth();
      return this.token != null;
   }

   public boolean login() {
      if (this.token == null) {
         return false;
      } else {
         super.login();
         setSession(new class_320(this.cache.username, UndashedUuid.fromStringLenient(this.cache.uuid), this.token, Optional.empty(), Optional.empty()));
         return true;
      }
   }

   private @Nullable String auth() {
      MicrosoftLogin.LoginData data = MicrosoftLogin.login(this.name);
      if (data != null && data.newRefreshToken() != null) {
         this.name = data.newRefreshToken();
         this.cache.username = data.username();
         this.cache.uuid = data.uuid();
         return data.mcToken();
      } else {
         return null;
      }
   }

   public boolean equals(Object o) {
      if (o instanceof MicrosoftAccount account) {
         return account.name.equals(this.name);
      } else {
         return false;
      }
   }
}
