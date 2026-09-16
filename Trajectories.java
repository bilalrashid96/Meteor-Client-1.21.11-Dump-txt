package meteordevelopment.meteorclient.systems.modules.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.simulator.ProjectileEntitySimulator;
import meteordevelopment.meteorclient.utils.entity.simulator.SimulationStep;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1676;
import net.minecraft.class_1685;
import net.minecraft.class_1687;
import net.minecraft.class_1764;
import net.minecraft.class_1771;
import net.minecraft.class_1776;
import net.minecraft.class_1779;
import net.minecraft.class_1787;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1811;
import net.minecraft.class_1823;
import net.minecraft.class_1835;
import net.minecraft.class_1893;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_4537;
import net.minecraft.class_7923;
import net.minecraft.class_9239;
import net.minecraft.class_239.class_240;
import org.joml.Vector3d;

public class Trajectories extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<List<class_1792>> items;
   private final Setting<Boolean> otherPlayers;
   private final Setting<Boolean> firedProjectiles;
   private final Setting<Boolean> ignoreWitherSkulls;
   private final Setting<Boolean> accurate;
   public final Setting<Integer> simulationSteps;
   private final Setting<Integer> ignoreFirstTicks;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<Boolean> renderPositionBox;
   private final Setting<Double> positionBoxSize;
   private final Setting<SettingColor> positionSideColor;
   private final Setting<SettingColor> positionLineColor;
   private final ProjectileEntitySimulator simulator;
   private final Pool<Vector3d> vec3s;
   private final List<Path> paths;
   private static final double MULTISHOT_OFFSET = Math.toRadians((double)10.0F);

   public Trajectories() {
      super(Categories.Render, "trajectories", "Predicts the trajectory of throwable items.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.items = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("items")).description("Items to display trajectories for.")).defaultValue(this.getDefaultItems())).filter(this::itemFilter).build());
      this.otherPlayers = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("other-players")).description("Calculates trajectories for other players.")).defaultValue(true)).build());
      this.firedProjectiles = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fired-projectiles")).description("Calculates trajectories for already fired projectiles.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-wither-skulls")).description("Whether to ignore fired wither skulls.")).defaultValue(false);
      Setting var10003 = this.firedProjectiles;
      Objects.requireNonNull(var10003);
      this.ignoreWitherSkulls = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.accurate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("accurate")).description("Whether or not to calculate more accurate.")).defaultValue(false)).build());
      this.simulationSteps = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("simulation-steps")).description("How many steps to simulate projectiles. Zero for no limit")).defaultValue(500)).sliderMax(5000).build());
      this.ignoreFirstTicks = this.sgRender.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("ignore-rendering-first-ticks")).description("Ignores rendering the first given ticks, to make the rest of the path more visible.")).defaultValue(3)).min(0).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(255, 150, 0, 35)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(255, 150, 0)).build());
      this.renderPositionBox = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-position-boxes")).description("Renders the actual position the projectile will be at each tick along it's trajectory.")).defaultValue(false)).build());
      var10001 = this.sgRender;
      DoubleSetting.Builder var4 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("position-box-size")).description("The size of the box drawn at the simulated positions.")).defaultValue(0.02).sliderRange(0.01, 0.1);
      var10003 = this.renderPositionBox;
      Objects.requireNonNull(var10003);
      this.positionBoxSize = var10001.add(((DoubleSetting.Builder)var4.visible(var10003::get)).build());
      var10001 = this.sgRender;
      ColorSetting.Builder var5 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("position-side-color")).description("The side color.")).defaultValue(new SettingColor(255, 150, 0, 35));
      var10003 = this.renderPositionBox;
      Objects.requireNonNull(var10003);
      this.positionSideColor = var10001.add(((ColorSetting.Builder)var5.visible(var10003::get)).build());
      var10001 = this.sgRender;
      var5 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("position-line-color")).description("The line color.")).defaultValue(new SettingColor(255, 150, 0));
      var10003 = this.renderPositionBox;
      Objects.requireNonNull(var10003);
      this.positionLineColor = var10001.add(((ColorSetting.Builder)var5.visible(var10003::get)).build());
      this.simulator = new ProjectileEntitySimulator();
      this.vec3s = new Pool<Vector3d>(Vector3d::new);
      this.paths = new ArrayList();
   }

   private boolean itemFilter(class_1792 item) {
      return item instanceof class_1811 || item instanceof class_1787 || item instanceof class_1835 || item instanceof class_1823 || item instanceof class_1771 || item instanceof class_1776 || item instanceof class_1779 || item instanceof class_4537 || item instanceof class_9239;
   }

   private List<class_1792> getDefaultItems() {
      List<class_1792> items = new ArrayList();

      for(class_1792 item : class_7923.field_41178) {
         if (this.itemFilter(item)) {
            items.add(item);
         }
      }

      return items;
   }

   private Path getEmptyPath() {
      for(Path path : this.paths) {
         if (path.points.isEmpty()) {
            return path;
         }
      }

      Path path = new Path();
      this.paths.add(path);
      return path;
   }

   private void calculatePath(class_1657 player, float tickDelta) {
      for(Path path : this.paths) {
         path.clear();
      }

      class_1799 itemStack = player.method_6047();
      if (!((List)this.items.get()).contains(itemStack.method_7909())) {
         itemStack = player.method_6079();
         if (!((List)this.items.get()).contains(itemStack.method_7909())) {
            return;
         }
      }

      if (this.simulator.set(player, itemStack, (double)0.0F, (Boolean)this.accurate.get(), tickDelta)) {
         Path p = this.getEmptyPath().calculate();
         if (player == this.mc.field_1724) {
            p.ignoreFirstTicks();
         }

         if (itemStack.method_7909() instanceof class_1764 && Utils.hasEnchantment(itemStack, class_1893.field_9108)) {
            if (!this.simulator.set(player, itemStack, MULTISHOT_OFFSET, (Boolean)this.accurate.get(), tickDelta)) {
               return;
            }

            p = this.getEmptyPath().calculate();
            if (player == this.mc.field_1724) {
               p.ignoreFirstTicks();
            }

            if (!this.simulator.set(player, itemStack, -MULTISHOT_OFFSET, (Boolean)this.accurate.get(), tickDelta)) {
               return;
            }

            p = this.getEmptyPath().calculate();
            if (player == this.mc.field_1724) {
               p.ignoreFirstTicks();
            }
         }

      }
   }

   private void calculateFiredPath(class_1297 entity, double tickDelta) {
      for(Path path : this.paths) {
         path.clear();
      }

      if (this.simulator.set(entity)) {
         this.getEmptyPath().setStart(entity, tickDelta).calculate();
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      float tickDelta = this.mc.field_1687.method_54719().method_54754() ? 1.0F : event.tickDelta;

      for(class_1657 player : this.mc.field_1687.method_18456()) {
         if ((Boolean)this.otherPlayers.get() || player == this.mc.field_1724) {
            this.calculatePath(player, tickDelta);

            for(Path path : this.paths) {
               path.render(event);
            }
         }
      }

      if ((Boolean)this.firedProjectiles.get()) {
         Iterator var7 = this.mc.field_1687.method_18112().iterator();

         while(true) {
            class_1297 entity;
            while(true) {
               if (!var7.hasNext()) {
                  return;
               }

               entity = (class_1297)var7.next();
               if (entity instanceof class_1676 && (!(Boolean)this.ignoreWitherSkulls.get() || !(entity instanceof class_1687))) {
                  if (!(entity instanceof class_1685)) {
                     break;
                  }

                  class_1685 trident = (class_1685)entity;
                  if (!trident.field_5960) {
                     break;
                  }
               }
            }

            this.calculateFiredPath(entity, (double)tickDelta);

            for(Path path : this.paths) {
               path.render(event);
            }
         }
      }
   }

   private class Path {
      private final List<Vector3d> points = new ArrayList();
      private boolean hitQuad;
      private boolean hitQuadHorizontal;
      private double hitQuadX1;
      private double hitQuadY1;
      private double hitQuadZ1;
      private double hitQuadX2;
      private double hitQuadY2;
      private double hitQuadZ2;
      private final List<class_1297> collidingEntities = new ArrayList();
      public Vector3d lastPoint;
      private int start;

      public void clear() {
         Trajectories.this.vec3s.freeAll(this.points);
         this.points.clear();
         this.hitQuad = false;
         this.collidingEntities.clear();
         this.lastPoint = null;
         this.start = 0;
      }

      public Path calculate() {
         this.addPoint();

         for(int i = 0; i < ((Integer)Trajectories.this.simulationSteps.get() > 0 ? (Integer)Trajectories.this.simulationSteps.get() : Integer.MAX_VALUE); ++i) {
            SimulationStep result = Trajectories.this.simulator.tick();
            this.processHitResults(result);
            if (result.shouldStop) {
               break;
            }

            this.addPoint();
         }

         return this;
      }

      public Path setStart(class_1297 entity, double tickDelta) {
         this.lastPoint = new Vector3d(class_3532.method_16436(tickDelta, entity.field_6038, entity.method_23317()), class_3532.method_16436(tickDelta, entity.field_5971, entity.method_23318()), class_3532.method_16436(tickDelta, entity.field_5989, entity.method_23321()));
         return this;
      }

      private void addPoint() {
         this.points.add(((Vector3d)Trajectories.this.vec3s.get()).set(Trajectories.this.simulator.pos));
      }

      private void processHitResults(SimulationStep step) {
         for(int i = 0; i < step.hitResults.length; ++i) {
            class_239 result = step.hitResults[i];
            if (result.method_17783() == class_240.field_1332) {
               class_3965 r = (class_3965)result;
               this.hitQuad = true;
               this.hitQuadX1 = r.method_17784().field_1352;
               this.hitQuadY1 = r.method_17784().field_1351;
               this.hitQuadZ1 = r.method_17784().field_1350;
               this.hitQuadX2 = r.method_17784().field_1352;
               this.hitQuadY2 = r.method_17784().field_1351;
               this.hitQuadZ2 = r.method_17784().field_1350;
               if (r.method_17780() != class_2350.field_11036 && r.method_17780() != class_2350.field_11033) {
                  if (r.method_17780() != class_2350.field_11043 && r.method_17780() != class_2350.field_11035) {
                     this.hitQuadHorizontal = false;
                     this.hitQuadZ1 -= (double)0.25F;
                     this.hitQuadY1 -= (double)0.25F;
                     this.hitQuadZ2 += (double)0.25F;
                     this.hitQuadY2 += (double)0.25F;
                  } else {
                     this.hitQuadHorizontal = false;
                     this.hitQuadX1 -= (double)0.25F;
                     this.hitQuadY1 -= (double)0.25F;
                     this.hitQuadX2 += (double)0.25F;
                     this.hitQuadY2 += (double)0.25F;
                  }
               } else {
                  this.hitQuadHorizontal = true;
                  this.hitQuadX1 -= (double)0.25F;
                  this.hitQuadZ1 -= (double)0.25F;
                  this.hitQuadX2 += (double)0.25F;
                  this.hitQuadZ2 += (double)0.25F;
               }

               this.points.add(Utils.set(Trajectories.this.vec3s.get(), result.method_17784()));
            } else if (result.method_17783() == class_240.field_1331) {
               class_1297 entity = ((class_3966)result).method_17782();
               this.collidingEntities.add(entity);
               if (step.shouldStop && i == step.hitResults.length - 1) {
                  this.points.add(Utils.set(Trajectories.this.vec3s.get(), result.method_17784()));
               }
            }
         }

      }

      public void ignoreFirstTicks() {
         this.start = this.points.size() <= (Integer)Trajectories.this.ignoreFirstTicks.get() ? 0 : (Integer)Trajectories.this.ignoreFirstTicks.get();
      }

      public void render(Render3DEvent event) {
         for(int i = this.start; i < this.points.size(); ++i) {
            Vector3d point = (Vector3d)this.points.get(i);
            if (this.lastPoint != null) {
               event.renderer.line(this.lastPoint.x, this.lastPoint.y, this.lastPoint.z, point.x, point.y, point.z, Trajectories.this.lineColor.get());
               if ((Boolean)Trajectories.this.renderPositionBox.get()) {
                  event.renderer.box(point.x - (Double)Trajectories.this.positionBoxSize.get(), point.y - (Double)Trajectories.this.positionBoxSize.get(), point.z - (Double)Trajectories.this.positionBoxSize.get(), point.x + (Double)Trajectories.this.positionBoxSize.get(), point.y + (Double)Trajectories.this.positionBoxSize.get(), point.z + (Double)Trajectories.this.positionBoxSize.get(), Trajectories.this.positionSideColor.get(), Trajectories.this.positionLineColor.get(), Trajectories.this.shapeMode.get(), 0);
               }
            }

            this.lastPoint = point;
         }

         if (this.hitQuad) {
            if (this.hitQuadHorizontal) {
               event.renderer.sideHorizontal(this.hitQuadX1, this.hitQuadY1, this.hitQuadZ1, this.hitQuadX1 + (double)0.5F, this.hitQuadZ1 + (double)0.5F, Trajectories.this.sideColor.get(), Trajectories.this.lineColor.get(), Trajectories.this.shapeMode.get());
            } else {
               event.renderer.sideVertical(this.hitQuadX1, this.hitQuadY1, this.hitQuadZ1, this.hitQuadX2, this.hitQuadY2, this.hitQuadZ2, Trajectories.this.sideColor.get(), Trajectories.this.lineColor.get(), Trajectories.this.shapeMode.get());
            }
         }

         for(class_1297 collidingEntity : this.collidingEntities) {
            double x = (collidingEntity.method_23317() - collidingEntity.field_6014) * (double)event.tickDelta;
            double y = (collidingEntity.method_23318() - collidingEntity.field_6036) * (double)event.tickDelta;
            double z = (collidingEntity.method_23321() - collidingEntity.field_5969) * (double)event.tickDelta;
            class_238 box = collidingEntity.method_5829();
            event.renderer.box(x + box.field_1323, y + box.field_1322, z + box.field_1321, x + box.field_1320, y + box.field_1325, z + box.field_1324, Trajectories.this.sideColor.get(), Trajectories.this.lineColor.get(), Trajectories.this.shapeMode.get(), 0);
         }

      }
   }
}
