package meteordevelopment.meteorclient.utils.network;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1657;
import net.minecraft.class_2960;

public class Capes {
   private static final String CAPE_OWNERS_URL = "https://meteorclient.com/api/capeowners";
   private static final String CAPES_URL = "https://meteorclient.com/api/capes";
   private static final Map<UUID, String> OWNERS = new HashMap();
   private static final Map<String, String> URLS = new HashMap();
   private static final Map<String, Cape> TEXTURES = new HashMap();
   private static final List<Cape> TO_REGISTER = new ArrayList();
   private static final List<Cape> TO_RETRY = new ArrayList();
   private static final List<Cape> TO_REMOVE = new ArrayList();

   private Capes() {
   }

   @PreInit(
      dependencies = {MeteorExecutor.class}
   )
   public static void init() {
      OWNERS.clear();
      URLS.clear();
      TEXTURES.clear();
      TO_REGISTER.clear();
      TO_RETRY.clear();
      TO_REMOVE.clear();
      MeteorExecutor.execute(() -> {
         Stream<String> lines = Http.get("https://meteorclient.com/api/capeowners").exceptionHandler((e) -> MeteorClient.LOG.error("Could not load capes: {}", e.getMessage())).sendLines();
         if (lines != null) {
            lines.forEach((s) -> {
               String[] split = s.split(" ");
               if (split.length >= 2) {
                  OWNERS.put(UUID.fromString(split[0]), split[1]);
                  if (!TEXTURES.containsKey(split[1])) {
                     TEXTURES.put(split[1], new Cape(split[1]));
                  }
               }

            });
            lines = Http.get("https://meteorclient.com/api/capes").sendLines();
            if (lines != null) {
               lines.forEach((s) -> {
                  String[] split = s.split(" ");
                  if (split.length >= 2 && !URLS.containsKey(split[0])) {
                     URLS.put(split[0], split[1]);
                  }

               });
            }

         }
      });
      MeteorClient.EVENT_BUS.subscribe(Capes.class);
   }

   @EventHandler
   private static void onTick(TickEvent.Post event) {
      synchronized(TO_REGISTER) {
         for(Cape cape : TO_REGISTER) {
            cape.register();
         }

         TO_REGISTER.clear();
      }

      synchronized(TO_RETRY) {
         TO_RETRY.removeIf(Cape::tick);
      }

      synchronized(TO_REMOVE) {
         for(Cape cape : TO_REMOVE) {
            URLS.remove(cape.name);
            TEXTURES.remove(cape.name);
            TO_REGISTER.remove(cape);
            TO_RETRY.remove(cape);
         }

         TO_REMOVE.clear();
      }
   }

   public static class_2960 get(class_1657 player) {
      String capeName = (String)OWNERS.get(player.method_5667());
      if (capeName != null) {
         Cape cape = (Cape)TEXTURES.get(capeName);
         if (cape == null) {
            return null;
         } else if (cape.isDownloaded()) {
            return cape.getIdentifier();
         } else {
            cape.download();
            return null;
         }
      } else {
         return null;
      }
   }

   private static class Cape {
      private static int COUNT = 0;
      private final String name;
      private final class_2960 identifier;
      private boolean downloaded;
      private boolean downloading;
      private class_1011 img;
      private int retryTimer;

      public Cape(String name) {
         int var10001 = COUNT++;
         this.identifier = MeteorClient.identifier("capes/" + var10001);
         this.name = name;
      }

      public class_2960 getIdentifier() {
         return this.identifier;
      }

      public void download() {
         if (!this.downloaded && !this.downloading && this.retryTimer <= 0) {
            this.downloading = true;
            MeteorExecutor.execute(() -> {
               try {
                  String url = (String)Capes.URLS.get(this.name);
                  if (url == null) {
                     synchronized(Capes.TO_REMOVE) {
                        Capes.TO_REMOVE.add(this);
                        this.downloading = false;
                        return;
                     }
                  }

                  InputStream in = Http.get(url).sendInputStream();
                  if (in == null) {
                     synchronized(Capes.TO_RETRY) {
                        Capes.TO_RETRY.add(this);
                        this.retryTimer = 200;
                        this.downloading = false;
                        return;
                     }
                  }

                  this.img = class_1011.method_4309(in);
                  synchronized(Capes.TO_REGISTER) {
                     Capes.TO_REGISTER.add(this);
                  }
               } catch (IOException e) {
                  MeteorClient.LOG.error("Failed to download cape '{}'", this.name, e);
               }

            });
         }
      }

      public void register() {
         MeteorClient.mc.method_1531().method_4616(this.identifier, new class_1043((Supplier)null, this.img));
         this.img = null;
         this.downloading = false;
         this.downloaded = true;
      }

      public boolean tick() {
         if (this.retryTimer > 0) {
            --this.retryTimer;
            return false;
         } else {
            this.download();
            return true;
         }
      }

      public boolean isDownloaded() {
         return this.downloaded;
      }
   }
}
