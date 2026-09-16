package meteordevelopment.meteorclient.systems.accounts;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.class_156;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.java.JavaAuthManager;
import net.raphimc.minecraftauth.java.model.MinecraftProfile;
import net.raphimc.minecraftauth.java.model.MinecraftToken;
import net.raphimc.minecraftauth.msa.model.MsaApplicationConfig;
import net.raphimc.minecraftauth.msa.model.MsaToken;
import net.raphimc.minecraftauth.msa.service.impl.DeviceCodeMsaAuthService;
import org.jspecify.annotations.Nullable;

public class MicrosoftLogin {
   private static final MsaApplicationConfig APPLICATION_CONFIG = new MsaApplicationConfig("00000000402b5328", "service::user.auth.xboxlive.com::MBI_SSL");
   private static final AtomicReference<Future<?>> loginTask = new AtomicReference();
   private static final AtomicBoolean cancelled = new AtomicBoolean();

   private MicrosoftLogin() {
   }

   public static String getRefreshToken(Consumer<String> callback) {
      cancelLogin();
      cancelled.set(false);
      CompletableFuture<String> urlFuture = new CompletableFuture();
      loginTask.set(MeteorExecutor.executor.submit(() -> {
         try {
            JavaAuthManager authManager = JavaAuthManager.create(MinecraftAuth.createHttpClient()).msaApplicationConfig(APPLICATION_CONFIG).login(DeviceCodeMsaAuthService::new, (Consumer)(deviceCode) -> {
               String urlString = deviceCode.getDirectVerificationUri();
               urlFuture.complete(urlString);
               class_156.method_668().method_670(urlString);
            });
            MsaToken msaToken = (MsaToken)authManager.getMsaToken().getUpToDate();
            if (!cancelled.get()) {
               callback.accept(msaToken.getRefreshToken());
            }
         } catch (InterruptedException var4) {
            Thread.currentThread().interrupt();
            if (!urlFuture.isDone()) {
               urlFuture.completeExceptionally(var4);
            }

            if (cancelled.get()) {
               return;
            }

            MeteorClient.LOG.error("Error logging into Microsoft account", var4);
            callback.accept((Object)null);
         } catch (Exception var5) {
            if (!urlFuture.isDone()) {
               urlFuture.completeExceptionally(var5);
            }

            if (cancelled.get()) {
               return;
            }

            MeteorClient.LOG.error("Error logging into Microsoft account", var5);
            callback.accept((Object)null);
         }

      }));

      try {
         return (String)urlFuture.get();
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new RuntimeException("Interrupted while starting Microsoft login", e);
      } catch (ExecutionException e) {
         throw new RuntimeException("Failed to start Microsoft login", e.getCause());
      }
   }

   public static @Nullable LoginData login(String refreshToken) {
      try {
         JavaAuthManager authManager = JavaAuthManager.create(MinecraftAuth.createHttpClient()).msaApplicationConfig(APPLICATION_CONFIG).login(refreshToken);
         MsaToken msaToken = (MsaToken)authManager.getMsaToken().getUpToDate();
         MinecraftToken minecraftToken = (MinecraftToken)authManager.getMinecraftToken().getUpToDate();
         MinecraftProfile profile = (MinecraftProfile)authManager.getMinecraftProfile().getUpToDate();
         return new LoginData(minecraftToken.getToken(), msaToken.getRefreshToken(), profile.getId().toString(), profile.getName());
      } catch (Exception e) {
         MeteorClient.LOG.error("Error logging into Microsoft account", e);
         return null;
      }
   }

   public static void cancelLogin() {
      cancelled.set(true);
      Future<?> task = (Future)loginTask.getAndSet((Object)null);
      if (task != null) {
         task.cancel(true);
      }

   }

   public static record LoginData(String mcToken, String newRefreshToken, String uuid, String username) {
   }
}
