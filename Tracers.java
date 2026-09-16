package meteordevelopment.meteorclient.systems.modules.render;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
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
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_241;
import org.joml.Vector2f;
import org.joml.Vector3d;

public class Tracers extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgAppearance;
   private final SettingGroup sgColors;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<Boolean> ignoreSelf;
   public final Setting<Boolean> ignoreFriends;
   public final Setting<Boolean> showInvis;
   private final Setting<TracerStyle> style;
   private final Setting<Target> target;
   private final Setting<Boolean> stem;
   private final Setting<Integer> maxDist;
   private final Setting<Integer> distanceOffscreen;
   private final Setting<Integer> sizeOffscreen;
   private final Setting<Boolean> blinkOffscreen;
   private final Setting<Double> blinkOffscreenSpeed;
   public final Setting<Boolean> distance;
   public final Setting<Boolean> friendOverride;
   private final Setting<SettingColor> playersColor;
   private final Setting<SettingColor> animalsColor;
   private final Setting<SettingColor> waterAnimalsColor;
   private final Setting<SettingColor> monstersColor;
   private final Setting<SettingColor> ambientColor;
   private final Setting<SettingColor> miscColor;
   private int count;
   private final Instant initTimer;

   public Tracers() {
      super(Categories.Render, "tracers", "Displays tracer lines to specified entities.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgAppearance = this.settings.createGroup("Appearance");
      this.sgColors = this.settings.createGroup("Colors");
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Select specific entities.")).defaultValue(class_1299.field_6097).build());
      this.ignoreSelf = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-self")).description("Doesn't draw tracers to yourself when in third person or freecam.")).defaultValue(false)).build());
      this.ignoreFriends = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-friends")).description("Doesn't draw tracers to friends.")).defaultValue(false)).build());
      this.showInvis = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-invisible")).description("Shows invisible entities.")).defaultValue(true)).build());
      this.style = this.sgAppearance.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("style")).description("What display mode should be used")).defaultValue(Tracers.TracerStyle.Lines)).build());
      this.target = this.sgAppearance.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target")).description("What part of the entity to target.")).defaultValue(Target.Body)).visible(() -> this.style.get() == Tracers.TracerStyle.Lines)).build());
      this.stem = this.sgAppearance.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("stem")).description("Draw a line through the center of the tracer target.")).defaultValue(true)).visible(() -> this.style.get() == Tracers.TracerStyle.Lines)).build());
      this.maxDist = this.sgAppearance.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-distance")).description("Maximum distance for tracers to show.")).defaultValue(256)).min(0).sliderMax(256).build());
      this.distanceOffscreen = this.sgAppearance.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("distance-offscreen")).description("Offscreen's distance from center.")).defaultValue(200)).min(0).sliderMax(500).visible(() -> this.style.get() == Tracers.TracerStyle.Offscreen)).build());
      this.sizeOffscreen = this.sgAppearance.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("size-offscreen")).description("Offscreen's size.")).defaultValue(10)).min(2).sliderMax(50).visible(() -> this.style.get() == Tracers.TracerStyle.Offscreen)).build());
      this.blinkOffscreen = this.sgAppearance.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("blink-offscreen")).description("Make offscreen Blink.")).defaultValue(true)).visible(() -> this.style.get() == Tracers.TracerStyle.Offscreen)).build());
      this.blinkOffscreenSpeed = this.sgAppearance.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("blink-offscreen-speed")).description("Offscreen's blink speed.")).defaultValue((double)4.0F).min((double)1.0F).sliderMax((double)15.0F).visible(() -> this.style.get() == Tracers.TracerStyle.Offscreen && (Boolean)this.blinkOffscreen.get())).build());
      this.distance = this.sgColors.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("distance-colors")).description("Changes the color of tracers depending on distance.")).defaultValue(false)).build());
      this.friendOverride = this.sgColors.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-friend-colors")).description("Whether or not to override the distance color of friends with the friend color.")).defaultValue(true)).visible(() -> (Boolean)this.distance.get() && !(Boolean)this.ignoreFriends.get())).build());
      this.playersColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("players-colors")).description("The player's color.")).defaultValue(new SettingColor(205, 205, 205, 127)).visible(() -> !(Boolean)this.distance.get())).build());
      this.animalsColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("animals-color")).description("The animal's color.")).defaultValue(new SettingColor(145, 255, 145, 127)).visible(() -> !(Boolean)this.distance.get())).build());
      this.waterAnimalsColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("water-animals-color")).description("The water animal's color.")).defaultValue(new SettingColor(145, 145, 255, 127)).visible(() -> !(Boolean)this.distance.get())).build());
      this.monstersColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("monsters-color")).description("The monster's color.")).defaultValue(new SettingColor(255, 145, 145, 127)).visible(() -> !(Boolean)this.distance.get())).build());
      this.ambientColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ambient-color")).description("The ambient color.")).defaultValue(new SettingColor(75, 75, 75, 127)).visible(() -> !(Boolean)this.distance.get())).build());
      this.miscColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("misc-color")).description("The misc color.")).defaultValue(new SettingColor(145, 145, 145, 127)).visible(() -> !(Boolean)this.distance.get())).build());
      this.initTimer = Instant.now();
   }

   private boolean shouldBeIgnored(class_1297 entity) {
      return !PlayerUtils.isWithin(entity, (double)(Integer)this.maxDist.get()) || !Modules.get().isActive(Freecam.class) && entity == this.mc.field_1724 || !((Set)this.entities.get()).contains(entity.method_5864()) || (Boolean)this.ignoreSelf.get() && entity == this.mc.field_1724 || (Boolean)this.ignoreFriends.get() && entity instanceof class_1657 && Friends.get().isFriend((class_1657)entity) || (!(Boolean)this.showInvis.get() && entity.method_5767()) | !EntityUtils.isInRenderDistance(entity);
   }

   private Color getEntityColor(class_1297 entity) {
      Color color;
      if ((Boolean)this.distance.get()) {
         if ((Boolean)this.friendOverride.get() && entity instanceof class_1657 && Friends.get().isFriend((class_1657)entity)) {
            color = Config.get().friendColor.get();
         } else {
            color = EntityUtils.getColorFromDistance(entity);
         }
      } else if (entity instanceof class_1657) {
         color = PlayerUtils.getPlayerColor((class_1657)entity, this.playersColor.get());
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

         color = var10000;
      }

      return new Color(color);
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (!this.mc.field_1690.field_1842 && this.style.get() != Tracers.TracerStyle.Offscreen) {
         this.count = 0;

         for(class_1297 entity : this.mc.field_1687.method_18112()) {
            if (!this.shouldBeIgnored(entity)) {
               Color color = this.getEntityColor(entity);
               double x = entity.field_6014 + (entity.method_23317() - entity.field_6014) * (double)event.tickDelta;
               double y = entity.field_6036 + (entity.method_23318() - entity.field_6036) * (double)event.tickDelta;
               double z = entity.field_5969 + (entity.method_23321() - entity.field_5969) * (double)event.tickDelta;
               double height = entity.method_5829().field_1325 - entity.method_5829().field_1322;
               if (this.target.get() == Target.Head) {
                  y += height;
               } else if (this.target.get() == Target.Body) {
                  y += height / (double)2.0F;
               }

               event.renderer.line(RenderUtils.center.field_1352, RenderUtils.center.field_1351, RenderUtils.center.field_1350, x, y, z, color);
               if ((Boolean)this.stem.get()) {
                  event.renderer.line(x, entity.method_23318(), z, x, entity.method_23318() + height, z, color);
               }

               ++this.count;
            }
         }

      }
   }

   @EventHandler
   public void onRender2D(Render2DEvent event) {
      if (!this.mc.field_1690.field_1842 && this.style.get() == Tracers.TracerStyle.Offscreen) {
         this.count = 0;
         Renderer2D.COLOR.begin();

         for(class_1297 entity : this.mc.field_1687.method_18112()) {
            if (!this.shouldBeIgnored(entity)) {
               Color color = this.getEntityColor(entity);
               if ((Boolean)this.blinkOffscreen.get()) {
                  color.a = (int)((float)color.a * this.getAlpha());
               }

               class_241 screenCenter = new class_241((float)this.mc.method_22683().method_4489() / 2.0F, (float)this.mc.method_22683().method_4506() / 2.0F);
               Vector3d projection = new Vector3d(entity.field_6014, entity.field_6036, entity.field_5969);
               boolean projSucceeded = NametagUtils.to2D(projection, (double)1.0F, false, false);
               if (!projSucceeded || !(projection.x > (double)0.0F) || !(projection.x < (double)this.mc.method_22683().method_4489()) || !(projection.y > (double)0.0F) || !(projection.y < (double)this.mc.method_22683().method_4506())) {
                  projection = new Vector3d(entity.field_6014, entity.field_6036, entity.field_5969);
                  NametagUtils.to2D(projection, (double)1.0F, false, true);
                  Vector2f angle = this.vectorAngles(new Vector3d((double)screenCenter.field_1343 - projection.x, (double)screenCenter.field_1342 - projection.y, (double)0.0F));
                  angle.y += 180.0F;
                  float angleYawRad = (float)Math.toRadians((double)angle.y);
                  Vector2f newPoint = new Vector2f(screenCenter.field_1343 + (float)(Integer)this.distanceOffscreen.get() * (float)Math.cos((double)angleYawRad), screenCenter.field_1342 + (float)(Integer)this.distanceOffscreen.get() * (float)Math.sin((double)angleYawRad));
                  Vector2f[] trianglePoints = new Vector2f[]{new Vector2f(newPoint.x - (float)(Integer)this.sizeOffscreen.get(), newPoint.y - (float)(Integer)this.sizeOffscreen.get()), new Vector2f(newPoint.x + (float)(Integer)this.sizeOffscreen.get() * 0.73205F, newPoint.y), new Vector2f(newPoint.x - (float)(Integer)this.sizeOffscreen.get(), newPoint.y + (float)(Integer)this.sizeOffscreen.get())};
                  this.rotateTriangle(trianglePoints, angle.y);
                  Renderer2D.COLOR.triangle((double)trianglePoints[2].x, (double)trianglePoints[2].y, (double)trianglePoints[1].x, (double)trianglePoints[1].y, (double)trianglePoints[0].x, (double)trianglePoints[0].y, color);
                  ++this.count;
               }
            }
         }

         Renderer2D.COLOR.render();
      }
   }

   private void rotateTriangle(Vector2f[] points, float ang) {
      Vector2f triangleCenter = new Vector2f(0.0F, 0.0F);
      triangleCenter.add(points[0]).add(points[1]).add(points[2]).div(3.0F);
      float theta = (float)Math.toRadians((double)ang);
      float cos = (float)Math.cos((double)theta);
      float sin = (float)Math.sin((double)theta);

      for(int i = 0; i < 3; ++i) {
         Vector2f point = (new Vector2f(points[i].x, points[i].y)).sub(triangleCenter);
         Vector2f newPoint = new Vector2f(point.x * cos - point.y * sin, point.x * sin + point.y * cos);
         newPoint.add(triangleCenter);
         points[i] = newPoint;
      }

   }

   private Vector2f vectorAngles(Vector3d forward) {
      float yaw;
      float pitch;
      if (forward.x == (double)0.0F && forward.y == (double)0.0F) {
         yaw = 0.0F;
         if (forward.z > (double)0.0F) {
            pitch = 270.0F;
         } else {
            pitch = 90.0F;
         }
      } else {
         yaw = (float)(Math.atan2(forward.y, forward.x) * (double)180.0F / Math.PI);
         if (yaw < 0.0F) {
            yaw += 360.0F;
         }

         float tmp = (float)Math.sqrt(forward.x * forward.x + forward.y * forward.y);
         pitch = (float)(Math.atan2(-forward.z, (double)tmp) * (double)180.0F / Math.PI);
         if (pitch < 0.0F) {
            pitch += 360.0F;
         }
      }

      return new Vector2f(pitch, yaw);
   }

   private float getAlpha() {
      double speed = (Double)this.blinkOffscreenSpeed.get() / (double)4.0F;
      double duration = (double)Math.abs(Duration.between(Instant.now(), this.initTimer).toMillis()) * speed;
      return (float)Math.abs(duration % (double)1000.0F - (double)500.0F) / 500.0F;
   }

   public String getInfoString() {
      return Integer.toString(this.count);
   }

   public static enum TracerStyle {
      Lines,
      Offscreen;

      // $FF: synthetic method
      private static TracerStyle[] $values() {
         return new TracerStyle[]{Lines, Offscreen};
      }
   }
}
