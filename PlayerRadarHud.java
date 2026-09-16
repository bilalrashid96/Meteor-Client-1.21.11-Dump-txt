package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.hud.Alignment;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1657;
import net.minecraft.class_742;

public class PlayerRadarHud extends HudElement {
   public static final HudElementInfo<PlayerRadarHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgScale;
   private final SettingGroup sgBackground;
   private final Setting<Integer> limit;
   private final Setting<Boolean> distance;
   private final Setting<Boolean> friends;
   private final Setting<Boolean> shadow;
   private final Setting<SettingColor> primaryColor;
   private final Setting<SettingColor> secondaryColor;
   private final Setting<Alignment> alignment;
   private final Setting<Integer> border;
   private final Setting<Boolean> customScale;
   private final Setting<Double> scale;
   private final Setting<Boolean> background;
   private final Setting<SettingColor> backgroundColor;
   private final List<class_742> players;

   public PlayerRadarHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgScale = this.settings.createGroup("Scale");
      this.sgBackground = this.settings.createGroup("Background");
      this.limit = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("limit")).description("The max number of players to show.")).defaultValue(10)).min(1).sliderRange(1, 20).build());
      this.distance = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("distance")).description("Shows the distance to the player next to their name.")).defaultValue(false)).build());
      this.friends = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("display-friends")).description("Whether to show friends or not.")).defaultValue(true)).build());
      this.shadow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("shadow")).description("Renders shadow behind text.")).defaultValue(true)).build());
      this.primaryColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("primary-color")).description("Primary color.")).defaultValue(new SettingColor()).build());
      this.secondaryColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("secondary-color")).description("Secondary color.")).defaultValue(new SettingColor(175, 175, 175)).build());
      this.alignment = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("alignment")).description("Horizontal alignment.")).defaultValue(Alignment.Auto)).build());
      this.border = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("border")).description("How much space to add around the element.")).defaultValue(0)).build());
      this.customScale = this.sgScale.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-scale")).description("Applies a custom scale to this hud element.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgScale;
      DoubleSetting.Builder var10002 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Custom scale.");
      Setting var10003 = this.customScale;
      Objects.requireNonNull(var10003);
      this.scale = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).defaultValue((double)1.0F).min((double)0.5F).sliderRange((double)0.5F, (double)3.0F).build());
      this.background = this.sgBackground.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      var10001 = this.sgBackground;
      ColorSetting.Builder var2 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var2.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
      this.players = new ArrayList();
   }

   public void setSize(double width, double height) {
      super.setSize(width + (double)((Integer)this.border.get() * 2), height + (double)((Integer)this.border.get() * 2));
   }

   protected double alignX(double width, Alignment alignment) {
      return this.box.alignX((double)(this.getWidth() - (Integer)this.border.get() * 2), width, alignment);
   }

   public void tick(HudRenderer renderer) {
      double width = renderer.textWidth("Players:", (Boolean)this.shadow.get(), this.getScale());
      double height = renderer.textHeight((Boolean)this.shadow.get(), this.getScale());
      if (MeteorClient.mc.field_1687 == null) {
         this.setSize(width, height);
      } else {
         for(class_1657 entity : this.getPlayers()) {
            if (!entity.equals(MeteorClient.mc.field_1724) && ((Boolean)this.friends.get() || !Friends.get().isFriend(entity))) {
               String text = entity.method_5477().getString();
               if ((Boolean)this.distance.get()) {
                  text = text + String.format("(%sm)", Math.round(MeteorClient.mc.method_1560().method_5739(entity)));
               }

               width = Math.max(width, renderer.textWidth(text, (Boolean)this.shadow.get(), this.getScale()));
               height += renderer.textHeight((Boolean)this.shadow.get(), this.getScale()) + (double)2.0F;
            }
         }

         this.setSize(width, height);
      }
   }

   public void render(HudRenderer renderer) {
      double y = (double)(this.y + (Integer)this.border.get());
      if ((Boolean)this.background.get()) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), this.backgroundColor.get());
      }

      renderer.text("Players:", (double)(this.x + (Integer)this.border.get()) + this.alignX(renderer.textWidth("Players:", (Boolean)this.shadow.get(), this.getScale()), this.alignment.get()), y, this.secondaryColor.get(), (Boolean)this.shadow.get(), this.getScale());
      if (MeteorClient.mc.field_1687 != null) {
         double spaceWidth = renderer.textWidth(" ", (Boolean)this.shadow.get(), this.getScale());

         for(class_1657 entity : this.getPlayers()) {
            if (!entity.equals(MeteorClient.mc.field_1724) && ((Boolean)this.friends.get() || !Friends.get().isFriend(entity))) {
               String text = entity.method_5477().getString();
               Color color = PlayerUtils.getPlayerColor(entity, this.primaryColor.get());
               String distanceText = null;
               double width = renderer.textWidth(text, (Boolean)this.shadow.get(), this.getScale());
               if ((Boolean)this.distance.get()) {
                  width += spaceWidth;
               }

               if ((Boolean)this.distance.get()) {
                  distanceText = String.format("(%sm)", Math.round(MeteorClient.mc.method_1560().method_5739(entity)));
                  width += renderer.textWidth(distanceText, (Boolean)this.shadow.get(), this.getScale());
               }

               double x = (double)(this.x + (Integer)this.border.get()) + this.alignX(width, this.alignment.get());
               y += renderer.textHeight((Boolean)this.shadow.get(), this.getScale()) + (double)2.0F;
               x = renderer.text(text, x, y, color, (Boolean)this.shadow.get());
               if ((Boolean)this.distance.get()) {
                  renderer.text(distanceText, x + spaceWidth, y, this.secondaryColor.get(), (Boolean)this.shadow.get(), this.getScale());
               }
            }
         }

      }
   }

   private List<class_742> getPlayers() {
      this.players.clear();
      this.players.addAll(MeteorClient.mc.field_1687.method_18456());
      if (this.players.size() > (Integer)this.limit.get()) {
         this.players.subList((Integer)this.limit.get() - 1, this.players.size() - 1).clear();
      }

      this.players.sort(Comparator.comparingDouble((e) -> e.method_5858(MeteorClient.mc.method_1560())));
      return this.players;
   }

   private double getScale() {
      return (Boolean)this.customScale.get() ? (Double)this.scale.get() : Hud.get().getTextScale();
   }

   static {
      INFO = new HudElementInfo<PlayerRadarHud>(Hud.GROUP, "player-radar", "Displays players in your visual range.", PlayerRadarHud::new);
   }
}
