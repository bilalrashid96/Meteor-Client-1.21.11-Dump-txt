package meteordevelopment.meteorclient.systems.modules.world;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.HorizontalDirection;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3965;

public class Flamethrower extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> distance;
   private final Setting<Boolean> antiBreak;
   private final Setting<Boolean> putOutFire;
   private final Setting<Boolean> targetBabies;
   private final Setting<Integer> tickInterval;
   private final Setting<Boolean> rotate;
   private final Setting<Set<class_1299<?>>> entities;
   private class_1297 entity;
   private int ticks;
   private class_1268 hand;

   public Flamethrower() {
      super(Categories.World, "flamethrower", "Ignites every alive piece of food.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.distance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("distance")).description("The maximum distance the animal has to be to be roasted.")).min((double)0.0F).defaultValue((double)5.0F).build());
      this.antiBreak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-break")).description("Prevents flint and steel from being broken.")).defaultValue(false)).build());
      this.putOutFire = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("put-out-fire")).description("Tries to put out the fire when animal is low health, so the items don't burn.")).defaultValue(true)).build());
      this.targetBabies = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("target-babies")).description("If checked babies will also be killed.")).defaultValue(false)).build());
      this.tickInterval = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("tick-interval")).defaultValue(5)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically faces towards the animal roasted.")).defaultValue(true)).build());
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Entities to cook.")).defaultValue(class_1299.field_6093, class_1299.field_6085, class_1299.field_6115, class_1299.field_6132, class_1299.field_6140).build());
      this.ticks = 0;
   }

   public void onDeactivate() {
      this.entity = null;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.entity = null;
      ++this.ticks;
      Iterator var2 = this.mc.field_1687.method_18112().iterator();

      class_1297 entity;
      while(true) {
         if (!var2.hasNext()) {
            return;
         }

         entity = (class_1297)var2.next();
         if (((Set)this.entities.get()).contains(entity.method_5864()) && PlayerUtils.isWithin(entity, (Double)this.distance.get()) && entity != this.mc.field_1724 && entity.method_5805() && !entity.field_27857 && !entity.method_5721() && !entity.method_5753()) {
            if ((Boolean)this.targetBabies.get() || !(entity instanceof class_1309)) {
               break;
            }

            class_1309 livingEntity = (class_1309)entity;
            if (!livingEntity.method_6109()) {
               break;
            }
         }
      }

      FindItemResult item = InvUtils.findInHotbar((Predicate)((itemStack) -> (itemStack.method_31574(class_1802.field_8884) || itemStack.method_31574(class_1802.field_8814)) && (!itemStack.method_7963() || !(Boolean)this.antiBreak.get() || itemStack.method_7919() < itemStack.method_7936() - 1)));
      if (InvUtils.swap(item.slot(), true)) {
         this.hand = item.getHand();
         this.entity = entity;
         if ((Boolean)this.rotate.get()) {
            Rotations.rotate(Rotations.getYaw(entity.method_24515()), Rotations.getPitch(entity.method_24515()), -100, this::interact);
         } else {
            this.interact();
         }

      }
   }

   private void interact() {
      class_2248 block = this.mc.field_1687.method_8320(this.entity.method_24515()).method_26204();
      class_2248 bottom = this.mc.field_1687.method_8320(this.entity.method_24515().method_10074()).method_26204();
      if (block != class_2246.field_10382 && bottom != class_2246.field_10382 && bottom != class_2246.field_10194) {
         if (block == class_2246.field_10219) {
            this.mc.field_1761.method_2910(this.entity.method_24515(), class_2350.field_11033);
         }

         label33: {
            if ((Boolean)this.putOutFire.get()) {
               class_1297 var4 = this.entity;
               if (var4 instanceof class_1309) {
                  class_1309 animal = (class_1309)var4;
                  if (animal.method_6032() < 2.0F) {
                     this.mc.field_1761.method_2910(this.entity.method_24515(), class_2350.field_11033);
                     HorizontalDirection[] var8 = HorizontalDirection.values();
                     int var5 = var8.length;
                     int var6 = 0;

                     while(true) {
                        if (var6 >= var5) {
                           break label33;
                        }

                        HorizontalDirection direction = var8[var6];
                        this.mc.field_1761.method_2910(this.entity.method_24515().method_10069(direction.offsetX, 0, direction.offsetZ), class_2350.field_11033);
                        ++var6;
                     }
                  }
               }
            }

            if (this.ticks >= (Integer)this.tickInterval.get() && !this.entity.method_5809()) {
               this.mc.field_1761.method_2896(this.mc.field_1724, this.hand, new class_3965(this.entity.method_73189().method_1020(new class_243((double)0.0F, (double)1.0F, (double)0.0F)), class_2350.field_11036, this.entity.method_24515().method_10074(), false));
               this.ticks = 0;
            }
         }

         InvUtils.swapBack();
      }
   }
}
