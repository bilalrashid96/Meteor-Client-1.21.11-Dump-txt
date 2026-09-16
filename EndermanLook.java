package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1560;
import net.minecraft.class_2246;
import net.minecraft.class_243;

public class EndermanLook extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Mode> lookMode;
   private final Setting<Boolean> stun;

   public EndermanLook() {
      super(Categories.World, "enderman-look", "Either looks at all Endermen or prevents you from looking at Endermen.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.lookMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("look-mode")).description("How this module behaves.")).defaultValue(EndermanLook.Mode.Away)).build());
      this.stun = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("stun-hostiles")).description("Automatically stares at hostile endermen to stun them in place.")).defaultValue(true)).visible(() -> this.lookMode.get() == EndermanLook.Mode.Away)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (!this.mc.field_1724.method_6118(class_1304.field_6169).method_31574(class_2246.field_10147.method_8389()) && !this.mc.field_1724.method_31549().field_7477) {
         for(class_1297 entity : this.mc.field_1687.method_18112()) {
            if (entity instanceof class_1560) {
               class_1560 enderman = (class_1560)entity;
               if (enderman.method_5805() && this.mc.field_1724.method_6057(enderman)) {
                  switch (((Mode)this.lookMode.get()).ordinal()) {
                     case 0:
                        if (!enderman.method_7028()) {
                           Rotations.rotate(Rotations.getYaw((class_1297)enderman), Rotations.getPitch(enderman, Target.Head), -75, (Runnable)null);
                        }
                        break;
                     case 1:
                        if (enderman.method_7028() && (Boolean)this.stun.get()) {
                           Rotations.rotate(Rotations.getYaw((class_1297)enderman), Rotations.getPitch(enderman, Target.Head), -75, (Runnable)null);
                        } else if (this.angleCheck(enderman)) {
                           Rotations.rotate((double)this.mc.field_1724.method_36454(), (double)90.0F, -75, (Runnable)null);
                        }
                  }
               }
            }
         }

      }
   }

   private boolean angleCheck(class_1560 entity) {
      class_243 vec3d = this.mc.field_1724.method_5828(1.0F).method_1029();
      class_243 vec3d2 = new class_243(entity.method_23317() - this.mc.field_1724.method_23317(), entity.method_23320() - this.mc.field_1724.method_23320(), entity.method_23321() - this.mc.field_1724.method_23321());
      double d = vec3d2.method_1033();
      vec3d2 = vec3d2.method_1029();
      double e = vec3d.method_1026(vec3d2);
      return e > (double)1.0F - 0.025 / d;
   }

   public static enum Mode {
      At,
      Away;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{At, Away};
      }
   }
}
