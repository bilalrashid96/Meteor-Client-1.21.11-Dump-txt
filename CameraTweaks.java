package meteordevelopment.meteorclient.systems.modules.render;

import java.util.Objects;
import meteordevelopment.meteorclient.events.game.ChangePerspectiveEvent;
import meteordevelopment.meteorclient.events.meteor.MouseScrollEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_5498;

public class CameraTweaks extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgScrolling;
   private final Setting<Boolean> clip;
   private final Setting<Double> cameraDistance;
   private final Setting<Boolean> scrollingEnabled;
   private final Setting<Keybind> scrollKeybind;
   private final Setting<Double> scrollSensitivity;
   public double distance;

   public CameraTweaks() {
      super(Categories.Render, "camera-tweaks", "Allows modification of the third person camera.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgScrolling = this.settings.createGroup("Scrolling");
      this.clip = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("clip")).description("Allows the camera to clip through blocks.")).defaultValue(true)).build());
      this.cameraDistance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("camera-distance")).description("The distance the third person camera is from the player.")).defaultValue((double)4.0F).min((double)0.0F).onChanged((value) -> this.distance = value)).build());
      this.scrollingEnabled = this.sgScrolling.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("scrolling")).description("Allows you to scroll to change camera distance.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgScrolling;
      KeybindSetting.Builder var10002 = (KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("bind")).description("Binds camera distance scrolling to a key.");
      Setting var10003 = this.scrollingEnabled;
      Objects.requireNonNull(var10003);
      this.scrollKeybind = var10001.add(((KeybindSetting.Builder)((KeybindSetting.Builder)var10002.visible(var10003::get)).defaultValue(Keybind.fromKey(342))).build());
      var10001 = this.sgScrolling;
      DoubleSetting.Builder var2 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("sensitivity")).description("Sensitivity of the scroll wheel when changing the cameras distance.");
      var10003 = this.scrollingEnabled;
      Objects.requireNonNull(var10003);
      this.scrollSensitivity = var10001.add(((DoubleSetting.Builder)var2.visible(var10003::get)).defaultValue((double)1.0F).min(0.01).build());
   }

   public void onActivate() {
      this.distance = (Double)this.cameraDistance.get();
   }

   @EventHandler
   private void onPerspectiveChanged(ChangePerspectiveEvent event) {
      this.distance = (Double)this.cameraDistance.get();
   }

   @EventHandler
   private void onMouseScroll(MouseScrollEvent event) {
      if (this.mc.field_1690.method_31044() != class_5498.field_26664 && this.mc.field_1755 == null && (Boolean)this.scrollingEnabled.get() && (!((Keybind)this.scrollKeybind.get()).isSet() || ((Keybind)this.scrollKeybind.get()).isPressed())) {
         if ((Double)this.scrollSensitivity.get() > (double)0.0F) {
            this.distance -= event.value * (double)0.25F * (Double)this.scrollSensitivity.get() * this.distance;
            event.cancel();
         }

      }
   }

   public boolean clip() {
      return this.isActive() && (Boolean)this.clip.get();
   }
}
