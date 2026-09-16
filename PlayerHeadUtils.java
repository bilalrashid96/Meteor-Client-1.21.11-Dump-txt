package meteordevelopment.meteorclient.utils.render;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.accounts.TexturesJson;
import meteordevelopment.meteorclient.systems.accounts.UuidToProfileResponse;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.network.Http;

public class PlayerHeadUtils {
   public static PlayerHeadTexture STEVE_HEAD;

   private PlayerHeadUtils() {
   }

   @PostInit
   public static void init() {
      STEVE_HEAD = new PlayerHeadTexture();
   }

   public static byte[] fetchHead(UUID id) {
      if (id == null) {
         return null;
      } else {
         String url = getSkinUrl(id);
         if (url == null) {
            return null;
         } else {
            try {
               return PlayerHeadTexture.downloadHead(url);
            } catch (IOException e) {
               MeteorClient.LOG.error("Could not fetch player head for {}.", id, e);
               return null;
            }
         }
      }
   }

   public static String getSkinUrl(UUID id) {
      UuidToProfileResponse res2 = (UuidToProfileResponse)Http.get("https://sessionserver.mojang.com/session/minecraft/profile/" + String.valueOf(id)).exceptionHandler((e) -> MeteorClient.LOG.error("Could not contact mojang session servers.", e)).sendJson(UuidToProfileResponse.class);
      if (res2 == null) {
         return null;
      } else {
         String base64Textures = res2.getPropertyValue("textures");
         if (base64Textures == null) {
            return null;
         } else {
            TexturesJson textures = (TexturesJson)(new Gson()).fromJson(new String(Base64.getDecoder().decode(base64Textures)), TexturesJson.class);
            return textures.textures.SKIN == null ? null : textures.textures.SKIN.url;
         }
      }
   }
}
