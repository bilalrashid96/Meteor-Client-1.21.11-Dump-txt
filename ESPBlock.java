package meteordevelopment.meteorclient.systems.modules.render.blockesp;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2350.class_2351;

public class ESPBlock {
   private static final class_2338.class_2339 blockPos = new class_2338.class_2339();
   private static final BlockESP blockEsp = (BlockESP)Modules.get().get(BlockESP.class);
   public static final int FO = 2;
   public static final int FO_RI = 4;
   public static final int RI = 8;
   public static final int BA_RI = 16;
   public static final int BA = 32;
   public static final int BA_LE = 64;
   public static final int LE = 128;
   public static final int FO_LE = 256;
   public static final int TO = 512;
   public static final int TO_FO = 1024;
   public static final int TO_BA = 2048;
   public static final int TO_RI = 4096;
   public static final int TO_LE = 8192;
   public static final int BO = 16384;
   public static final int BO_FO = 32768;
   public static final int BO_BA = 65536;
   public static final int BO_RI = 131072;
   public static final int BO_LE = 262144;
   public static final int[] SIDES = new int[]{2, 32, 128, 8, 512, 16384};
   public final int x;
   public final int y;
   public final int z;
   private class_2680 state;
   public int neighbours;
   public ESPGroup group;
   public boolean loaded = true;

   public ESPBlock(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public ESPBlock getSideBlock(int side) {
      ESPBlock var10000;
      switch (side) {
         case 2 -> var10000 = blockEsp.getBlock(this.x, this.y, this.z + 1);
         case 8 -> var10000 = blockEsp.getBlock(this.x + 1, this.y, this.z);
         case 32 -> var10000 = blockEsp.getBlock(this.x, this.y, this.z - 1);
         case 128 -> var10000 = blockEsp.getBlock(this.x - 1, this.y, this.z);
         case 512 -> var10000 = blockEsp.getBlock(this.x, this.y + 1, this.z);
         case 16384 -> var10000 = blockEsp.getBlock(this.x, this.y - 1, this.z);
         default -> var10000 = null;
      }

      return var10000;
   }

   private void assignGroup() {
      ESPGroup firstGroup = null;

      for(int side : SIDES) {
         if ((this.neighbours & side) == side) {
            ESPBlock neighbour = this.getSideBlock(side);
            if (neighbour != null && neighbour.group != null) {
               if (firstGroup == null) {
                  firstGroup = neighbour.group;
               } else if (firstGroup != neighbour.group) {
                  firstGroup.merge(neighbour.group);
               }
            }
         }
      }

      if (firstGroup == null) {
         firstGroup = blockEsp.newGroup(this.state.method_26204());
      }

      firstGroup.add(this);
   }

   public void update() {
      this.state = MeteorClient.mc.field_1687.method_8320(blockPos.method_10103(this.x, this.y, this.z));
      this.neighbours = 0;
      if (this.isNeighbour(class_2350.field_11035)) {
         this.neighbours |= 2;
      }

      if (this.isNeighbourDiagonal((double)1.0F, (double)0.0F, (double)1.0F)) {
         this.neighbours |= 4;
      }

      if (this.isNeighbour(class_2350.field_11034)) {
         this.neighbours |= 8;
      }

      if (this.isNeighbourDiagonal((double)1.0F, (double)0.0F, (double)-1.0F)) {
         this.neighbours |= 16;
      }

      if (this.isNeighbour(class_2350.field_11043)) {
         this.neighbours |= 32;
      }

      if (this.isNeighbourDiagonal((double)-1.0F, (double)0.0F, (double)-1.0F)) {
         this.neighbours |= 64;
      }

      if (this.isNeighbour(class_2350.field_11039)) {
         this.neighbours |= 128;
      }

      if (this.isNeighbourDiagonal((double)-1.0F, (double)0.0F, (double)1.0F)) {
         this.neighbours |= 256;
      }

      if (this.isNeighbour(class_2350.field_11036)) {
         this.neighbours |= 512;
      }

      if (this.isNeighbourDiagonal((double)0.0F, (double)1.0F, (double)1.0F)) {
         this.neighbours |= 1024;
      }

      if (this.isNeighbourDiagonal((double)0.0F, (double)1.0F, (double)-1.0F)) {
         this.neighbours |= 2048;
      }

      if (this.isNeighbourDiagonal((double)1.0F, (double)1.0F, (double)0.0F)) {
         this.neighbours |= 4096;
      }

      if (this.isNeighbourDiagonal((double)-1.0F, (double)1.0F, (double)0.0F)) {
         this.neighbours |= 8192;
      }

      if (this.isNeighbour(class_2350.field_11033)) {
         this.neighbours |= 16384;
      }

      if (this.isNeighbourDiagonal((double)0.0F, (double)-1.0F, (double)1.0F)) {
         this.neighbours |= 32768;
      }

      if (this.isNeighbourDiagonal((double)0.0F, (double)-1.0F, (double)-1.0F)) {
         this.neighbours |= 65536;
      }

      if (this.isNeighbourDiagonal((double)1.0F, (double)-1.0F, (double)0.0F)) {
         this.neighbours |= 131072;
      }

      if (this.isNeighbourDiagonal((double)-1.0F, (double)-1.0F, (double)0.0F)) {
         this.neighbours |= 262144;
      }

      if (this.group == null) {
         this.assignGroup();
      }

   }

   private boolean isNeighbour(class_2350 dir) {
      blockPos.method_10103(this.x + dir.method_10148(), this.y + dir.method_10164(), this.z + dir.method_10165());
      class_2680 neighbourState = MeteorClient.mc.field_1687.method_8320(blockPos);
      if (neighbourState.method_26204() != this.state.method_26204()) {
         return false;
      } else {
         class_265 cube = class_259.method_1077();
         class_265 shape = this.state.method_26218(MeteorClient.mc.field_1687, blockPos);
         class_265 neighbourShape = neighbourState.method_26218(MeteorClient.mc.field_1687, blockPos);
         if (shape.method_1110()) {
            shape = cube;
         }

         if (neighbourShape.method_1110()) {
            neighbourShape = cube;
         }

         switch (dir) {
            case field_11035:
               if (shape.method_1105(class_2351.field_11051) == (double)1.0F && neighbourShape.method_1091(class_2351.field_11051) == (double)0.0F) {
                  return true;
               }
               break;
            case field_11043:
               if (shape.method_1091(class_2351.field_11051) == (double)0.0F && neighbourShape.method_1105(class_2351.field_11051) == (double)1.0F) {
                  return true;
               }
               break;
            case field_11034:
               if (shape.method_1105(class_2351.field_11048) == (double)1.0F && neighbourShape.method_1091(class_2351.field_11048) == (double)0.0F) {
                  return true;
               }
               break;
            case field_11039:
               if (shape.method_1091(class_2351.field_11048) == (double)0.0F && neighbourShape.method_1105(class_2351.field_11048) == (double)1.0F) {
                  return true;
               }
               break;
            case field_11036:
               if (shape.method_1105(class_2351.field_11052) == (double)1.0F && neighbourShape.method_1091(class_2351.field_11052) == (double)0.0F) {
                  return true;
               }
               break;
            case field_11033:
               if (shape.method_1091(class_2351.field_11052) == (double)0.0F && neighbourShape.method_1105(class_2351.field_11052) == (double)1.0F) {
                  return true;
               }
         }

         return false;
      }
   }

   private boolean isNeighbourDiagonal(double x, double y, double z) {
      blockPos.method_10102((double)this.x + x, (double)this.y + y, (double)this.z + z);
      return this.state.method_26204() == MeteorClient.mc.field_1687.method_8320(blockPos).method_26204();
   }

   public void render(Render3DEvent event) {
      double x1 = (double)this.x;
      double y1 = (double)this.y;
      double z1 = (double)this.z;
      double x2 = (double)(this.x + 1);
      double y2 = (double)(this.y + 1);
      double z2 = (double)(this.z + 1);
      class_265 shape = this.state.method_26218(MeteorClient.mc.field_1687, blockPos);
      if (!shape.method_1110()) {
         x1 = (double)this.x + shape.method_1091(class_2351.field_11048);
         y1 = (double)this.y + shape.method_1091(class_2351.field_11052);
         z1 = (double)this.z + shape.method_1091(class_2351.field_11051);
         x2 = (double)this.x + shape.method_1105(class_2351.field_11048);
         y2 = (double)this.y + shape.method_1105(class_2351.field_11052);
         z2 = (double)this.z + shape.method_1105(class_2351.field_11051);
      }

      ESPBlockData blockData = blockEsp.getBlockData(this.state.method_26204());
      ShapeMode shapeMode = blockData.shapeMode;
      Color lineColor = blockData.lineColor;
      Color sideColor = blockData.sideColor;
      if (this.neighbours == 0) {
         event.renderer.box(x1, y1, z1, x2, y2, z2, sideColor, lineColor, shapeMode, 0);
      } else {
         if (shapeMode.lines()) {
            if ((this.neighbours & 128) != 128 && (this.neighbours & 32) != 32 || (this.neighbours & 128) == 128 && (this.neighbours & 32) == 32 && (this.neighbours & 64) != 64) {
               event.renderer.line(x1, y1, z1, x1, y2, z1, lineColor);
            }

            if ((this.neighbours & 128) != 128 && (this.neighbours & 2) != 2 || (this.neighbours & 128) == 128 && (this.neighbours & 2) == 2 && (this.neighbours & 256) != 256) {
               event.renderer.line(x1, y1, z2, x1, y2, z2, lineColor);
            }

            if ((this.neighbours & 8) != 8 && (this.neighbours & 32) != 32 || (this.neighbours & 8) == 8 && (this.neighbours & 32) == 32 && (this.neighbours & 16) != 16) {
               event.renderer.line(x2, y1, z1, x2, y2, z1, lineColor);
            }

            if ((this.neighbours & 8) != 8 && (this.neighbours & 2) != 2 || (this.neighbours & 8) == 8 && (this.neighbours & 2) == 2 && (this.neighbours & 4) != 4) {
               event.renderer.line(x2, y1, z2, x2, y2, z2, lineColor);
            }

            if ((this.neighbours & 32) != 32 && (this.neighbours & 16384) != 16384 || (this.neighbours & 32) != 32 && (this.neighbours & 65536) == 65536) {
               event.renderer.line(x1, y1, z1, x2, y1, z1, lineColor);
            }

            if ((this.neighbours & 2) != 2 && (this.neighbours & 16384) != 16384 || (this.neighbours & 2) != 2 && (this.neighbours & '耀') == 32768) {
               event.renderer.line(x1, y1, z2, x2, y1, z2, lineColor);
            }

            if ((this.neighbours & 32) != 32 && (this.neighbours & 512) != 512 || (this.neighbours & 32) != 32 && (this.neighbours & 2048) == 2048) {
               event.renderer.line(x1, y2, z1, x2, y2, z1, lineColor);
            }

            if ((this.neighbours & 2) != 2 && (this.neighbours & 512) != 512 || (this.neighbours & 2) != 2 && (this.neighbours & 1024) == 1024) {
               event.renderer.line(x1, y2, z2, x2, y2, z2, lineColor);
            }

            if ((this.neighbours & 128) != 128 && (this.neighbours & 16384) != 16384 || (this.neighbours & 128) != 128 && (this.neighbours & 262144) == 262144) {
               event.renderer.line(x1, y1, z1, x1, y1, z2, lineColor);
            }

            if ((this.neighbours & 8) != 8 && (this.neighbours & 16384) != 16384 || (this.neighbours & 8) != 8 && (this.neighbours & 131072) == 131072) {
               event.renderer.line(x2, y1, z1, x2, y1, z2, lineColor);
            }

            if ((this.neighbours & 128) != 128 && (this.neighbours & 512) != 512 || (this.neighbours & 128) != 128 && (this.neighbours & 8192) == 8192) {
               event.renderer.line(x1, y2, z1, x1, y2, z2, lineColor);
            }

            if ((this.neighbours & 8) != 8 && (this.neighbours & 512) != 512 || (this.neighbours & 8) != 8 && (this.neighbours & 4096) == 4096) {
               event.renderer.line(x2, y2, z1, x2, y2, z2, lineColor);
            }
         }

         if (shapeMode.sides()) {
            if ((this.neighbours & 16384) != 16384) {
               event.renderer.quadHorizontal(x1, y1, z1, x2, z2, sideColor);
            }

            if ((this.neighbours & 512) != 512) {
               event.renderer.quadHorizontal(x1, y2, z1, x2, z2, sideColor);
            }

            if ((this.neighbours & 2) != 2) {
               event.renderer.quadVertical(x1, y1, z2, x2, y2, z2, sideColor);
            }

            if ((this.neighbours & 32) != 32) {
               event.renderer.quadVertical(x1, y1, z1, x2, y2, z1, sideColor);
            }

            if ((this.neighbours & 8) != 8) {
               event.renderer.quadVertical(x2, y1, z1, x2, y2, z2, sideColor);
            }

            if ((this.neighbours & 128) != 128) {
               event.renderer.quadVertical(x1, y1, z1, x1, y2, z2, sideColor);
            }
         }
      }

   }

   public static long getKey(int x, int y, int z) {
      return (long)y << 16 | (long)(z & 15) << 8 | (long)(x & 15);
   }

   public static long getKey(class_2338 blockPos) {
      return getKey(blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260());
   }
}
