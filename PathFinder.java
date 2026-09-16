package meteordevelopment.meteorclient.utils.player;

import java.util.ArrayList;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2183.class_2184;

public class PathFinder {
   private static final int PATH_AHEAD = 3;
   private static final int QUAD_1 = 1;
   private static final int QUAD_2 = 2;
   private static final int SOUTH = 0;
   private static final int NORTH = 180;
   private final ArrayList<PathBlock> path = new ArrayList(3);
   private class_1297 target;
   private PathBlock currentPathBlock;

   public PathBlock getNextPathBlock() {
      PathBlock nextBlock = new PathBlock(class_2338.method_49638(this.getNextStraightPos()));
      if (this.isSolidFloor(nextBlock.blockPos) && this.isAirAbove(nextBlock.blockPos)) {
         return nextBlock;
      } else {
         if (!this.isSolidFloor(nextBlock.blockPos) && this.isAirAbove(nextBlock.blockPos)) {
            int drop = this.getDrop(nextBlock.blockPos);
            if (this.getDrop(nextBlock.blockPos) < 3) {
               nextBlock = new PathBlock(new class_2338(nextBlock.blockPos.method_10263(), nextBlock.blockPos.method_10264() - drop, nextBlock.blockPos.method_10260()));
            }
         }

         return nextBlock;
      }
   }

   public int getDrop(class_2338 pos) {
      int drop;
      for(drop = 0; !this.isSolidFloor(pos) && drop < 3; pos = new class_2338(pos.method_10263(), pos.method_10264() - 1, pos.method_10260())) {
         ++drop;
      }

      return drop;
   }

   public boolean isAirAbove(class_2338 blockPos) {
      return !this.getBlockStateAtPos(blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260()).method_26215() ? false : this.getBlockStateAtPos(blockPos.method_10263(), blockPos.method_10264() + 1, blockPos.method_10260()).method_26215();
   }

   public class_243 getNextStraightPos() {
      class_243 nextPos = new class_243(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23318(), MeteorClient.mc.field_1724.method_23321());

      for(double multiplier = (double)1.0F; nextPos == MeteorClient.mc.field_1724.method_73189(); multiplier += 0.1) {
         nextPos = new class_243((double)((int)(MeteorClient.mc.field_1724.method_23317() + multiplier * Math.cos(Math.toRadians((double)MeteorClient.mc.field_1724.method_36454())))), (double)((int)MeteorClient.mc.field_1724.method_23318()), (double)((int)(MeteorClient.mc.field_1724.method_23321() + multiplier * Math.sin(Math.toRadians((double)MeteorClient.mc.field_1724.method_36454())))));
      }

      return nextPos;
   }

   public int getYawToTarget() {
      if (this.target != null && MeteorClient.mc.field_1724 != null) {
         class_243 tPos = this.target.method_73189();
         class_243 pPos = MeteorClient.mc.field_1724.method_73189();
         int direction = this.getDirection();
         double tan = (tPos.field_1350 - pPos.field_1350) / (tPos.field_1352 - pPos.field_1352);
         int yaw;
         if (direction == 1) {
            yaw = (int)((Math.PI / 2D) - Math.atan(tan));
         } else {
            if (direction != 2) {
               return direction;
            }

            yaw = (int)((-Math.PI / 2D) - Math.atan(tan));
         }

         return yaw;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public int getDirection() {
      if (this.target != null && MeteorClient.mc.field_1724 != null) {
         class_243 targetPos = this.target.method_73189();
         class_243 playerPos = MeteorClient.mc.field_1724.method_73189();
         if (targetPos.field_1352 == playerPos.field_1352 && targetPos.field_1350 > playerPos.field_1350) {
            return 0;
         } else if (targetPos.field_1352 == playerPos.field_1352 && targetPos.field_1350 < playerPos.field_1350) {
            return 180;
         } else if (targetPos.field_1352 < playerPos.field_1352) {
            return 1;
         } else {
            return targetPos.field_1352 > playerPos.field_1352 ? 2 : 0;
         }
      } else {
         return 0;
      }
   }

   public class_2680 getBlockStateAtPos(class_2338 pos) {
      return MeteorClient.mc.field_1687 != null ? MeteorClient.mc.field_1687.method_8320(pos) : null;
   }

   public class_2680 getBlockStateAtPos(int x, int y, int z) {
      return MeteorClient.mc.field_1687 != null ? MeteorClient.mc.field_1687.method_8320(new class_2338(x, y, z)) : null;
   }

   public class_2248 getBlockAtPos(class_2338 pos) {
      return MeteorClient.mc.field_1687 != null ? MeteorClient.mc.field_1687.method_8320(pos).method_26204() : null;
   }

   public boolean isSolidFloor(class_2338 blockPos) {
      return this.isAir(this.getBlockAtPos(blockPos));
   }

   public boolean isAir(class_2248 block) {
      return block == class_2246.field_10124;
   }

   public boolean isWater(class_2248 block) {
      return block == class_2246.field_10382;
   }

   public void lookAtDestination(PathBlock pathBlock) {
      if (MeteorClient.mc.field_1724 != null) {
         MeteorClient.mc.field_1724.method_5702(class_2184.field_9851, new class_243((double)pathBlock.blockPos.method_10263(), (double)((float)pathBlock.blockPos.method_10264() + MeteorClient.mc.field_1724.method_5751()), (double)pathBlock.blockPos.method_10260()));
      }

   }

   @EventHandler
   private void moveEventListener(PlayerMoveEvent event) {
      if (this.target != null && MeteorClient.mc.field_1724 != null) {
         if (!PlayerUtils.isWithin(this.target, (double)3.0F)) {
            if (this.currentPathBlock == null) {
               this.currentPathBlock = this.getNextPathBlock();
            }

            if (MeteorClient.mc.field_1724.method_73189().method_1025(new class_243((double)this.currentPathBlock.blockPos.method_10263(), (double)this.currentPathBlock.blockPos.method_10264(), (double)this.currentPathBlock.blockPos.method_10260())) < 0.01) {
               this.currentPathBlock = this.getNextPathBlock();
            }

            this.lookAtDestination(this.currentPathBlock);
            if (!MeteorClient.mc.field_1690.field_1894.method_1434()) {
               MeteorClient.mc.field_1690.field_1894.method_23481(true);
            }
         } else {
            if (MeteorClient.mc.field_1690.field_1894.method_1434()) {
               MeteorClient.mc.field_1690.field_1894.method_23481(false);
            }

            this.path.clear();
            this.currentPathBlock = null;
         }
      }

   }

   public void initiate(class_1297 entity) {
      this.target = entity;
      if (this.target != null) {
         this.currentPathBlock = this.getNextPathBlock();
      }

      MeteorClient.EVENT_BUS.subscribe(this);
   }

   public void disable() {
      this.target = null;
      this.path.clear();
      if (MeteorClient.mc.field_1690.field_1894.method_1434()) {
         MeteorClient.mc.field_1690.field_1894.method_23481(false);
      }

      MeteorClient.EVENT_BUS.unsubscribe(this);
   }

   public class PathBlock {
      public final class_2248 block;
      public final class_2338 blockPos;
      public final class_2680 blockState;
      public double yaw;

      public PathBlock(class_2248 b, class_2338 pos, class_2680 state) {
         this.block = b;
         this.blockPos = pos;
         this.blockState = state;
      }

      public PathBlock(class_2248 b, class_2338 pos) {
         this.block = b;
         this.blockPos = pos;
         this.blockState = PathFinder.this.getBlockStateAtPos(this.blockPos);
      }

      public PathBlock(class_2338 pos) {
         this.blockPos = pos;
         this.block = PathFinder.this.getBlockAtPos(pos);
         this.blockState = PathFinder.this.getBlockStateAtPos(this.blockPos);
      }
   }
}
