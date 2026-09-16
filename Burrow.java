package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1747;
import net.minecraft.class_1802;
import net.minecraft.class_2199;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2828;
import net.minecraft.class_2879;
import net.minecraft.class_3965;

public class Burrow extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Block> block;
   private final Setting<Boolean> instant;
   private final Setting<Boolean> automatic;
   private final Setting<Double> triggerHeight;
   private final Setting<Double> rubberbandHeight;
   private final Setting<Double> timer;
   private final Setting<Boolean> onlyInHole;
   private final Setting<Boolean> center;
   private final Setting<Boolean> rotate;
   private final class_2338.class_2339 blockPos;
   private boolean shouldBurrow;

   public Burrow() {
      super(Categories.Combat, "burrow", "Attempts to clip you into a block.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.block = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("block-to-use")).description("The block to use for Burrow.")).defaultValue(Burrow.Block.EChest)).build());
      this.instant = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("instant")).description("Jumps with packets rather than vanilla jump.")).defaultValue(true)).build());
      this.automatic = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("automatic")).description("Automatically burrows on activate rather than waiting for jump.")).defaultValue(true)).build());
      this.triggerHeight = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("trigger-height")).description("How high you have to jump before a rubberband is triggered.")).defaultValue(1.12).range(0.01, 1.4).sliderRange(0.01, 1.4).build());
      this.rubberbandHeight = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rubberband-height")).description("How far to attempt to cause rubberband.")).defaultValue((double)12.0F).sliderMin((double)-30.0F).sliderMax((double)30.0F).build());
      this.timer = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("timer")).description("Timer override.")).defaultValue((double)1.0F).min(0.01).sliderRange(0.01, (double)10.0F).build());
      this.onlyInHole = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-in-holes")).description("Stops you from burrowing when not in a hole.")).defaultValue(false)).build());
      this.center = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("center")).description("Centers you to the middle of the block before burrowing.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Faces the block you place server-side.")).defaultValue(true)).build());
      this.blockPos = new class_2338.class_2339();
   }

   public void onActivate() {
      if (!this.mc.field_1687.method_8320(this.mc.field_1724.method_24515()).method_45474()) {
         this.error("Already burrowed, disabling.", new Object[0]);
         this.toggle();
      } else if (!PlayerUtils.isInHole(false) && (Boolean)this.onlyInHole.get()) {
         this.error("Not in a hole, disabling.", new Object[0]);
         this.toggle();
      } else if (!this.checkHead()) {
         this.error("Not enough headroom to burrow, disabling.", new Object[0]);
         this.toggle();
      } else {
         FindItemResult result = this.getItem();
         if (!result.isHotbar() && !result.isOffhand()) {
            this.error("No burrow block found, disabling.", new Object[0]);
            this.toggle();
         } else {
            this.blockPos.method_10101(this.mc.field_1724.method_24515());
            ((Timer)Modules.get().get(Timer.class)).setOverride((Double)this.timer.get());
            this.shouldBurrow = false;
            if ((Boolean)this.automatic.get()) {
               if ((Boolean)this.instant.get()) {
                  this.shouldBurrow = true;
               } else {
                  this.mc.field_1724.method_6043();
               }
            } else {
               this.info("Waiting for manual jump.", new Object[0]);
            }

         }
      }
   }

   public void onDeactivate() {
      ((Timer)Modules.get().get(Timer.class)).setOverride((double)1.0F);
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (!(Boolean)this.instant.get()) {
         this.shouldBurrow = this.mc.field_1724.method_23318() > (double)this.blockPos.method_10264() + (Double)this.triggerHeight.get();
      }

      if (!this.shouldBurrow && (Boolean)this.instant.get()) {
         this.blockPos.method_10101(this.mc.field_1724.method_24515());
      }

      if (this.shouldBurrow) {
         if ((Boolean)this.rotate.get()) {
            Rotations.rotate(Rotations.getYaw(this.mc.field_1724.method_24515()), Rotations.getPitch(this.mc.field_1724.method_24515()), 50, this::burrow);
         } else {
            this.burrow();
         }

         this.toggle();
      }

   }

   @EventHandler
   private void onKey(KeyEvent event) {
      if ((Boolean)this.instant.get() && !this.shouldBurrow) {
         if (event.action == KeyAction.Press && this.mc.field_1690.field_1903.method_1417(event.input)) {
            this.shouldBurrow = true;
         }

         this.blockPos.method_10101(this.mc.field_1724.method_24515());
      }

   }

   private void burrow() {
      if ((Boolean)this.center.get()) {
         PlayerUtils.centerPlayer();
      }

      if ((Boolean)this.instant.get()) {
         this.mc.field_1724.field_3944.method_52787(new class_2828.class_2829(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + 0.4, this.mc.field_1724.method_23321(), false, this.mc.field_1724.field_5976));
         this.mc.field_1724.field_3944.method_52787(new class_2828.class_2829(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + (double)0.75F, this.mc.field_1724.method_23321(), false, this.mc.field_1724.field_5976));
         this.mc.field_1724.field_3944.method_52787(new class_2828.class_2829(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + 1.01, this.mc.field_1724.method_23321(), false, this.mc.field_1724.field_5976));
         this.mc.field_1724.field_3944.method_52787(new class_2828.class_2829(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + 1.15, this.mc.field_1724.method_23321(), false, this.mc.field_1724.field_5976));
      }

      FindItemResult block = this.getItem();
      if (this.mc.field_1724.method_31548().method_5438(block.slot()).method_7909() instanceof class_1747) {
         InvUtils.swap(block.slot(), true);
         this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5808, new class_3965(Utils.vec3d(this.blockPos), class_2350.field_11036, this.blockPos, false));
         this.mc.field_1724.field_3944.method_52787(new class_2879(class_1268.field_5808));
         InvUtils.swapBack();
         if ((Boolean)this.instant.get()) {
            this.mc.field_1724.field_3944.method_52787(new class_2828.class_2829(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + (Double)this.rubberbandHeight.get(), this.mc.field_1724.method_23321(), false, this.mc.field_1724.field_5976));
         } else {
            this.mc.field_1724.method_30634(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + (Double)this.rubberbandHeight.get(), this.mc.field_1724.method_23321());
         }

      }
   }

   private FindItemResult getItem() {
      FindItemResult var10000;
      switch (((Block)this.block.get()).ordinal()) {
         case 0:
            var10000 = InvUtils.findInHotbar(class_1802.field_8466);
            break;
         case 1:
         default:
            var10000 = InvUtils.findInHotbar(class_1802.field_8281, class_1802.field_22421);
            break;
         case 2:
            var10000 = InvUtils.findInHotbar((Predicate)((itemStack) -> class_2248.method_9503(itemStack.method_7909()) instanceof class_2199));
            break;
         case 3:
            var10000 = new FindItemResult(this.mc.field_1724.method_31548().method_67532(), this.mc.field_1724.method_6047().method_7947());
      }

      return var10000;
   }

   private boolean checkHead() {
      class_2680 blockState1 = this.mc.field_1687.method_8320(this.blockPos.method_10102(this.mc.field_1724.method_23317() + 0.3, this.mc.field_1724.method_23318() + 2.3, this.mc.field_1724.method_23321() + 0.3));
      class_2680 blockState2 = this.mc.field_1687.method_8320(this.blockPos.method_10102(this.mc.field_1724.method_23317() + 0.3, this.mc.field_1724.method_23318() + 2.3, this.mc.field_1724.method_23321() - 0.3));
      class_2680 blockState3 = this.mc.field_1687.method_8320(this.blockPos.method_10102(this.mc.field_1724.method_23317() - 0.3, this.mc.field_1724.method_23318() + 2.3, this.mc.field_1724.method_23321() - 0.3));
      class_2680 blockState4 = this.mc.field_1687.method_8320(this.blockPos.method_10102(this.mc.field_1724.method_23317() - 0.3, this.mc.field_1724.method_23318() + 2.3, this.mc.field_1724.method_23321() + 0.3));
      boolean air1 = blockState1.method_45474();
      boolean air2 = blockState2.method_45474();
      boolean air3 = blockState3.method_45474();
      boolean air4 = blockState4.method_45474();
      return air1 && air2 && air3 && air4;
   }

   public static enum Block {
      EChest,
      Obsidian,
      Anvil,
      Held;

      // $FF: synthetic method
      private static Block[] $values() {
         return new Block[]{EChest, Obsidian, Anvil, Held};
      }
   }
}
