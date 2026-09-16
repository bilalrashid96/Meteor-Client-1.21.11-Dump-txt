package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_3532;

public class CompassHud extends HudElement {
   public static final HudElementInfo<CompassHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgScale;
   private final SettingGroup sgBackground;
   private final Setting<Mode> mode;
   private final Setting<SettingColor> colorNorth;
   private final Setting<SettingColor> colorOther;
   private final Setting<Boolean> shadow;
   private final Setting<Boolean> customScale;
   private final Setting<Double> textScale;
   private final Setting<Double> compassScale;
   private final Setting<Boolean> background;
   private final Setting<SettingColor> backgroundColor;

   public CompassHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgScale = this.settings.createGroup("Scale");
      this.sgBackground = this.settings.createGroup("Background");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("type")).description("Which type of direction information to show.")).defaultValue(CompassHud.Mode.Axis)).build());
      this.colorNorth = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color-north")).description("Color of north.")).defaultValue(new SettingColor(225, 45, 45)).build());
      this.colorOther = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color-other")).description("Color of other directions.")).defaultValue(new SettingColor()).build());
      this.shadow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("shadow")).description("Text shadow.")).defaultValue(false)).build());
      this.customScale = this.sgScale.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-scale")).description("Apply custom scales to this hud element.")).defaultValue(false)).onChanged((aBoolean) -> this.calculateSize())).build());
      SettingGroup var10001 = this.sgScale;
      DoubleSetting.Builder var10002 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("text-scale")).description("Scale to use for the letters.");
      Setting var10003 = this.customScale;
      Objects.requireNonNull(var10003);
      this.textScale = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).defaultValue((double)1.0F).min((double)0.5F).sliderRange((double)0.5F, (double)3.0F).build());
      var10001 = this.sgScale;
      var10002 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("compass-scale")).description("Scale of the whole HUD element.");
      var10003 = this.customScale;
      Objects.requireNonNull(var10003);
      this.compassScale = var10001.add(((DoubleSetting.Builder)((DoubleSetting.Builder)var10002.visible(var10003::get)).defaultValue((double)1.0F).min((double)0.5F).sliderRange((double)0.5F, (double)3.0F).onChanged((aDouble) -> this.calculateSize())).build());
      this.background = this.sgBackground.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      var10001 = this.sgBackground;
      ColorSetting.Builder var4 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var4.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
      this.calculateSize();
   }

   private void calculateSize() {
      this.setSize((double)100.0F * this.getCompassScale(), (double)100.0F * this.getCompassScale());
   }

   public void render(HudRenderer renderer) {
      double x = (double)this.x + (double)this.getWidth() / (double)2.0F;
      double y = (double)this.y + (double)this.getHeight() / (double)2.0F;
      double pitch = this.isInEditor() ? (double)120.0F : (double)class_3532.method_15363(MeteorClient.mc.field_1724.method_36455() + 30.0F, -90.0F, 90.0F);
      pitch = Math.toRadians(pitch);
      double yaw = this.isInEditor() ? (double)180.0F : (double)class_3532.method_15393(MeteorClient.mc.field_1724.method_36454());
      yaw = Math.toRadians(yaw);

      for(Direction direction : CompassHud.Direction.values()) {
         String axis = this.mode.get() == CompassHud.Mode.Axis ? direction.getAxis() : direction.name();
         renderer.text(axis, x + this.getX(direction, yaw) - renderer.textWidth(axis, (Boolean)this.shadow.get(), this.getTextScale()) / (double)2.0F, y + this.getY(direction, yaw, pitch) - renderer.textHeight((Boolean)this.shadow.get(), this.getTextScale()) / (double)2.0F, direction == CompassHud.Direction.N ? (Color)this.colorNorth.get() : (Color)this.colorOther.get(), (Boolean)this.shadow.get(), this.getTextScale());
      }

      if ((Boolean)this.background.get()) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), this.backgroundColor.get());
      }

   }

   private double getX(Direction direction, double yaw) {
      return Math.sin(this.getPos(direction, yaw)) * this.getCompassScale() * (double)40.0F;
   }

   private double getY(Direction direction, double yaw, double pitch) {
      return Math.cos(this.getPos(direction, yaw)) * Math.sin(pitch) * this.getCompassScale() * (double)40.0F;
   }

   private double getPos(Direction direction, double yaw) {
      return yaw + (double)direction.ordinal() * Math.PI / (double)2.0F;
   }

   private double getTextScale() {
      return (Boolean)this.customScale.get() ? (Double)this.textScale.get() : Hud.get().getTextScale();
   }

   private double getCompassScale() {
      return (Boolean)this.customScale.get() ? (Double)this.compassScale.get() : Hud.get().getTextScale();
   }

   static {
      INFO = new HudElementInfo<CompassHud>(Hud.GROUP, "compass", "Displays a compass.", CompassHud::new);
   }

   private static enum Direction {
      N("Z-"),
      W("X-"),
      S("Z+"),
      E("X+");

      private final String axis;

      private Direction(String axis) {
         this.axis = axis;
      }

      public String getAxis() {
         return this.axis;
      }

      // $FF: synthetic method
      private static Direction[] $values() {
         return new Direction[]{N, W, S, E};
      }
   }

   public static enum Mode {
      Direction,
      Axis;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Direction, Axis};
      }
   }
}
