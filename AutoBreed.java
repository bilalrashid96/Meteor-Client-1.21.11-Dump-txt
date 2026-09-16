package meteordevelopment.meteorclient.systems.modules.world;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1429;
import net.minecraft.class_3966;

public class AutoBreed extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<Double> range;
   private final Setting<class_1268> hand;
   private final Setting<EntityAge> mobAgeFilter;
   private final Setting<Boolean> continuousBreeding;
   private final Setting<Integer> breedingInterval;
   private final LinkedHashMap<class_1297, Integer> animalsFed;
   private int tickCounter;

   public AutoBreed() {
      super(Categories.World, "auto-breed", "Automatically breeds specified animals.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Entities to breed.")).defaultValue(class_1299.field_6139, class_1299.field_6067, class_1299.field_6085, class_1299.field_6143, class_1299.field_6115, class_1299.field_6093, class_1299.field_6132, class_1299.field_6055, class_1299.field_16281, class_1299.field_6081, class_1299.field_6140, class_1299.field_6074, class_1299.field_6113, class_1299.field_6146, class_1299.field_17943, class_1299.field_20346, class_1299.field_23214, class_1299.field_21973).onlyAttackable().build());
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("How far away the animals can be to be bred.")).min((double)0.0F).defaultValue((double)4.5F).build());
      this.hand = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("hand-for-breeding")).description("The hand to use for breeding.")).defaultValue(class_1268.field_5808)).build());
      this.mobAgeFilter = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mob-age-filter")).description("Determines the age of the mobs to target (baby, adult, or both).")).defaultValue(AutoBreed.EntityAge.Adult)).build());
      this.continuousBreeding = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("continuous-breeding")).description("Whether to feed the same animal again after a certain time period.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      IntSetting.Builder var10002 = (IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("breeding-interval")).description("Determines how often the same animal is fed in ticks.")).min(1).sliderMax(24000).defaultValue(6600);
      Setting var10003 = this.continuousBreeding;
      Objects.requireNonNull(var10003);
      this.breedingInterval = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.animalsFed = new LinkedHashMap();
      this.tickCounter = 0;
   }

   public void onActivate() {
      this.animalsFed.clear();
      this.tickCounter = 0;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      for(class_1297 entity : this.mc.field_1687.method_18112()) {
         if (entity instanceof class_1429 animal) {
            if (((Set)this.entities.get()).contains(animal.method_5864()) && this.isCorrectAge(animal) && !this.animalsFed.containsKey(animal) && PlayerUtils.isWithin((class_1297)animal, (Double)this.range.get()) && animal.method_6481(this.hand.get() == class_1268.field_5808 ? this.mc.field_1724.method_6047() : this.mc.field_1724.method_6079())) {
               Rotations.rotate(Rotations.getYaw(entity), Rotations.getPitch(entity), -100, () -> {
                  class_3966 location = new class_3966(animal, animal.method_5829().method_1005());
                  this.mc.field_1761.method_2917(this.mc.field_1724, animal, location, this.hand.get());
                  this.mc.field_1761.method_2905(this.mc.field_1724, animal, this.hand.get());
                  this.mc.field_1724.method_6104(this.hand.get());
                  this.animalsFed.putLast(animal, this.tickCounter);
               });
               break;
            }
         }
      }

      if ((Boolean)this.continuousBreeding.get()) {
         while(!this.animalsFed.isEmpty() && (Integer)this.animalsFed.firstEntry().getValue() < this.tickCounter - (Integer)this.breedingInterval.get()) {
            this.animalsFed.pollFirstEntry();
         }

         ++this.tickCounter;
      }

   }

   private boolean isCorrectAge(class_1429 animal) {
      boolean var10000;
      switch (((EntityAge)this.mobAgeFilter.get()).ordinal()) {
         case 0 -> var10000 = animal.method_6109();
         case 1 -> var10000 = !animal.method_6109();
         case 2 -> var10000 = true;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
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
