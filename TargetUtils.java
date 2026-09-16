package meteordevelopment.meteorclient.utils.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerManager;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1934;

public class TargetUtils {
   private static final List<class_1297> ENTITIES = new ArrayList();

   private TargetUtils() {
   }

   @Nullable
   public static class_1297 get(Predicate<class_1297> isGood, SortPriority sortPriority) {
      ENTITIES.clear();
      getList(ENTITIES, isGood, sortPriority, 1);
      return !ENTITIES.isEmpty() ? (class_1297)ENTITIES.getFirst() : null;
   }

   public static void getList(List<class_1297> targetList, Predicate<class_1297> isGood, SortPriority sortPriority, int maxCount) {
      targetList.clear();

      for(class_1297 entity : MeteorClient.mc.field_1687.method_18112()) {
         if (entity != null && isGood.test(entity)) {
            targetList.add(entity);
         }
      }

      FakePlayerManager.forEach((fp) -> {
         if (fp != null && isGood.test(fp)) {
            targetList.add(fp);
         }

      });
      targetList.sort(sortPriority);
      if (targetList.size() > maxCount) {
         targetList.subList(maxCount, targetList.size()).clear();
      }

   }

   @Nullable
   public static class_1657 getPlayerTarget(double range, SortPriority priority) {
      return !Utils.canUpdate() ? null : (class_1657)get((entity) -> {
         if (entity instanceof class_1657 player) {
            if (entity != MeteorClient.mc.field_1724) {
               if (!player.method_29504() && !(player.method_6032() <= 0.0F)) {
                  if (!PlayerUtils.isWithin(entity, range)) {
                     return false;
                  }

                  if (!Friends.get().shouldAttack(player)) {
                     return false;
                  }

                  if (entity instanceof FakePlayerEntity) {
                     FakePlayerEntity fakePlayer = (FakePlayerEntity)entity;
                     return !fakePlayer.noHit;
                  }

                  return EntityUtils.getGameMode(player) == class_1934.field_9215;
               }

               return false;
            }
         }

         return false;
      }, priority);
   }

   public static boolean isBadTarget(class_1657 target, double range) {
      if (target == null) {
         return true;
      } else {
         return !PlayerUtils.isWithin((class_1297)target, range) || !target.method_5805() || target.method_29504() || target.method_6032() <= 0.0F;
      }
   }
}
