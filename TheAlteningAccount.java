package meteordevelopment.meteorclient.systems.accounts.types;

import com.mojang.authlib.Environment;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import de.florianmichael.waybackauthlib.InvalidCredentialsException;
import de.florianmichael.waybackauthlib.WaybackAuthLib;
import java.util.Optional;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.AccountType;
import meteordevelopment.meteorclient.systems.accounts.TokenAccount;
import meteordevelopment.meteorclient.utils.misc.NbtException;
import net.minecraft.class_2487;
import net.minecraft.class_320;
import org.jetbrains.annotations.Nullable;

public class TheAlteningAccount extends Account<TheAlteningAccount> implements TokenAccount {
   private static final Environment ENVIRONMENT = new Environment("http://sessionserver.thealtening.com", "http://authserver.thealtening.com", "https://api.mojang.com", "The Altening");
   private static final YggdrasilAuthenticationService SERVICE;
   private String token;
   private @Nullable WaybackAuthLib auth;

   public TheAlteningAccount(String token) {
      super(AccountType.TheAltening, token);
      this.token = token;
   }

   public boolean fetchInfo() {
      this.auth = this.getAuth();

      try {
         this.auth.logIn();
         this.cache.username = this.auth.getCurrentProfile().name();
         this.cache.uuid = this.auth.getCurrentProfile().id().toString();
         this.cache.loadHead();
         return true;
      } catch (InvalidCredentialsException var2) {
         MeteorClient.LOG.error("Invalid TheAltening credentials.");
         return false;
      } catch (Exception var3) {
         MeteorClient.LOG.error("Failed to fetch info for TheAltening account!");
         return false;
      }
   }

   public boolean login() {
      if (this.auth == null) {
         return false;
      } else {
         applyLoginEnvironment(SERVICE);

         try {
            setSession(new class_320(this.auth.getCurrentProfile().name(), this.auth.getCurrentProfile().id(), this.auth.getAccessToken(), Optional.empty(), Optional.empty()));
            return true;
         } catch (Exception var2) {
            MeteorClient.LOG.error("Failed to login with TheAltening.");
            return false;
         }
      }
   }

   private WaybackAuthLib getAuth() {
      WaybackAuthLib auth = new WaybackAuthLib(ENVIRONMENT.servicesHost());
      auth.setUsername(this.name);
      auth.setPassword("Meteor on Crack!");
      return auth;
   }

   public String getToken() {
      return this.token;
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("type", this.type.name());
      tag.method_10582("name", this.name);
      tag.method_10582("token", this.token);
      tag.method_10566("cache", this.cache.toTag());
      return tag;
   }

   public TheAlteningAccount fromTag(class_2487 tag) {
      if (!tag.method_10558("name").isEmpty() && !tag.method_10562("cache").isEmpty() && !tag.method_10558("token").isEmpty()) {
         this.name = (String)tag.method_10558("name").get();
         this.token = (String)tag.method_10558("token").get();
         this.cache.fromTag((class_2487)tag.method_10562("cache").get());
         return this;
      } else {
         throw new NbtException();
      }
   }

   static {
      SERVICE = new YggdrasilAuthenticationService(MeteorClient.mc.method_1487(), ENVIRONMENT);
   }
}
