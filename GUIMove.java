package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.mixin.CreativeInventoryScreenAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_304;
import net.minecraft.class_3532;
import net.minecraft.class_408;
import net.minecraft.class_463;
import net.minecraft.class_471;
import net.minecraft.class_481;
import net.minecraft.class_497;
import net.minecraft.class_498;
import net.minecraft.class_7706;

public class GUIMove extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Screens> screens;
   public final Setting<Boolean> jump;
   public final Setting<Boolean> sneak;
   public final Setting<Boolean> sprint;
   private final Setting<Boolean> arrowsRotate;
   private final Setting<Double> rotateSpeed;

   public GUIMove() {
      super(Categories.Movement, "gui-move", "Allows you to perform various actions while in GUIs.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.screens = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("guis")).description("Which GUIs to move in.")).defaultValue(GUIMove.Screens.Inventory)).build());
      this.jump = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("jump")).description("Allows you to jump while in GUIs.")).defaultValue(true)).onChanged((aBoolean) -> {
         if (this.isActive() && !aBoolean) {
            this.mc.field_1690.field_1903.method_23481(false);
         }

      })).build());
      this.sneak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sneak")).description("Allows you to sneak while in GUIs.")).defaultValue(true)).onChanged((aBoolean) -> {
         if (this.isActive() && !aBoolean) {
            this.mc.field_1690.field_1832.method_23481(false);
         }

      })).build());
      this.sprint = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sprint")).description("Allows you to sprint while in GUIs.")).defaultValue(true)).onChanged((aBoolean) -> {
         if (this.isActive() && !aBoolean) {
            this.mc.field_1690.field_1867.method_23481(false);
         }

      })).build());
      this.arrowsRotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("arrows-rotate")).description("Allows you to use your arrow keys to rotate while in GUIs.")).defaultValue(true)).build());
      this.rotateSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rotate-speed")).description("Rotation speed while in GUIs.")).defaultValue((double)4.0F).min((double)0.0F).build());
   }

   public void onDeactivate() {
      this.mc.field_1690.field_1894.method_23481(false);
      this.mc.field_1690.field_1881.method_23481(false);
      this.mc.field_1690.field_1913.method_23481(false);
      this.mc.field_1690.field_1849.method_23481(false);
      if ((Boolean)this.jump.get()) {
         this.mc.field_1690.field_1903.method_23481(false);
      }

      if ((Boolean)this.sneak.get()) {
         this.mc.field_1690.field_1832.method_23481(false);
      }

      if ((Boolean)this.sprint.get()) {
         this.mc.field_1690.field_1867.method_23481(false);
      }

   }

   public boolean disableSpace() {
      return this.isActive() && (Boolean)this.jump.get() && this.mc.field_1690.field_1903.method_1427();
   }

   public boolean disableArrows() {
      return this.isActive() && (Boolean)this.arrowsRotate.get();
   }

   @EventHandler
   private void onKey(KeyEvent event) {
      this.onInput(event.key(), event.action);
   }

   @EventHandler
   private void onButton(MouseClickEvent event) {
      this.onInput(event.button(), event.action);
   }

   private void onInput(int key, KeyAction action) {
      if (!this.skip()) {
         this.pass(this.mc.field_1690.field_1894, key, action);
         this.pass(this.mc.field_1690.field_1881, key, action);
         this.pass(this.mc.field_1690.field_1913, key, action);
         this.pass(this.mc.field_1690.field_1849, key, action);
         if ((Boolean)this.jump.get()) {
            this.pass(this.mc.field_1690.field_1903, key, action);
         }

         if ((Boolean)this.sneak.get()) {
            this.pass(this.mc.field_1690.field_1832, key, action);
         }

         if ((Boolean)this.sprint.get()) {
            this.pass(this.mc.field_1690.field_1867, key, action);
         }

      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if (!this.skip()) {
         float rotationDelta = Math.min((float)((Double)this.rotateSpeed.get() * event.frameTime * (double)20.0F), 100.0F);
         Freecam freecam = (Freecam)Modules.get().get(Freecam.class);
         if ((Boolean)this.arrowsRotate.get()) {
            if (!freecam.isActive()) {
               float yaw = this.mc.field_1724.method_36454();
               float pitch = this.mc.field_1724.method_36455();
               if (Input.isKeyPressed(263)) {
                  yaw -= rotationDelta;
               }

               if (Input.isKeyPressed(262)) {
                  yaw += rotationDelta;
               }

               if (Input.isKeyPressed(265)) {
                  pitch -= rotationDelta;
               }

               if (Input.isKeyPressed(264)) {
                  pitch += rotationDelta;
               }

               pitch = class_3532.method_15363(pitch, -90.0F, 90.0F);
               this.mc.field_1724.method_36456(yaw);
               this.mc.field_1724.method_36457(pitch);
            } else {
               double dy = (double)0.0F;
               double dx = (double)0.0F;
               if (Input.isKeyPressed(263)) {
                  dy = (double)(-rotationDelta);
               }

               if (Input.isKeyPressed(262)) {
                  dy = (double)rotationDelta;
               }

               if (Input.isKeyPressed(265)) {
                  dx = (double)(-rotationDelta);
               }

               if (Input.isKeyPressed(264)) {
                  dx = (double)rotationDelta;
               }

               freecam.changeLookDirection(dy, dx);
            }
         }

      }
   }

   private void pass(class_304 bind, int key, KeyAction action) {
      if (Input.getKey(bind) == key) {
         if (action == KeyAction.Press) {
            bind.method_23481(true);
         }

         if (action == KeyAction.Release) {
            bind.method_23481(false);
         }

      }
   }

   public boolean skip() {
      if (this.mc.field_1755 != null && (!(this.mc.field_1755 instanceof class_481) || CreativeInventoryScreenAccessor.meteor$getSelectedTab() != class_7706.method_47344()) && !(this.mc.field_1755 instanceof class_408) && !(this.mc.field_1755 instanceof class_498) && !(this.mc.field_1755 instanceof class_471) && !(this.mc.field_1755 instanceof class_463) && !(this.mc.field_1755 instanceof class_497)) {
         if (this.screens.get() == GUIMove.Screens.GUI && !(this.mc.field_1755 instanceof WidgetScreen)) {
            return true;
         } else {
            return this.screens.get() == GUIMove.Screens.Inventory && this.mc.field_1755 instanceof WidgetScreen;
         }
      } else {
         return true;
      }
   }

   public static enum Screens {
      GUI,
      Inventory,
      Both;

      // $FF: synthetic method
      private static Screens[] $values() {
         return new Screens[]{GUI, Inventory, Both};
      }
   }
}
