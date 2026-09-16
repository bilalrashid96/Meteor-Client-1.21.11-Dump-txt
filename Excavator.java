package meteordevelopment.meteorclient.systems.modules.world;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.utils.BetterBlockPos;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_3965;

public class Excavator extends Module {
   private final IBaritone baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRendering;
   private final Setting<Keybind> selectionBind;
   private final Setting<Boolean> logSelection;
   private final Setting<Boolean> keepActive;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private Status status;
   private BetterBlockPos start;
   private BetterBlockPos end;

   public Excavator() {
      super(Categories.World, "excavator", "Excavate a selection area.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRendering = this.settings.createGroup("Rendering");
      this.selectionBind = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("selection-bind")).description("Bind to draw selection.")).defaultValue(Keybind.fromButton(1))).build());
      this.logSelection = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("log-selection")).description("Logs the selection coordinates to the chat.")).defaultValue(true)).build());
      this.keepActive = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("keep-active")).description("Keep the module active after finishing the excavation.")).defaultValue(false)).build());
      this.shapeMode = this.sgRendering.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRendering.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(255, 255, 255, 50)).build());
      this.lineColor = this.sgRendering.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.status = Excavator.Status.SEL_START;
   }

   public void onDeactivate() {
      this.baritone.getSelectionManager().removeSelection(this.baritone.getSelectionManager().getLastSelection());
      if (this.baritone.getBuilderProcess().isActive()) {
         this.baritone.getCommandManager().execute("stop");
      }

      this.status = Excavator.Status.SEL_START;
   }

   @EventHandler
   private void onMouseClick(MouseClickEvent event) {
      if (event.action == KeyAction.Press && ((Keybind)this.selectionBind.get()).isPressed() && this.mc.field_1755 == null) {
         this.selectCorners();
      }
   }

   @EventHandler
   private void onKey(KeyEvent event) {
      if (event.action == KeyAction.Press && ((Keybind)this.selectionBind.get()).isPressed() && this.mc.field_1755 == null) {
         this.selectCorners();
      }
   }

   private void selectCorners() {
      class_239 var2 = this.mc.field_1765;
      if (var2 instanceof class_3965 result) {
         if (this.status == Excavator.Status.SEL_START) {
            this.start = BetterBlockPos.from(result.method_17777());
            this.status = Excavator.Status.SEL_END;
            if ((Boolean)this.logSelection.get()) {
               this.info("Start corner set: (%d, %d, %d)".formatted(this.start.method_10263(), this.start.method_10264(), this.start.method_10260()), new Object[0]);
            }
         } else if (this.status == Excavator.Status.SEL_END) {
            this.end = BetterBlockPos.from(result.method_17777());
            this.status = Excavator.Status.WORKING;
            if ((Boolean)this.logSelection.get()) {
               this.info("End corner set: (%d, %d, %d)".formatted(this.end.method_10263(), this.end.method_10264(), this.end.method_10260()), new Object[0]);
            }

            this.baritone.getSelectionManager().addSelection(this.start, this.end);
            this.baritone.getBuilderProcess().clearArea(this.start, this.end);
         }

      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if (this.status != Excavator.Status.SEL_START && this.status != Excavator.Status.SEL_END) {
         if (this.status == Excavator.Status.WORKING && !this.baritone.getBuilderProcess().isActive()) {
            if ((Boolean)this.keepActive.get()) {
               this.baritone.getSelectionManager().removeSelection(this.baritone.getSelectionManager().getLastSelection());
               this.status = Excavator.Status.SEL_START;
            } else {
               this.toggle();
            }
         }
      } else {
         class_239 var3 = this.mc.field_1765;
         if (!(var3 instanceof class_3965)) {
            return;
         }

         class_3965 result = (class_3965)var3;
         event.renderer.box((class_2338)result.method_17777(), this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
      }

   }

   private static enum Status {
      SEL_START,
      SEL_END,
      WORKING;

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{SEL_START, SEL_END, WORKING};
      }
   }
}
