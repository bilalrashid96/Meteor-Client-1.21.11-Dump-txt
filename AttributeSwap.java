package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.entity.player.DoAttackEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1743;
import net.minecraft.class_1799;
import net.minecraft.class_1835;
import net.minecraft.class_1893;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3483;
import net.minecraft.class_3489;
import net.minecraft.class_5134;
import net.minecraft.class_9274;
import net.minecraft.class_9362;
import net.minecraft.class_239.class_240;

public class AttributeSwap extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgSwappingOptions;
   private final SettingGroup sgSwordEnchants;
   private final SettingGroup sgMaceEnchants;
   private final SettingGroup sgSpearEnchants;
   private final SettingGroup sgOtherEnchants;
   private final SettingGroup sgWeapon;
   private final Setting<Mode> mode;
   private final Setting<Integer> targetSlot;
   private final Setting<Boolean> swapOnMiss;
   private final Setting<Boolean> swapBack;
   private final Setting<Integer> swapBackDelay;
   private final Setting<Boolean> smartShieldBreak;
   private final Setting<Boolean> smartDurability;
   private final Setting<Boolean> swordSwapping;
   private final Setting<Boolean> maceSwapping;
   private final Setting<Boolean> spearSwapping;
   private final Setting<Boolean> otherSwapping;
   private final Setting<Boolean> enchantFireAspect;
   private final Setting<Boolean> enchantLooting;
   private final Setting<Boolean> enchantSharpness;
   private final Setting<Boolean> enchantSmite;
   private final Setting<Boolean> enchantBaneOfArthropods;
   private final Setting<Boolean> enchantSweepingEdge;
   private final Setting<Boolean> regularMace;
   private final Setting<Boolean> enchantDensity;
   private final Setting<Boolean> enchantBreach;
   private final Setting<Boolean> enchantWindBurst;
   private final Setting<Boolean> enchantImpaling;
   private final Setting<Boolean> enchantLunge;
   private final Setting<Boolean> spearHitbox;
   private final Setting<Boolean> excludeLungeFromHitbox;
   private final Setting<Boolean> onlyOnWeapon;
   private final Setting<Boolean> sword;
   private final Setting<Boolean> axe;
   private final Setting<Boolean> pickaxe;
   private final Setting<Boolean> shovel;
   private final Setting<Boolean> hoe;
   private final Setting<Boolean> mace;
   private final Setting<Boolean> trident;
   private int backTimer;
   private boolean awaitingBack;

   public AttributeSwap() {
      super(Categories.Combat, "attribute-swap", "Swaps to a target slot when you attack.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgSwappingOptions = this.settings.createGroup("Swapping Options");
      this.sgSwordEnchants = this.settings.createGroup("Sword Enchants");
      this.sgMaceEnchants = this.settings.createGroup("Mace Enchants");
      this.sgSpearEnchants = this.settings.createGroup("Spear Enchants");
      this.sgOtherEnchants = this.settings.createGroup("Other Enchants");
      this.sgWeapon = this.settings.createGroup("Weapon Options");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The mode to use.")).defaultValue(AttributeSwap.Mode.Simple)).build());
      this.targetSlot = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("target-slot")).description("Hotbar slot to swap to (1-9).")).defaultValue(1)).range(1, 9).visible(() -> this.mode.get() == AttributeSwap.Mode.Simple)).build());
      this.swapOnMiss = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swap-on-miss")).description("Whether to swap on a missed attack. Useful for quickly lunging with spears.")).defaultValue(false)).visible(() -> this.mode.get() == AttributeSwap.Mode.Simple)).build());
      this.swapBack = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swap-back")).description("Swap back to the original slot after a delay.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("swap-back-delay")).description("Delay in ticks before swapping back.")).defaultValue(2)).min(0).max(100).sliderRange(0, 20);
      Setting var10003 = this.swapBack;
      Objects.requireNonNull(var10003);
      this.swapBackDelay = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.smartShieldBreak = this.sgSwappingOptions.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("shield-breaker")).description("Automatically swaps to an axe if the target is blocking.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart)).build());
      this.smartDurability = this.sgSwappingOptions.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("durability-saver")).description("Swaps to a non-damageable item to save durability on the main weapon.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart)).build());
      this.swordSwapping = this.sgSwappingOptions.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sword-swapping")).description("Enables smart swapping for sword enchantments.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart)).build());
      this.maceSwapping = this.sgSwappingOptions.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("mace-swapping")).description("Enables smart swapping for mace enchantments.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart)).build());
      this.spearSwapping = this.sgSwappingOptions.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("spear-swapping")).description("Enables smart swapping for spear enchantments.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart)).build());
      this.otherSwapping = this.sgSwappingOptions.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("other-swapping")).description("Enables smart swapping for other enchantments like Impaling.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart)).build());
      this.enchantFireAspect = this.sgSwordEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fire-aspect")).description("Swaps to an item with Fire Aspect to set the target on fire, if target isn't already on fire")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.swordSwapping.get())).build());
      this.enchantLooting = this.sgSwordEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("looting")).description("Swaps to an item with Looting for better drops or more experience. Only prefers for mobs (but fire aspect is priority)")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.swordSwapping.get())).build());
      this.enchantSharpness = this.sgSwordEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sharpness")).description("Swaps to an item with Sharpness for increased damage against all entities.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.swordSwapping.get())).build());
      this.enchantSmite = this.sgSwordEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smite")).description("Swaps to an item with Smite for increased damage against undead mobs.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.swordSwapping.get())).build());
      this.enchantBaneOfArthropods = this.sgSwordEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("bane-of-arthropods")).description("Swaps to an item with Bane of Arthropods for increased damage against arthropods.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.swordSwapping.get())).build());
      this.enchantSweepingEdge = this.sgSwordEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sweeping-edge")).description("Swaps to an item with Sweeping Edge for increased sweeping attack damage.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.swordSwapping.get())).build());
      this.regularMace = this.sgMaceEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("regular-mace")).description("Swaps to a regular Mace when falling if no better option is available.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.maceSwapping.get())).build());
      this.enchantDensity = this.sgMaceEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("density")).description("Swaps to a Mace with Density to deal increased damage when falling.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.maceSwapping.get())).build());
      this.enchantBreach = this.sgMaceEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("breach")).description("Swaps to a Mace with Breach to reduce the target's armor effectiveness.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.maceSwapping.get())).build());
      this.enchantWindBurst = this.sgMaceEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("wind-burst")).description("Swaps to a Mace with Wind Burst to launch up when hitting while falling.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.maceSwapping.get())).build());
      this.enchantImpaling = this.sgOtherEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("impaling")).description("Swaps to an item with Impaling for increased damage against aquatic mobs.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.otherSwapping.get())).build());
      this.enchantLunge = this.sgSpearEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("lunge")).description("Swaps to a spear with Lunge for traveling.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.spearSwapping.get())).build());
      this.spearHitbox = this.sgSpearEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hitbox")).description("Swaps to a spear for extended reach when target is far.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.spearSwapping.get())).build());
      this.excludeLungeFromHitbox = this.sgSpearEnchants.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("exclude-lunge-from-hitbox")).description("Don't use lunge-enchanted spears for hitbox extension.")).defaultValue(true)).visible(() -> this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.spearSwapping.get() && (Boolean)this.spearHitbox.get())).build());
      this.onlyOnWeapon = this.sgWeapon.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-weapon")).description("Only swaps when holding a selected weapon in hand.")).defaultValue(false)).build());
      var10001 = this.sgWeapon;
      BoolSetting.Builder var8 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sword")).description("Works while holding a sword.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.sword = var10001.add(((BoolSetting.Builder)var8.visible(var10003::get)).build());
      var10001 = this.sgWeapon;
      var8 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("axe")).description("Works while holding an axe.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.axe = var10001.add(((BoolSetting.Builder)var8.visible(var10003::get)).build());
      var10001 = this.sgWeapon;
      var8 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pickaxe")).description("Works while holding a pickaxe.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.pickaxe = var10001.add(((BoolSetting.Builder)var8.visible(var10003::get)).build());
      var10001 = this.sgWeapon;
      var8 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("shovel")).description("Works while holding a shovel.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.shovel = var10001.add(((BoolSetting.Builder)var8.visible(var10003::get)).build());
      var10001 = this.sgWeapon;
      var8 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hoe")).description("Works while holding a hoe.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.hoe = var10001.add(((BoolSetting.Builder)var8.visible(var10003::get)).build());
      var10001 = this.sgWeapon;
      var8 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("mace")).description("Works while holding a mace.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.mace = var10001.add(((BoolSetting.Builder)var8.visible(var10003::get)).build());
      var10001 = this.sgWeapon;
      var8 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("trident")).description("Works while holding a trident.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.trident = var10001.add(((BoolSetting.Builder)var8.visible(var10003::get)).build());
   }

   public void onDeactivate() {
      this.backTimer = 0;
      this.awaitingBack = false;
   }

   @EventHandler
   private void onAttack(DoAttackEvent event) {
      if (this.mc.field_1765.method_17783() != class_240.field_1332 && this.canSwapByWeapon()) {
         if (this.mode.get() == AttributeSwap.Mode.Smart && (Boolean)this.spearSwapping.get()) {
            if ((Boolean)this.spearHitbox.get()) {
               class_1297 target = this.getTargetEntity();
               if (target != null) {
                  if ((double)this.mc.field_1724.method_5739(target) <= this.mc.field_1724.method_55755() + (double)0.5F) {
                     return;
                  }

                  int spearSlot = this.getSmartSpearSlot(false);
                  if (spearSlot != -1) {
                     this.doSwap(spearSlot);
                     return;
                  }
               }
            }

            if ((Boolean)this.enchantLunge.get()) {
               int lungeSlot = this.getSmartSpearSlot(true);
               if (lungeSlot != -1) {
                  this.doSwap(lungeSlot);
                  return;
               }
            }
         }

         if (this.mode.get() != AttributeSwap.Mode.Smart && (Boolean)this.swapOnMiss.get()) {
            this.doSwap((Integer)this.targetSlot.get() - 1);
         }
      }
   }

   @EventHandler
   private void onAttackEntity(AttackEntityEvent event) {
      if (this.canSwapByWeapon() && (this.mode.get() != AttributeSwap.Mode.Simple || !(Boolean)this.swapOnMiss.get())) {
         this.performSwap(event.entity);
      }
   }

   private void performSwap(class_1297 target) {
      if (!this.awaitingBack) {
         if (this.mode.get() == AttributeSwap.Mode.Simple) {
            this.doSwap((Integer)this.targetSlot.get() - 1);
         } else {
            this.doSwap(this.getSmartSlot(target));
         }

      }
   }

   private void doSwap(int slotIndex) {
      if (!this.awaitingBack) {
         if (slotIndex >= 0 && slotIndex <= 8) {
            if (slotIndex != this.mc.field_1724.method_31548().method_67532()) {
               if (InvUtils.swap(slotIndex, (Boolean)this.swapBack.get())) {
                  this.awaitingBack = (Boolean)this.swapBack.get();
                  if (this.awaitingBack) {
                     this.backTimer = (Integer)this.swapBackDelay.get();
                  }

               }
            }
         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.awaitingBack) {
         if (this.backTimer-- <= 0) {
            InvUtils.swapBack();
            this.awaitingBack = false;
         }
      }
   }

   private boolean canSwapByWeapon() {
      return !(Boolean)this.onlyOnWeapon.get() ? true : InvUtils.testInMainHand((Predicate)((item) -> (Boolean)this.sword.get() && item.method_31573(class_3489.field_42611) || (Boolean)this.axe.get() && item.method_31573(class_3489.field_42612) || (Boolean)this.pickaxe.get() && item.method_31573(class_3489.field_42614) || (Boolean)this.shovel.get() && item.method_31573(class_3489.field_42615) || (Boolean)this.hoe.get() && item.method_31573(class_3489.field_42613) || (Boolean)this.mace.get() && item.method_7909() instanceof class_9362 || (Boolean)this.trident.get() && item.method_7909() instanceof class_1835));
   }

   private int getSmartSlot(class_1297 target) {
      class_1799 currentStack = this.mc.field_1724.method_6047();
      if (target != null && (Boolean)this.smartShieldBreak.get() && target instanceof class_1309 living) {
         if (living.method_6039()) {
            if (currentStack.method_7909() instanceof class_1743) {
               return -1;
            }

            int axeSlot = InvUtils.findInHotbar((Predicate)((item) -> item.method_7909() instanceof class_1743)).slot();
            if (axeSlot != -1) {
               return axeSlot;
            }
         }
      }

      boolean isFalling = this.mc.field_1724.field_6017 > (double)1.5F;
      boolean durability = (Boolean)this.smartDurability.get();
      boolean isLiving = target instanceof class_1309;
      boolean isPlayer = target instanceof class_1657;
      boolean isOnFire = target != null && target.method_5809();
      boolean isUndead = target != null && target.method_5864().method_20210(class_3483.field_49931);
      boolean isArthropod = target != null && target.method_5864().method_20210(class_3483.field_48285);
      boolean isAquatic = target != null && target.method_5864().method_20210(class_3483.field_48284);
      boolean hasFireResistance = isLiving && (((class_1309)target).method_6059(class_1294.field_5918) || this.hasFireProtectionArmor((class_1309)target));
      double armor = isLiving ? ((class_1309)target).method_45325(class_5134.field_23724) : (double)0.0F;
      float health = isLiving ? ((class_1309)target).method_6032() : 0.0F;
      int bestSlot = -1;
      double bestScore = this.getItemScore(currentStack, isFalling, durability, isLiving, isPlayer, isOnFire, hasFireResistance, isUndead, isArthropod, isAquatic, armor, health);

      for(int i = 0; i < 9; ++i) {
         if (i != this.mc.field_1724.method_31548().method_67532()) {
            class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
            if (!stack.method_7960() || durability) {
               double score = this.getItemScore(stack, isFalling, durability, isLiving, isPlayer, isOnFire, hasFireResistance, isUndead, isArthropod, isAquatic, armor, health);
               if (score > bestScore) {
                  bestScore = score;
                  bestSlot = i;
               }
            }
         }
      }

      return bestSlot;
   }

   private int getSmartSpearSlot(boolean requireLunge) {
      for(int i = 0; i < 9; ++i) {
         if (i != this.mc.field_1724.method_31548().method_67532()) {
            class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
            if (stack.method_31573(class_3489.field_63257)) {
               boolean hasLunge = Utils.getEnchantmentLevel(stack, class_1893.field_63420) > 0;
               if ((!requireLunge || hasLunge) && (requireLunge || !(Boolean)this.excludeLungeFromHitbox.get() || !hasLunge)) {
                  return i;
               }
            }
         }
      }

      return -1;
   }

   private class_1297 getTargetEntity() {
      double maxDistance = (double)7.0F;
      class_243 start = this.mc.field_1724.method_5836(1.0F);
      class_243 look = this.mc.field_1724.method_5828(1.0F);
      class_243 end = start.method_1019(look.method_1021(maxDistance));
      class_238 box = this.mc.field_1724.method_5829().method_18804(look.method_1021(maxDistance)).method_1014((double)1.0F);
      class_1297 target = null;
      double closestDistance = maxDistance * maxDistance;

      for(class_1297 entity : this.mc.field_1687.method_8333(this.mc.field_1724, box, (e) -> !e.method_7325() && e.method_5863())) {
         class_238 expandedBox = entity.method_5829().method_1014(0.15);
         if (expandedBox.method_992(start, end).isPresent()) {
            double distSq = start.method_1028(entity.method_23317(), entity.method_23318(), entity.method_23321());
            if (distSq < closestDistance) {
               closestDistance = distSq;
               target = entity;
            }
         }
      }

      return target;
   }

   private double getItemScore(class_1799 stack, boolean isFalling, boolean durability, boolean isLiving, boolean isPlayer, boolean isOnFire, boolean hasFireResistance, boolean isUndead, boolean isArthropod, boolean isAquatic, double armor, float health) {
      double score = (double)0.0F;
      if (durability) {
         score += this.getDurabilityScore(stack);
      }

      if (stack.method_7960()) {
         return score;
      } else {
         score += this.getCombatScore(stack, isFalling, isLiving, isPlayer, isOnFire, hasFireResistance, isUndead, isArthropod, isAquatic, armor, health);
         return score;
      }
   }

   private double getDurabilityScore(class_1799 stack) {
      if (!stack.method_7963()) {
         return (double)4.0F;
      } else {
         int unbreaking = Utils.getEnchantmentLevel(stack, class_1893.field_9119);
         return unbreaking > 0 ? (double)unbreaking * 0.05 : (double)0.0F;
      }
   }

   private double getCombatScore(class_1799 stack, boolean isFalling, boolean isLiving, boolean isPlayer, boolean isOnFire, boolean hasFireResistance, boolean isUndead, boolean isArthropod, boolean isAquatic, double armor, float health) {
      double score = (double)0.0F;
      if ((Boolean)this.swordSwapping.get()) {
         score += this.getFireAspectScore(stack, isOnFire, hasFireResistance);
         score += this.getLootingScore(stack, isPlayer, isLiving, isOnFire, health);
         score += this.getSharpnessScore(stack, isOnFire);
         score += this.getSmiteScore(stack, isUndead, isOnFire);
         score += this.getBaneOfArthropodsScore(stack, isArthropod, isOnFire);
         score += this.getSweepingEdgeScore(stack);
      }

      if ((Boolean)this.maceSwapping.get()) {
         score += this.getBreachScore(stack, isLiving, armor);
         score += this.getDensityScore(stack, isFalling);
         score += this.getWindBurstScore(stack, isFalling);
         score += this.getMaceScore(stack, isFalling);
      }

      if ((Boolean)this.otherSwapping.get()) {
         score += this.getImpalingScore(stack, isAquatic);
      }

      return score;
   }

   private double getFireAspectScore(class_1799 stack, boolean isOnFire, boolean hasFireResistance) {
      if ((Boolean)this.enchantFireAspect.get() && !isOnFire && !hasFireResistance) {
         int level = Utils.getEnchantmentLevel(stack, class_1893.field_9124);
         return level > 0 ? (double)30.0F : (double)0.0F;
      } else {
         return (double)0.0F;
      }
   }

   private double getLootingScore(class_1799 stack, boolean isPlayer, boolean isLiving, boolean isOnFire, float health) {
      if ((Boolean)this.enchantLooting.get() && !isPlayer) {
         int level = Utils.getEnchantmentLevel(stack, class_1893.field_9110);
         if (level <= 0) {
            return (double)0.0F;
         } else {
            boolean execute = isLiving && health < 20.0F || isOnFire;
            return (double)(level * (execute ? 10 : 5));
         }
      } else {
         return (double)0.0F;
      }
   }

   private double getSharpnessScore(class_1799 stack, boolean isOnFire) {
      if (!(Boolean)this.enchantSharpness.get()) {
         return (double)0.0F;
      } else {
         int level = Utils.getEnchantmentLevel(stack, class_1893.field_9118);
         if (level > 0) {
            double baseScore = ((double)1.0F + (double)0.5F * (double)(level - 1)) * (double)3.0F;
            return isOnFire ? baseScore * (double)1.5F : baseScore;
         } else {
            return (double)0.0F;
         }
      }
   }

   private double getSmiteScore(class_1799 stack, boolean isUndead, boolean isOnFire) {
      if ((Boolean)this.enchantSmite.get() && isUndead) {
         int level = Utils.getEnchantmentLevel(stack, class_1893.field_9123);
         if (level > 0) {
            double baseScore = (double)(level * 5);
            return isOnFire ? baseScore * (double)1.5F : baseScore;
         } else {
            return (double)0.0F;
         }
      } else {
         return (double)0.0F;
      }
   }

   private double getBaneOfArthropodsScore(class_1799 stack, boolean isArthropod, boolean isOnFire) {
      if ((Boolean)this.enchantBaneOfArthropods.get() && isArthropod) {
         int level = Utils.getEnchantmentLevel(stack, class_1893.field_9112);
         if (level > 0) {
            double baseScore = (double)(level * 5);
            return isOnFire ? baseScore * (double)1.5F : baseScore;
         } else {
            return (double)0.0F;
         }
      } else {
         return (double)0.0F;
      }
   }

   private double getSweepingEdgeScore(class_1799 stack) {
      if (!(Boolean)this.enchantSweepingEdge.get()) {
         return (double)0.0F;
      } else {
         int level = Utils.getEnchantmentLevel(stack, class_1893.field_9115);
         return level > 0 ? (double)(level * 3) : (double)0.0F;
      }
   }

   private double getImpalingScore(class_1799 stack, boolean isAquatic) {
      if ((Boolean)this.enchantImpaling.get() && isAquatic) {
         int level = Utils.getEnchantmentLevel(stack, class_1893.field_9106);
         return level > 0 ? (double)(level * 5) : (double)0.0F;
      } else {
         return (double)0.0F;
      }
   }

   private double getBreachScore(class_1799 stack, boolean isLiving, double armor) {
      if ((Boolean)this.enchantBreach.get() && isLiving && !(armor <= (double)0.0F)) {
         int level = Utils.getEnchantmentLevel(stack, class_1893.field_50158);
         return level > 0 ? (double)level * armor * 0.3 : (double)0.0F;
      } else {
         return (double)0.0F;
      }
   }

   private double getDensityScore(class_1799 stack, boolean isFalling) {
      if ((Boolean)this.enchantDensity.get() && isFalling) {
         int level = Utils.getEnchantmentLevel(stack, class_1893.field_50157);
         return level > 0 ? (double)50.0F + (double)level * this.mc.field_1724.field_6017 * (double)2.0F : (double)0.0F;
      } else {
         return (double)0.0F;
      }
   }

   private double getWindBurstScore(class_1799 stack, boolean isFalling) {
      if ((Boolean)this.enchantWindBurst.get() && isFalling) {
         int level = Utils.getEnchantmentLevel(stack, class_1893.field_50159);
         return level > 0 ? (double)(level * 20) : (double)0.0F;
      } else {
         return (double)0.0F;
      }
   }

   private double getMaceScore(class_1799 stack, boolean isFalling) {
      if ((Boolean)this.regularMace.get() && isFalling) {
         return stack.method_7909() instanceof class_9362 ? (double)40.0F : (double)0.0F;
      } else {
         return (double)0.0F;
      }
   }

   private boolean hasFireProtectionArmor(class_1309 entity) {
      for(class_1304 slot : class_9274.field_49224) {
         class_1799 stack = entity.method_6118(slot);
         if (!stack.method_7960()) {
            int fireProtection = Utils.getEnchantmentLevel(stack, class_1893.field_9095);
            if (fireProtection > 0) {
               return true;
            }
         }
      }

      return false;
   }

   public static enum Mode {
      Simple,
      Smart;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Simple, Smart};
      }
   }
}
