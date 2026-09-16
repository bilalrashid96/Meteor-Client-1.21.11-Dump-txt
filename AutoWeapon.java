package meteordevelopment.meteorclient.systems.modules.combat;

import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1743;
import net.minecraft.class_1799;
import net.minecraft.class_3489;

public class AutoWeapon extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Weapon> weapon;
   private final Setting<Integer> threshold;
   private final Setting<Boolean> antiBreak;

   public AutoWeapon() {
      super(Categories.Combat, "auto-weapon", "Finds the best weapon to use in your hotbar.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.weapon = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("weapon")).description("What type of weapon to use.")).defaultValue(AutoWeapon.Weapon.Sword)).build());
      this.threshold = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("threshold")).description("If the non-preferred weapon produces this much damage this will favor it over your preferred weapon.")).defaultValue(4)).build());
      this.antiBreak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-break")).description("Prevents you from breaking your weapon.")).defaultValue(false)).build());
   }

   @EventHandler
   private void onAttack(AttackEntityEvent event) {
      class_1297 var3 = event.entity;
      if (var3 instanceof class_1309 livingEntity) {
         InvUtils.swap(this.getBestWeapon(livingEntity), false);
      }

   }

   private int getBestWeapon(class_1309 target) {
      int slotS = this.mc.field_1724.method_31548().method_67532();
      int slotA = this.mc.field_1724.method_31548().method_67532();
      double damageS = (double)0.0F;
      double damageA = (double)0.0F;

      for(int i = 0; i < 9; ++i) {
         class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
         if (!stack.method_31573(class_3489.field_42611) || (Boolean)this.antiBreak.get() && stack.method_7936() - stack.method_7919() <= 10) {
            if (stack.method_7909() instanceof class_1743 && (!(Boolean)this.antiBreak.get() || stack.method_7936() - stack.method_7919() > 10)) {
               double currentDamageA = (double)DamageUtils.getAttackDamage(this.mc.field_1724, target, stack);
               if (currentDamageA > damageA) {
                  damageA = currentDamageA;
                  slotA = i;
               }
            }
         } else {
            double currentDamageS = (double)DamageUtils.getAttackDamage(this.mc.field_1724, target, stack);
            if (currentDamageS > damageS) {
               damageS = currentDamageS;
               slotS = i;
            }
         }
      }

      if (this.weapon.get() == AutoWeapon.Weapon.Sword && (double)(Integer)this.threshold.get() > damageA - damageS) {
         return slotS;
      } else if (this.weapon.get() == AutoWeapon.Weapon.Axe && (double)(Integer)this.threshold.get() > damageS - damageA) {
         return slotA;
      } else if (this.weapon.get() == AutoWeapon.Weapon.Sword && (double)(Integer)this.threshold.get() < damageA - damageS) {
         return slotA;
      } else if (this.weapon.get() == AutoWeapon.Weapon.Axe && (double)(Integer)this.threshold.get() < damageS - damageA) {
         return slotS;
      } else {
         return this.mc.field_1724.method_31548().method_67532();
      }
   }

   public static enum Weapon {
      Sword,
      Axe;

      // $FF: synthetic method
      private static Weapon[] $values() {
         return new Weapon[]{Sword, Axe};
      }
   }
}
