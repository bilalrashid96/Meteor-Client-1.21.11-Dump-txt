package meteordevelopment.meteorclient.systems.modules.player;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import meteordevelopment.meteorclient.events.entity.player.ItemUseCrosshairTargetEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.AnchorAura;
import meteordevelopment.meteorclient.systems.modules.combat.BedAura;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_4174;
import net.minecraft.class_9334;

public class AutoEat extends Module {
   private static final Class<? extends Module>[] AURAS = new Class[]{KillAura.class, CrystalAura.class, AnchorAura.class, BedAura.class};
   private final SettingGroup sgGeneral;
   private final SettingGroup sgThreshold;
   public final Setting<List<class_1792>> blacklist;
   private final Setting<Boolean> pauseAuras;
   private final Setting<Boolean> pauseBaritone;
   private final Setting<Boolean> searchInventory;
   private final Setting<Priority> prioritise;
   private final Setting<ThresholdMode> thresholdMode;
   private final Setting<Double> healthThreshold;
   private final Setting<Integer> hungerThreshold;
   public boolean eating;
   private int slot;
   private int prevSlot;
   private final List<Class<? extends Module>> wasAura;
   private boolean wasBaritone;

   public AutoEat() {
      super(Categories.Player, "auto-eat", "Automatically eats food.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgThreshold = this.settings.createGroup("Threshold");
      this.blacklist = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("blacklist")).description("Which items to not eat.")).defaultValue(class_1802.field_8367, class_1802.field_8463, class_1802.field_8233, class_1802.field_8635, class_1802.field_8323, class_1802.field_8726, class_1802.field_8511, class_1802.field_8680, class_1802.field_8766).filter((item) -> item.method_57347().method_58694(class_9334.field_50075) != null).build());
      this.pauseAuras = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-auras")).description("Pauses all auras when eating.")).defaultValue(true)).build());
      this.pauseBaritone = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-baritone")).description("Pause baritone when eating.")).defaultValue(true)).build());
      this.searchInventory = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("search-inventory")).description("Search the full inventory for food, not only the hotbar.")).defaultValue(false)).build());
      this.prioritise = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("food-priority")).description("Which aspect of the food to prioritise selecting for.")).defaultValue(AutoEat.Priority.Saturation)).build());
      this.thresholdMode = this.sgThreshold.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("threshold-mode")).description("The threshold mode to trigger auto eat.\n'Both' == health AND hunger, 'Any' == health OR hunger")).defaultValue(AutoEat.ThresholdMode.Any)).build());
      this.healthThreshold = this.sgThreshold.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("health-threshold")).description("The level of health you eat at.")).defaultValue((double)10.0F).range((double)1.0F, (double)19.0F).sliderRange((double)1.0F, (double)19.0F).visible(() -> this.thresholdMode.get() != AutoEat.ThresholdMode.Hunger)).build());
      this.hungerThreshold = this.sgThreshold.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("hunger-threshold")).description("The level of hunger you eat at.")).defaultValue(16)).range(1, 19).sliderRange(1, 19).visible(() -> this.thresholdMode.get() != AutoEat.ThresholdMode.Health)).build());
      this.wasAura = new ReferenceArrayList();
      this.wasBaritone = false;
   }

   public void onDeactivate() {
      if (this.eating) {
         this.stopEating();
      }

   }

   @EventHandler(
      priority = -100
   )
   private void onTick(TickEvent.Pre event) {
      if (!((AutoGap)Modules.get().get(AutoGap.class)).isEating()) {
         if (this.eating) {
            if (!this.shouldEat()) {
               this.stopEating();
            } else {
               if (this.mc.field_1724.method_31548().method_5438(this.slot).method_58694(class_9334.field_50075) == null) {
                  int newSlot = this.findSlot();
                  if (newSlot == -1) {
                     this.stopEating();
                     return;
                  }

                  this.changeSlot(newSlot);
               }

               this.eat();
            }
         } else {
            if (this.shouldEat()) {
               this.startEating();
            }

         }
      }
   }

   @EventHandler
   private void onItemUseCrosshairTarget(ItemUseCrosshairTargetEvent event) {
      if (this.eating) {
         event.target = null;
      }

   }

   private void startEating() {
      this.prevSlot = this.mc.field_1724.method_31548().method_67532();
      this.eat();
      this.wasAura.clear();
      if ((Boolean)this.pauseAuras.get()) {
         for(Class<? extends Module> klass : AURAS) {
            Module module = Modules.get().get(klass);
            if (module.isActive()) {
               this.wasAura.add(klass);
               module.toggle();
            }
         }
      }

      if ((Boolean)this.pauseBaritone.get() && PathManagers.get().isPathing() && !this.wasBaritone) {
         this.wasBaritone = true;
         PathManagers.get().pause();
      }

   }

   private void eat() {
      if (this.changeSlot(this.slot)) {
         this.setPressed(true);
         if (!this.mc.field_1724.method_6115()) {
            Utils.rightClick();
         }

         this.eating = true;
      }
   }

   private void stopEating() {
      if (this.prevSlot != 40) {
         this.changeSlot(this.prevSlot);
      }

      this.setPressed(false);
      this.eating = false;
      if ((Boolean)this.pauseAuras.get()) {
         for(Class<? extends Module> klass : AURAS) {
            if (this.wasAura.contains(klass)) {
               Modules.get().get(klass).enable();
            }
         }
      }

      if ((Boolean)this.pauseBaritone.get() && this.wasBaritone) {
         this.wasBaritone = false;
         PathManagers.get().resume();
      }

   }

   private void setPressed(boolean pressed) {
      this.mc.field_1690.field_1904.method_23481(pressed);
   }

   private boolean changeSlot(int slot) {
      if (slot == 40) {
         this.slot = 40;
         return true;
      } else if (SlotUtils.isHotbar(slot)) {
         InvUtils.swap(slot, false);
         this.slot = slot;
         return true;
      } else {
         int emptySlot = InvUtils.find(class_1799::method_7960, 0, 8).slot();
         if (emptySlot == -1) {
            return false;
         } else {
            InvUtils.move().from(slot).toHotbar(emptySlot);
            InvUtils.swap(emptySlot, false);
            this.slot = emptySlot;
            return true;
         }
      }
   }

   public boolean shouldEat() {
      boolean healthLow = (double)this.mc.field_1724.method_6032() <= (Double)this.healthThreshold.get();
      boolean hungerLow = this.mc.field_1724.method_7344().method_7586() <= (Integer)this.hungerThreshold.get();
      if (!((ThresholdMode)this.thresholdMode.get()).test(healthLow, hungerLow)) {
         return false;
      } else {
         this.slot = this.findSlot();
         if (this.slot == -1) {
            return false;
         } else {
            class_4174 food = (class_4174)this.mc.field_1724.method_31548().method_5438(this.slot).method_58694(class_9334.field_50075);
            if (food == null) {
               return false;
            } else {
               return this.mc.field_1724.method_7344().method_7587() || food.comp_2493();
            }
         }
      }
   }

   private int findSlot() {
      class_1792 offHandItem = this.mc.field_1724.method_6079().method_7909();
      class_4174 offHandFood = (class_4174)offHandItem.method_57347().method_58694(class_9334.field_50075);
      if (offHandFood != null && !((List)this.blacklist.get()).contains(offHandItem)) {
         return 40;
      } else {
         int slot = this.findBestFood(0, 8);
         if (slot != -1) {
            return slot;
         } else {
            return (Boolean)this.searchInventory.get() ? this.findBestFood(9, 35) : -1;
         }
      }
   }

   private int findBestFood(int start, int end) {
      int best = -1;
      float bestHunger = -1.0F;

      for(int i = start; i <= end; ++i) {
         class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
         class_4174 food = (class_4174)stack.method_58694(class_9334.field_50075);
         if (food != null) {
            class_1792 item = stack.method_7909();
            if (!((List)this.blacklist.get()).contains(item)) {
               float hunger = ((Priority)this.prioritise.get()).value(food);
               if (hunger > bestHunger) {
                  bestHunger = hunger;
                  best = i;
               }
            }
         }
      }

      return best;
   }

   public static enum ThresholdMode {
      Health((health, hunger) -> health),
      Hunger((health, hunger) -> hunger),
      Any((health, hunger) -> health || hunger),
      Both((health, hunger) -> health && hunger);

      private final BiPredicate<Boolean, Boolean> predicate;

      private ThresholdMode(BiPredicate<Boolean, Boolean> predicate) {
         this.predicate = predicate;
      }

      public boolean test(boolean health, boolean hunger) {
         return this.predicate.test(health, hunger);
      }

      // $FF: synthetic method
      private static ThresholdMode[] $values() {
         return new ThresholdMode[]{Health, Hunger, Any, Both};
      }
   }

   public static enum Priority {
      Combined,
      Hunger,
      Saturation;

      public float value(class_4174 food) {
         float var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = (float)food.comp_2491() + food.comp_2492();
            case 1 -> var10000 = (float)food.comp_2491();
            case 2 -> var10000 = food.comp_2492();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static Priority[] $values() {
         return new Priority[]{Combined, Hunger, Saturation};
      }
   }
}
