package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1304;
import net.minecraft.class_1657;
import net.minecraft.class_2199;
import net.minecraft.class_2231;
import net.minecraft.class_2248;
import net.minecraft.class_2269;
import net.minecraft.class_2338;
import net.minecraft.class_471;

public class AutoAnvil extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> range;
   private final Setting<SortPriority> priority;
   private final Setting<Integer> height;
   private final Setting<Integer> delay;
   private final Setting<Boolean> placeButton;
   private final Setting<Boolean> multiPlace;
   private final Setting<Boolean> toggleOnBreak;
   private final Setting<Boolean> rotate;
   private class_1657 target;
   private int timer;

   public AutoAnvil() {
      super(Categories.Combat, "auto-anvil", "Automatically places anvils above players to destroy helmets.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("The radius in which players get targeted.")).defaultValue((double)4.0F).min((double)0.0F).sliderMax((double)5.0F).build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-priority")).description("How to select the player to target.")).defaultValue(SortPriority.LowestHealth)).build());
      this.height = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("height")).description("The height to place anvils at.")).defaultValue(2)).range(0, 5).sliderMax(5).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("The delay in between anvil placements.")).defaultValue(10)).min(0).sliderMax(50).build());
      this.placeButton = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("place-at-feet")).description("Automatically places a button or pressure plate at the targets feet to break the anvils.")).defaultValue(true)).build());
      this.multiPlace = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("multi-place")).description("Places multiple anvils at once.")).defaultValue(true)).build());
      this.toggleOnBreak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-break")).description("Toggles when the target's helmet slot is empty.")).defaultValue(false)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically rotates towards the position anvils/pressure plates/buttons are placed.")).defaultValue(true)).build());
   }

   public void onActivate() {
      this.timer = 0;
      this.target = null;
   }

   @EventHandler
   private void onOpenScreen(OpenScreenEvent event) {
      if (event.screen instanceof class_471) {
         event.cancel();
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if ((Boolean)this.toggleOnBreak.get() && this.target != null && this.target.method_6118(class_1304.field_6169).method_7960()) {
         this.error("Target head slot is empty... disabling.", new Object[0]);
         this.toggle();
      } else {
         if (TargetUtils.isBadTarget(this.target, (Double)this.range.get())) {
            this.target = TargetUtils.getPlayerTarget((Double)this.range.get(), this.priority.get());
            if (TargetUtils.isBadTarget(this.target, (Double)this.range.get())) {
               return;
            }
         }

         if ((Boolean)this.placeButton.get()) {
            FindItemResult floorBlock = InvUtils.findInHotbar((Predicate)((itemStack) -> class_2248.method_9503(itemStack.method_7909()) instanceof class_2231 || class_2248.method_9503(itemStack.method_7909()) instanceof class_2269));
            BlockUtils.place(this.target.method_24515(), floorBlock, (Boolean)this.rotate.get(), 0, false);
         }

         if (this.timer >= (Integer)this.delay.get()) {
            this.timer = 0;
            FindItemResult anvil = InvUtils.findInHotbar((Predicate)((itemStack) -> class_2248.method_9503(itemStack.method_7909()) instanceof class_2199));
            if (!anvil.found()) {
               return;
            }

            for(int i = (Integer)this.height.get(); i > 1; --i) {
               class_2338 blockPos = this.target.method_24515().method_10084().method_10069(0, i, 0);

               for(int j = 0; j < i && this.mc.field_1687.method_8320(this.target.method_24515().method_10086(j + 1)).method_45474(); ++j) {
               }

               if (BlockUtils.place(blockPos, anvil, (Boolean)this.rotate.get(), 0) && !(Boolean)this.multiPlace.get()) {
                  break;
               }
            }
         } else {
            ++this.timer;
         }

      }
   }

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }
}
