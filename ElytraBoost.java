package meteordevelopment.meteorclient.systems.modules.movement;

import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.events.entity.player.InteractItemEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1671;
import net.minecraft.class_1781;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_9284;
import net.minecraft.class_9334;

public class ElytraBoost extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> dontConsumeFirework;
   private final Setting<Integer> fireworkLevel;
   private final Setting<Boolean> playSound;
   private final Setting<Keybind> keybind;
   private final List<class_1671> fireworks;

   public ElytraBoost() {
      super(Categories.Movement, "elytra-boost", "Boosts your elytra as if you used a firework.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.dontConsumeFirework = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-consume")).description("Prevents fireworks from being consumed when using Elytra Boost.")).defaultValue(true)).build());
      this.fireworkLevel = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("firework-duration")).description("The duration of the firework.")).defaultValue(0)).range(0, 255).sliderMax(255).build());
      this.playSound = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("play-sound")).description("Plays the firework sound when a boost is triggered.")).defaultValue(true)).build());
      this.keybind = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("keybind")).description("The keybind to boost.")).action(this::boost).build());
      this.fireworks = new ArrayList();
   }

   public void onDeactivate() {
      this.fireworks.clear();
   }

   @EventHandler
   private void onInteractItem(InteractItemEvent event) {
      class_1799 itemStack = this.mc.field_1724.method_5998(event.hand);
      if (itemStack.method_7909() instanceof class_1781 && (Boolean)this.dontConsumeFirework.get()) {
         event.toReturn = class_1269.field_5811;
         this.boost();
      }

   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      this.fireworks.removeIf(class_1297::method_31481);
   }

   private void boost() {
      if (Utils.canUpdate()) {
         if (this.mc.field_1724.method_6128() && this.mc.field_1755 == null) {
            class_1799 itemStack = class_1802.field_8639.method_7854();
            itemStack.method_57379(class_9334.field_49616, new class_9284((Integer)this.fireworkLevel.get(), ((class_9284)itemStack.method_58694(class_9334.field_49616)).comp_2392()));
            class_1671 entity = new class_1671(this.mc.field_1687, itemStack, this.mc.field_1724);
            this.fireworks.add(entity);
            if ((Boolean)this.playSound.get()) {
               this.mc.field_1687.method_43129(this.mc.field_1724, entity, class_3417.field_14702, class_3419.field_15256, 3.0F, 1.0F);
            }

            this.mc.field_1687.method_53875(entity);
         }

      }
   }

   public boolean isFirework(class_1671 firework) {
      return this.isActive() && this.fireworks.contains(firework);
   }
}
