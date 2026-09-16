package meteordevelopment.meteorclient.systems.modules.combat;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2244;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2846;
import net.minecraft.class_2879;
import net.minecraft.class_2846.class_2847;

public class AntiBed extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> placeStringTop;
   private final Setting<Boolean> placeStringMiddle;
   private final Setting<Boolean> placeStringBottom;
   private final Setting<Boolean> onlyInHole;
   private boolean breaking;

   public AntiBed() {
      super(Categories.Combat, "anti-bed", "Places string to prevent beds being placed on you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.placeStringTop = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("place-string-top")).description("Places string above you.")).defaultValue(false)).build());
      this.placeStringMiddle = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("place-string-middle")).description("Places string in your upper hitbox.")).defaultValue(true)).build());
      this.placeStringBottom = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("place-string-bottom")).description("Places string at your feet.")).defaultValue(false)).build());
      this.onlyInHole = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-in-hole")).description("Only functions when you are standing in a hole.")).defaultValue(true)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (!(Boolean)this.onlyInHole.get() || PlayerUtils.isInHole(true)) {
         class_2338 head = this.mc.field_1724.method_24515().method_10084();
         if (this.mc.field_1687.method_8320(head).method_26204() instanceof class_2244 && !this.breaking) {
            Rotations.rotate(Rotations.getYaw(head), Rotations.getPitch(head), 50, () -> this.sendMinePackets(head));
            this.breaking = true;
         } else if (this.breaking) {
            Rotations.rotate(Rotations.getYaw(head), Rotations.getPitch(head), 50, () -> this.sendStopPackets(head));
            this.breaking = false;
         }

         if ((Boolean)this.placeStringTop.get()) {
            this.place(this.mc.field_1724.method_24515().method_10086(2));
         }

         if ((Boolean)this.placeStringMiddle.get()) {
            this.place(this.mc.field_1724.method_24515().method_10086(1));
         }

         if ((Boolean)this.placeStringBottom.get()) {
            this.place(this.mc.field_1724.method_24515());
         }

      }
   }

   private void place(class_2338 blockPos) {
      if (this.mc.field_1687.method_8320(blockPos).method_26204().method_8389() != class_1802.field_8276) {
         BlockUtils.place(blockPos, InvUtils.findInHotbar(class_1802.field_8276), 50, false);
      }

   }

   private void sendMinePackets(class_2338 blockPos) {
      this.mc.field_1761.method_41931(this.mc.field_1687, (sequence) -> new class_2846(class_2847.field_12968, blockPos, class_2350.field_11036, sequence));
      this.mc.field_1761.method_41931(this.mc.field_1687, (sequence) -> new class_2846(class_2847.field_12973, blockPos, class_2350.field_11036, sequence));
   }

   private void sendStopPackets(class_2338 blockPos) {
      this.mc.method_1562().method_52787(new class_2846(class_2847.field_12971, blockPos, class_2350.field_11036));
      this.mc.method_1562().method_52787(new class_2879(class_1268.field_5808));
   }
}
