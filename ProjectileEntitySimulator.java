package meteordevelopment.meteorclient.utils.entity.simulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.CrossbowItemAccessor;
import meteordevelopment.meteorclient.mixin.ProjectileInGroundAccessor;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import meteordevelopment.meteorclient.systems.modules.movement.Sneak;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.class_10690;
import net.minecraft.class_10691;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1665;
import net.minecraft.class_1667;
import net.minecraft.class_1668;
import net.minecraft.class_1673;
import net.minecraft.class_1675;
import net.minecraft.class_1676;
import net.minecraft.class_1679;
import net.minecraft.class_1680;
import net.minecraft.class_1681;
import net.minecraft.class_1682;
import net.minecraft.class_1683;
import net.minecraft.class_1684;
import net.minecraft.class_1685;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1771;
import net.minecraft.class_1776;
import net.minecraft.class_1779;
import net.minecraft.class_1787;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1803;
import net.minecraft.class_1823;
import net.minecraft.class_1828;
import net.minecraft.class_1835;
import net.minecraft.class_1893;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3486;
import net.minecraft.class_3532;
import net.minecraft.class_3610;
import net.minecraft.class_3730;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_4048;
import net.minecraft.class_4050;
import net.minecraft.class_4076;
import net.minecraft.class_8949;
import net.minecraft.class_9109;
import net.minecraft.class_9236;
import net.minecraft.class_9239;
import net.minecraft.class_9278;
import net.minecraft.class_9334;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class ProjectileEntitySimulator {
   private final class_2338.class_2339 blockPos = new class_2338.class_2339();
   private final class_243 pos3d = new class_243((double)0.0F, (double)0.0F, (double)0.0F);
   private final class_243 prevPos3d = new class_243((double)0.0F, (double)0.0F, (double)0.0F);
   public final Vector3d pos = new Vector3d();
   private final Vector3d velocity = new Vector3d();
   private class_1676 simulatingEntity;
   private class_4048 dimensions;
   private int age;
   private int pierceLevel;
   private double gravity;
   private float airDrag;
   private float waterDrag;
   private boolean isTouchingWater;
   private static final MotionData EGG;
   private static final MotionData ENDER_PEARL;
   private static final MotionData SNOWBALL;
   private static final MotionData EXPERIENCE_BOTTLE;
   private static final MotionData LINGERING_POTION;
   private static final MotionData SPLASH_POTION;
   private static final MotionData EXPLOSIVE;
   private static final MotionData WIND_CHARGE;
   private static final MotionData ARROW;
   private static final MotionData TRIDENT;
   private static final MotionData FIREWORK_ROCKET;
   private static final MotionData FISHING_BOBBER;
   private static final MotionData LLAMA_SPIT;

   public boolean set(class_1297 user, class_1799 itemStack, double angleOffset, boolean accurate, float tickDelta) {
      class_1792 item = itemStack.method_7909();
      Objects.requireNonNull(item);
      byte var9 = 0;
      //$FF: var9->value
      //0->net/minecraft/class_1753
      //1->net/minecraft/class_1764
      //2->net/minecraft/class_9239
      //3->net/minecraft/class_1835
      //4->net/minecraft/class_1823
      //5->net/minecraft/class_1771
      //6->net/minecraft/class_1776
      //7->net/minecraft/class_1779
      //8->net/minecraft/class_1828
      //9->net/minecraft/class_1803
      //10->net/minecraft/class_1787
      switch (item.typeSwitch<invokedynamic>(item, var9)) {
         case 0:
            class_1753 ignored = (class_1753)item;
            if (!(user instanceof class_1309)) {
               return false;
            }

            class_1309 livingEntity = (class_1309)user;
            float charge = class_1753.method_7722(livingEntity.method_6048());
            if ((double)charge <= 0.1) {
               if (user != MeteorClient.mc.field_1724) {
                  return false;
               }

               charge = 1.0F;
            }

            this.set(user, angleOffset, accurate, tickDelta, ARROW.withPower(charge * 3.0F));
            break;
         case 1:
            class_1764 ignored = (class_1764)item;
            class_9278 projectilesComponent = (class_9278)itemStack.method_58694(class_9334.field_49649);
            if (projectilesComponent == null) {
               return false;
            }

            float speed = CrossbowItemAccessor.meteor$getSpeed(projectilesComponent);
            if (projectilesComponent.method_57438(class_1802.field_8639)) {
               this.set(user, angleOffset, accurate, tickDelta, FIREWORK_ROCKET.withPower(speed));
            } else {
               this.set(user, angleOffset, accurate, tickDelta, ARROW.withPower(speed));
            }

            this.pierceLevel = projectilesComponent.method_57438(class_1802.field_8639) ? 0 : Utils.getEnchantmentLevel(itemStack, class_1893.field_9132);
            break;
         case 2:
            class_9239 ignored = (class_9239)item;
            this.set(user, angleOffset, accurate, tickDelta, WIND_CHARGE);
            break;
         case 3:
            class_1835 ignored = (class_1835)item;
            this.set(user, angleOffset, accurate, tickDelta, TRIDENT);
            break;
         case 4:
            class_1823 ignored = (class_1823)item;
            this.set(user, angleOffset, accurate, tickDelta, SNOWBALL);
            break;
         case 5:
            class_1771 ignored = (class_1771)item;
            this.set(user, angleOffset, accurate, tickDelta, EGG);
            break;
         case 6:
            class_1776 ignored = (class_1776)item;
            this.set(user, angleOffset, accurate, tickDelta, ENDER_PEARL);
            break;
         case 7:
            class_1779 ignored = (class_1779)item;
            this.set(user, angleOffset, accurate, tickDelta, EXPERIENCE_BOTTLE);
            break;
         case 8:
            class_1828 ignored = (class_1828)item;
            this.set(user, angleOffset, accurate, tickDelta, SPLASH_POTION);
            break;
         case 9:
            class_1803 ignored = (class_1803)item;
            this.set(user, angleOffset, accurate, tickDelta, LINGERING_POTION);
            break;
         case 10:
            class_1787 ignored = (class_1787)item;
            this.setFishingBobber(user, tickDelta, FISHING_BOBBER);
            break;
         default:
            return false;
      }

      return true;
   }

   public void set(class_1297 user, double angleOffset, boolean accurate, float tickDelta, MotionData data) {
      class_4050 pose = user.method_18376();
      if (user == MeteorClient.mc.field_1724 && (((NoSlow)Modules.get().get(NoSlow.class)).airStrict() || ((Sneak)Modules.get().get(Sneak.class)).doPacket())) {
         pose = class_4050.field_18081;
      }

      Utils.set(this.pos, user, (double)tickDelta).add((double)0.0F, (double)(user.method_18381(pose) - 0.1F), (double)0.0F);
      double yaw;
      double pitch;
      if (user == MeteorClient.mc.field_1724 && Rotations.rotating) {
         yaw = (double)Rotations.serverYaw;
         pitch = (double)Rotations.serverPitch;
      } else {
         yaw = (double)user.method_5705(tickDelta);
         pitch = (double)user.method_5695(tickDelta);
      }

      double x;
      double y;
      double z;
      if (angleOffset == (double)0.0F) {
         x = -Math.sin(yaw * 0.017453292) * Math.cos(pitch * 0.017453292);
         y = -Math.sin((pitch + (double)data.roll()) * 0.017453292);
         z = Math.cos(yaw * 0.017453292) * Math.cos(pitch * 0.017453292);
      } else {
         class_243 oppositeRotationVec = user.method_18864(1.0F);
         Quaterniond quaternion = (new Quaterniond()).setAngleAxis(angleOffset, oppositeRotationVec.field_1352, oppositeRotationVec.field_1351, oppositeRotationVec.field_1350);
         class_243 rotationVec = user.method_5828(1.0F);
         Vector3d vector3d = new Vector3d(rotationVec.field_1352, rotationVec.field_1351, rotationVec.field_1350);
         vector3d.rotate(quaternion);
         x = vector3d.x;
         y = vector3d.y;
         z = vector3d.z;
      }

      this.velocity.set(x, y, z).normalize().mul((double)data.power());
      if (accurate) {
         class_243 vel = user.method_60478();
         this.velocity.add(vel.field_1352, user.method_24828() ? (double)0.0F : vel.field_1351, vel.field_1350);
      }

      this.setSimulationData((class_1676)data.entity().method_5883(MeteorClient.mc.field_1687, (class_3730)null), data);
   }

   public void setFishingBobber(class_1297 user, float tickDelta, MotionData data) {
      double yaw;
      double pitch;
      if (user == MeteorClient.mc.field_1724 && Rotations.rotating) {
         yaw = (double)Rotations.serverYaw;
         pitch = (double)Rotations.serverPitch;
      } else {
         yaw = (double)user.method_5705(tickDelta);
         pitch = (double)user.method_5695(tickDelta);
      }

      double h = Math.cos(-yaw * (double)((float)Math.PI / 180F) - (double)(float)Math.PI);
      double i = Math.sin(-yaw * (double)((float)Math.PI / 180F) - (double)(float)Math.PI);
      double j = -Math.cos(-pitch * (double)((float)Math.PI / 180F));
      double k = Math.sin(-pitch * (double)((float)Math.PI / 180F));
      class_4050 pose = user.method_18376();
      if (user == MeteorClient.mc.field_1724 && (((NoSlow)Modules.get().get(NoSlow.class)).airStrict() || ((Sneak)Modules.get().get(Sneak.class)).doPacket())) {
         pose = class_4050.field_18081;
      }

      Utils.set(this.pos, user, (double)tickDelta).sub(i * 0.3, (double)0.0F, h * 0.3).add((double)0.0F, (double)user.method_18381(pose), (double)0.0F);
      this.velocity.set(-i, class_3532.method_15350(-(k / j), (double)-5.0F, (double)5.0F), -h);
      double l = this.velocity.length();
      this.velocity.mul(0.6 / l + (double)0.5F, 0.6 / l + (double)0.5F, 0.6 / l + (double)0.5F);
      this.setSimulationData((class_1676)data.entity().method_5883(MeteorClient.mc.field_1687, (class_3730)null), data);
   }

   public boolean set(class_1297 entity) {
      if (entity instanceof ProjectileInGroundAccessor ppe) {
         if (ppe.meteor$invokeIsInGround()) {
            return false;
         }
      }

      Objects.requireNonNull(entity);
      byte var3 = 0;
      //$FF: var3->value
      //0->net/minecraft/class_1667
      //1->net/minecraft/class_1679
      //2->net/minecraft/class_1685
      //3->net/minecraft/class_1684
      //4->net/minecraft/class_1680
      //5->net/minecraft/class_1681
      //6->net/minecraft/class_1683
      //7->net/minecraft/class_10691
      //8->net/minecraft/class_10690
      //9->net/minecraft/class_9236
      //10->net/minecraft/class_1668
      //11->net/minecraft/class_1673
      switch (entity.typeSwitch<invokedynamic>(entity, var3)) {
         case 0:
            class_1667 e = (class_1667)entity;
            this.set(e, ARROW);
            break;
         case 1:
            class_1679 e = (class_1679)entity;
            this.set(e, ARROW);
            break;
         case 2:
            class_1685 e = (class_1685)entity;
            this.set(e, TRIDENT);
            break;
         case 3:
            class_1684 e = (class_1684)entity;
            this.set(e, ENDER_PEARL);
            break;
         case 4:
            class_1680 e = (class_1680)entity;
            this.set(e, SNOWBALL);
            break;
         case 5:
            class_1681 e = (class_1681)entity;
            this.set(e, EGG);
            break;
         case 6:
            class_1683 e = (class_1683)entity;
            this.set(e, EXPERIENCE_BOTTLE);
            break;
         case 7:
            class_10691 e = (class_10691)entity;
            this.set(e, SPLASH_POTION);
            break;
         case 8:
            class_10690 e = (class_10690)entity;
            this.set(e, LINGERING_POTION);
            break;
         case 9:
            class_9236 e = (class_9236)entity;
            this.set(e, WIND_CHARGE);
            break;
         case 10:
            class_1668 e = (class_1668)entity;
            this.set(e, EXPLOSIVE);
            break;
         case 11:
            class_1673 e = (class_1673)entity;
            this.set(e, LLAMA_SPIT);
            break;
         default:
            return false;
      }

      if (entity.method_5740()) {
         this.gravity = (double)0.0F;
      }

      return true;
   }

   public void set(class_1676 entity, MotionData data) {
      this.pos.set(entity.method_23317(), entity.method_23318(), entity.method_23321());
      double speed = entity.method_18798().method_1033();
      this.velocity.set(entity.method_18798().field_1352, entity.method_18798().field_1351, entity.method_18798().field_1350).normalize().mul(speed);
      this.setSimulationData(entity, data);
   }

   private void setSimulationData(class_1676 entity, MotionData data) {
      this.gravity = data.gravity();
      this.airDrag = data.airDrag();
      this.waterDrag = data.waterDrag();
      this.simulatingEntity = entity;
      this.dimensions = this.simulatingEntity.method_18377(this.simulatingEntity.method_18376());
      this.isTouchingWater = this.simulatingEntity.method_5799();
      this.age = this.simulatingEntity.field_6012;
      this.pierceLevel = 0;
   }

   public SimulationStep tick() {
      ++this.age;
      ((IVec3d)this.prevPos3d).meteor$set(this.pos);
      if (!(this.simulatingEntity instanceof class_1682) && !(this.simulatingEntity instanceof class_1668)) {
         if (!(this.simulatingEntity instanceof class_1665) && !(this.simulatingEntity instanceof class_1673)) {
            if (this.simulatingEntity instanceof class_1676) {
               this.tickIsTouchingWater();
               this.velocity.sub((double)0.0F, this.gravity, (double)0.0F);
               this.pos.add(this.velocity);
               this.velocity.mul(this.isTouchingWater ? (double)this.waterDrag : (double)this.airDrag);
            }
         } else {
            this.pos.add(this.velocity);
            this.velocity.mul(this.isTouchingWater ? (double)this.waterDrag : (double)this.airDrag);
            this.velocity.sub((double)0.0F, this.gravity, (double)0.0F);
            this.tickIsTouchingWater();
         }
      } else {
         this.velocity.sub((double)0.0F, this.gravity, (double)0.0F);
         this.velocity.mul(this.isTouchingWater ? (double)this.waterDrag : (double)this.airDrag);
         this.pos.add(this.velocity);
         this.tickIsTouchingWater();
      }

      if (this.pos.y < (double)MeteorClient.mc.field_1687.method_31607()) {
         return SimulationStep.MISS;
      } else {
         int chunkX = class_4076.method_32204(this.pos.x);
         int chunkZ = class_4076.method_32204(this.pos.z);
         if (!MeteorClient.mc.field_1687.method_2935().method_12123(chunkX, chunkZ)) {
            return SimulationStep.MISS;
         } else {
            ((IVec3d)this.pos3d).meteor$set(this.pos);
            return this.pos3d.equals(this.prevPos3d) ? SimulationStep.MISS : this.getCollision();
         }
      }
   }

   public void tickIsTouchingWater() {
      class_238 box = this.dimensions.method_30231(this.pos.x, this.pos.y, this.pos.z).method_1011(0.001);
      int minX = class_3532.method_15357(box.field_1323);
      int maxX = class_3532.method_15384(box.field_1320);
      int minY = class_3532.method_15357(box.field_1322);
      int maxY = class_3532.method_15384(box.field_1325);
      int minZ = class_3532.method_15357(box.field_1321);
      int maxZ = class_3532.method_15384(box.field_1324);

      for(int x = minX; x < maxX; ++x) {
         for(int y = minY; y < maxY; ++y) {
            for(int z = minZ; z < maxZ; ++z) {
               this.blockPos.method_10103(x, y, z);
               class_3610 fluidState = MeteorClient.mc.field_1687.method_8316(this.blockPos);
               if (fluidState.method_15767(class_3486.field_15517)) {
                  double fluidY = (double)((float)y + fluidState.method_15763(MeteorClient.mc.field_1687, this.blockPos));
                  if (fluidY >= box.field_1322) {
                     this.isTouchingWater = true;
                     return;
                  }
               }
            }
         }
      }

      this.isTouchingWater = false;
   }

   private SimulationStep getCollision() {
      class_239 blockCollision = MeteorClient.mc.field_1687.method_61717(new class_3959(this.prevPos3d, this.pos3d, class_3960.field_17558, this.waterDrag == 0.0F ? class_242.field_1347 : class_242.field_1348, this.simulatingEntity));
      if (blockCollision.method_17783() != class_240.field_1333) {
         ((IVec3d)this.pos3d).meteor$set(blockCollision.method_17784());
      }

      if (this.simulatingEntity instanceof class_1665) {
         Collection<class_3966> entityCollisions = class_1675.method_75215(MeteorClient.mc.field_1687, this.simulatingEntity, this.prevPos3d, this.pos3d, this.dimensions.method_30757(this.prevPos3d).method_1012(this.velocity.x, this.velocity.y, this.velocity.z).method_1014((double)1.0F), (entity) -> !entity.method_7325() && entity.method_5805() && entity.method_5863(), this.getToleranceMargin(), class_3960.field_17558, false);
         entityCollisions.removeIf((collision) -> this.age <= 1 && collision.method_17782() == MeteorClient.mc.field_1724);
         if (entityCollisions.isEmpty()) {
            return new SimulationStep(this.hitOrDeflect(blockCollision), new class_239[]{blockCollision});
         } else {
            boolean stop = false;
            ArrayList<class_3966> hits = new ArrayList();

            for(class_3966 result : entityCollisions) {
               boolean hit = this.hitOrDeflect(result);
               if (!hit) {
                  break;
               }

               hits.add(result);
               if (this.pierceLevel <= 0) {
                  stop = true;
                  break;
               }

               --this.pierceLevel;
            }

            return new SimulationStep(stop, (class_239[])hits.toArray(new class_239[0]));
         }
      } else {
         class_239 entityCollision = class_1675.method_37226(MeteorClient.mc.field_1687, this.simulatingEntity, this.prevPos3d, this.pos3d, this.dimensions.method_30757(this.prevPos3d).method_1012(this.velocity.x, this.velocity.y, this.velocity.z).method_1014((double)1.0F), (entity) -> !entity.method_7325() && entity.method_5805() && entity.method_5863(), this.getToleranceMargin());
         if (entityCollision != null) {
            if (this.age <= 1 && entityCollision instanceof class_3966) {
               class_3966 ehr = (class_3966)entityCollision;
               if (ehr.method_17782() == MeteorClient.mc.field_1724) {
                  return new SimulationStep(this.hitOrDeflect(blockCollision), new class_239[]{blockCollision});
               }
            }

            if (this.hitOrDeflect(entityCollision)) {
               return new SimulationStep(true, new class_239[]{entityCollision});
            } else {
               return new SimulationStep(false, new class_239[0]);
            }
         } else {
            return new SimulationStep(this.hitOrDeflect(blockCollision), new class_239[]{blockCollision});
         }
      }
   }

   private boolean hitOrDeflect(class_239 hitResult) {
      if (!(hitResult instanceof class_3966 entityHitResult)) {
         if (hitResult instanceof class_3965 bhr) {
            Utils.set(this.pos, bhr.method_17784());
            if (this.simulatingEntity.method_62823() && bhr.method_62877()) {
               this.velocity.mul((double)-0.5F).mul(0.2);
               return false;
            } else {
               return bhr.method_17783() != class_240.field_1333;
            }
         } else {
            return false;
         }
      } else {
         class_1297 entity = entityHitResult.method_17782();
         Utils.set(this.pos, entityHitResult.method_17784());
         if ((!(entity instanceof class_8949) || this.simulatingEntity instanceof class_9236) && entity.method_56071(this.simulatingEntity) != class_9109.field_48348) {
            if (entity instanceof class_1309) {
               class_1309 livingEntity = (class_1309)entity;
               if (livingEntity.method_6039() && this.simulatingEntity instanceof class_1665) {
                  this.velocity.mul((double)-0.5F).mul(0.2);
                  return this.velocity.lengthSquared() < 1.0E-7;
               }
            }

            return true;
         } else {
            this.velocity.mul((double)-0.5F);
            return false;
         }
      }
   }

   private float getToleranceMargin() {
      return Math.max(0.0F, Math.min(0.3F, (float)(this.age - 2) / 20.0F));
   }

   static {
      EGG = new MotionData(1.5F, 0.0F, 0.03, 0.99F, 0.8F, class_1299.field_6144);
      ENDER_PEARL = new MotionData(1.5F, 0.0F, 0.03, 0.99F, 0.8F, class_1299.field_6082);
      SNOWBALL = new MotionData(1.5F, 0.0F, 0.03, 0.99F, 0.8F, class_1299.field_6068);
      EXPERIENCE_BOTTLE = new MotionData(0.7F, -20.0F, 0.07, 0.99F, 0.8F, class_1299.field_6064);
      LINGERING_POTION = new MotionData(0.5F, -20.0F, 0.05, 0.99F, 0.8F, class_1299.field_56255);
      SPLASH_POTION = new MotionData(0.5F, -20.0F, 0.05, 0.99F, 0.8F, class_1299.field_56254);
      EXPLOSIVE = new MotionData(0.0F, 0.0F, (double)0.0F, 1.0F, 1.0F, (class_1299)null);
      WIND_CHARGE = new MotionData(1.5F, 0.0F, (double)0.0F, 1.0F, 1.0F, class_1299.field_47243);
      ARROW = new MotionData(0.0F, 0.0F, 0.05, 0.99F, 0.6F, class_1299.field_6122);
      TRIDENT = new MotionData(2.5F, 0.0F, 0.05, 0.99F, 0.99F, class_1299.field_6127);
      FIREWORK_ROCKET = new MotionData(0.0F, 0.0F, (double)0.0F, 1.0F, 1.0F, class_1299.field_6133);
      FISHING_BOBBER = new MotionData(0.0F, 0.0F, 0.03, 0.92F, 0.0F, class_1299.field_6103);
      LLAMA_SPIT = new MotionData(1.5F, 0.0F, 0.06, 0.99F, 0.0F, class_1299.field_6124);
   }

   public static record MotionData(float power, float roll, double gravity, float airDrag, float waterDrag, class_1299<?> entity) {
      public MotionData withPower(float power) {
         return new MotionData(power, this.roll(), this.gravity(), this.airDrag(), this.waterDrag(), this.entity());
      }
   }
}
