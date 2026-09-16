package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.AbstractBlockAccessor;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_3532;

public class Anchor extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Integer> maxHeight;
   private final Setting<Integer> minPitch;
   private final Setting<Boolean> cancelMove;
   private final Setting<Boolean> pull;
   private final Setting<Double> pullSpeed;
   private final class_2338.class_2339 blockPos;
   private boolean wasInHole;
   private boolean foundHole;
   private int holeX;
   private int holeZ;
   public boolean cancelJump;
   public boolean controlMovement;
   public double deltaX;
   public double deltaZ;

   public Anchor() {
      super(Categories.Movement, "anchor", "Helps you get into holes by stopping your movement completely over a hole.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.maxHeight = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-height")).description("The maximum height Anchor will work at.")).defaultValue(10)).range(0, 255).sliderMax(20).build());
      this.minPitch = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("min-pitch")).description("The minimum pitch at which anchor will work.")).defaultValue(0)).range(-90, 90).sliderRange(-90, 90).build());
      this.cancelMove = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("cancel-jump-in-hole")).description("Prevents you from jumping when Anchor is active and Min Pitch is met.")).defaultValue(false)).build());
      this.pull = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pull")).description("The pull strength of Anchor.")).defaultValue(false)).build());
      this.pullSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("pull-speed")).description("How fast to pull towards the hole in blocks per second.")).defaultValue(0.3).min((double)0.0F).sliderMax((double)5.0F).build());
      this.blockPos = new class_2338.class_2339();
   }

   public void onActivate() {
      this.wasInHole = false;
      this.holeX = this.holeZ = 0;
   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      this.cancelJump = this.foundHole && (Boolean)this.cancelMove.get() && this.mc.field_1724.method_36455() >= (float)(Integer)this.minPitch.get();
   }

   @EventHandler
   private void onPostTick(TickEvent.Post event) {
      this.controlMovement = false;
      int x = class_3532.method_15357(this.mc.field_1724.method_23317());
      int y = class_3532.method_15357(this.mc.field_1724.method_23318());
      int z = class_3532.method_15357(this.mc.field_1724.method_23321());
      if (this.isHole(x, y, z)) {
         this.wasInHole = true;
         this.holeX = x;
         this.holeZ = z;
      } else if (!this.wasInHole || this.holeX != x || this.holeZ != z) {
         if (this.wasInHole) {
            this.wasInHole = false;
         }

         if (!(this.mc.field_1724.method_36455() < (float)(Integer)this.minPitch.get())) {
            this.foundHole = false;
            double holeX = (double)0.0F;
            double holeZ = (double)0.0F;

            for(int i = 0; i < (Integer)this.maxHeight.get(); ++i) {
               --y;
               if (y <= this.mc.field_1687.method_31607() || !this.isAir(x, y, z)) {
                  break;
               }

               if (this.isHole(x, y, z)) {
                  this.foundHole = true;
                  holeX = (double)x + (double)0.5F;
                  holeZ = (double)z + (double)0.5F;
                  break;
               }
            }

            if (this.foundHole) {
               this.controlMovement = true;
               this.deltaX = class_3532.method_15350(holeX - this.mc.field_1724.method_23317(), -0.05, 0.05);
               this.deltaZ = class_3532.method_15350(holeZ - this.mc.field_1724.method_23321(), -0.05, 0.05);
               ((IVec3d)this.mc.field_1724.method_18798()).meteor$set(this.deltaX, this.mc.field_1724.method_18798().field_1351 - ((Boolean)this.pull.get() ? (Double)this.pullSpeed.get() : (double)0.0F), this.deltaZ);
            }

         }
      }
   }

   private boolean isHole(int x, int y, int z) {
      return this.isHoleBlock(x, y - 1, z) && this.isHoleBlock(x + 1, y, z) && this.isHoleBlock(x - 1, y, z) && this.isHoleBlock(x, y, z + 1) && this.isHoleBlock(x, y, z - 1);
   }

   private boolean isHoleBlock(int x, int y, int z) {
      this.blockPos.method_10103(x, y, z);
      class_2248 block = this.mc.field_1687.method_8320(this.blockPos).method_26204();
      return block == class_2246.field_9987 || block == class_2246.field_10540 || block == class_2246.field_22423;
   }

   private boolean isAir(int x, int y, int z) {
      this.blockPos.method_10103(x, y, z);
      return !((AbstractBlockAccessor)this.mc.field_1687.method_8320(this.blockPos).method_26204()).meteor$isCollidable();
   }
}
