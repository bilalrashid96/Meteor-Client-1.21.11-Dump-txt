package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1657;
import net.minecraft.class_3532;

public class PlayerModelHud extends HudElement {
   public static final HudElementInfo<PlayerModelHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgScale;
   private final SettingGroup sgBackground;
   private final Setting<Boolean> copyYaw;
   private final Setting<Integer> customYaw;
   private final Setting<Boolean> copyPitch;
   private final Setting<Integer> customPitch;
   private final Setting<CenterOrientation> centerOrientation;
   public final Setting<Boolean> customScale;
   public final Setting<Double> scale;
   private final Setting<Boolean> background;
   private final Setting<SettingColor> backgroundColor;

   public PlayerModelHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgScale = this.settings.createGroup("Scale");
      this.sgBackground = this.settings.createGroup("Background");
      this.copyYaw = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("copy-yaw")).description("Makes the player model's yaw equal to yours.")).defaultValue(true)).build());
      this.customYaw = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("custom-yaw")).description("Custom yaw for when copy yaw is off.")).defaultValue(0)).range(-180, 180).sliderRange(-180, 180).visible(() -> !(Boolean)this.copyYaw.get())).build());
      this.copyPitch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("copy-pitch")).description("Makes the player model's pitch equal to yours.")).defaultValue(true)).build());
      this.customPitch = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("custom-pitch")).description("Custom pitch for when copy pitch is off.")).defaultValue(0)).range(-90, 90).sliderRange(-90, 90).visible(() -> !(Boolean)this.copyPitch.get())).build());
      this.centerOrientation = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("center-orientation")).description("Which direction the player faces when the HUD model faces directly forward.")).defaultValue(PlayerModelHud.CenterOrientation.South)).build());
      this.customScale = this.sgScale.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-scale")).description("Applies a custom scale to this hud element.")).defaultValue(false)).onChanged((aBoolean) -> this.calculateSize())).build());
      SettingGroup var10001 = this.sgScale;
      DoubleSetting.Builder var10002 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Custom scale.");
      Setting var10003 = this.customScale;
      Objects.requireNonNull(var10003);
      this.scale = var10001.add(((DoubleSetting.Builder)((DoubleSetting.Builder)var10002.visible(var10003::get)).defaultValue((double)2.0F).onChanged((aDouble) -> this.calculateSize())).min((double)0.5F).sliderRange((double)0.5F, (double)3.0F).build());
      this.background = this.sgBackground.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      var10001 = this.sgBackground;
      ColorSetting.Builder var2 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var2.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
      this.calculateSize();
   }

   public void render(HudRenderer renderer) {
      renderer.post(() -> {
         class_1657 player = MeteorClient.mc.field_1724;
         if (player != null) {
            float offsetYaw = this.centerOrientation.get() == PlayerModelHud.CenterOrientation.North ? 180.0F : 0.0F;
            float yaw = (Boolean)this.copyYaw.get() ? class_3532.method_15393(player.field_5982 + (player.method_36454() - player.field_5982) * MeteorClient.mc.method_61966().method_60637(true) + offsetYaw) : (float)(Integer)this.customYaw.get();
            float pitch = (Boolean)this.copyPitch.get() ? player.method_36455() : (float)(Integer)this.customPitch.get();
            renderer.entity(player, this.x, this.y, this.getWidth(), this.getHeight(), -yaw, -pitch);
         }
      });
      if ((Boolean)this.background.get()) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), this.backgroundColor.get());
      } else if (MeteorClient.mc.field_1724 == null) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), this.backgroundColor.get());
         renderer.line((double)this.x, (double)this.y, (double)(this.x + this.getWidth()), (double)(this.y + this.getHeight()), Color.GRAY);
         renderer.line((double)(this.x + this.getWidth()), (double)this.y, (double)this.x, (double)(this.y + this.getHeight()), Color.GRAY);
      }

   }

   private void calculateSize() {
      this.setSize((double)50.0F * this.getScale(), (double)75.0F * this.getScale());
   }

   private double getScale() {
      return (Boolean)this.customScale.get() ? (Double)this.scale.get() : (Double)this.scale.getDefaultValue();
   }

   static {
      INFO = new HudElementInfo<PlayerModelHud>(Hud.GROUP, "player-model", "Displays a model of your player.", PlayerModelHud::new);
   }

   private static enum CenterOrientation {
      North,
      South;

      // $FF: synthetic method
      private static CenterOrientation[] $values() {
         return new CenterOrientation[]{North, South};
      }
   }
}
