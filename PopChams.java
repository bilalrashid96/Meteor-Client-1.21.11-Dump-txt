package meteordevelopment.meteorclient.systems.modules.render;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2596;
import net.minecraft.class_2663;

public class PopChams extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> onlyOne;
   private final Setting<Double> renderTime;
   private final Setting<Double> yModifier;
   private final Setting<Double> scaleModifier;
   private final Setting<Boolean> fadeOut;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final List<GhostPlayer> ghosts;

   public PopChams() {
      super(Categories.Render, "pop-chams", "Renders a ghost where players pop totem.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.onlyOne = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-one")).description("Only allow one ghost per player.")).defaultValue(false)).build());
      this.renderTime = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("render-time")).description("How long the ghost is rendered in seconds.")).defaultValue((double)1.0F).min(0.1).sliderMax((double)6.0F).build());
      this.yModifier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("y-modifier")).description("How much should the Y position of the ghost change per second.")).defaultValue((double)0.75F).sliderRange((double)-4.0F, (double)4.0F).build());
      this.scaleModifier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale-modifier")).description("How much should the scale of the ghost change per second.")).defaultValue((double)-0.25F).sliderRange((double)-4.0F, (double)4.0F).build());
      this.fadeOut = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fade-out")).description("Fades out the color.")).defaultValue(true)).build());
      this.shapeMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(255, 255, 255, 25)).build());
      this.lineColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(255, 255, 255, 127)).build());
      this.ghosts = new ArrayList();
   }

   public void onDeactivate() {
      synchronized(this.ghosts) {
         this.ghosts.clear();
      }
   }

   @EventHandler
   private void onReceivePacket(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2663 p) {
         if (p.method_11470() == 35) {
            class_1297 entity = p.method_11469(this.mc.field_1687);
            if (entity instanceof class_1657) {
               class_1657 player = (class_1657)entity;
               if (entity != this.mc.field_1724) {
                  synchronized(this.ghosts) {
                     if ((Boolean)this.onlyOne.get()) {
                        this.ghosts.removeIf((ghostPlayer) -> ghostPlayer.uuid.equals(entity.method_5667()));
                     }

                     this.ghosts.add(new GhostPlayer(player));
                     return;
                  }
               }
            }

         }
      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      synchronized(this.ghosts) {
         this.ghosts.removeIf((ghostPlayer) -> ghostPlayer.render(event));
      }
   }

   private class GhostPlayer extends FakePlayerEntity {
      private final UUID uuid;
      private double timer;
      private double scale = (double)1.0F;

      public GhostPlayer(class_1657 player) {
         super(player, "ghost", 20.0F, false);
         this.uuid = player.method_5667();
      }

      public boolean render(Render3DEvent event) {
         this.timer += event.frameTime;
         if (this.timer > (Double)PopChams.this.renderTime.get()) {
            return true;
         } else {
            this.field_5971 = this.method_23318();
            ((IVec3d)this.method_73189()).meteor$setY(this.method_23318() + (Double)PopChams.this.yModifier.get() * event.frameTime);
            this.scale += (Double)PopChams.this.scaleModifier.get() * event.frameTime;
            int preSideA = (PopChams.this.sideColor.get()).a;
            int preLineA = (PopChams.this.lineColor.get()).a;
            if ((Boolean)PopChams.this.fadeOut.get()) {
               SettingColor var10000 = PopChams.this.sideColor.get();
               var10000.a = (int)((double)var10000.a * ((double)1.0F - this.timer / (Double)PopChams.this.renderTime.get()));
               var10000 = PopChams.this.lineColor.get();
               var10000.a = (int)((double)var10000.a * ((double)1.0F - this.timer / (Double)PopChams.this.renderTime.get()));
            }

            WireframeEntityRenderer.render(event, this, this.scale, PopChams.this.sideColor.get(), PopChams.this.lineColor.get(), PopChams.this.shapeMode.get());
            (PopChams.this.sideColor.get()).a = preSideA;
            (PopChams.this.lineColor.get()).a = preLineA;
            return false;
         }
      }
   }
}
