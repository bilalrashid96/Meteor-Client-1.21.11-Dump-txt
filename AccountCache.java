package meteordevelopment.meteorclient.systems.accounts;

import com.mojang.util.UndashedUuid;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.NbtException;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.render.PlayerHeadTexture;
import meteordevelopment.meteorclient.utils.render.PlayerHeadUtils;
import net.minecraft.class_2487;

public class AccountCache implements ISerializable<AccountCache> {
   public String username = "";
   public String uuid = "";
   private PlayerHeadTexture headTexture;
   private volatile boolean loadingHead;

   public PlayerHeadTexture getHeadTexture() {
      return this.headTexture != null ? this.headTexture : PlayerHeadUtils.STEVE_HEAD;
   }

   public void loadHead() {
      this.loadHead((Runnable)null);
   }

   public void loadHead(Runnable callback) {
      if (this.headTexture == null && this.uuid != null && !this.uuid.isBlank()) {
         if (!this.loadingHead) {
            this.loadingHead = true;
            MeteorExecutor.execute(() -> {
               byte[] head = PlayerHeadUtils.fetchHead(UndashedUuid.fromStringLenient(this.uuid));
               MeteorClient.mc.execute(() -> {
                  if (head != null) {
                     this.headTexture = new PlayerHeadTexture(head, true);
                  }

                  this.loadingHead = false;
                  if (callback != null) {
                     callback.run();
                  }

               });
            });
         }
      } else {
         if (callback != null) {
            MeteorClient.mc.execute(callback);
         }

      }
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("username", this.username);
      tag.method_10582("uuid", this.uuid);
      return tag;
   }

   public AccountCache fromTag(class_2487 tag) {
      if (!tag.method_10558("username").isEmpty() && !tag.method_10558("uuid").isEmpty()) {
         this.username = (String)tag.method_10558("username").get();
         this.uuid = (String)tag.method_10558("uuid").get();
         this.loadHead();
         return this;
      } else {
         throw new NbtException();
      }
   }
}
