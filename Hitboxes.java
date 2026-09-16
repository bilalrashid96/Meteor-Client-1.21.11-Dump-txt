package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_1835;
import net.minecraft.class_3489;
import net.minecraft.class_9362;

public class Hitboxes extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgWeapon;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<Double> value;
   private final Setting<Boolean> ignoreFriends;
   private final Setting<Boolean> onlyOnWeapon;
   private final Setting<Boolean> sword;
   private final Setting<Boolean> axe;
   private final Setting<Boolean> pickaxe;
   private final Setting<Boolean> shovel;
   private final Setting<Boolean> hoe;
   private final Setting<Boolean> mace;
   private final Setting<Boolean> spear;
   private final Setting<Boolean> trident;

   public Hitboxes() {
      super(Categories.Combat, "hitboxes", "Expands an entity's hitboxes.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgWeapon = this.settings.createGroup("Weapon Options");
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Which entities to target.")).defaultValue(class_1299.field_6097).build());
      this.value = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("expand")).description("How much to expand the hitbox of the entity.")).defaultValue((double)0.5F).build());
      this.ignoreFriends = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-friends")).description("Doesn't expand the hitboxes of friends.")).defaultValue(true)).build());
      this.onlyOnWeapon = this.sgWeapon.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-weapon")).description("Only modifies hitbox when holding a weapon in hand.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgWeapon;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sword")).description("Enable when holding a sword.")).defaultValue(true);
      Setting var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.sword = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgWeapon;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("axe")).description("Enable when holding an axe.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.axe = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgWeapon;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pickaxe")).description("Enable when holding a pickaxe.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.pickaxe = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgWeapon;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("shovel")).description("Enable when holding a shovel.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.shovel = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgWeapon;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hoe")).description("Enable when holding a hoe.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.hoe = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgWeapon;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("mace")).description("Enable when holding a mace.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.mace = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgWeapon;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("spear")).description("Enable when holding a spear.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.spear = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgWeapon;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("trident")).description("Enable when holding a trident.")).defaultValue(true);
      var10003 = this.onlyOnWeapon;
      Objects.requireNonNull(var10003);
      this.trident = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
   }

   public double getEntityValue(class_1297 entity) {
      if (this.isActive() && this.testWeapon()) {
         if ((Boolean)this.ignoreFriends.get() && entity instanceof class_1657) {
            class_1657 playerEntity = (class_1657)entity;
            if (Friends.get().isFriend(playerEntity)) {
               return (double)0.0F;
            }
         }

         if (((Set)this.entities.get()).contains(entity.method_5864())) {
            return (Double)this.value.get();
         } else {
            return (double)0.0F;
         }
      } else {
         return (double)0.0F;
      }
   }

   private boolean testWeapon() {
      return !(Boolean)this.onlyOnWeapon.get() ? true : InvUtils.testInMainHand((Predicate)((itemStack) -> {
         if ((Boolean)this.sword.get() && itemStack.method_31573(class_3489.field_42611)) {
            return true;
         } else if ((Boolean)this.axe.get() && itemStack.method_31573(class_3489.field_42612)) {
            return true;
         } else if ((Boolean)this.pickaxe.get() && itemStack.method_31573(class_3489.field_42614)) {
            return true;
         } else if ((Boolean)this.shovel.get() && itemStack.method_31573(class_3489.field_42615)) {
            return true;
         } else if ((Boolean)this.hoe.get() && itemStack.method_31573(class_3489.field_42613)) {
            return true;
         } else if ((Boolean)this.mace.get() && itemStack.method_7909() instanceof class_9362) {
            return true;
         } else if ((Boolean)this.spear.get() && itemStack.method_31573(class_3489.field_63257)) {
            return true;
         } else {
            return (Boolean)this.trident.get() && itemStack.method_7909() instanceof class_1835;
         }
      }));
   }
}
