package meteordevelopment.meteorclient.systems.modules.render;

import java.util.Objects;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.events.meteor.MouseScrollEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.ChunkOcclusionEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.GUIMove;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1675;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2724;
import net.minecraft.class_2749;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3726;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_4184;
import net.minecraft.class_5498;
import net.minecraft.class_5892;
import net.minecraft.class_761;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class Freecam extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPathing;
   private final Setting<Double> speed;
   private final Setting<Double> speedScrollSensitivity;
   private final Setting<Boolean> staySneaking;
   private final Setting<Boolean> toggleOnDamage;
   private final Setting<Boolean> toggleOnDeath;
   private final Setting<Boolean> toggleOnLog;
   private final Setting<Boolean> reloadChunks;
   private final Setting<Boolean> renderHands;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> staticView;
   private final Setting<Boolean> baritoneClick;
   private final Setting<Boolean> requireDoubleClick;
   public final Vector3d pos;
   public final Vector3d prevPos;
   private class_5498 perspective;
   private double speedValue;
   public float yaw;
   public float pitch;
   public float lastYaw;
   public float lastPitch;
   private double fovScale;
   private boolean bobView;
   private boolean forward;
   private boolean backward;
   private boolean right;
   private boolean left;
   private boolean up;
   private boolean down;
   private boolean isSneaking;
   private long clickTs;

   public Freecam() {
      super(Categories.Render, "freecam", "Allows the camera to move away from the player.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPathing = this.settings.createGroup("Pathing");
      this.speed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("speed")).description("Your speed while in freecam.")).onChanged((aDouble) -> this.speedValue = aDouble)).defaultValue((double)1.0F).min((double)0.0F).build());
      this.speedScrollSensitivity = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("speed-scroll-sensitivity")).description("Allows you to change speed value using scroll wheel. 0 to disable.")).defaultValue((double)0.0F).min((double)0.0F).sliderMax((double)2.0F).build());
      this.staySneaking = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("stay-sneaking")).description("If you are sneaking when you enter freecam, whether your player should remain sneaking.")).defaultValue(true)).build());
      this.toggleOnDamage = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-damage")).description("Disables freecam when you take damage.")).defaultValue(false)).build());
      this.toggleOnDeath = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-death")).description("Disables freecam when you die.")).defaultValue(false)).build());
      this.toggleOnLog = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-log")).description("Disables freecam when you disconnect from a server.")).defaultValue(true)).build());
      this.reloadChunks = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("reload-chunks")).description("Disables cave culling.")).defaultValue(true)).build());
      this.renderHands = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-hands")).description("Whether or not to render your hands in freecam.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates to the block or entity you are looking at.")).defaultValue(false)).build());
      this.staticView = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("static")).description("Disables settings that move the view.")).defaultValue(true)).build());
      this.baritoneClick = this.sgPathing.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("click-to-path")).description("Sets a pathfinding goal to any block/entity you click at.")).defaultValue(false)).build());
      this.requireDoubleClick = this.sgPathing.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("double-click")).description("Require two clicks to start pathing.")).defaultValue(false)).build());
      this.pos = new Vector3d();
      this.prevPos = new Vector3d();
      this.clickTs = 0L;
   }

   public void onActivate() {
      this.fovScale = (Double)this.mc.field_1690.method_42454().method_41753();
      this.bobView = (Boolean)this.mc.field_1690.method_42448().method_41753();
      if ((Boolean)this.staticView.get()) {
         this.mc.field_1690.method_42454().method_41748((double)0.0F);
         this.mc.field_1690.method_42448().method_41748(false);
      }

      this.yaw = this.mc.field_1724.method_36454();
      this.pitch = this.mc.field_1724.method_36455();
      this.perspective = this.mc.field_1690.method_31044();
      this.speedValue = (Double)this.speed.get();
      Utils.set(this.pos, this.mc.field_1773.method_19418().method_71156());
      Utils.set(this.prevPos, this.mc.field_1773.method_19418().method_71156());
      if (this.mc.field_1690.method_31044() == class_5498.field_26666) {
         this.yaw += 180.0F;
         this.pitch *= -1.0F;
      }

      this.lastYaw = this.yaw;
      this.lastPitch = this.pitch;
      this.isSneaking = this.mc.field_1690.field_1832.method_1434();
      this.forward = Input.isPressed(this.mc.field_1690.field_1894);
      this.backward = Input.isPressed(this.mc.field_1690.field_1881);
      this.right = Input.isPressed(this.mc.field_1690.field_1849);
      this.left = Input.isPressed(this.mc.field_1690.field_1913);
      this.up = Input.isPressed(this.mc.field_1690.field_1903);
      this.down = Input.isPressed(this.mc.field_1690.field_1832);
      this.unpress();
      if ((Boolean)this.reloadChunks.get()) {
         this.mc.field_1769.method_3279();
      }

   }

   public void onDeactivate() {
      if ((Boolean)this.reloadChunks.get()) {
         class_310 var10000 = this.mc;
         class_761 var10001 = this.mc.field_1769;
         Objects.requireNonNull(var10001);
         var10000.execute(var10001::method_3279);
      }

      this.mc.field_1690.method_31043(this.perspective);
      if ((Boolean)this.staticView.get()) {
         this.mc.field_1690.method_42454().method_41748(this.fovScale);
         this.mc.field_1690.method_42448().method_41748(this.bobView);
      }

      this.isSneaking = false;
   }

   @EventHandler
   private void onOpenScreen(OpenScreenEvent event) {
      this.unpress();
      this.prevPos.set(this.pos);
      this.lastYaw = this.yaw;
      this.lastPitch = this.pitch;
   }

   private void unpress() {
      this.mc.field_1690.field_1894.method_23481(false);
      this.mc.field_1690.field_1881.method_23481(false);
      this.mc.field_1690.field_1849.method_23481(false);
      this.mc.field_1690.field_1913.method_23481(false);
      this.mc.field_1690.field_1903.method_23481(false);
      this.mc.field_1690.field_1832.method_23481(false);
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.method_1560().method_5757()) {
         this.mc.method_1560().field_5960 = true;
      }

      if (!this.perspective.method_31034()) {
         this.mc.field_1690.method_31043(class_5498.field_26664);
      }

      class_243 forward = class_243.method_1030(0.0F, this.yaw);
      class_243 right = class_243.method_1030(0.0F, this.yaw + 90.0F);
      double velX = (double)0.0F;
      double velY = (double)0.0F;
      double velZ = (double)0.0F;
      if ((Boolean)this.rotate.get()) {
         if (this.mc.field_1765 instanceof class_3966) {
            class_2338 crossHairPos = ((class_3966)this.mc.field_1765).method_17782().method_24515();
            Rotations.rotate(Rotations.getYaw(crossHairPos), Rotations.getPitch(crossHairPos), 0, (Runnable)null);
         } else {
            class_243 crossHairPosition = this.mc.field_1765.method_17784();
            class_2338 crossHairPos = ((class_3965)this.mc.field_1765).method_17777();
            if (!this.mc.field_1687.method_8320(crossHairPos).method_26215()) {
               Rotations.rotate(Rotations.getYaw(crossHairPosition), Rotations.getPitch(crossHairPosition), 0, (Runnable)null);
            }
         }
      }

      double s = (double)0.5F;
      if (Input.isPressed(this.mc.field_1690.field_1867)) {
         s = (double)1.0F;
      }

      boolean a = false;
      if (this.forward) {
         velX += forward.field_1352 * s * this.speedValue;
         velZ += forward.field_1350 * s * this.speedValue;
         a = true;
      }

      if (this.backward) {
         velX -= forward.field_1352 * s * this.speedValue;
         velZ -= forward.field_1350 * s * this.speedValue;
         a = true;
      }

      boolean b = false;
      if (this.right) {
         velX += right.field_1352 * s * this.speedValue;
         velZ += right.field_1350 * s * this.speedValue;
         b = true;
      }

      if (this.left) {
         velX -= right.field_1352 * s * this.speedValue;
         velZ -= right.field_1350 * s * this.speedValue;
         b = true;
      }

      if (a && b) {
         double diagonal = (double)1.0F / Math.sqrt((double)2.0F);
         velX *= diagonal;
         velZ *= diagonal;
      }

      if (this.up) {
         velY += s * this.speedValue;
      }

      if (this.down) {
         velY -= s * this.speedValue;
      }

      this.prevPos.set(this.pos);
      this.pos.set(this.pos.x + velX, this.pos.y + velY, this.pos.z + velZ);
   }

   @EventHandler(
      priority = 100
   )
   public void onKey(KeyEvent event) {
      if (!Input.isKeyPressed(292)) {
         if (!this.checkGuiMove()) {
            if (this.onInput(event.key(), event.action)) {
               event.cancel();
            }

         }
      }
   }

   private @Nullable class_2338 rayCastEntity(class_243 posVec, class_243 max, short maxDist) {
      class_3966 res = class_1675.method_18075(this.mc.field_1724, posVec, max, class_238.method_54784(class_2338.method_49637(posVec.field_1352, posVec.field_1351, posVec.field_1350), class_2338.method_49637(max.field_1352, max.field_1351, max.field_1350)), (entity) -> true, (double)maxDist);
      if (res == null) {
         return null;
      } else {
         class_243 vec = res.method_17784();
         return class_2338.method_49637(vec.field_1352, vec.field_1351, vec.field_1350);
      }
   }

   private @Nullable class_2338 rayCastBlock(class_243 posVec, class_243 max) {
      class_3959 ctx = new class_3959(posVec, max, class_3960.field_23142, class_242.field_1345, class_3726.method_16194());
      class_3965 res = this.mc.field_1687.method_17742(ctx);
      return res.method_17783() == class_240.field_1333 ? null : res.method_17777().method_10081(res.method_17780().method_62675());
   }

   private void setGoal() {
      long prevClick = this.clickTs;
      this.clickTs = System.currentTimeMillis();
      if (!(Boolean)this.requireDoubleClick.get() || this.clickTs - prevClick <= 500L) {
         class_4184 cam = this.mc.field_1773.method_19418();
         class_243 posVec = cam.method_71156();
         class_243 lookVec = class_243.method_1030(cam.method_19329(), cam.method_19330());
         short maxDist = 256;
         class_243 max = posVec.method_1019(lookVec.method_1021((double)maxDist));
         class_2338 pos = this.rayCastEntity(posVec, max, maxDist);
         if (pos == null) {
            pos = this.rayCastBlock(posVec, max);
         }

         if (pos != null) {
            PathManagers.get().moveTo(pos);
         }
      }
   }

   @EventHandler(
      priority = 100
   )
   private void onMouseClick(MouseClickEvent event) {
      if (!this.checkGuiMove()) {
         if ((Boolean)this.baritoneClick.get() && event.action == KeyAction.Press && this.mc.field_1690.field_1886.method_1433(event.click)) {
            this.setGoal();
         }

         if (this.onInput(event.button(), event.action)) {
            event.cancel();
         }

      }
   }

   private boolean onInput(int key, KeyAction action) {
      if (Input.getKey(this.mc.field_1690.field_1894) == key) {
         this.forward = action != KeyAction.Release;
         this.mc.field_1690.field_1894.method_23481(false);
      } else if (Input.getKey(this.mc.field_1690.field_1881) == key) {
         this.backward = action != KeyAction.Release;
         this.mc.field_1690.field_1881.method_23481(false);
      } else if (Input.getKey(this.mc.field_1690.field_1849) == key) {
         this.right = action != KeyAction.Release;
         this.mc.field_1690.field_1849.method_23481(false);
      } else if (Input.getKey(this.mc.field_1690.field_1913) == key) {
         this.left = action != KeyAction.Release;
         this.mc.field_1690.field_1913.method_23481(false);
      } else if (Input.getKey(this.mc.field_1690.field_1903) == key) {
         this.up = action != KeyAction.Release;
         this.mc.field_1690.field_1903.method_23481(false);
      } else {
         if (Input.getKey(this.mc.field_1690.field_1832) != key) {
            return false;
         }

         this.down = action != KeyAction.Release;
         this.mc.field_1690.field_1832.method_23481(false);
      }

      return true;
   }

   @EventHandler(
      priority = -100
   )
   private void onMouseScroll(MouseScrollEvent event) {
      if ((Double)this.speedScrollSensitivity.get() > (double)0.0F && this.mc.field_1755 == null) {
         this.speedValue += event.value * (double)0.25F * (Double)this.speedScrollSensitivity.get() * this.speedValue;
         if (this.speedValue < 0.1) {
            this.speedValue = 0.1;
         }

         event.cancel();
      }

   }

   @EventHandler
   private void onChunkOcclusion(ChunkOcclusionEvent event) {
      event.cancel();
   }

   @EventHandler
   private void onGameLeft(GameLeftEvent event) {
      if ((Boolean)this.toggleOnLog.get()) {
         this.toggle();
      }
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      class_2596 entity = event.packet;
      if (entity instanceof class_5892 packet) {
         class_1297 entity = this.mc.field_1687.method_8469(packet.comp_2275());
         if (entity == this.mc.field_1724 && (Boolean)this.toggleOnDeath.get()) {
            this.toggle();
            this.info("Toggled off because you died.", new Object[0]);
         }
      } else {
         entity = event.packet;
         if (entity instanceof class_2749 packet) {
            if (this.mc.field_1724.method_6032() - packet.method_11833() > 0.0F && (Boolean)this.toggleOnDamage.get()) {
               this.toggle();
               this.info("Toggled off because you took damage.", new Object[0]);
            }
         } else if (event.packet instanceof class_2724 && this.isActive()) {
            this.toggle();
            this.info("Toggled off because you changed dimensions.", new Object[0]);
         }
      }

   }

   private boolean checkGuiMove() {
      GUIMove guiMove = (GUIMove)Modules.get().get(GUIMove.class);
      if (this.mc.field_1755 != null && !guiMove.isActive()) {
         return true;
      } else {
         return this.mc.field_1755 != null && guiMove.isActive() && guiMove.skip();
      }
   }

   public void changeLookDirection(double deltaX, double deltaY) {
      this.lastYaw = this.yaw;
      this.lastPitch = this.pitch;
      this.yaw += (float)deltaX;
      this.pitch += (float)deltaY;
      this.pitch = class_3532.method_15363(this.pitch, -90.0F, 90.0F);
   }

   public boolean renderHands() {
      return !this.isActive() || (Boolean)this.renderHands.get();
   }

   public boolean staySneaking() {
      return this.isActive() && !this.mc.field_1724.method_31549().field_7479 && (Boolean)this.staySneaking.get() && this.isSneaking;
   }

   public double getX(float tickDelta) {
      return class_3532.method_16436((double)tickDelta, this.prevPos.x, this.pos.x);
   }

   public double getY(float tickDelta) {
      return class_3532.method_16436((double)tickDelta, this.prevPos.y, this.pos.y);
   }

   public double getZ(float tickDelta) {
      return class_3532.method_16436((double)tickDelta, this.prevPos.z, this.pos.z);
   }

   public double getYaw(float tickDelta) {
      return (double)class_3532.method_16439(tickDelta, this.lastYaw, this.yaw);
   }

   public double getPitch(float tickDelta) {
      return (double)class_3532.method_16439(tickDelta, this.lastPitch, this.pitch);
   }
}
