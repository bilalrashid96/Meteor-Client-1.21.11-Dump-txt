package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.pathing.NopPathManager;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_11908;
import net.minecraft.class_11909;

public class AutoWalk extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Mode> mode;
   private final Setting<Direction> direction;
   private final Setting<Boolean> disableOnInput;
   private final Setting<Boolean> disableOnY;
   private final Setting<Boolean> waitForChunks;

   public AutoWalk() {
      super(Categories.Movement, "auto-walk", "Automatically walks forward.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Walking mode.")).defaultValue(AutoWalk.Mode.Smart)).onChanged((mode1) -> {
         if (this.isActive() && Utils.canUpdate()) {
            if (mode1 == AutoWalk.Mode.Simple) {
               PathManagers.get().stop();
            } else {
               this.createGoal();
            }

            this.unpress();
         }

      })).build());
      this.direction = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("simple-direction")).description("The direction to walk in simple mode.")).defaultValue(AutoWalk.Direction.Forwards)).onChanged((direction1) -> {
         if (this.isActive()) {
            this.unpress();
         }

      })).visible(() -> this.mode.get() == AutoWalk.Mode.Simple)).build());
      this.disableOnInput = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-input")).description("Disable module on manual movement input")).defaultValue(false)).build());
      this.disableOnY = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-y-change")).description("Disable module if player moves vertically")).defaultValue(false)).visible(() -> this.mode.get() == AutoWalk.Mode.Simple)).build());
      this.waitForChunks = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("no-unloaded-chunks")).description("Do not allow movement into unloaded chunks")).defaultValue(true)).visible(() -> this.mode.get() == AutoWalk.Mode.Simple)).build());
   }

   public void onActivate() {
      if (this.mode.get() == AutoWalk.Mode.Smart) {
         this.createGoal();
      }

   }

   public void onDeactivate() {
      if (this.mode.get() == AutoWalk.Mode.Simple) {
         this.unpress();
      } else {
         PathManagers.get().stop();
      }

   }

   @EventHandler(
      priority = 100
   )
   private void onTick(TickEvent.Pre event) {
      if (this.mode.get() == AutoWalk.Mode.Simple) {
         if ((Boolean)this.disableOnY.get() && this.mc.field_1724.field_6036 != this.mc.field_1724.method_23318()) {
            this.toggle();
            return;
         }

         switch (((Direction)this.direction.get()).ordinal()) {
            case 0 -> this.mc.field_1690.field_1894.method_23481(true);
            case 1 -> this.mc.field_1690.field_1881.method_23481(true);
            case 2 -> this.mc.field_1690.field_1913.method_23481(true);
            case 3 -> this.mc.field_1690.field_1849.method_23481(true);
         }
      } else if (PathManagers.get() instanceof NopPathManager) {
         this.info("Smart mode requires Baritone", new Object[0]);
         this.toggle();
      }

   }

   private void onMovement() {
      if ((Boolean)this.disableOnInput.get()) {
         if (this.mc.field_1755 != null) {
            GUIMove guiMove = (GUIMove)Modules.get().get(GUIMove.class);
            if (!guiMove.isActive()) {
               return;
            }

            if (guiMove.skip()) {
               return;
            }
         }

         this.toggle();
      }
   }

   @EventHandler
   private void onKey(KeyEvent event) {
      if (this.isMovementKey(event.input) && event.action == KeyAction.Press) {
         this.onMovement();
      }

   }

   @EventHandler
   private void onMouseClick(MouseClickEvent event) {
      if (this.isMovementButton(event.click) && event.action == KeyAction.Press) {
         this.onMovement();
      }

   }

   @EventHandler
   private void onPlayerMove(PlayerMoveEvent event) {
      if (this.mode.get() == AutoWalk.Mode.Simple && (Boolean)this.waitForChunks.get()) {
         int chunkX = (int)((this.mc.field_1724.method_23317() + event.movement.field_1352 * (double)2.0F) / (double)16.0F);
         int chunkZ = (int)((this.mc.field_1724.method_23321() + event.movement.field_1350 * (double)2.0F) / (double)16.0F);
         if (!this.mc.field_1687.method_2935().method_12123(chunkX, chunkZ)) {
            ((IVec3d)event.movement).meteor$set((double)0.0F, event.movement.field_1351, (double)0.0F);
         }
      }

   }

   private void unpress() {
      this.mc.field_1690.field_1894.method_23481(false);
      this.mc.field_1690.field_1881.method_23481(false);
      this.mc.field_1690.field_1913.method_23481(false);
      this.mc.field_1690.field_1849.method_23481(false);
   }

   private boolean isMovementKey(class_11908 input) {
      return this.mc.field_1690.field_1894.method_1417(input) || this.mc.field_1690.field_1881.method_1417(input) || this.mc.field_1690.field_1913.method_1417(input) || this.mc.field_1690.field_1849.method_1417(input) || this.mc.field_1690.field_1832.method_1417(input) || this.mc.field_1690.field_1903.method_1417(input);
   }

   private boolean isMovementButton(class_11909 click) {
      return this.mc.field_1690.field_1894.method_1433(click) || this.mc.field_1690.field_1881.method_1433(click) || this.mc.field_1690.field_1913.method_1433(click) || this.mc.field_1690.field_1849.method_1433(click) || this.mc.field_1690.field_1832.method_1433(click) || this.mc.field_1690.field_1903.method_1433(click);
   }

   private void createGoal() {
      PathManagers.get().moveInDirection(this.mc.field_1724.method_36454());
   }

   public static enum Mode {
      Simple,
      Smart;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Simple, Smart};
      }
   }

   public static enum Direction {
      Forwards,
      Backwards,
      Left,
      Right;

      // $FF: synthetic method
      private static Direction[] $values() {
         return new Direction[]{Forwards, Backwards, Left, Right};
      }
   }
}
