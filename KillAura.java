package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1296;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1453;
import net.minecraft.class_1493;
import net.minecraft.class_1560;
import net.minecraft.class_1590;
import net.minecraft.class_1642;
import net.minecraft.class_1657;
import net.minecraft.class_1743;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1835;
import net.minecraft.class_1934;
import net.minecraft.class_238;
import net.minecraft.class_2868;
import net.minecraft.class_3489;
import net.minecraft.class_3532;
import net.minecraft.class_4760;
import net.minecraft.class_4836;
import net.minecraft.class_5136;
import net.minecraft.class_6025;
import net.minecraft.class_7102;
import net.minecraft.class_9362;

public class KillAura extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgTargeting;
   private final SettingGroup sgTiming;
   private final Setting<AttackItems> attackWhenHolding;
   private final Setting<List<class_1792>> weapons;
   private final Setting<RotationMode> rotation;
   private final Setting<Boolean> autoSwitch;
   private final Setting<Boolean> swapBack;
   private final Setting<ShieldMode> shieldMode;
   private final Setting<Boolean> onlyOnClick;
   private final Setting<Boolean> onlyOnLook;
   private final Setting<Boolean> pauseOnCombat;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<SortPriority> priority;
   private final Setting<Integer> maxTargets;
   private final Setting<Double> range;
   private final Setting<Double> wallsRange;
   private final Setting<EntityAge> passiveMobAgeFilter;
   private final Setting<EntityAge> hostileMobAgeFilter;
   private final Setting<Boolean> ignoreNamed;
   private final Setting<Boolean> ignorePassive;
   private final Setting<Boolean> ignoreTamed;
   private final Setting<Boolean> pauseOnLag;
   private final Setting<Boolean> pauseOnUse;
   private final Setting<Boolean> pauseOnCA;
   private final Setting<Boolean> tpsSync;
   private final Setting<Boolean> customDelay;
   private final Setting<Integer> hitDelay;
   private final Setting<Integer> switchDelay;
   private static final ArrayList<class_1792> FILTER;
   private final List<class_1297> targets;
   private int switchTimer;
   private int hitTimer;
   private boolean wasPathing;
   public boolean attacking;
   public boolean swapped;
   public static int previousSlot;

   public KillAura() {
      super(Categories.Combat, "kill-aura", "Attacks specified entities around you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgTargeting = this.settings.createGroup("Targeting");
      this.sgTiming = this.settings.createGroup("Timing");
      this.attackWhenHolding = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("attack-when-holding")).description("Only attacks an entity when a specified item is in your hand.")).defaultValue(KillAura.AttackItems.Weapons)).build());
      SettingGroup var10001 = this.sgGeneral;
      ItemListSetting.Builder var10002 = ((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("selected-weapon-types")).description("Which types of weapons to attack with (if you select the diamond sword, any type of sword may be used to attack).")).defaultValue(class_1802.field_8802, class_1802.field_8556, class_1802.field_8547);
      ArrayList var10003 = FILTER;
      Objects.requireNonNull(var10003);
      this.weapons = var10001.add(((ItemListSetting.Builder)var10002.filter(var10003::contains).visible(() -> this.attackWhenHolding.get() == KillAura.AttackItems.Weapons)).build());
      this.rotation = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("rotate")).description("Determines when you should rotate towards the target.")).defaultValue(KillAura.RotationMode.Always)).build());
      this.autoSwitch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-switch")).description("Switches to an acceptable weapon when attacking the target.")).defaultValue(false)).build());
      var10001 = this.sgGeneral;
      BoolSetting.Builder var3 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swap-back")).description("Switches to your previous slot when done attacking the target.")).defaultValue(false);
      Setting var5 = this.autoSwitch;
      Objects.requireNonNull(var5);
      this.swapBack = var10001.add(((BoolSetting.Builder)var3.visible(var5::get)).build());
      this.shieldMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shield-mode")).description("    What to do when your target is blocking with a shield:\n    - Ignore:   Don't attack them if they are blocking\n    - Break:    Swap to an axe to disable the shield (Only if Auto Switch is enabled)\n    - None:     Attack them as normal\n")).defaultValue(KillAura.ShieldMode.None)).build());
      this.onlyOnClick = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-click")).description("Only attacks when holding left click.")).defaultValue(false)).build());
      this.onlyOnLook = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-look")).description("Only attacks when looking at an entity.")).defaultValue(false)).build());
      this.pauseOnCombat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-baritone")).description("Freezes Baritone temporarily until you are finished attacking the entity.")).defaultValue(true)).build());
      this.entities = this.sgTargeting.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Entities to attack.")).onlyAttackable().defaultValue(class_1299.field_6097).build());
      this.priority = this.sgTargeting.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("priority")).description("How to filter targets within range.")).defaultValue(SortPriority.ClosestAngle)).build());
      this.maxTargets = this.sgTargeting.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-targets")).description("How many entities to target at once.")).defaultValue(1)).min(1).sliderRange(1, 5).visible(() -> !(Boolean)this.onlyOnLook.get())).build());
      this.range = this.sgTargeting.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("The maximum range the entity can be to attack it.")).defaultValue((double)4.5F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.wallsRange = this.sgTargeting.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("walls-range")).description("The maximum range the entity can be attacked through walls.")).defaultValue((double)3.5F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.passiveMobAgeFilter = this.sgTargeting.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("passive-mob-age-filter")).description("Determines the age of passive mobs to target (animals, villagers).")).defaultValue(KillAura.EntityAge.Adult)).build());
      this.hostileMobAgeFilter = this.sgTargeting.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("hostile-mob-age-filter")).description("Determines the age of hostile mobs to target (zombies, piglins, hoglins, zoglins).")).defaultValue(KillAura.EntityAge.Both)).build());
      this.ignoreNamed = this.sgTargeting.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-named")).description("Whether or not to attack mobs with a name.")).defaultValue(false)).build());
      this.ignorePassive = this.sgTargeting.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-passive")).description("Will only attack sometimes passive mobs if they are targeting you.")).defaultValue(true)).build());
      this.ignoreTamed = this.sgTargeting.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-tamed")).description("Will avoid attacking mobs you tamed.")).defaultValue(false)).build());
      this.pauseOnLag = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-lag")).description("Pauses if the server is lagging.")).defaultValue(true)).build());
      this.pauseOnUse = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-use")).description("Does not attack while using an item.")).defaultValue(false)).build());
      this.pauseOnCA = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-CA")).description("Does not attack while CA is placing.")).defaultValue(true)).build());
      this.tpsSync = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("TPS-sync")).description("Tries to sync attack delay with the server's TPS.")).defaultValue(true)).build());
      this.customDelay = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-delay")).description("Use a custom delay instead of the vanilla cooldown.")).defaultValue(false)).build());
      var10001 = this.sgTiming;
      IntSetting.Builder var4 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("hit-delay")).description("How fast you hit the entity in ticks.")).defaultValue(11)).min(0).sliderMax(60);
      var5 = this.customDelay;
      Objects.requireNonNull(var5);
      this.hitDelay = var10001.add(((IntSetting.Builder)var4.visible(var5::get)).build());
      this.switchDelay = this.sgTiming.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("switch-delay")).description("How many ticks to wait before hitting an entity after switching hotbar slots.")).defaultValue(0)).min(0).sliderMax(10).build());
      this.targets = new ArrayList();
      this.wasPathing = false;
   }

   public void onActivate() {
      previousSlot = -1;
      this.swapped = false;
   }

   public void onDeactivate() {
      this.targets.clear();
      this.stopAttacking();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.mc.field_1724.method_5805() && PlayerUtils.getGameMode() != class_1934.field_9219) {
         if (!(Boolean)this.pauseOnUse.get() || !this.mc.field_1761.method_2923() && !this.mc.field_1724.method_6115()) {
            if ((Boolean)this.onlyOnClick.get() && !this.mc.field_1690.field_1886.method_1434()) {
               this.stopAttacking();
            } else if (TickRate.INSTANCE.getTimeSinceLastTick() >= 1.0F && (Boolean)this.pauseOnLag.get()) {
               this.stopAttacking();
            } else if ((Boolean)this.pauseOnCA.get() && ((CrystalAura)Modules.get().get(CrystalAura.class)).isActive() && ((CrystalAura)Modules.get().get(CrystalAura.class)).kaTimer > 0) {
               this.stopAttacking();
            } else {
               if ((Boolean)this.onlyOnLook.get()) {
                  class_1297 targeted = this.mc.field_1692;
                  if (targeted == null || !this.entityCheck(targeted)) {
                     this.stopAttacking();
                     return;
                  }

                  this.targets.clear();
                  this.targets.add(this.mc.field_1692);
               } else {
                  this.targets.clear();
                  TargetUtils.getList(this.targets, this::entityCheck, this.priority.get(), (Integer)this.maxTargets.get());
               }

               if (this.targets.isEmpty()) {
                  this.stopAttacking();
               } else {
                  class_1297 primary = (class_1297)this.targets.getFirst();
                  if ((Boolean)this.autoSwitch.get()) {
                     FindItemResult weaponResult = new FindItemResult(this.mc.field_1724.method_31548().method_67532(), -1);
                     if (this.attackWhenHolding.get() == KillAura.AttackItems.Weapons) {
                        weaponResult = InvUtils.find(this::acceptableWeapon, 0, 8);
                     }

                     if (this.shouldShieldBreak()) {
                        FindItemResult axeResult = InvUtils.find((itemStack) -> itemStack.method_7909() instanceof class_1743, 0, 8);
                        if (axeResult.found()) {
                           weaponResult = axeResult;
                        }
                     }

                     if (!this.swapped) {
                        previousSlot = this.mc.field_1724.method_31548().method_67532();
                        this.swapped = true;
                     }

                     InvUtils.swap(weaponResult.slot(), false);
                  }

                  if (!this.acceptableWeapon(this.mc.field_1724.method_6047())) {
                     this.stopAttacking();
                  } else {
                     this.attacking = true;
                     if (this.rotation.get() == KillAura.RotationMode.Always) {
                        Rotations.rotate(Rotations.getYaw(primary), Rotations.getPitch(primary, Target.Body));
                     }

                     if ((Boolean)this.pauseOnCombat.get() && PathManagers.get().isPathing() && !this.wasPathing) {
                        PathManagers.get().pause();
                        this.wasPathing = true;
                     }

                     if (this.delayCheck()) {
                        this.targets.forEach(this::attack);
                     }

                  }
               }
            }
         } else {
            this.stopAttacking();
         }
      } else {
         this.stopAttacking();
      }
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      if (event.packet instanceof class_2868) {
         this.switchTimer = (Integer)this.switchDelay.get();
      }

   }

   private void stopAttacking() {
      if (this.attacking) {
         this.attacking = false;
         if (this.wasPathing) {
            PathManagers.get().resume();
            this.wasPathing = false;
         }

         if ((Boolean)this.swapBack.get() && this.swapped) {
            InvUtils.swap(previousSlot, false);
            this.swapped = false;
         }

      }
   }

   private boolean shouldShieldBreak() {
      for(class_1297 target : this.targets) {
         if (target instanceof class_1657 player) {
            if (player.method_6039() && this.shieldMode.get() == KillAura.ShieldMode.Break) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean entityCheck(class_1297 entity) {
      if (!entity.equals(this.mc.field_1724) && !entity.equals(this.mc.method_1560())) {
         if (entity instanceof class_1309) {
            class_1309 livingEntity = (class_1309)entity;
            if (livingEntity.method_29504()) {
               return false;
            }
         }

         if (entity.method_5805()) {
            class_238 hitbox = entity.method_5829();
            if (!PlayerUtils.isWithin(class_3532.method_15350(this.mc.field_1724.method_23317(), hitbox.field_1323, hitbox.field_1320), class_3532.method_15350(this.mc.field_1724.method_23318(), hitbox.field_1322, hitbox.field_1325), class_3532.method_15350(this.mc.field_1724.method_23321(), hitbox.field_1321, hitbox.field_1324), (Double)this.range.get())) {
               return false;
            } else if (!((Set)this.entities.get()).contains(entity.method_5864())) {
               return false;
            } else if ((Boolean)this.ignoreNamed.get() && entity.method_16914()) {
               return false;
            } else if (!PlayerUtils.canSeeEntity(entity) && !PlayerUtils.isWithin(entity, (Double)this.wallsRange.get())) {
               return false;
            } else {
               if ((Boolean)this.ignoreTamed.get() && entity instanceof class_6025) {
                  class_6025 tameable = (class_6025)entity;
                  if (tameable.method_35057() != null && tameable.method_35057().equals(this.mc.field_1724)) {
                     return false;
                  }
               }

               if ((Boolean)this.ignorePassive.get()) {
                  if (entity instanceof class_1560) {
                     class_1560 enderman = (class_1560)entity;
                     if (!enderman.method_7028()) {
                        return false;
                     }
                  }

                  if ((entity instanceof class_4836 || entity instanceof class_1590 || entity instanceof class_1493) && !((class_1308)entity).method_6510()) {
                     return false;
                  }
               }

               if (entity instanceof class_1657) {
                  class_1657 player = (class_1657)entity;
                  if (player.method_68878()) {
                     return false;
                  }

                  if (!Friends.get().shouldAttack(player)) {
                     return false;
                  }

                  if (this.shieldMode.get() == KillAura.ShieldMode.Ignore && player.method_6039()) {
                     return false;
                  }

                  if (player instanceof FakePlayerEntity) {
                     FakePlayerEntity fakePlayer = (FakePlayerEntity)player;
                     if (fakePlayer.noHit) {
                        return false;
                     }
                  }
               }

               if (entity instanceof class_1309) {
                  class_1309 livingEntity = (class_1309)entity;
                  if (entity instanceof class_1642 || entity instanceof class_4836 || entity instanceof class_4760 || entity instanceof class_5136) {
                     boolean var9;
                     switch (((EntityAge)this.hostileMobAgeFilter.get()).ordinal()) {
                        case 0 -> var9 = livingEntity.method_6109();
                        case 1 -> var9 = !livingEntity.method_6109();
                        case 2 -> var9 = true;
                        default -> throw new MatchException((String)null, (Throwable)null);
                     }

                     return var9;
                  }

                  if (entity instanceof class_1296 && !(entity instanceof class_7102) && !(entity instanceof class_1453)) {
                     boolean var10000;
                     switch (((EntityAge)this.passiveMobAgeFilter.get()).ordinal()) {
                        case 0 -> var10000 = livingEntity.method_6109();
                        case 1 -> var10000 = !livingEntity.method_6109();
                        case 2 -> var10000 = true;
                        default -> throw new MatchException((String)null, (Throwable)null);
                     }

                     return var10000;
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean delayCheck() {
      if (this.switchTimer > 0) {
         --this.switchTimer;
         return false;
      } else {
         float delay = (Boolean)this.customDelay.get() ? (float)(Integer)this.hitDelay.get() : 0.5F;
         if ((Boolean)this.tpsSync.get()) {
            delay /= TickRate.INSTANCE.getTickRate() / 20.0F;
         }

         if ((Boolean)this.customDelay.get()) {
            if ((float)this.hitTimer < delay) {
               ++this.hitTimer;
               return false;
            } else {
               return true;
            }
         } else {
            return this.mc.field_1724.method_7261(delay) >= 1.0F;
         }
      }
   }

   private void attack(class_1297 target) {
      if (this.rotation.get() == KillAura.RotationMode.OnHit) {
         Rotations.rotate(Rotations.getYaw(target), Rotations.getPitch(target, Target.Body));
      }

      this.mc.field_1761.method_2918(this.mc.field_1724, target);
      this.mc.field_1724.method_6104(class_1268.field_5808);
      this.hitTimer = 0;
   }

   private boolean acceptableWeapon(class_1799 stack) {
      if (this.shouldShieldBreak()) {
         return stack.method_7909() instanceof class_1743;
      } else if (this.attackWhenHolding.get() == KillAura.AttackItems.All) {
         return true;
      } else if (((List)this.weapons.get()).contains(class_1802.field_8802) && stack.method_31573(class_3489.field_42611)) {
         return true;
      } else if (((List)this.weapons.get()).contains(class_1802.field_8556) && stack.method_31573(class_3489.field_42612)) {
         return true;
      } else if (((List)this.weapons.get()).contains(class_1802.field_8377) && stack.method_31573(class_3489.field_42614)) {
         return true;
      } else if (((List)this.weapons.get()).contains(class_1802.field_8250) && stack.method_31573(class_3489.field_42615)) {
         return true;
      } else if (((List)this.weapons.get()).contains(class_1802.field_8527) && stack.method_31573(class_3489.field_42613)) {
         return true;
      } else if (((List)this.weapons.get()).contains(class_1802.field_49814) && stack.method_7909() instanceof class_9362) {
         return true;
      } else if (((List)this.weapons.get()).contains(class_1802.field_63389) && stack.method_31573(class_3489.field_63257)) {
         return true;
      } else {
         return ((List)this.weapons.get()).contains(class_1802.field_8547) && stack.method_7909() instanceof class_1835;
      }
   }

   public class_1297 getTarget() {
      return !this.targets.isEmpty() ? (class_1297)this.targets.getFirst() : null;
   }

   public String getInfoString() {
      return !this.targets.isEmpty() ? EntityUtils.getName(this.getTarget()) : null;
   }

   static {
      FILTER = new ArrayList(List.of(class_1802.field_8802, class_1802.field_8556, class_1802.field_8377, class_1802.field_8250, class_1802.field_8527, class_1802.field_49814, class_1802.field_63389, class_1802.field_8547));
   }

   public static enum AttackItems {
      Weapons,
      All;

      // $FF: synthetic method
      private static AttackItems[] $values() {
         return new AttackItems[]{Weapons, All};
      }
   }

   public static enum RotationMode {
      Always,
      OnHit,
      None;

      // $FF: synthetic method
      private static RotationMode[] $values() {
         return new RotationMode[]{Always, OnHit, None};
      }
   }

   public static enum ShieldMode {
      Ignore,
      Break,
      None;

      // $FF: synthetic method
      private static ShieldMode[] $values() {
         return new ShieldMode[]{Ignore, Break, None};
      }
   }

   public static enum EntityAge {
      Baby,
      Adult,
      Both;

      // $FF: synthetic method
      private static EntityAge[] $values() {
         return new EntityAge[]{Baby, Adult, Both};
      }
   }
}
