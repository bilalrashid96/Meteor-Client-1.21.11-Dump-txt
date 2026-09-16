package meteordevelopment.meteorclient.utils.entity;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.DirectionAccessor;
import meteordevelopment.meteorclient.mixin.EntityTrackingSectionAccessor;
import meteordevelopment.meteorclient.mixin.SectionedEntityCacheAccessor;
import meteordevelopment.meteorclient.mixin.SimpleEntityLookupAccessor;
import meteordevelopment.meteorclient.mixin.WorldAccessor;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1934;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_3611;
import net.minecraft.class_3612;
import net.minecraft.class_4076;
import net.minecraft.class_4970;
import net.minecraft.class_5572;
import net.minecraft.class_5573;
import net.minecraft.class_5577;
import net.minecraft.class_5578;
import net.minecraft.class_640;

public class EntityUtils {
   private static final class_2338.class_2339 testPos = new class_2338.class_2339();

   private EntityUtils() {
   }

   public static boolean isAttackable(class_1299<?> type) {
      return type != class_1299.field_6083 && type != class_1299.field_6122 && type != class_1299.field_6089 && type != class_1299.field_6133 && type != class_1299.field_6052 && type != class_1299.field_6124 && type != class_1299.field_6135 && type != class_1299.field_6082 && type != class_1299.field_6064 && type != class_1299.field_56254 && type != class_1299.field_56255 && type != class_1299.field_6127 && type != class_1299.field_6112 && type != class_1299.field_6103 && type != class_1299.field_6044 && type != class_1299.field_6144;
   }

   public static boolean isRideable(class_1299<?> type) {
      return type == class_1299.field_6093 || type == class_1299.field_23214 || type == class_1299.field_6139 || type == class_1299.field_6067 || type == class_1299.field_6057 || type == class_1299.field_6075 || type == class_1299.field_6048 || type == class_1299.field_6074 || type == class_1299.field_17714 || type == class_1299.field_40116 || type == class_1299.field_64132 || type == class_1299.field_6096 || type == class_1299.field_54410 || type == class_1299.field_54416 || type == class_1299.field_54420 || type == class_1299.field_54412 || type == class_1299.field_54408 || type == class_1299.field_54422 || type == class_1299.field_54406 || type == class_1299.field_54562 || type == class_1299.field_54414 || type == class_1299.field_54419 || type == class_1299.field_54415 || type == class_1299.field_54421 || type == class_1299.field_54423 || type == class_1299.field_54407 || type == class_1299.field_54413 || type == class_1299.field_54409 || type == class_1299.field_54411 || type == class_1299.field_54563 || type == class_1299.field_54417 || type == class_1299.field_54418 || type == class_1299.field_63289 || type == class_1299.field_63290 || type == class_1299.field_59668;
   }

   public static float getTotalHealth(class_1309 target) {
      return target.method_6032() + target.method_6067();
   }

   public static int getPing(class_1657 player) {
      if (MeteorClient.mc.method_1562() == null) {
         return 0;
      } else {
         class_640 playerListEntry = MeteorClient.mc.method_1562().method_2871(player.method_5667());
         return playerListEntry == null ? 0 : playerListEntry.method_2959();
      }
   }

   public static class_1934 getGameMode(class_1657 player) {
      if (player == null) {
         return null;
      } else {
         class_640 playerListEntry = MeteorClient.mc.method_1562().method_2871(player.method_5667());
         return playerListEntry == null ? null : playerListEntry.method_2958();
      }
   }

   public static boolean isAboveWater(class_1297 entity) {
      class_2338.class_2339 blockPos = entity.method_24515().method_25503();
      int bottom = MeteorClient.mc.field_1687.method_31607();

      while(true) {
         if (blockPos.method_10264() > bottom) {
            class_2680 state = MeteorClient.mc.field_1687.method_8320(blockPos);
            if (!state.method_51366()) {
               class_3611 fluid = state.method_26227().method_15772();
               if (fluid != class_3612.field_15910 && fluid != class_3612.field_15909) {
                  blockPos.method_10100(0, -1, 0);
                  continue;
               }

               return true;
            }
         }

         return false;
      }
   }

   public static boolean isInCobweb(class_1297 entity) {
      return MeteorClient.mc.field_1687.method_29556(entity.method_5829()).anyMatch((state) -> state.method_27852(class_2246.field_10343));
   }

   public static boolean isInRenderDistance(class_1297 entity) {
      return entity == null ? false : isInRenderDistance(entity.method_23317(), entity.method_23321());
   }

   public static boolean isInRenderDistance(class_2586 entity) {
      return entity == null ? false : isInRenderDistance((double)entity.method_11016().method_10263(), (double)entity.method_11016().method_10260());
   }

   public static boolean isInRenderDistance(class_2338 pos) {
      return pos == null ? false : isInRenderDistance((double)pos.method_10263(), (double)pos.method_10260());
   }

   public static boolean isInRenderDistance(double posX, double posZ) {
      double x = Math.abs(MeteorClient.mc.field_1773.method_19418().method_71156().field_1352 - posX);
      double z = Math.abs(MeteorClient.mc.field_1773.method_19418().method_71156().field_1350 - posZ);
      double d = (double)(((Integer)MeteorClient.mc.field_1690.method_42503().method_41753() + 1) * 16);
      return x < d && z < d;
   }

   public static class_2338 getCityBlock(class_1657 player) {
      if (player == null) {
         return null;
      } else {
         double bestDistanceSquared = (double)36.0F;
         class_2350 bestDirection = null;

         for(class_2350 direction : DirectionAccessor.meteor$getHorizontal()) {
            testPos.method_10101(player.method_24515().method_10093(direction));
            class_2248 block = MeteorClient.mc.field_1687.method_8320(testPos).method_26204();
            if (block == class_2246.field_10540 || block == class_2246.field_22108 || block == class_2246.field_22423 || block == class_2246.field_23152 || block == class_2246.field_22109) {
               double testDistanceSquared = PlayerUtils.squaredDistanceTo((class_2338)testPos);
               if (testDistanceSquared < bestDistanceSquared) {
                  bestDistanceSquared = testDistanceSquared;
                  bestDirection = direction;
               }
            }
         }

         if (bestDirection == null) {
            return null;
         } else {
            return player.method_24515().method_10093(bestDirection);
         }
      }
   }

   public static String getName(class_1297 entity) {
      if (entity == null) {
         return null;
      } else {
         return entity instanceof class_1657 ? entity.method_5477().getString() : entity.method_5864().method_5897().getString();
      }
   }

   public static Color getColorFromDistance(class_1297 entity) {
      Color distanceColor = new Color(255, 255, 255);
      double distance = PlayerUtils.distanceToCamera(entity);
      double percent = distance / (double)60.0F;
      if (!(percent < (double)0.0F) && !(percent > (double)1.0F)) {
         int r;
         int g;
         if (percent < (double)0.5F) {
            r = 255;
            g = (int)((double)255.0F * percent / (double)0.5F);
         } else {
            g = 255;
            r = 255 - (int)((double)255.0F * (percent - (double)0.5F) / (double)0.5F);
         }

         distanceColor.set(r, g, 0, 255);
         return distanceColor;
      } else {
         distanceColor.set(0, 255, 0, 255);
         return distanceColor;
      }
   }

   public static Color getColorFromHealth(class_1297 entity, Color nonLivingEntityColor) {
      if (entity instanceof class_1309 living) {
         float health = living.method_6032();
         float maxHealth = living.method_6063();
         if (maxHealth <= 0.0F) {
            return new Color(nonLivingEntityColor);
         } else {
            double percent = (double)(health / maxHealth);
            percent = Math.max((double)0.0F, Math.min((double)1.0F, percent));
            int r;
            int g;
            if (percent < (double)0.5F) {
               r = 255;
               g = (int)((double)255.0F * (percent / (double)0.5F));
            } else {
               g = 255;
               r = 255 - (int)((double)255.0F * ((percent - (double)0.5F) / (double)0.5F));
            }

            return new Color(r, g, 0, 255);
         }
      } else {
         return new Color(nonLivingEntityColor);
      }
   }

   public static boolean intersectsWithEntity(class_238 box, Predicate<class_1297> predicate) {
      class_5577<class_1297> entityLookup = ((WorldAccessor)MeteorClient.mc.field_1687).meteor$getEntityLookup();
      if (!(entityLookup instanceof class_5578<class_1297> simpleEntityLookup)) {
         AtomicBoolean found = new AtomicBoolean(false);
         entityLookup.method_31807(box, (entityx) -> {
            if (!found.get() && predicate.test(entityx)) {
               found.set(true);
            }

         });
         return found.get();
      } else {
         class_5573<class_1297> cache = ((SimpleEntityLookupAccessor)simpleEntityLookup).<class_1297>meteor$getCache();
         LongSortedSet trackedPositions = ((SectionedEntityCacheAccessor)cache).meteor$getTrackedPositions();
         Long2ObjectMap<class_5572<class_1297>> trackingSections = ((SectionedEntityCacheAccessor)cache).meteor$getTrackingSections();
         int i = class_4076.method_32204(box.field_1323 - (double)2.0F);
         int j = class_4076.method_32204(box.field_1322 - (double)2.0F);
         int k = class_4076.method_32204(box.field_1321 - (double)2.0F);
         int l = class_4076.method_32204(box.field_1320 + (double)2.0F);
         int m = class_4076.method_32204(box.field_1325 + (double)2.0F);
         int n = class_4076.method_32204(box.field_1324 + (double)2.0F);

         for(int o = i; o <= l; ++o) {
            long p = class_4076.method_18685(o, 0, 0);
            long q = class_4076.method_18685(o, -1, -1);
            LongBidirectionalIterator longIterator = trackedPositions.subSet(p, q + 1L).iterator();

            while(longIterator.hasNext()) {
               long r = longIterator.nextLong();
               int s = class_4076.method_18689(r);
               int t = class_4076.method_18690(r);
               if (s >= j && s <= m && t >= k && t <= n) {
                  class_5572<class_1297> entityTrackingSection = (class_5572)trackingSections.get(r);
                  if (entityTrackingSection != null && entityTrackingSection.method_31768().method_31885()) {
                     for(class_1297 entity : ((EntityTrackingSectionAccessor)entityTrackingSection).meteor$getCollection()) {
                        if (entity.method_5829().method_994(box) && predicate.test(entity)) {
                           return true;
                        }
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   public static class_1299<?> getGroup(class_1297 entity) {
      return entity.method_5864();
   }

   public static boolean isOnAir(class_1297 entity) {
      return entity.method_73183().method_29546(entity.method_5829().method_1014((double)0.0625F).method_1012((double)0.0F, -0.55, (double)0.0F)).allMatch(class_4970.class_4971::method_26215);
   }
}
