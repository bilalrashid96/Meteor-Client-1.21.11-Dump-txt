package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseScrollEvent;
import meteordevelopment.meteorclient.events.render.GetFovEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_3532;

public class Zoom extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> zoom;
   private final Setting<Double> scrollSensitivity;
   private final Setting<Boolean> smooth;
   private final Setting<Boolean> cinematic;
   private final Setting<Boolean> hideHud;
   private final Setting<Boolean> renderHands;
   private boolean enabled;
   private boolean preCinematic;
   private double preMouseSensitivity;
   private double value;
   private double lastFov;
   private double time;
   private boolean hudManualToggled;

   public Zoom() {
      super(Categories.Render, "zoom", "Zooms your view.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.zoom = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("zoom")).description("How much to zoom.")).defaultValue((double)6.0F).min((double)1.0F).build());
      this.scrollSensitivity = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scroll-sensitivity")).description("Allows you to change zoom value using scroll wheel. 0 to disable.")).defaultValue((double)1.0F).min((double)0.0F).build());
      this.smooth = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smooth")).description("Smooth transition.")).defaultValue(true)).build());
      this.cinematic = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("cinematic")).description("Enables cinematic camera.")).defaultValue(false)).build());
      this.hideHud = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hide-HUD")).description("Whether or not to hide the Minecraft HUD.")).defaultValue(false)).build());
      this.renderHands = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-hands")).description("Whether or not to render your hands.")).defaultValue(false)).visible(() -> !(Boolean)this.hideHud.get())).build());
      this.autoSubscribe = false;
   }

   public void onActivate() {
      if (!this.enabled) {
         this.preCinematic = this.mc.field_1690.field_1914;
         this.preMouseSensitivity = (Double)this.mc.field_1690.method_42495().method_41753();
         this.value = (Double)this.zoom.get();
         this.lastFov = (double)(Integer)this.mc.field_1690.method_41808().method_41753();
         this.time = 0.001;
         MeteorClient.EVENT_BUS.subscribe(this);
         this.enabled = true;
      }

      if ((Boolean)this.hideHud.get() && !this.mc.field_1690.field_1842) {
         this.hudManualToggled = false;
         this.mc.field_1690.field_1842 = true;
      }

   }

   public void onDeactivate() {
      if ((Boolean)this.hideHud.get() && !this.hudManualToggled) {
         this.mc.field_1690.field_1842 = false;
      }

   }

   @EventHandler
   public void onKeyPressed(KeyEvent event) {
      if (event.key() == 290) {
         this.hudManualToggled = true;
      }
   }

   public void onStop() {
      this.mc.field_1690.field_1914 = this.preCinematic;
      this.mc.field_1690.method_42495().method_41748(this.preMouseSensitivity);
      this.mc.field_1769.method_3292();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      this.mc.field_1690.field_1914 = (Boolean)this.cinematic.get();
      if (!(Boolean)this.cinematic.get()) {
         this.mc.field_1690.method_42495().method_41748(this.preMouseSensitivity / Math.max(this.getScaling() * (double)0.5F, (double)1.0F));
      }

      if (this.time == (double)0.0F) {
         MeteorClient.EVENT_BUS.unsubscribe(this);
         this.enabled = false;
         this.onStop();
      }

   }

   @EventHandler
   private void onMouseScroll(MouseScrollEvent event) {
      if ((Double)this.scrollSensitivity.get() > (double)0.0F && this.isActive()) {
         this.value += event.value * (double)0.25F * (Double)this.scrollSensitivity.get() * this.value;
         if (this.value < (double)1.0F) {
            this.value = (double)1.0F;
         }

         event.cancel();
      }

   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if (!(Boolean)this.smooth.get()) {
         this.time = this.isActive() ? (double)1.0F : (double)0.0F;
      } else {
         if (this.isActive()) {
            this.time += event.frameTime * (double)5.0F;
         } else {
            this.time -= event.frameTime * (double)5.0F;
         }

         this.time = class_3532.method_15350(this.time, (double)0.0F, (double)1.0F);
      }
   }

   @EventHandler
   private void onGetFov(GetFovEvent event) {
      event.fov /= (float)this.getScaling();
      if (this.lastFov != (double)event.fov) {
         this.mc.field_1769.method_3292();
      }

      this.lastFov = (double)event.fov;
   }

   public double getScaling() {
      double delta = this.time < (double)0.5F ? (double)4.0F * this.time * this.time * this.time : (double)1.0F - Math.pow((double)-2.0F * this.time + (double)2.0F, (double)3.0F) / (double)2.0F;
      return class_3532.method_16436(delta, (double)1.0F, this.value);
   }

   public boolean renderHands() {
      return !this.isActive() || (Boolean)this.renderHands.get();
   }
}
