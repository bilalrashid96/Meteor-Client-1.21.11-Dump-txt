package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.FishingBobberEntityAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1787;
import net.minecraft.class_1799;
import net.minecraft.class_1893;
import net.minecraft.class_1536.class_1537;

public class AutoFish extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> autoSwitch;
   private final Setting<Boolean> antiBreak;
   private final Setting<Boolean> autoCast;
   private final Setting<Integer> castDelay;
   private final Setting<Integer> castDelayVariance;
   private final Setting<Integer> catchDelay;
   private final Setting<Integer> catchDelayVariance;
   private double castDelayLeft;
   private double catchDelayLeft;
   private boolean wasHooked;

   public AutoFish() {
      super(Categories.Player, "auto-fish", "Automatically fishes for you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.autoSwitch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-switch")).description("Automatically switch to a fishing rod.")).defaultValue(true)).build());
      this.antiBreak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-break")).description("Avoid using rods that would break if they were cast.")).defaultValue(true)).build());
      this.autoCast = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-cast")).description("Automatically cast the fishing rod.")).defaultValue(true)).build());
      this.castDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("cast-delay")).description("How long to wait between recasts if the bobber fails to land in water.")).defaultValue(14)).min(1).sliderMax(60).build());
      this.castDelayVariance = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("cast-delay-variance")).description("Maximum amount of randomness added to cast delay.")).defaultValue(0)).min(0).sliderMax(30).build());
      this.catchDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("catch-delay")).description("How long to wait after hooking a fish to reel it in.")).defaultValue(6)).min(1).sliderMax(20).build());
      this.catchDelayVariance = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("catch-delay-variance")).description("Maximum amount of randomness added to catch delay.")).defaultValue(0)).min(0).sliderMax(10).build());
      this.castDelayLeft = (double)0.0F;
      this.catchDelayLeft = (double)0.0F;
      this.wasHooked = false;
   }

   public void onActivate() {
      this.castDelayLeft = (double)0.0F;
      this.catchDelayLeft = (double)0.0F;
      this.wasHooked = false;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      int bestRodSlot = this.findBestRod();
      if ((Boolean)this.autoSwitch.get() && bestRodSlot != -1 && this.mc.field_1724.method_31548().method_67532() != bestRodSlot) {
         InvUtils.swap(bestRodSlot, false);
      }

      if (this.mc.field_1724.method_6047().method_7909() instanceof class_1787) {
         this.tryCast();
         this.tryCatch();
      }
   }

   private void tryCast() {
      if (this.mc.field_1724.field_7513 == null) {
         if ((Boolean)this.autoCast.get()) {
            if (this.castDelayLeft > (double)0.0F) {
               this.castDelayLeft -= (double)TickRate.INSTANCE.getTickRate() / (double)20.0F;
            } else {
               this.useRod();
            }
         }
      }
   }

   private void tryCatch() {
      if (this.mc.field_1724.field_7513 != null) {
         if (this.mc.field_1724.field_7513.method_26957() != null) {
            this.useRod();
         } else if (this.mc.field_1724.field_7513.field_7175 == class_1537.field_7179) {
            if (!this.wasHooked) {
               if (((FishingBobberEntityAccessor)this.mc.field_1724.field_7513).meteor$hasCaughtFish()) {
                  this.catchDelayLeft = this.randomizeDelay((Integer)this.catchDelay.get(), (Integer)this.catchDelayVariance.get());
                  this.wasHooked = true;
               }

            } else if (this.catchDelayLeft > (double)0.0F) {
               this.catchDelayLeft -= (double)TickRate.INSTANCE.getTickRate() / (double)20.0F;
            } else {
               this.useRod();
            }
         }
      }
   }

   private void useRod() {
      Utils.rightClick();
      this.wasHooked = false;
      this.castDelayLeft = this.randomizeDelay((Integer)this.castDelay.get(), (Integer)this.castDelayVariance.get());
   }

   private int findBestRod() {
      int bestSlot = -1;
      int bestScore = -1;

      for(int i = 0; i < 9; ++i) {
         class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
         if (stack.method_7909() instanceof class_1787 && (!(Boolean)this.antiBreak.get() || stack.method_7919() != stack.method_7936() - 1)) {
            int score = 0;
            score += Utils.getEnchantmentLevel(stack, class_1893.field_9114);
            score += Utils.getEnchantmentLevel(stack, class_1893.field_9100);
            score += Utils.getEnchantmentLevel(stack, class_1893.field_9101);
            score += Utils.getEnchantmentLevel(stack, class_1893.field_9119);
            if (score > bestScore) {
               bestScore = score;
               bestSlot = i;
            }

            if (score == 10) {
               break;
            }
         }
      }

      return bestSlot;
   }

   private double randomizeDelay(int delay, int variance) {
      if (variance == 0) {
         return (double)delay;
      } else {
         double scale = Math.sqrt((double)-2.0F * Math.log(Utils.random(1.0E-4, (double)1.0F)));
         double angle = (Math.PI * 2D) * Utils.random((double)0.0F, (double)1.0F);
         double norm = scale * Math.cos(angle);
         double MAX_SD = (double)3.0F;
         norm = Math.clamp(norm, (double)-3.0F, (double)3.0F) / (double)3.0F;
         delay += Math.round((float)(norm * (double)variance));
         return (double)Math.max(1, delay);
      }
   }
}
