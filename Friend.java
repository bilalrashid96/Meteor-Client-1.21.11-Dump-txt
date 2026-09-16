package meteordevelopment.meteorclient.systems.friends;

import com.mojang.util.UndashedUuid;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.network.FailedHttpResponse;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.render.PlayerHeadTexture;
import meteordevelopment.meteorclient.utils.render.PlayerHeadUtils;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import org.jetbrains.annotations.NotNull;

public class Friend implements ISerializable<Friend>, Comparable<Friend> {
   public volatile String name;
   @Nullable
   private volatile UUID id;
   @Nullable
   private volatile PlayerHeadTexture headTexture;
   private volatile boolean updating;

   public Friend(String name, @Nullable UUID id) {
      this.name = name;
      this.id = id;
      this.headTexture = null;
   }

   public Friend(class_1657 player) {
      this(player.method_5477().getString(), player.method_5667());
   }

   public Friend(String name) {
      this(name, (UUID)null);
   }

   public String getName() {
      return this.name;
   }

   public PlayerHeadTexture getHead() {
      return this.headTexture != null ? this.headTexture : PlayerHeadUtils.STEVE_HEAD;
   }

   public void updateInfo() {
      this.updating = true;
      HttpResponse<APIResponse> res = null;
      if (this.id != null) {
         res = Http.get("https://sessionserver.mojang.com/session/minecraft/profile/" + UndashedUuid.toString(this.id)).exceptionHandler((e) -> MeteorClient.LOG.error("Error while trying to connect session server for friend '{}'", this.name)).<APIResponse>sendJsonResponse(APIResponse.class);
      }

      if (res == null || res.statusCode() != 200) {
         res = Http.get("https://api.mojang.com/users/profiles/minecraft/" + this.name).exceptionHandler((e) -> MeteorClient.LOG.error("Error while trying to update info for friend '{}'", this.name)).<APIResponse>sendJsonResponse(APIResponse.class);
      }

      if (res != null && res.statusCode() == 200) {
         this.name = ((APIResponse)res.body()).name;
         this.id = UndashedUuid.fromStringLenient(((APIResponse)res.body()).id);
         byte[] head = PlayerHeadUtils.fetchHead(this.id);
         MeteorClient.mc.execute(() -> {
            if (head != null) {
               this.headTexture = new PlayerHeadTexture(head, true);
            }

         });
      } else if (!(res instanceof FailedHttpResponse)) {
         this.id = null;
      }

      this.updating = false;
   }

   public boolean headTextureNeedsUpdate() {
      return !this.updating && this.headTexture == null;
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("name", this.name);
      if (this.id != null) {
         tag.method_10582("id", UndashedUuid.toString(this.id));
      }

      return tag;
   }

   public Friend fromTag(class_2487 tag) {
      return this;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Friend friend = (Friend)o;
         return Objects.equals(this.name, friend.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name});
   }

   public int compareTo(@NotNull Friend friend) {
      return this.name.compareToIgnoreCase(friend.name);
   }

   private static class APIResponse {
      String name;
      String id;
   }
}
