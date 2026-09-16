package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.AbstractBlockAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StatusEffectListSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1657;
import net.minecraft.class_1753;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2828;
import net.minecraft.class_9334;

public class Quiver extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgSafety;
   private final Setting<List<class_1291>> effects;
   private final Setting<Integer> cooldown;
   private final Setting<Boolean> checkEffects;
   private final Setting<Boolean> silentBow;
   private final Setting<Boolean> chatInfo;
   private final Setting<Boolean> onlyInHoles;
   private final Setting<Boolean> onlyOnGround;
   private final Setting<Double> minHealth;
   private final List<Integer> arrowSlots;
   private FindItemResult bow;
   private boolean wasMainhand;
   private boolean wasHotbar;
   private int timer;
   private int prevSlot;
   private final class_2338.class_2339 testPos;

   public Quiver() {
      super(Categories.Combat, "quiver", "Shoots arrows at yourself.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgSafety = this.settings.createGroup("Safety");
      this.effects = this.sgGeneral.add(((StatusEffectListSetting.Builder)((StatusEffectListSetting.Builder)(new StatusEffectListSetting.Builder()).name("effects")).description("Which effects to shoot you with.")).defaultValue((class_1291)class_1294.field_5910.comp_349()).build());
      this.cooldown = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("cooldown")).description("How many ticks between shooting effects (19 minimum for NCP).")).defaultValue(10)).range(0, 40).sliderRange(0, 40).build());
      this.checkEffects = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("check-effects")).description("Won't shoot you with effects you already have.")).defaultValue(true)).build());
      this.silentBow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("silent-bow")).description("Takes a bow from your inventory to quiver.")).defaultValue(true)).build());
      this.chatInfo = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("chat-info")).description("Sends info about quiver checks in chat.")).defaultValue(false)).build());
      this.onlyInHoles = this.sgSafety.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-in-holes")).description("Only quiver when you're in a hole.")).defaultValue(true)).build());
      this.onlyOnGround = this.sgSafety.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Only quiver when you're on the ground.")).defaultValue(true)).build());
      this.minHealth = this.sgSafety.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-health")).description("How much health you must have to quiver.")).defaultValue((double)10.0F).range((double)0.0F, (double)36.0F).sliderRange((double)0.0F, (double)36.0F).build());
      this.arrowSlots = new ArrayList();
      this.testPos = new class_2338.class_2339();
   }

   public void onActivate() {
      this.bow = InvUtils.find(class_1802.field_8102);
      if (this.shouldQuiver()) {
         this.mc.field_1690.field_1904.method_23481(false);
         this.mc.field_1761.method_2897(this.mc.field_1724);
         this.prevSlot = this.bow.slot();
         this.wasHotbar = this.bow.isHotbar();
         this.timer = 0;
         if (!this.bow.isMainHand()) {
            if (this.wasHotbar) {
               InvUtils.swap(this.bow.slot(), true);
            } else {
               InvUtils.move().from(this.mc.field_1724.method_31548().method_67532()).to(this.prevSlot);
            }
         } else {
            this.wasMainhand = true;
         }

         this.arrowSlots.clear();
         List<class_1291> usedEffects = new ArrayList();

         for(int i = this.mc.field_1724.method_31548().method_5439(); i > 0; --i) {
            if (i != this.mc.field_1724.method_31548().method_67532()) {
               class_1799 item = this.mc.field_1724.method_31548().method_5438(i);
               if (item.method_7909() == class_1802.field_8087) {
                  Iterator<class_1293> effects = ((class_1844)item.method_7909().method_57347().method_58694(class_9334.field_49651)).method_57397().iterator();
                  if (effects.hasNext()) {
                     class_1291 effect = (class_1291)((class_1293)effects.next()).method_5579().comp_349();
                     if (((List)this.effects.get()).contains(effect) && !usedEffects.contains(effect) && (!this.hasEffect(effect) || !(Boolean)this.checkEffects.get())) {
                        usedEffects.add(effect);
                        this.arrowSlots.add(i);
                     }
                  }
               }
            }
         }

      }
   }

   public void onDeactivate() {
      if (!this.wasMainhand) {
         if (this.wasHotbar) {
            InvUtils.swapBack();
         } else {
            InvUtils.move().from(this.mc.field_1724.method_31548().method_67532()).to(this.prevSlot);
         }
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.bow = InvUtils.find(class_1802.field_8102);
      if (this.shouldQuiver()) {
         if (this.arrowSlots.isEmpty()) {
            this.toggle();
         } else if (this.timer > 0) {
            --this.timer;
         } else {
            boolean charging = this.mc.field_1690.field_1904.method_1434();
            if (!charging) {
               InvUtils.move().from((Integer)this.arrowSlots.getFirst()).to(9);
               this.mc.field_1690.field_1904.method_23481(true);
            } else if ((double)class_1753.method_7722(this.mc.field_1724.method_6048()) >= 0.12) {
               int targetSlot = (Integer)this.arrowSlots.getFirst();
               this.arrowSlots.removeFirst();
               this.mc.method_1562().method_52787(new class_2828.class_2831(this.mc.field_1724.method_36454(), -90.0F, this.mc.field_1724.method_24828(), this.mc.field_1724.field_5976));
               this.mc.field_1690.field_1904.method_23481(false);
               this.mc.field_1761.method_2897(this.mc.field_1724);
               if (targetSlot != 9) {
                  InvUtils.move().from(9).to(targetSlot);
               }

               this.timer = (Integer)this.cooldown.get();
            }

         }
      }
   }

   private boolean shouldQuiver() {
      if (!this.bow.found() || !this.bow.isHotbar() && !(Boolean)this.silentBow.get()) {
         if ((Boolean)this.chatInfo.get()) {
            this.error("Couldn't find a usable bow, disabling.", new Object[0]);
         }

         this.toggle();
         return false;
      } else if (!this.headIsOpen()) {
         if ((Boolean)this.chatInfo.get()) {
            this.error("Not enough space to quiver, disabling.", new Object[0]);
         }

         this.toggle();
         return false;
      } else if ((double)EntityUtils.getTotalHealth(this.mc.field_1724) < (Double)this.minHealth.get()) {
         if ((Boolean)this.chatInfo.get()) {
            this.error("Not enough health to quiver, disabling.", new Object[0]);
         }

         this.toggle();
         return false;
      } else if ((Boolean)this.onlyOnGround.get() && !this.mc.field_1724.method_24828()) {
         if ((Boolean)this.chatInfo.get()) {
            this.error("You are not on the ground, disabling.", new Object[0]);
         }

         this.toggle();
         return false;
      } else if ((Boolean)this.onlyInHoles.get() && !this.isSurrounded(this.mc.field_1724)) {
         if ((Boolean)this.chatInfo.get()) {
            this.error("You are not in a hole, disabling.", new Object[0]);
         }

         this.toggle();
         return false;
      } else {
         return true;
      }
   }

   private boolean headIsOpen() {
      this.testPos.method_10101(this.mc.field_1724.method_24515().method_10069(0, 1, 0));
      class_2680 pos1 = this.mc.field_1687.method_8320(this.testPos);
      if (((AbstractBlockAccessor)pos1.method_26204()).meteor$isCollidable()) {
         return false;
      } else {
         this.testPos.method_10069(0, 1, 0);
         class_2680 pos2 = this.mc.field_1687.method_8320(this.testPos);
         return !((AbstractBlockAccessor)pos2.method_26204()).meteor$isCollidable();
      }
   }

   private boolean hasEffect(class_1291 effect) {
      for(class_1293 statusEffect : this.mc.field_1724.method_6026()) {
         if (((class_1291)statusEffect.method_5579().comp_349()).equals(effect)) {
            return true;
         }
      }

      return false;
   }

   private boolean isSurrounded(class_1657 target) {
      for(class_2350 dir : class_2350.values()) {
         if (dir != class_2350.field_11036 && dir != class_2350.field_11033) {
            this.testPos.method_10101(target.method_24515()).method_10093(dir);
            class_2248 block = this.mc.field_1687.method_8320(this.testPos).method_26204();
            if (block != class_2246.field_10540 && block != class_2246.field_9987 && block != class_2246.field_23152 && block != class_2246.field_22423 && block != class_2246.field_22108) {
               return false;
            }
         }
      }

      return true;
   }
}
