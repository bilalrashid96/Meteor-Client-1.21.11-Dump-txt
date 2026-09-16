package meteordevelopment.meteorclient.systems.modules.world;

import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1472;
import net.minecraft.class_1802;
import net.minecraft.class_3966;

public class AutoShearer extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> distance;
   private final Setting<Boolean> antiBreak;
   private final Setting<Boolean> rotate;
   private class_1297 entity;
   private class_1268 hand;

   public AutoShearer() {
      super(Categories.World, "auto-shearer", "Automatically shears sheep.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.distance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("distance")).description("The maximum distance the sheep have to be to be sheared.")).min((double)0.0F).defaultValue((double)5.0F).build());
      this.antiBreak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-break")).description("Prevents shears from being broken.")).defaultValue(false)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically faces towards the animal being sheared.")).defaultValue(true)).build());
   }

   public void onDeactivate() {
      this.entity = null;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.entity = null;

      for(class_1297 entity : this.mc.field_1687.method_18112()) {
         if (entity instanceof class_1472 && !((class_1472)entity).method_6629() && !((class_1472)entity).method_6109() && PlayerUtils.isWithin(entity, (Double)this.distance.get())) {
            FindItemResult findShear = InvUtils.findInHotbar((Predicate)((itemStack) -> itemStack.method_7909() == class_1802.field_8868 && (!(Boolean)this.antiBreak.get() || itemStack.method_7919() < itemStack.method_7936() - 1)));
            if (!InvUtils.swap(findShear.slot(), true)) {
               return;
            }

            this.hand = findShear.getHand();
            this.entity = entity;
            if ((Boolean)this.rotate.get()) {
               Rotations.rotate(Rotations.getYaw(entity), Rotations.getPitch(entity), -100, this::interact);
            } else {
               this.interact();
            }

            return;
         }
      }

   }

   private void interact() {
      class_3966 location = new class_3966(this.entity, this.entity.method_5829().method_1005());
      this.mc.field_1761.method_2917(this.mc.field_1724, this.entity, location, this.hand);
      this.mc.field_1761.method_2905(this.mc.field_1724, this.entity, this.hand);
      InvUtils.swapBack();
   }
}
