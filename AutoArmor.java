package meteordevelopment.meteorclient.systems.modules.combat;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnchantmentListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.ChestSwap;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_10192;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1887;
import net.minecraft.class_1893;
import net.minecraft.class_3489;
import net.minecraft.class_5134;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_9285;
import net.minecraft.class_9334;

public class AutoArmor extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Protection> preferredProtection;
   private final Setting<Integer> delay;
   private final Setting<Set<class_5321<class_1887>>> avoidedEnchantments;
   private final Setting<Boolean> blastLeggings;
   private final Setting<Boolean> antiBreak;
   private final Setting<Boolean> ignoreElytra;
   private final Object2IntMap<class_6880<class_1887>> enchantments;
   private final ArmorPiece[] armorPieces;
   private final ArmorPiece helmet;
   private final ArmorPiece chestplate;
   private final ArmorPiece leggings;
   private final ArmorPiece boots;
   private int timer;

   public AutoArmor() {
      super(Categories.Combat, "auto-armor", "Automatically equips armor.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.preferredProtection = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("preferred-protection")).description("Which type of protection to prefer.")).defaultValue(AutoArmor.Protection.Protection)).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("swap-delay")).description("The delay between equipping armor pieces.")).defaultValue(1)).min(0).sliderMax(5).build());
      this.avoidedEnchantments = this.sgGeneral.add(((EnchantmentListSetting.Builder)((EnchantmentListSetting.Builder)(new EnchantmentListSetting.Builder()).name("avoided-enchantments")).description("Enchantments that should be avoided.")).defaultValue(class_1893.field_9113, class_1893.field_9122).build());
      this.blastLeggings = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("blast-prot-leggings")).description("Uses blast protection for leggings regardless of preferred protection.")).defaultValue(true)).build());
      this.antiBreak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-break")).description("Takes off armor if it is about to break.")).defaultValue(false)).build());
      this.ignoreElytra = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-elytra")).description("Will not replace your elytra if you have it equipped.")).defaultValue(true)).build());
      this.enchantments = new Object2IntOpenHashMap();
      this.armorPieces = new ArmorPiece[4];
      this.helmet = new ArmorPiece(class_1304.field_6169);
      this.chestplate = new ArmorPiece(class_1304.field_6174);
      this.leggings = new ArmorPiece(class_1304.field_6172);
      this.boots = new ArmorPiece(class_1304.field_6166);
      this.armorPieces[0] = this.helmet;
      this.armorPieces[1] = this.chestplate;
      this.armorPieces[2] = this.leggings;
      this.armorPieces[3] = this.boots;
   }

   public void onActivate() {
      this.timer = 0;
   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      if (this.timer > 0) {
         --this.timer;
      } else {
         for(ArmorPiece armorPiece : this.armorPieces) {
            armorPiece.reset();
         }

         for(int i = 0; i < this.mc.field_1724.method_31548().method_67533().size(); ++i) {
            class_1799 itemStack = this.mc.field_1724.method_31548().method_5438(i);
            if (!itemStack.method_7960() && this.isArmor(itemStack) && (!(Boolean)this.antiBreak.get() || !itemStack.method_7963() || itemStack.method_7936() - itemStack.method_7919() > 10)) {
               Utils.getEnchantments(itemStack, this.enchantments);
               if (!this.hasAvoidedEnchantment()) {
                  switch (this.getItemSlotId(itemStack)) {
                     case 0:
                        this.boots.add(itemStack, i);
                        break;
                     case 1:
                        this.leggings.add(itemStack, i);
                        break;
                     case 2:
                        this.chestplate.add(itemStack, i);
                        break;
                     case 3:
                        this.helmet.add(itemStack, i);
                  }
               }
            }
         }

         for(ArmorPiece armorPiece : this.armorPieces) {
            armorPiece.calculate();
         }

         Arrays.sort(this.armorPieces, Comparator.comparingInt(ArmorPiece::getSortScore));

         for(ArmorPiece armorPiece : this.armorPieces) {
            armorPiece.apply();
         }

      }
   }

   private boolean hasAvoidedEnchantment() {
      ObjectIterator var1 = this.enchantments.keySet().iterator();

      while(var1.hasNext()) {
         class_6880<class_1887> enchantment = (class_6880)var1.next();
         Set var10001 = this.avoidedEnchantments.get();
         Objects.requireNonNull(var10001);
         if (enchantment.method_40224(var10001::contains)) {
            return true;
         }
      }

      return false;
   }

   private int getItemSlotId(class_1799 itemStack) {
      return itemStack.method_57826(class_9334.field_54197) ? 2 : ((class_10192)itemStack.method_58694(class_9334.field_54196)).comp_3174().method_5927();
   }

   private int getScore(class_1799 itemStack) {
      if (itemStack.method_7960()) {
         return 0;
      } else {
         int score = 0;
         class_5321<class_1887> protection = (this.preferredProtection.get()).enchantment;
         if (this.isArmor(itemStack) && (Boolean)this.blastLeggings.get() && this.getItemSlotId(itemStack) == 1) {
            protection = class_1893.field_9107;
         }

         score += 3 * Utils.getEnchantmentLevel(this.enchantments, protection);
         score += Utils.getEnchantmentLevel(this.enchantments, class_1893.field_9111);
         score += Utils.getEnchantmentLevel(this.enchantments, class_1893.field_9107);
         score += Utils.getEnchantmentLevel(this.enchantments, class_1893.field_9095);
         score += Utils.getEnchantmentLevel(this.enchantments, class_1893.field_9096);
         score += Utils.getEnchantmentLevel(this.enchantments, class_1893.field_9119);
         score += 2 * Utils.getEnchantmentLevel(this.enchantments, class_1893.field_9101);
         if (itemStack.method_57826(class_9334.field_49636)) {
            class_9285 component = (class_9285)itemStack.method_58694(class_9334.field_49636);

            for(class_9285.class_9287 modifier : component.comp_2393()) {
               if (modifier.comp_2395() == class_5134.field_23724 || modifier.comp_2395() == class_5134.field_23725) {
                  double e = modifier.comp_2396().comp_2449();
                  int var10001;
                  switch (modifier.comp_2396().comp_2450()) {
                     case field_6328 -> var10001 = (int)e;
                     case field_6330 -> var10001 = (int)(e * this.mc.field_1724.method_45326(modifier.comp_2395()));
                     case field_6331 -> var10001 = (int)(e * (double)score);
                     default -> throw new MatchException((String)null, (Throwable)null);
                  }

                  score += var10001;
               }
            }
         }

         return score;
      }
   }

   private boolean cannotSwap() {
      return this.timer > 0;
   }

   private void swap(int from, int armorSlotId) {
      InvUtils.move().from(from).toArmor(armorSlotId);
      this.timer = (Integer)this.delay.get();
   }

   private void moveToEmpty(int armorSlotId) {
      for(int i = 0; i < this.mc.field_1724.method_31548().method_67533().size(); ++i) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_7960()) {
            InvUtils.move().fromArmor(armorSlotId).to(i);
            this.timer = (Integer)this.delay.get();
            break;
         }
      }

   }

   private boolean isArmor(class_1799 itemStack) {
      return itemStack.method_31573(class_3489.field_48294) || itemStack.method_31573(class_3489.field_48295) || itemStack.method_31573(class_3489.field_48296) || itemStack.method_31573(class_3489.field_48297);
   }

   public static enum Protection {
      Protection(class_1893.field_9111),
      BlastProtection(class_1893.field_9107),
      FireProtection(class_1893.field_9095),
      ProjectileProtection(class_1893.field_9096);

      private final class_5321<class_1887> enchantment;

      private Protection(class_5321<class_1887> enchantment) {
         this.enchantment = enchantment;
      }

      // $FF: synthetic method
      private static Protection[] $values() {
         return new Protection[]{Protection, BlastProtection, FireProtection, ProjectileProtection};
      }
   }

   private class ArmorPiece {
      private final class_1304 slot;
      private int bestSlot;
      private int bestScore;
      private int score;
      private int durability;

      public ArmorPiece(class_1304 slot) {
         this.slot = slot;
      }

      public void reset() {
         this.bestSlot = -1;
         this.bestScore = -1;
         this.score = -1;
         this.durability = Integer.MAX_VALUE;
      }

      public void add(class_1799 itemStack, int slot) {
         int score = AutoArmor.this.getScore(itemStack);
         if (score > this.bestScore) {
            this.bestScore = score;
            this.bestSlot = slot;
         }

      }

      public void calculate() {
         if (!AutoArmor.this.cannotSwap()) {
            class_1799 itemStack = AutoArmor.this.mc.field_1724.method_6118(this.slot);
            if (((Boolean)AutoArmor.this.ignoreElytra.get() || Modules.get().isActive(ChestSwap.class)) && itemStack.method_7909() == class_1802.field_8833) {
               this.score = Integer.MAX_VALUE;
            } else {
               Utils.getEnchantments(itemStack, AutoArmor.this.enchantments);
               if (AutoArmor.this.enchantments.containsKey(class_1893.field_9113)) {
                  this.score = Integer.MAX_VALUE;
               } else {
                  this.score = AutoArmor.this.getScore(itemStack);
                  this.score = this.decreaseScoreByAvoidedEnchantments(this.score);
                  this.score = this.applyAntiBreakScore(this.score, itemStack);
                  if (!itemStack.method_7960()) {
                     this.durability = itemStack.method_7936() - itemStack.method_7919();
                  }

               }
            }
         }
      }

      public int getSortScore() {
         return (Boolean)AutoArmor.this.antiBreak.get() && this.durability <= 10 ? -1 : this.bestScore;
      }

      public void apply() {
         if (!AutoArmor.this.cannotSwap() && this.score != Integer.MAX_VALUE) {
            if (this.bestScore > this.score) {
               AutoArmor.this.swap(this.bestSlot, this.slot.method_5927());
            } else if ((Boolean)AutoArmor.this.antiBreak.get() && this.durability <= 10) {
               AutoArmor.this.moveToEmpty(this.slot.method_5927());
            }

         }
      }

      private int decreaseScoreByAvoidedEnchantments(int score) {
         for(class_5321<class_1887> enchantment : AutoArmor.this.avoidedEnchantments.get()) {
            score -= 2 * AutoArmor.this.enchantments.getInt(enchantment);
         }

         return score;
      }

      private int applyAntiBreakScore(int score, class_1799 itemStack) {
         return (Boolean)AutoArmor.this.antiBreak.get() && itemStack.method_7963() && itemStack.method_7936() - itemStack.method_7919() <= 10 ? -1 : score;
      }
   }
}
