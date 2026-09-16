package meteordevelopment.meteorclient.utils.entity;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.function.BiFunction;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.class_1280;
import net.minecraft.class_1282;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1887;
import net.minecraft.class_1893;
import net.minecraft.class_1922;
import net.minecraft.class_1927;
import net.minecraft.class_1934;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3483;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_5134;
import net.minecraft.class_6880;
import net.minecraft.class_8103;
import net.minecraft.class_9274;
import net.minecraft.class_9285;
import net.minecraft.class_9334;
import net.minecraft.class_9362;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2902.class_2903;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class DamageUtils {
   public static final RaycastFactory HIT_FACTORY = (context, blockPos) -> {
      class_2680 blockState = MeteorClient.mc.field_1687.method_8320(blockPos);
      return blockState.method_26204().method_9520() < 600.0F ? null : blockState.method_26220(MeteorClient.mc.field_1687, blockPos).method_1092(context.start(), context.end(), blockPos);
   };

   private DamageUtils() {
   }

   public static float crystalDamage(class_1309 target, class_243 targetPos, class_238 targetBox, class_243 explosionPos, RaycastFactory raycastFactory) {
      return explosionDamage(target, targetPos, targetBox, explosionPos, 12.0F, raycastFactory);
   }

   public static float bedDamage(class_1309 target, class_243 targetPos, class_238 targetBox, class_243 explosionPos, RaycastFactory raycastFactory) {
      return explosionDamage(target, targetPos, targetBox, explosionPos, 10.0F, raycastFactory);
   }

   public static float anchorDamage(class_1309 target, class_243 targetPos, class_238 targetBox, class_243 explosionPos, RaycastFactory raycastFactory) {
      return explosionDamage(target, targetPos, targetBox, explosionPos, 10.0F, raycastFactory);
   }

   public static float explosionDamage(class_1309 target, class_243 targetPos, class_238 targetBox, class_243 explosionPos, float power, RaycastFactory raycastFactory) {
      double modDistance = PlayerUtils.distance(targetPos.field_1352, targetPos.field_1351, targetPos.field_1350, explosionPos.field_1352, explosionPos.field_1351, explosionPos.field_1350);
      if (modDistance > (double)power) {
         return 0.0F;
      } else {
         double exposure = (double)getExposure(explosionPos, targetBox, raycastFactory);
         double impact = ((double)1.0F - modDistance / (double)power) * exposure;
         float damage = (float)((int)((impact * impact + impact) / (double)2.0F * (double)7.0F * (double)12.0F + (double)1.0F));
         return calculateReductions(damage, target, MeteorClient.mc.field_1687.method_48963().method_48807((class_1927)null));
      }
   }

   public static float crystalDamage(class_1309 target, class_243 crystal, boolean predictMovement, class_2338 obsidianPos) {
      return overridingExplosionDamage(target, crystal, 12.0F, predictMovement, obsidianPos, class_2246.field_10540.method_9564());
   }

   public static float crystalDamage(class_1309 target, class_243 crystal) {
      return explosionDamage(target, crystal, 12.0F, false);
   }

   public static float bedDamage(class_1309 target, class_243 bed) {
      return explosionDamage(target, bed, 10.0F, false);
   }

   public static float anchorDamage(class_1309 target, class_243 anchor) {
      return overridingExplosionDamage(target, anchor, 10.0F, false, class_2338.method_49638(anchor), class_2246.field_10124.method_9564());
   }

   private static float overridingExplosionDamage(class_1309 target, class_243 explosionPos, float power, boolean predictMovement, class_2338 overridePos, class_2680 overrideState) {
      return explosionDamage(target, explosionPos, power, predictMovement, getOverridingHitFactory(overridePos, overrideState));
   }

   private static float explosionDamage(class_1309 target, class_243 explosionPos, float power, boolean predictMovement) {
      return explosionDamage(target, explosionPos, power, predictMovement, HIT_FACTORY);
   }

   private static float explosionDamage(class_1309 target, class_243 explosionPos, float power, boolean predictMovement, RaycastFactory raycastFactory) {
      if (target == null) {
         return 0.0F;
      } else {
         if (target instanceof class_1657) {
            class_1657 player = (class_1657)target;
            if (EntityUtils.getGameMode(player) == class_1934.field_9220 && !(player instanceof FakePlayerEntity)) {
               return 0.0F;
            }
         }

         class_243 position = predictMovement ? target.method_73189().method_1019(target.method_18798()) : target.method_73189();
         class_238 box = target.method_5829();
         if (predictMovement) {
            box = box.method_997(target.method_18798());
         }

         return explosionDamage(target, position, box, explosionPos, power, raycastFactory);
      }
   }

   public static RaycastFactory getOverridingHitFactory(class_2338 overridePos, class_2680 overrideState) {
      return (context, blockPos) -> {
         class_2680 blockState;
         if (blockPos.equals(overridePos)) {
            blockState = overrideState;
         } else {
            blockState = MeteorClient.mc.field_1687.method_8320(blockPos);
            if (blockState.method_26204().method_9520() < 600.0F) {
               return null;
            }
         }

         return blockState.method_26220(MeteorClient.mc.field_1687, blockPos).method_1092(context.start(), context.end(), blockPos);
      };
   }

   public static float getAttackDamage(class_1309 attacker, class_1297 target) {
      float itemDamage = (float)attacker.method_45325(class_5134.field_23721);
      class_1282 var10000;
      if (attacker instanceof class_1657 player) {
         var10000 = MeteorClient.mc.field_1687.method_48963().method_48802(player);
      } else {
         var10000 = MeteorClient.mc.field_1687.method_48963().method_48812(attacker);
      }

      class_1282 damageSource = var10000;
      float damage = modifyAttackDamage(attacker, target, attacker.method_59958(), damageSource, itemDamage);
      return calculateReductions(damage, target, damageSource);
   }

   public static float getAttackDamage(class_1309 attacker, class_1297 target, class_1799 weapon) {
      class_1324 original = attacker.method_5996(class_5134.field_23721);
      class_1324 copy = new class_1324(class_5134.field_23721, (o) -> {
      });
      copy.method_6192(original.method_6201());

      for(class_1322 modifier : original.method_6195()) {
         copy.method_26835(modifier);
      }

      copy.method_6200(class_1792.field_8006);
      class_9285 attributeModifiers = (class_9285)weapon.method_58694(class_9334.field_49636);
      if (attributeModifiers != null) {
         attributeModifiers.method_57482(class_1304.field_6173, (entry, modifierx) -> {
            if (entry == class_5134.field_23721) {
               copy.method_55696(modifierx);
            }

         });
      }

      float itemDamage = (float)copy.method_6194();
      class_1282 var10000;
      if (attacker instanceof class_1657 player) {
         var10000 = MeteorClient.mc.field_1687.method_48963().method_48802(player);
      } else {
         var10000 = MeteorClient.mc.field_1687.method_48963().method_48812(attacker);
      }

      class_1282 damageSource = var10000;
      float damage = modifyAttackDamage(attacker, target, weapon, damageSource, itemDamage);
      return calculateReductions(damage, target, damageSource);
   }

   private static float modifyAttackDamage(class_1309 attacker, class_1297 target, class_1799 weapon, class_1282 damageSource, float damage) {
      Object2IntMap<class_6880<class_1887>> enchantments = new Object2IntOpenHashMap();
      Utils.getEnchantments(weapon, enchantments);
      float enchantDamage = 0.0F;
      int sharpness = Utils.getEnchantmentLevel(enchantments, class_1893.field_9118);
      if (sharpness > 0) {
         enchantDamage += 1.0F + 0.5F * (float)(sharpness - 1);
      }

      int baneOfArthropods = Utils.getEnchantmentLevel(enchantments, class_1893.field_9112);
      if (baneOfArthropods > 0 && target.method_5864().method_20210(class_3483.field_48285)) {
         enchantDamage += 2.5F * (float)baneOfArthropods;
      }

      int impaling = Utils.getEnchantmentLevel(enchantments, class_1893.field_9106);
      if (impaling > 0 && target.method_5864().method_20210(class_3483.field_48284)) {
         enchantDamage += 2.5F * (float)impaling;
      }

      int smite = Utils.getEnchantmentLevel(enchantments, class_1893.field_9123);
      if (smite > 0 && target.method_5864().method_20210(class_3483.field_49931)) {
         enchantDamage += 2.5F * (float)smite;
      }

      if (attacker instanceof class_1657 playerEntity) {
         float charge = playerEntity.method_7261(0.5F);
         damage *= 0.2F + charge * charge * 0.8F;
         enchantDamage *= charge;
         class_1792 var14 = weapon.method_7909();
         if (var14 instanceof class_9362 item) {
            float bonusDamage = item.method_58403(target, damage, damageSource);
            if (bonusDamage > 0.0F) {
               int density = Utils.getEnchantmentLevel(weapon, class_1893.field_50157);
               if (density > 0) {
                  bonusDamage += (float)((double)0.5F * attacker.field_6017);
               }

               damage += bonusDamage;
            }
         }

         if (charge > 0.9F && attacker.field_6017 > (double)0.0F && !attacker.method_24828() && !attacker.method_6101() && !attacker.method_5799() && !attacker.method_6059(class_1294.field_5919) && !attacker.method_5765()) {
            damage *= 1.5F;
         }
      }

      return damage + enchantDamage;
   }

   public static float fallDamage(class_1309 entity) {
      if (entity instanceof class_1657 player) {
         if (player.method_31549().field_7479) {
            return 0.0F;
         }
      }

      if (!entity.method_6059(class_1294.field_5906) && !entity.method_6059(class_1294.field_5902)) {
         int surface = MeteorClient.mc.field_1687.method_8500(entity.method_24515()).method_12032(class_2903.field_13197).method_12603(entity.method_31477() & 15, entity.method_31479() & 15);
         if (entity.method_31478() >= surface) {
            return fallDamageReductions(entity, surface);
         } else {
            class_3965 raycastResult = MeteorClient.mc.field_1687.method_17742(new class_3959(entity.method_73189(), new class_243(entity.method_23317(), (double)MeteorClient.mc.field_1687.method_31607(), entity.method_23321()), class_3960.field_17558, class_242.field_36338, entity));
            return raycastResult.method_17783() == class_240.field_1333 ? 0.0F : fallDamageReductions(entity, raycastResult.method_17777().method_10264());
         }
      } else {
         return 0.0F;
      }
   }

   private static float fallDamageReductions(class_1309 entity, int surface) {
      int fallHeight = (int)(entity.method_23318() - (double)surface + entity.field_6017 - (double)3.0F);
      class_1293 jumpBoostInstance = entity.method_6112(class_1294.field_5913);
      if (jumpBoostInstance != null) {
         fallHeight -= jumpBoostInstance.method_5578() + 1;
      }

      return calculateReductions((float)fallHeight, entity, MeteorClient.mc.field_1687.method_48963().method_48827());
   }

   public static float calculateReductions(float damage, class_1297 entity, class_1282 damageSource) {
      if (damageSource.method_5514()) {
         switch (MeteorClient.mc.field_1687.method_8407()) {
            case field_5805 -> damage = Math.min(damage / 2.0F + 1.0F, damage);
            case field_5807 -> damage *= 1.5F;
         }
      }

      if (entity instanceof class_1309 livingEntity) {
         damage = class_1280.method_5496(livingEntity, damage, damageSource, getArmor(livingEntity), (float)livingEntity.method_45325(class_5134.field_23725));
         damage = resistanceReduction(livingEntity, damage);
         damage = protectionReduction(livingEntity, damage, damageSource);
      }

      return Math.max(damage, 0.0F);
   }

   private static float getArmor(class_1309 entity) {
      return (float)Math.floor(entity.method_45325(class_5134.field_23724));
   }

   private static float protectionReduction(class_1309 player, float damage, class_1282 source) {
      if (source.method_48789(class_8103.field_42242)) {
         return damage;
      } else {
         int damageProtection = 0;

         for(class_1304 slot : class_9274.field_49224) {
            class_1799 stack = player.method_6118(slot);
            Object2IntMap<class_6880<class_1887>> enchantments = new Object2IntOpenHashMap();
            Utils.getEnchantments(stack, enchantments);
            int protection = Utils.getEnchantmentLevel(enchantments, class_1893.field_9111);
            if (protection > 0) {
               damageProtection += protection;
            }

            int fireProtection = Utils.getEnchantmentLevel(enchantments, class_1893.field_9095);
            if (fireProtection > 0 && source.method_48789(class_8103.field_42246)) {
               damageProtection += 2 * fireProtection;
            }

            int blastProtection = Utils.getEnchantmentLevel(enchantments, class_1893.field_9107);
            if (blastProtection > 0 && source.method_48789(class_8103.field_42249)) {
               damageProtection += 2 * blastProtection;
            }

            int projectileProtection = Utils.getEnchantmentLevel(enchantments, class_1893.field_9096);
            if (projectileProtection > 0 && source.method_48789(class_8103.field_42247)) {
               damageProtection += 2 * projectileProtection;
            }

            int featherFalling = Utils.getEnchantmentLevel(enchantments, class_1893.field_9129);
            if (featherFalling > 0 && source.method_48789(class_8103.field_42250)) {
               damageProtection += 3 * featherFalling;
            }
         }

         return class_1280.method_5497(damage, (float)damageProtection);
      }
   }

   private static float resistanceReduction(class_1309 player, float damage) {
      class_1293 resistance = player.method_6112(class_1294.field_5907);
      if (resistance != null) {
         int lvl = resistance.method_5578() + 1;
         damage *= 1.0F - (float)lvl * 0.2F;
      }

      return Math.max(damage, 0.0F);
   }

   private static float getExposure(class_243 source, class_238 box, RaycastFactory raycastFactory) {
      double xDiff = box.field_1320 - box.field_1323;
      double yDiff = box.field_1325 - box.field_1322;
      double zDiff = box.field_1324 - box.field_1321;
      double xStep = (double)1.0F / (xDiff * (double)2.0F + (double)1.0F);
      double yStep = (double)1.0F / (yDiff * (double)2.0F + (double)1.0F);
      double zStep = (double)1.0F / (zDiff * (double)2.0F + (double)1.0F);
      if (xStep > (double)0.0F && yStep > (double)0.0F && zStep > (double)0.0F) {
         int misses = 0;
         int hits = 0;
         double xOffset = ((double)1.0F - Math.floor((double)1.0F / xStep) * xStep) * (double)0.5F;
         double zOffset = ((double)1.0F - Math.floor((double)1.0F / zStep) * zStep) * (double)0.5F;
         xStep *= xDiff;
         yStep *= yDiff;
         zStep *= zDiff;
         double startX = box.field_1323 + xOffset;
         double startY = box.field_1322;
         double startZ = box.field_1321 + zOffset;
         double endX = box.field_1320 + xOffset;
         double endY = box.field_1325;
         double endZ = box.field_1324 + zOffset;

         for(double x = startX; x <= endX; x += xStep) {
            for(double y = startY; y <= endY; y += yStep) {
               for(double z = startZ; z <= endZ; z += zStep) {
                  class_243 position = new class_243(x, y, z);
                  if (raycast(new ExposureRaycastContext(position, source), raycastFactory) == null) {
                     ++misses;
                  }

                  ++hits;
               }
            }
         }

         return (float)misses / (float)hits;
      } else {
         return 0.0F;
      }
   }

   private static class_3965 raycast(ExposureRaycastContext context, RaycastFactory raycastFactory) {
      return (class_3965)class_1922.method_17744(context.start, context.end, context, raycastFactory, (ctx) -> null);
   }

   public static record ExposureRaycastContext(class_243 start, class_243 end) {
   }

   @FunctionalInterface
   public interface RaycastFactory extends BiFunction<ExposureRaycastContext, class_2338, class_3965> {
   }
}
