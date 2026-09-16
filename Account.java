package meteordevelopment.meteorclient.systems.accounts;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.FileCacheAccessor;
import meteordevelopment.meteorclient.mixin.MinecraftClientAccessor;
import meteordevelopment.meteorclient.mixin.PlayerSkinProviderAccessor;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.NbtException;
import net.minecraft.class_10538;
import net.minecraft.class_1071;
import net.minecraft.class_156;
import net.minecraft.class_2487;
import net.minecraft.class_320;
import net.minecraft.class_5520;
import net.minecraft.class_7497;
import net.minecraft.class_7500;
import net.minecraft.class_7569;
import net.minecraft.class_7574;
import net.minecraft.class_7853;

public abstract class Account<T extends Account<?>> implements ISerializable<T> {
   protected AccountType type;
   protected String name;
   protected final AccountCache cache;

   protected Account(AccountType type, String name) {
      this.type = type;
      this.name = name;
      this.cache = new AccountCache();
   }

   public abstract boolean fetchInfo();

   public boolean login() {
      YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(MeteorClient.mc.method_1487());
      applyLoginEnvironment(authenticationService);
      return true;
   }

   public String getUsername() {
      return this.cache.username.isEmpty() ? this.name : this.cache.username;
   }

   public AccountType getType() {
      return this.type;
   }

   public AccountCache getCache() {
      return this.cache;
   }

   public static void setSession(class_320 session) {
      MinecraftClientAccessor mca = (MinecraftClientAccessor)MeteorClient.mc;
      mca.meteor$setSession(session);
      YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(MeteorClient.mc.method_1487());
      UserApiService apiService = yggdrasilAuthenticationService.createUserApiService(session.method_1674());
      mca.meteor$setUserApiService(apiService);
      mca.meteor$setSocialInteractionsManager(new class_5520(MeteorClient.mc, apiService));
      mca.meteor$setProfileKeys(class_7853.method_46532(apiService, session, MeteorClient.mc.field_1697.toPath()));
      mca.meteor$setAbuseReportContext(class_7574.method_44599(class_7569.method_44586(), apiService));
      mca.meteor$setGameProfileFuture(CompletableFuture.supplyAsync(() -> MeteorClient.mc.method_73361().comp_837().fetchProfile(MeteorClient.mc.method_1548().method_44717(), true), class_156.method_27958()));
   }

   public static void applyLoginEnvironment(YggdrasilAuthenticationService authService) {
      MinecraftClientAccessor mca = (MinecraftClientAccessor)MeteorClient.mc;
      class_7500.method_44172(authService.getServicesKeySet(), ServicesKeyType.PROFILE_KEY);
      class_1071.class_8687 skinCache = ((PlayerSkinProviderAccessor)MeteorClient.mc.method_1582()).meteor$getSkinCache();
      Path skinCachePath = ((FileCacheAccessor)skinCache).meteor$getDirectory();
      mca.meteor$setApiServices(class_7497.method_44143(authService, MeteorClient.mc.field_1697));
      mca.meteor$setSkinProvider(new class_1071(skinCachePath, MeteorClient.mc.method_73361(), new class_10538(MeteorClient.mc.method_1487(), MeteorClient.mc.method_1531(), MeteorClient.mc), MeteorClient.mc));
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("type", this.type.name());
      tag.method_10582("name", this.name);
      tag.method_10566("cache", this.cache.toTag());
      return tag;
   }

   public T fromTag(class_2487 tag) {
      if (!tag.method_10558("name").isEmpty() && !tag.method_10562("cache").isEmpty()) {
         this.name = (String)tag.method_10558("name").get();
         this.cache.fromTag((class_2487)tag.method_10562("cache").get());
         return (T)this;
      } else {
         throw new NbtException();
      }
   }
}
