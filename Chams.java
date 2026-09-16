package meteordevelopment.meteorclient.systems.modules.render;

import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_2960;

public class Chams extends Module {
   private final SettingGroup sgThroughWalls;
   private final SettingGroup sgPlayers;
   private final SettingGroup sgCrystals;
   private final SettingGroup sgHand;
   public final Setting<Set<class_1299<?>>> entities;
   public final Setting<Shader> shader;
   public final Setting<SettingColor> shaderColor;
   public final Setting<Boolean> ignoreSelfDepth;
   public final Setting<Boolean> players;
   public final Setting<Boolean> ignoreSelf;
   public final Setting<Boolean> playersTexture;
   public final Setting<SettingColor> playersColor;
   public final Setting<Double> playersScale;
   public final Setting<Boolean> crystals;
   public final Setting<Double> crystalsScale;
   public final Setting<Double> crystalsBounce;
   public final Setting<Double> crystalsRotationSpeed;
   public final Setting<Boolean> crystalsTexture;
   public final Setting<SettingColor> crystalsColor;
   public final Setting<Boolean> hand;
   public final Setting<Boolean> handTexture;
   public final Setting<SettingColor> handColor;
   public static final class_2960 BLANK = MeteorClient.identifier("textures/blank.png");

   public Chams() {
      super(Categories.Render, "chams", "Tweaks rendering of entities.");
      this.sgThroughWalls = this.settings.createGroup("Through Walls");
      this.sgPlayers = this.settings.createGroup("Players");
      this.sgCrystals = this.settings.createGroup("Crystals");
      this.sgHand = this.settings.createGroup("Hand");
      this.entities = this.sgThroughWalls.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Select entities to show through walls.")).onlyAttackable().build());
      this.shader = this.sgThroughWalls.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shader")).description("Renders a shader over of the entities.")).defaultValue(Chams.Shader.Image)).build());
      this.shaderColor = this.sgThroughWalls.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color")).description("The color that the shader is drawn with.")).defaultValue(new SettingColor(255, 255, 255, 150)).visible(() -> this.shader.get() != Chams.Shader.None)).build());
      this.ignoreSelfDepth = this.sgThroughWalls.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-self")).description("Ignores yourself drawing the player.")).defaultValue(true)).build());
      this.players = this.sgPlayers.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("players")).description("Enables model tweaks for players.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgPlayers;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-self")).description("Ignores yourself when tweaking player models.")).defaultValue(false);
      Setting var10003 = this.players;
      Objects.requireNonNull(var10003);
      this.ignoreSelf = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgPlayers;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("texture")).description("Enables player model textures.")).defaultValue(false);
      var10003 = this.players;
      Objects.requireNonNull(var10003);
      this.playersTexture = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgPlayers;
      ColorSetting.Builder var12 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color")).description("The color of player models.")).defaultValue(new SettingColor(198, 135, 254, 150));
      var10003 = this.players;
      Objects.requireNonNull(var10003);
      this.playersColor = var10001.add(((ColorSetting.Builder)var12.visible(var10003::get)).build());
      var10001 = this.sgPlayers;
      DoubleSetting.Builder var13 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Players scale.")).defaultValue((double)1.0F).min((double)0.0F);
      var10003 = this.players;
      Objects.requireNonNull(var10003);
      this.playersScale = var10001.add(((DoubleSetting.Builder)var13.visible(var10003::get)).build());
      this.crystals = this.sgCrystals.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("crystals")).description("Enables model tweaks for end crystals.")).defaultValue(false)).build());
      var10001 = this.sgCrystals;
      var13 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Crystal scale.")).defaultValue(0.6).min((double)0.0F);
      var10003 = this.crystals;
      Objects.requireNonNull(var10003);
      this.crystalsScale = var10001.add(((DoubleSetting.Builder)var13.visible(var10003::get)).build());
      var10001 = this.sgCrystals;
      var13 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("bounce")).description("How high crystals bounce.")).defaultValue(0.6).min((double)0.0F);
      var10003 = this.crystals;
      Objects.requireNonNull(var10003);
      this.crystalsBounce = var10001.add(((DoubleSetting.Builder)var13.visible(var10003::get)).build());
      var10001 = this.sgCrystals;
      var13 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rotation-speed")).description("Multiplies the rotation speed of the crystal.")).defaultValue(0.3).min((double)0.0F);
      var10003 = this.crystals;
      Objects.requireNonNull(var10003);
      this.crystalsRotationSpeed = var10001.add(((DoubleSetting.Builder)var13.visible(var10003::get)).build());
      var10001 = this.sgCrystals;
      BoolSetting.Builder var17 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("texture")).description("Whether to render crystal model textures.")).defaultValue(true);
      var10003 = this.crystals;
      Objects.requireNonNull(var10003);
      this.crystalsTexture = var10001.add(((BoolSetting.Builder)var17.visible(var10003::get)).build());
      var10001 = this.sgCrystals;
      ColorSetting.Builder var18 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("crystal-color")).description("The color of the of the crystal.")).defaultValue(new SettingColor(198, 135, 254, 255));
      var10003 = this.crystals;
      Objects.requireNonNull(var10003);
      this.crystalsColor = var10001.add(((ColorSetting.Builder)var18.visible(var10003::get)).build());
      this.hand = this.sgHand.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("enabled")).description("Enables tweaks of hand rendering.")).defaultValue(false)).build());
      var10001 = this.sgHand;
      BoolSetting.Builder var19 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("texture")).description("Whether to render hand textures.")).defaultValue(false);
      var10003 = this.hand;
      Objects.requireNonNull(var10003);
      this.handTexture = var10001.add(((BoolSetting.Builder)var19.visible(var10003::get)).build());
      var10001 = this.sgHand;
      ColorSetting.Builder var20 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("hand-color")).description("The color of your hand.")).defaultValue(new SettingColor(198, 135, 254, 150));
      var10003 = this.hand;
      Objects.requireNonNull(var10003);
      this.handColor = var10001.add(((ColorSetting.Builder)var20.visible(var10003::get)).build());
   }

   public boolean shouldRender(class_1297 entity) {
      return this.isActive() && !this.isShader() && ((Set)this.entities.get()).contains(entity.method_5864()) && (entity != this.mc.field_1724 || !(Boolean)this.ignoreSelfDepth.get());
   }

   public boolean isShader() {
      return this.isActive() && this.shader.get() != Chams.Shader.None;
   }

   public static enum Shader {
      Image,
      None;

      // $FF: synthetic method
      private static Shader[] $values() {
         return new Shader[]{Image, None};
      }
   }
}
