package meteordevelopment.meteorclient.utils.misc;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1842;
import net.minecraft.class_1844;
import net.minecraft.class_1847;
import net.minecraft.class_6880;

public enum MyPotion {
   Swiftness(class_1847.field_9005, new class_1792[]{class_1802.field_8790, class_1802.field_8479}),
   SwiftnessLong(class_1847.field_8983, new class_1792[]{class_1802.field_8790, class_1802.field_8479, class_1802.field_8725}),
   SwiftnessStrong(class_1847.field_8966, new class_1792[]{class_1802.field_8790, class_1802.field_8479, class_1802.field_8601}),
   Slowness(class_1847.field_8996, new class_1792[]{class_1802.field_8790, class_1802.field_8479, class_1802.field_8711}),
   SlownessLong(class_1847.field_8989, new class_1792[]{class_1802.field_8790, class_1802.field_8479, class_1802.field_8711, class_1802.field_8725}),
   SlownessStrong(class_1847.field_8976, new class_1792[]{class_1802.field_8790, class_1802.field_8479, class_1802.field_8711, class_1802.field_8601}),
   JumpBoost(class_1847.field_8979, new class_1792[]{class_1802.field_8790, class_1802.field_8073}),
   JumpBoostLong(class_1847.field_8971, new class_1792[]{class_1802.field_8790, class_1802.field_8073, class_1802.field_8725}),
   JumpBoostStrong(class_1847.field_8998, new class_1792[]{class_1802.field_8790, class_1802.field_8073, class_1802.field_8601}),
   Strength(class_1847.field_8978, new class_1792[]{class_1802.field_8790, class_1802.field_8183}),
   StrengthLong(class_1847.field_8965, new class_1792[]{class_1802.field_8790, class_1802.field_8183, class_1802.field_8725}),
   StrengthStrong(class_1847.field_8993, new class_1792[]{class_1802.field_8790, class_1802.field_8183, class_1802.field_8601}),
   Healing(class_1847.field_8963, new class_1792[]{class_1802.field_8790, class_1802.field_8597}),
   HealingStrong(class_1847.field_8980, new class_1792[]{class_1802.field_8790, class_1802.field_8597, class_1802.field_8601}),
   Harming(class_1847.field_9004, new class_1792[]{class_1802.field_8790, class_1802.field_8597, class_1802.field_8711}),
   HarmingStrong(class_1847.field_8973, new class_1792[]{class_1802.field_8790, class_1802.field_8597, class_1802.field_8711, class_1802.field_8601}),
   Poison(class_1847.field_8982, new class_1792[]{class_1802.field_8790, class_1802.field_8680}),
   PoisonLong(class_1847.field_9002, new class_1792[]{class_1802.field_8790, class_1802.field_8680, class_1802.field_8725}),
   PoisonStrong(class_1847.field_8972, new class_1792[]{class_1802.field_8790, class_1802.field_8680, class_1802.field_8601}),
   Regeneration(class_1847.field_8986, new class_1792[]{class_1802.field_8790, class_1802.field_8070}),
   RegenerationLong(class_1847.field_9003, new class_1792[]{class_1802.field_8790, class_1802.field_8070, class_1802.field_8725}),
   RegenerationStrong(class_1847.field_8992, new class_1792[]{class_1802.field_8790, class_1802.field_8070, class_1802.field_8601}),
   FireResistance(class_1847.field_8987, new class_1792[]{class_1802.field_8790, class_1802.field_8135}),
   FireResistanceLong(class_1847.field_8969, new class_1792[]{class_1802.field_8790, class_1802.field_8135, class_1802.field_8725}),
   WaterBreathing(class_1847.field_8994, new class_1792[]{class_1802.field_8790, class_1802.field_8323}),
   WaterBreathingLong(class_1847.field_9001, new class_1792[]{class_1802.field_8790, class_1802.field_8323, class_1802.field_8725}),
   NightVision(class_1847.field_8968, new class_1792[]{class_1802.field_8790, class_1802.field_8071}),
   NightVisionLong(class_1847.field_8981, new class_1792[]{class_1802.field_8790, class_1802.field_8071, class_1802.field_8725}),
   Invisibility(class_1847.field_8997, new class_1792[]{class_1802.field_8790, class_1802.field_8071, class_1802.field_8711}),
   InvisibilityLong(class_1847.field_9000, new class_1792[]{class_1802.field_8790, class_1802.field_8071, class_1802.field_8711, class_1802.field_8725}),
   TurtleMaster(class_1847.field_8990, new class_1792[]{class_1802.field_8790, class_1802.field_8090}),
   TurtleMasterLong(class_1847.field_8988, new class_1792[]{class_1802.field_8790, class_1802.field_8090, class_1802.field_8725}),
   TurtleMasterStrong(class_1847.field_8977, new class_1792[]{class_1802.field_8790, class_1802.field_8090, class_1802.field_8601}),
   SlowFalling(class_1847.field_8974, new class_1792[]{class_1802.field_8790, class_1802.field_8614}),
   SlowFallingLong(class_1847.field_8964, new class_1792[]{class_1802.field_8790, class_1802.field_8614, class_1802.field_8725}),
   Weakness(class_1847.field_8975, new class_1792[]{class_1802.field_8711}),
   WeaknessLong(class_1847.field_8970, new class_1792[]{class_1802.field_8711, class_1802.field_8725});

   public final class_1799 potion;
   public final class_1792[] ingredients;

   private MyPotion(class_6880<class_1842> potion, class_1792... ingredients) {
      this.potion = class_1844.method_57400(class_1802.field_8574, potion);
      this.ingredients = ingredients;
   }

   // $FF: synthetic method
   private static MyPotion[] $values() {
      return new MyPotion[]{Swiftness, SwiftnessLong, SwiftnessStrong, Slowness, SlownessLong, SlownessStrong, JumpBoost, JumpBoostLong, JumpBoostStrong, Strength, StrengthLong, StrengthStrong, Healing, HealingStrong, Harming, HarmingStrong, Poison, PoisonLong, PoisonStrong, Regeneration, RegenerationLong, RegenerationStrong, FireResistance, FireResistanceLong, WaterBreathing, WaterBreathingLong, NightVision, NightVisionLong, Invisibility, InvisibilityLong, TurtleMaster, TurtleMasterLong, TurtleMasterStrong, SlowFalling, SlowFallingLong, Weakness, WeaknessLong};
   }
}
