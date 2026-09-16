package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_3532;
import net.minecraft.class_5498;

public class FreeLook extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgArrows;
   public final Setting<Mode> mode;
   public final Setting<Boolean> togglePerspective;
   public final Setting<Double> sensitivity;
   public final Setting<Boolean> arrows;
   private final Setting<Double> arrowSpeed;
   public float cameraYaw;
   public float cameraPitch;
   private class_5498 prePers;

   public FreeLook() {
      super(Categories.Render, "free-look", "Allows more rotation options in third person.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgArrows = this.settings.createGroup("Arrows");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Which entity to rotate.")).defaultValue(FreeLook.Mode.Player)).build());
      this.togglePerspective = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-perspective")).description("Changes your perspective on toggle.")).defaultValue(true)).build());
      this.sensitivity = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("camera-sensitivity")).description("How fast the camera moves in camera mode.")).defaultValue((double)8.0F).min((double)0.0F).sliderMax((double)10.0F).build());
      this.arrows = this.sgArrows.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("arrows-control-opposite")).description("Allows you to control the other entities rotation with the arrow keys.")).defaultValue(true)).build());
      this.arrowSpeed = this.sgArrows.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("arrow-speed")).description("Rotation speed with arrow keys.")).defaultValue((double)4.0F).min((double)0.0F).build());
   }

   public void onActivate() {
      this.cameraYaw = this.mc.field_1724.method_36454();
      this.cameraPitch = this.mc.field_1724.method_36455();
      this.prePers = this.mc.field_1690.method_31044();
      if (this.prePers != class_5498.field_26665 && (Boolean)this.togglePerspective.get()) {
         this.mc.field_1690.method_31043(class_5498.field_26665);
      }

   }

   public void onDeactivate() {
      if (this.mc.field_1690.method_31044() != this.prePers && (Boolean)this.togglePerspective.get()) {
         this.mc.field_1690.method_31043(this.prePers);
      }

   }

   public boolean playerMode() {
      return this.isActive() && this.mc.field_1690.method_31044() == class_5498.field_26665 && this.mode.get() == FreeLook.Mode.Player;
   }

   public boolean cameraMode() {
      return this.isActive() && this.mode.get() == FreeLook.Mode.Camera;
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if ((Boolean)this.arrows.get()) {
         for(int i = 0; (double)i < (Double)this.arrowSpeed.get() * (double)2.0F; ++i) {
            switch (((Mode)this.mode.get()).ordinal()) {
               case 0:
                  if (Input.isKeyPressed(263)) {
                     this.cameraYaw = (float)((double)this.cameraYaw - (double)0.5F);
                  }

                  if (Input.isKeyPressed(262)) {
                     this.cameraYaw = (float)((double)this.cameraYaw + (double)0.5F);
                  }

                  if (Input.isKeyPressed(265)) {
                     this.cameraPitch = (float)((double)this.cameraPitch - (double)0.5F);
                  }

                  if (Input.isKeyPressed(264)) {
                     this.cameraPitch = (float)((double)this.cameraPitch + (double)0.5F);
                  }
                  break;
               case 1:
                  float yaw = this.mc.field_1724.method_36454();
                  float pitch = this.mc.field_1724.method_36455();
                  if (Input.isKeyPressed(263)) {
                     yaw = (float)((double)yaw - (double)0.5F);
                  }

                  if (Input.isKeyPressed(262)) {
                     yaw = (float)((double)yaw + (double)0.5F);
                  }

                  if (Input.isKeyPressed(265)) {
                     pitch = (float)((double)pitch - (double)0.5F);
                  }

                  if (Input.isKeyPressed(264)) {
                     pitch = (float)((double)pitch + (double)0.5F);
                  }

                  this.mc.field_1724.method_36456(yaw);
                  this.mc.field_1724.method_36457(pitch);
            }
         }
      }

      this.mc.field_1724.method_36457(class_3532.method_15363(this.mc.field_1724.method_36455(), -90.0F, 90.0F));
      this.cameraPitch = class_3532.method_15363(this.cameraPitch, -90.0F, 90.0F);
   }

   public static enum Mode {
      Player,
      Camera;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Player, Camera};
      }
   }
}
