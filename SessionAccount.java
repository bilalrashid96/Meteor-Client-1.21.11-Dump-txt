package meteordevelopment.meteorclient.systems.accounts.types;

import com.mojang.util.UndashedUuid;
import java.util.Optional;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.AccountType;
import meteordevelopment.meteorclient.systems.accounts.TokenAccount;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.class_2487;
import net.minecraft.class_320;

public class SessionAccount extends Account<SessionAccount> implements TokenAccount {
   private String accessToken;

   public SessionAccount(String label) {
      super(AccountType.Session, label);
      this.accessToken = label;
   }

   public SessionAccount fromTag(class_2487 tag) {
      super.fromTag(tag);
      this.accessToken = tag.method_68564("token", "");
      return this;
   }

   public class_2487 toTag() {
      class_2487 tag = super.toTag();
      tag.method_10582("token", this.accessToken);
      return tag;
   }

   public boolean fetchInfo() {
      if (this.accessToken != null && !this.accessToken.isBlank()) {
         ProfileResponse profile;
         try {
            profile = (ProfileResponse)Http.get("https://api.minecraftservices.com/minecraft/profile").bearer(this.accessToken).sendJson(ProfileResponse.class);
         } catch (IllegalArgumentException e) {
            MeteorClient.LOG.error("Invalid session account token", e);
            return false;
         }

         if (profile != null && profile.id != null && profile.name != null) {
            this.cache.username = profile.name;
            this.cache.uuid = profile.id;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean login() {
      if (this.accessToken != null && !this.accessToken.isBlank()) {
         super.login();
         setSession(new class_320(this.cache.username, UndashedUuid.fromStringLenient(this.cache.uuid), this.accessToken, Optional.empty(), Optional.empty()));
         return true;
      } else {
         return false;
      }
   }

   public String getToken() {
      return this.accessToken;
   }

   public boolean equals(Object o) {
      if (o instanceof SessionAccount account2) {
         return account2.name.equals(this.name);
      } else {
         return false;
      }
   }

   private static class ProfileResponse {
      public String id;
      public String name;
   }
}
