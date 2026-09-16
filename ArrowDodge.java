package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.simulator.ProjectileEntitySimulator;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1667;
import net.minecraft.class_1676;
import net.minecraft.class_1679;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2828;
import org.joml.Vector3d;

public class ArrowDodge extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgMovement;
   private final Setting<MoveType> moveType;
   private final Setting<Double> moveSpeed;
   private final Setting<Double> distanceCheck;
   private final Setting<Boolean> groundCheck;
   private final Setting<Boolean> allProjectiles;
   private final Setting<Boolean> ignoreOwn;
   public final Setting<Integer> simulationSteps;
   private final List<class_243> possibleMoveDirections;
   private final ProjectileEntitySimulator simulator;
   private final Pool<Vector3d> vec3s;
   private final List<Vector3d> points;

   public ArrowDodge() {
      super(Categories.Combat, "arrow-dodge", "Tries to dodge arrows coming at you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgMovement = this.settings.createGroup("Movement");
      this.moveType = this.sgMovement.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("move-type")).description("The way you are moved by this module.")).defaultValue(ArrowDodge.MoveType.Velocity)).build());
      this.moveSpeed = this.sgMovement.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("move-speed")).description("How fast should you be when dodging arrow.")).defaultValue((double)1.0F).min(0.01).sliderRange(0.01, (double)5.0F).build());
      this.distanceCheck = this.sgMovement.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("distance-check")).description("How far should an arrow be from the player to be considered not hitting.")).defaultValue((double)1.0F).min(0.01).sliderRange(0.01, (double)5.0F).build());
      this.groundCheck = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ground-check")).description("Tries to prevent you from falling to your death.")).defaultValue(true)).build());
      this.allProjectiles = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("all-projectiles")).description("Dodge all projectiles, not only arrows.")).defaultValue(false)).build());
      this.ignoreOwn = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-own")).description("Ignore your own projectiles.")).defaultValue(true)).build());
      this.simulationSteps = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("simulation-steps")).description("How many steps to simulate projectiles. Zero for no limit.")).defaultValue(500)).sliderMax(5000).build());
      this.possibleMoveDirections = Arrays.asList(new class_243((double)1.0F, (double)0.0F, (double)1.0F), new class_243((double)0.0F, (double)0.0F, (double)1.0F), new class_243((double)-1.0F, (double)0.0F, (double)1.0F), new class_243((double)1.0F, (double)0.0F, (double)0.0F), new class_243((double)-1.0F, (double)0.0F, (double)0.0F), new class_243((double)1.0F, (double)0.0F, (double)-1.0F), new class_243((double)0.0F, (double)0.0F, (double)-1.0F), new class_243((double)-1.0F, (double)0.0F, (double)-1.0F));
      this.simulator = new ProjectileEntitySimulator();
      this.vec3s = new Pool<Vector3d>(Vector3d::new);
      this.points = new ArrayList();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.vec3s.freeAll(this.points);
      this.points.clear();
      Iterator var2 = this.mc.field_1687.method_18112().iterator();

      while(true) {
         class_1676 projectile;
         while(true) {
            if (!var2.hasNext()) {
               if (this.isValid(class_243.field_1353, false)) {
                  return;
               }

               double speed = (Double)this.moveSpeed.get();

               for(int i = 0; i < 500; ++i) {
                  boolean didMove = false;
                  Collections.shuffle(this.possibleMoveDirections);

                  for(class_243 direction : this.possibleMoveDirections) {
                     class_243 velocity = direction.method_1021(speed);
                     if (this.isValid(velocity, true)) {
                        this.move(velocity);
                        didMove = true;
                        break;
                     }
                  }

                  if (didMove) {
                     break;
                  }

                  speed += (Double)this.moveSpeed.get();
               }

               return;
            }

            class_1297 e = (class_1297)var2.next();
            if (e instanceof class_1676 projectile) {
               if ((Boolean)this.allProjectiles.get() || projectile instanceof class_1667 || projectile instanceof class_1679) {
                  if (!(Boolean)this.ignoreOwn.get()) {
                     break;
                  }

                  class_1297 owner = projectile.method_24921();
                  if (owner == null || !owner.method_5667().equals(this.mc.field_1724.method_5667())) {
                     break;
                  }
               }
            }
         }

         if (this.simulator.set(projectile)) {
            for(int i = 0; i < ((Integer)this.simulationSteps.get() > 0 ? (Integer)this.simulationSteps.get() : Integer.MAX_VALUE); ++i) {
               this.points.add(((Vector3d)this.vec3s.get()).set(this.simulator.pos));
               if (this.simulator.tick().shouldStop) {
                  break;
               }
            }
         }
      }
   }

   private void move(class_243 vel) {
      this.move(vel.field_1352, vel.field_1351, vel.field_1350);
   }

   private void move(double velX, double velY, double velZ) {
      switch (((MoveType)this.moveType.get()).ordinal()) {
         case 0:
            this.mc.field_1724.method_18800(velX, velY, velZ);
            break;
         case 1:
            class_243 newPos = this.mc.field_1724.method_73189().method_1031(velX, velY, velZ);
            this.mc.field_1724.field_3944.method_52787(new class_2828.class_2829(newPos.field_1352, newPos.field_1351, newPos.field_1350, false, this.mc.field_1724.field_5976));
            this.mc.field_1724.field_3944.method_52787(new class_2828.class_2829(newPos.field_1352, newPos.field_1351 - 0.01, newPos.field_1350, true, this.mc.field_1724.field_5976));
      }

   }

   private boolean isValid(class_243 velocity, boolean checkGround) {
      class_243 playerPos = this.mc.field_1724.method_73189().method_1019(velocity);
      class_243 headPos = playerPos.method_1031((double)0.0F, (double)1.0F, (double)0.0F);

      for(Vector3d pos : this.points) {
         class_243 projectilePos = new class_243(pos.x, pos.y, pos.z);
         if (projectilePos.method_24802(playerPos, (Double)this.distanceCheck.get())) {
            return false;
         }

         if (projectilePos.method_24802(headPos, (Double)this.distanceCheck.get())) {
            return false;
         }
      }

      if (checkGround) {
         class_2338 blockPos = this.mc.field_1724.method_24515().method_10081(class_2338.method_49637(velocity.field_1352, velocity.field_1351, velocity.field_1350));
         if (!this.mc.field_1687.method_8320(blockPos).method_26220(this.mc.field_1687, blockPos).method_1110()) {
            return false;
         }

         if (!this.mc.field_1687.method_8320(blockPos.method_10084()).method_26220(this.mc.field_1687, blockPos.method_10084()).method_1110()) {
            return false;
         }

         if ((Boolean)this.groundCheck.get()) {
            return !this.mc.field_1687.method_8320(blockPos.method_10074()).method_26220(this.mc.field_1687, blockPos.method_10074()).method_1110();
         }
      }

      return true;
   }

   public static enum MoveType {
      Velocity,
      Packet;

      // $FF: synthetic method
      private static MoveType[] $values() {
         return new MoveType[]{Velocity, Packet};
      }
   }
}
