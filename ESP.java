package meteordevelopment.meteorclient.systems.modules.render;

import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_3532;
import net.minecraft.class_3966;
import org.joml.Vector3d;

public class ESP extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgColors;
   public final Setting<Mode> mode;
   public final Setting<Boolean> highlightTarget;
   public final Setting<Boolean> targetHitbox;
   public final Setting<Integer> outlineWidth;
   public final Setting<Double> glowMultiplier;
   public final Setting<Boolean> ignoreSelf;
   public final Setting<ShapeMode> shapeMode;
   public final Setting<Double> fillOpacity;
   private final Setting<Double> fadeDistance;
   private final Setting<Set<class_1299<?>>> entities;
   public final Setting<ESPColorMode> colorMode;
   public final Setting<Boolean> friendOverride;
   private final Setting<SettingColor> nonLivingEntityColor;
   private final Setting<SettingColor> playersColor;
   private final Setting<SettingColor> animalsColor;
   private final Setting<SettingColor> waterAnimalsColor;
   private final Setting<SettingColor> monstersColor;
   private final Setting<SettingColor> ambientColor;
   private final Setting<SettingColor> miscColor;
   private final Setting<SettingColor> targetColor;
   private final Setting<SettingColor> targetHitboxColor;
   private final Color lineColor;
   private final Color sideColor;
   private final Color baseColor;
   private final Vector3d pos1;
   private final Vector3d pos2;
   private final Vector3d pos;
   private int count;

   public ESP() {
      super(Categories.Render, "esp", "Renders entities through walls.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgColors = this.settings.createGroup("Colors");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Rendering mode.")).defaultValue(ESP.Mode.Shader)).build());
      this.highlightTarget = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("highlight-target")).description("highlights the currently targeted entity differently")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("target-hitbox")).description("draw the hitbox of the target entity")).defaultValue(true);
      Setting var10003 = this.highlightTarget;
      Objects.requireNonNull(var10003);
      this.targetHitbox = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.outlineWidth = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("outline-width")).description("The width of the shader outline.")).visible(() -> this.mode.get() == ESP.Mode.Shader)).defaultValue(2)).range(1, 10).sliderRange(1, 5).build());
      this.glowMultiplier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("glow-multiplier")).description("Multiplier for glow effect")).visible(() -> this.mode.get() == ESP.Mode.Shader)).decimalPlaces(3).defaultValue((double)3.5F).min((double)0.0F).sliderMax((double)10.0F).build());
      this.ignoreSelf = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-self")).description("Ignores yourself drawing the shader.")).defaultValue(true)).build());
      this.shapeMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).visible(() -> this.mode.get() != ESP.Mode.Glow)).defaultValue(ShapeMode.Both)).build());
      this.fillOpacity = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fill-opacity")).description("The opacity of the shape fill.")).visible(() -> this.shapeMode.get() != ShapeMode.Lines && this.mode.get() != ESP.Mode.Glow)).defaultValue(0.3).range((double)0.0F, (double)1.0F).sliderMax((double)1.0F).build());
      this.fadeDistance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fade-distance")).description("The distance from an entity where the color begins to fade.")).defaultValue((double)3.0F).min((double)0.0F).sliderMax((double)12.0F).build());
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Select specific entities.")).defaultValue(class_1299.field_6097).build());
      this.colorMode = this.sgColors.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("color-mode")).description("Determines the colors used for entities.")).defaultValue(ESP.ESPColorMode.EntityType)).build());
      this.friendOverride = this.sgColors.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-friend-colors")).description("Whether or not to override the distance/health color of friends with the friend color.")).defaultValue(true)).visible(() -> this.colorMode.get() == ESP.ESPColorMode.Distance || this.colorMode.get() == ESP.ESPColorMode.Health)).build());
      this.nonLivingEntityColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("non-living-entity-color")).description("The color used for non living entities such as dropped items.")).defaultValue(new SettingColor(25, 25, 25)).visible(() -> this.colorMode.get() == ESP.ESPColorMode.Health)).build());
      this.playersColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("players-color")).description("The other player's color.")).defaultValue(new SettingColor(255, 255, 255)).visible(() -> this.colorMode.get() == ESP.ESPColorMode.EntityType)).build());
      this.animalsColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("animals-color")).description("The animal's color.")).defaultValue(new SettingColor(25, 255, 25, 255)).visible(() -> this.colorMode.get() == ESP.ESPColorMode.EntityType)).build());
      this.waterAnimalsColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("water-animals-color")).description("The water animal's color.")).defaultValue(new SettingColor(25, 25, 255, 255)).visible(() -> this.colorMode.get() == ESP.ESPColorMode.EntityType)).build());
      this.monstersColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("monsters-color")).description("The monster's color.")).defaultValue(new SettingColor(255, 25, 25, 255)).visible(() -> this.colorMode.get() == ESP.ESPColorMode.EntityType)).build());
      this.ambientColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ambient-color")).description("The ambient's color.")).defaultValue(new SettingColor(25, 25, 25, 255)).visible(() -> this.colorMode.get() == ESP.ESPColorMode.EntityType)).build());
      this.miscColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("misc-color")).description("The misc color.")).defaultValue(new SettingColor(175, 175, 175, 255)).visible(() -> this.colorMode.get() == ESP.ESPColorMode.EntityType)).build());
      var10001 = this.sgColors;
      ColorSetting.Builder var2 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("target-color")).description("The target color.")).defaultValue(new SettingColor(200, 200, 200, 255));
      var10003 = this.highlightTarget;
      Objects.requireNonNull(var10003);
      this.targetColor = var10001.add(((ColorSetting.Builder)var2.visible(var10003::get)).build());
      this.targetHitboxColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("target-hitbox-color")).description("The target hitbox color.")).defaultValue(new SettingColor(100, 200, 200, 255)).visible(() -> (Boolean)this.highlightTarget.get() && (Boolean)this.targetHitbox.get())).build());
      this.lineColor = new Color();
      this.sideColor = new Color();
      this.baseColor = new Color();
      this.pos1 = new Vector3d();
      this.pos2 = new Vector3d();
      this.pos = new Vector3d();
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if (this.mode.get() != ESP.Mode._2D) {
         this.count = 0;
         class_1297 target = null;
         if ((Boolean)this.highlightTarget.get() && (Boolean)this.targetHitbox.get()) {
            class_239 var4 = this.mc.field_1765;
            if (var4 instanceof class_3966) {
               class_3966 hr = (class_3966)var4;
               target = hr.method_17782();
            }
         }

         for(class_1297 entity : this.mc.field_1687.method_18112()) {
            if (target == entity || !this.shouldSkip(entity)) {
               if (target == entity || this.mode.get() == ESP.Mode.Box || this.mode.get() == ESP.Mode.Wireframe) {
                  this.drawBoundingBox(event, entity);
               }

               ++this.count;
            }
         }

      }
   }

   private void drawBoundingBox(Render3DEvent event, class_1297 entity) {
      Color color = this.getColor(entity);
      if (color != null) {
         this.lineColor.set(color);
         this.sideColor.set(color).a((int)((double)this.sideColor.a * (Double)this.fillOpacity.get()));
      }

      if (this.mode.get() == ESP.Mode.Wireframe) {
         WireframeEntityRenderer.render(event, entity, (double)1.0F, this.sideColor, this.lineColor, this.shapeMode.get());
      }

      boolean target = this.drawAsTarget(entity);
      if (this.mode.get() == ESP.Mode.Box || (Boolean)this.targetHitbox.get() && target) {
         double x = class_3532.method_16436((double)event.tickDelta, entity.field_6038, entity.method_23317()) - entity.method_23317();
         double y = class_3532.method_16436((double)event.tickDelta, entity.field_5971, entity.method_23318()) - entity.method_23318();
         double z = class_3532.method_16436((double)event.tickDelta, entity.field_5989, entity.method_23321()) - entity.method_23321();
         ShapeMode shape = this.shapeMode.get();
         if (target && this.mode.get() != ESP.Mode.Box) {
            shape = ShapeMode.Lines;
         }

         if (target) {
            this.lineColor.set(this.targetHitboxColor.get());
         }

         class_238 box = entity.method_5829();
         event.renderer.box(x + box.field_1323, y + box.field_1322, z + box.field_1321, x + box.field_1320, y + box.field_1325, z + box.field_1324, this.sideColor, this.lineColor, shape, 0);
      }

   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      if (this.mode.get() == ESP.Mode._2D) {
         Renderer2D.COLOR.begin();
         this.count = 0;

         for(class_1297 entity : this.mc.field_1687.method_18112()) {
            if (!this.shouldSkip(entity)) {
               class_238 box = entity.method_5829();
               double x = class_3532.method_16436((double)event.tickDelta, entity.field_6038, entity.method_23317()) - entity.method_23317();
               double y = class_3532.method_16436((double)event.tickDelta, entity.field_5971, entity.method_23318()) - entity.method_23318();
               double z = class_3532.method_16436((double)event.tickDelta, entity.field_5989, entity.method_23321()) - entity.method_23321();
               this.pos1.set(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
               this.pos2.set((double)0.0F, (double)0.0F, (double)0.0F);
               if (!this.checkCorner(box.field_1323 + x, box.field_1322 + y, box.field_1321 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1320 + x, box.field_1322 + y, box.field_1321 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1323 + x, box.field_1322 + y, box.field_1324 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1320 + x, box.field_1322 + y, box.field_1324 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1323 + x, box.field_1325 + y, box.field_1321 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1320 + x, box.field_1325 + y, box.field_1321 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1323 + x, box.field_1325 + y, box.field_1324 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1320 + x, box.field_1325 + y, box.field_1324 + z, this.pos1, this.pos2)) {
                  Color color = this.getColor(entity);
                  if (color != null) {
                     this.lineColor.set(color);
                     this.sideColor.set(color).a((int)((double)this.sideColor.a * (Double)this.fillOpacity.get()));
                  }

                  if (this.shapeMode.get() != ShapeMode.Lines && this.sideColor.a > 0) {
                     Renderer2D.COLOR.quad(this.pos1.x, this.pos1.y, this.pos2.x - this.pos1.x, this.pos2.y - this.pos1.y, this.sideColor);
                  }

                  if (this.shapeMode.get() != ShapeMode.Sides) {
                     Renderer2D.COLOR.line(this.pos1.x, this.pos1.y, this.pos1.x, this.pos2.y, this.lineColor);
                     Renderer2D.COLOR.line(this.pos2.x, this.pos1.y, this.pos2.x, this.pos2.y, this.lineColor);
                     Renderer2D.COLOR.line(this.pos1.x, this.pos1.y, this.pos2.x, this.pos1.y, this.lineColor);
                     Renderer2D.COLOR.line(this.pos1.x, this.pos2.y, this.pos2.x, this.pos2.y, this.lineColor);
                  }

                  ++this.count;
               }
            }
         }

         Renderer2D.COLOR.render();
      }
   }

   public boolean forceRender() {
      return this.isActive() && (this.mode.get() == ESP.Mode.Shader || this.mode.get() == ESP.Mode.Glow);
   }

   private boolean checkCorner(double x, double y, double z, Vector3d min, Vector3d max) {
      this.pos.set(x, y, z);
      if (!NametagUtils.to2D(this.pos, (double)1.0F)) {
         return true;
      } else {
         if (this.pos.x < min.x) {
            min.x = this.pos.x;
         }

         if (this.pos.y < min.y) {
            min.y = this.pos.y;
         }

         if (this.pos.z < min.z) {
            min.z = this.pos.z;
         }

         if (this.pos.x > max.x) {
            max.x = this.pos.x;
         }

         if (this.pos.y > max.y) {
            max.y = this.pos.y;
         }

         if (this.pos.z > max.z) {
            max.z = this.pos.z;
         }

         return false;
      }
   }

   public boolean drawAsTarget(class_1297 entity) {
      boolean var10000;
      if ((Boolean)this.highlightTarget.get()) {
         class_239 var3 = this.mc.field_1765;
         if (var3 instanceof class_3966) {
            class_3966 hr = (class_3966)var3;
            if (hr.method_17782() == entity) {
               var10000 = true;
               return var10000;
            }
         }
      }

      var10000 = false;
      return var10000;
   }

   public boolean shouldSkip(class_1297 entity) {
      if (this.drawAsTarget(entity)) {
         return false;
      } else if (!((Set)this.entities.get()).contains(entity.method_5864())) {
         return true;
      } else if (entity == this.mc.field_1724 && (Boolean)this.ignoreSelf.get()) {
         return true;
      } else if (entity == this.mc.method_1560() && this.mc.field_1690.method_31044().method_31034()) {
         return true;
      } else {
         return !EntityUtils.isInRenderDistance(entity);
      }
   }

   public boolean shouldSkip(class_1299<?> entityType) {
      return !((Set)this.entities.get()).contains(entityType);
   }

   public Color getColor(class_1297 entity) {
      double alpha = (double)1.0F;
      Color color;
      if (this.drawAsTarget(entity)) {
         color = this.targetColor.get();
      } else {
         if (!((Set)this.entities.get()).contains(entity.method_5864())) {
            return null;
         }

         alpha = this.getFadeAlpha(entity);
         if (alpha == (double)0.0F) {
            return null;
         }

         color = this.getEntityTypeColor(entity);
      }

      return this.baseColor.set(color.r, color.g, color.b, (int)((double)color.a * alpha));
   }

   private double getFadeAlpha(class_1297 entity) {
      double dist = PlayerUtils.squaredDistanceToCamera(entity.method_23317(), entity.method_23318() + (double)entity.method_18381(entity.method_18376()), entity.method_23321());
      double fadeDist = Math.pow((Double)this.fadeDistance.get(), (double)2.0F);
      double alpha = (double)1.0F;
      if (dist <= fadeDist * fadeDist) {
         alpha = (double)((float)(Math.sqrt(dist) / fadeDist));
      }

      if (alpha <= 0.075) {
         alpha = (double)0.0F;
      }

      return alpha;
   }

   public Color getEntityTypeColor(class_1297 entity) {
      if (this.colorMode.get() == ESP.ESPColorMode.EntityType) {
         if (entity instanceof class_1657) {
            class_1657 player = (class_1657)entity;
            return PlayerUtils.getPlayerColor(player, this.playersColor.get());
         } else {
            SettingColor var10000;
            switch (entity.method_5864().method_5891()) {
               case field_6294:
                  var10000 = this.animalsColor.get();
                  break;
               case field_24460:
               case field_6300:
               case field_30092:
               case field_34447:
                  var10000 = this.waterAnimalsColor.get();
                  break;
               case field_6302:
                  var10000 = this.monstersColor.get();
                  break;
               case field_6303:
                  var10000 = this.ambientColor.get();
                  break;
               default:
                  var10000 = this.miscColor.get();
            }

            return var10000;
         }
      } else {
         if ((Boolean)this.friendOverride.get() && entity instanceof class_1657) {
            class_1657 player = (class_1657)entity;
            if (Friends.get().isFriend(player)) {
               return Config.get().friendColor.get();
            }
         }

         return this.colorMode.get() == ESP.ESPColorMode.Health ? EntityUtils.getColorFromHealth(entity, this.nonLivingEntityColor.get()) : EntityUtils.getColorFromDistance(entity);
      }
   }

   public String getInfoString() {
      return Integer.toString(this.count);
   }

   public boolean isShader() {
      return this.isActive() && this.mode.get() == ESP.Mode.Shader;
   }

   public boolean isGlow() {
      return this.isActive() && this.mode.get() == ESP.Mode.Glow;
   }

   public static enum ESPColorMode {
      EntityType,
      Distance,
      Health;

      public String toString() {
         return this == EntityType ? "Entity Type" : super.toString();
      }

      // $FF: synthetic method
      private static ESPColorMode[] $values() {
         return new ESPColorMode[]{EntityType, Distance, Health};
      }
   }

   public static enum Mode {
      Box,
      Wireframe,
      _2D,
      Shader,
      Glow;

      public String toString() {
         return this == _2D ? "2D" : super.toString();
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Box, Wireframe, _2D, Shader, Glow};
      }
   }
}
