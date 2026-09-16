package meteordevelopment.meteorclient.systems.modules.render;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StorageBlockListSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.MeshBuilderVertexConsumerProvider;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.SimpleBlockRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.render.postprocess.PostProcessShaders;
import meteordevelopment.meteorclient.utils.world.Dir;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_2281;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2586;
import net.minecraft.class_2589;
import net.minecraft.class_2591;
import net.minecraft.class_2595;
import net.minecraft.class_2601;
import net.minecraft.class_2609;
import net.minecraft.class_2611;
import net.minecraft.class_2614;
import net.minecraft.class_2627;
import net.minecraft.class_2646;
import net.minecraft.class_2680;
import net.minecraft.class_2745;
import net.minecraft.class_3719;
import net.minecraft.class_7716;
import net.minecraft.class_8172;
import net.minecraft.class_8887;

public class StorageESP extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgOpened;
   private final Set<class_2338> interactedBlocks;
   private final Setting<Mode> mode;
   private final Setting<List<class_2591<?>>> storageBlocks;
   private final Setting<Boolean> tracers;
   public final Setting<ShapeMode> shapeMode;
   public final Setting<Integer> fillOpacity;
   public final Setting<Integer> outlineWidth;
   public final Setting<Double> glowMultiplier;
   private final Setting<SettingColor> chest;
   private final Setting<SettingColor> trappedChest;
   private final Setting<SettingColor> barrel;
   private final Setting<SettingColor> shulker;
   private final Setting<SettingColor> enderChest;
   private final Setting<SettingColor> other;
   private final Setting<Double> fadeDistance;
   private final Setting<Boolean> hideOpened;
   private final Setting<SettingColor> openedColor;
   private final Color lineColor;
   private final Color sideColor;
   private boolean render;
   private int count;
   private final MeshBuilder mesh;
   private final MeshBuilderVertexConsumerProvider vertexConsumerProvider;

   public StorageESP() {
      super(Categories.Render, "storage-esp", "Renders all specified storage blocks.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgOpened = this.settings.createGroup("Opened Rendering");
      this.interactedBlocks = new HashSet();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Rendering mode.")).defaultValue(StorageESP.Mode.Shader)).build());
      this.storageBlocks = this.sgGeneral.add(((StorageBlockListSetting.Builder)((StorageBlockListSetting.Builder)(new StorageBlockListSetting.Builder()).name("storage-blocks")).description("Select the storage blocks to display.")).defaultValue(StorageBlockListSetting.STORAGE_BLOCKS).build());
      this.tracers = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("tracers")).description("Draws tracers to storage blocks.")).defaultValue(false)).build());
      this.shapeMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.fillOpacity = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("fill-opacity")).description("The opacity of the shape fill.")).visible(() -> this.shapeMode.get() != ShapeMode.Lines)).defaultValue(50)).range(0, 255).sliderMax(255).build());
      this.outlineWidth = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("width")).description("The width of the shader outline.")).visible(() -> this.mode.get() == StorageESP.Mode.Shader)).defaultValue(1)).range(1, 10).sliderRange(1, 5).build());
      this.glowMultiplier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("glow-multiplier")).description("Multiplier for glow effect")).visible(() -> this.mode.get() == StorageESP.Mode.Shader)).decimalPlaces(3).defaultValue((double)3.5F).min((double)0.0F).sliderMax((double)10.0F).build());
      this.chest = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("chest")).description("The color of chests.")).defaultValue(new SettingColor(255, 160, 0, 255)).build());
      this.trappedChest = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("trapped-chest")).description("The color of trapped chests.")).defaultValue(new SettingColor(255, 0, 0, 255)).build());
      this.barrel = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("barrel")).description("The color of barrels.")).defaultValue(new SettingColor(255, 160, 0, 255)).build());
      this.shulker = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("shulker")).description("The color of Shulker Boxes.")).defaultValue(new SettingColor(255, 160, 0, 255)).build());
      this.enderChest = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ender-chest")).description("The color of Ender Chests.")).defaultValue(new SettingColor(120, 0, 255, 255)).build());
      this.other = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("other")).description("The color of furnaces, dispensers, droppers and hoppers.")).defaultValue(new SettingColor(140, 140, 140, 255)).build());
      this.fadeDistance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fade-distance")).description("The distance at which the color will fade.")).defaultValue((double)6.0F).min((double)0.0F).sliderMax((double)12.0F).build());
      this.hideOpened = this.sgOpened.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hide-opened")).description("Hides opened containers.")).defaultValue(false)).build());
      this.openedColor = this.sgOpened.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("opened-color")).description("Optional setting to change colors of opened chests, as opposed to not rendering. Disabled at zero opacity.")).defaultValue(new SettingColor(203, 90, 203, 0)).build());
      this.lineColor = new Color(0, 0, 0, 0);
      this.sideColor = new Color(0, 0, 0, 0);
      this.mesh = new MeshBuilder(MeteorRenderPipelines.WORLD_COLORED);
      this.vertexConsumerProvider = new MeshBuilderVertexConsumerProvider(this.mesh);
   }

   private void getBlockEntityColor(class_2586 blockEntity) {
      this.render = false;
      if (((List)this.storageBlocks.get()).contains(blockEntity.method_11017())) {
         if (blockEntity instanceof class_2646) {
            this.lineColor.set(this.trappedChest.get());
         } else if (blockEntity instanceof class_2595) {
            this.lineColor.set(this.chest.get());
         } else if (blockEntity instanceof class_3719) {
            this.lineColor.set(this.barrel.get());
         } else if (blockEntity instanceof class_2627) {
            this.lineColor.set(this.shulker.get());
         } else if (blockEntity instanceof class_2611) {
            this.lineColor.set(this.enderChest.get());
         } else {
            if (!(blockEntity instanceof class_2609) && !(blockEntity instanceof class_2589) && !(blockEntity instanceof class_7716) && !(blockEntity instanceof class_8887) && !(blockEntity instanceof class_2601) && !(blockEntity instanceof class_8172) && !(blockEntity instanceof class_2614)) {
               return;
            }

            this.lineColor.set(this.other.get());
         }

         this.render = true;
         if (this.shapeMode.get() == ShapeMode.Sides || this.shapeMode.get() == ShapeMode.Both) {
            this.sideColor.set(this.lineColor);
            this.sideColor.a = (Integer)this.fillOpacity.get();
         }

      }
   }

   public WWidget getWidget(GuiTheme theme) {
      WVerticalList list = theme.verticalList();
      WButton clear = (WButton)list.add(theme.button("Clear Rendering Cache")).expandX().widget();
      Set var10001 = this.interactedBlocks;
      Objects.requireNonNull(var10001);
      clear.action = var10001::clear;
      return list;
   }

   @EventHandler
   private void onBlockInteract(InteractBlockEvent event) {
      class_2338 pos = event.result.method_17777();
      class_2586 blockEntity = this.mc.field_1687.method_8321(pos);
      if (blockEntity != null) {
         this.interactedBlocks.add(pos);
         if (blockEntity instanceof class_2595) {
            class_2595 chestBlockEntity = (class_2595)blockEntity;
            class_2680 state = chestBlockEntity.method_11010();
            class_2745 chestType = (class_2745)state.method_11654(class_2281.field_10770);
            if (chestType == class_2745.field_12574 || chestType == class_2745.field_12571) {
               class_2350 facing = (class_2350)state.method_11654(class_2281.field_10768);
               class_2338 otherPartPos = pos.method_10093(chestType == class_2745.field_12574 ? facing.method_10170() : facing.method_10160());
               this.interactedBlocks.add(otherPartPos);
            }
         }

      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      this.count = 0;

      for(class_2586 blockEntity : Utils.blockEntities()) {
         boolean interacted = this.interactedBlocks.contains(blockEntity.method_11016());
         if (!interacted || !(Boolean)this.hideOpened.get()) {
            this.getBlockEntityColor(blockEntity);
            if (interacted && (this.openedColor.get()).a > 0) {
               this.lineColor.set(this.openedColor.get());
               this.sideColor.set(this.openedColor.get());
               this.sideColor.a = (Integer)this.fillOpacity.get();
            }

            if (this.render) {
               double dist = PlayerUtils.squaredDistanceTo((double)blockEntity.method_11016().method_10263() + (double)0.5F, (double)blockEntity.method_11016().method_10264() + (double)0.5F, (double)blockEntity.method_11016().method_10260() + (double)0.5F);
               double a = (double)1.0F;
               if (dist <= (Double)this.fadeDistance.get() * (Double)this.fadeDistance.get()) {
                  a = dist / ((Double)this.fadeDistance.get() * (Double)this.fadeDistance.get());
               }

               if (!(a < 0.075)) {
                  if (this.count == 0 && this.mode.get() == StorageESP.Mode.Shader) {
                     this.mesh.begin();
                  }

                  int prevLineA = this.lineColor.a;
                  int prevSideA = this.sideColor.a;
                  Color var10000 = this.lineColor;
                  var10000.a = (int)((double)var10000.a * a);
                  var10000 = this.sideColor;
                  var10000.a = (int)((double)var10000.a * a);
                  if ((Boolean)this.tracers.get()) {
                     event.renderer.line(RenderUtils.center.field_1352, RenderUtils.center.field_1351, RenderUtils.center.field_1350, (double)blockEntity.method_11016().method_10263() + (double)0.5F, (double)blockEntity.method_11016().method_10264() + (double)0.5F, (double)blockEntity.method_11016().method_10260() + (double)0.5F, this.lineColor);
                  }

                  if (this.mode.get() == StorageESP.Mode.Box) {
                     this.renderBox(event, blockEntity);
                  }

                  if (this.mode.get() == StorageESP.Mode.Shader) {
                     this.renderShader(event, blockEntity);
                  }

                  this.lineColor.a = prevLineA;
                  this.sideColor.a = prevSideA;
                  ++this.count;
               }
            }
         }
      }

      if (this.mode.get() == StorageESP.Mode.Shader && this.count > 0) {
         MeshRenderer.begin().attachments(PostProcessShaders.STORAGE_OUTLINE.framebuffer).clearColor(Color.CLEAR).pipeline(MeteorRenderPipelines.WORLD_COLORED).mesh(this.mesh, event.matrices).end();
         PostProcessShaders.STORAGE_OUTLINE.render();
      }

   }

   private void renderBox(Render3DEvent event, class_2586 blockEntity) {
      double x1 = (double)blockEntity.method_11016().method_10263();
      double y1 = (double)blockEntity.method_11016().method_10264();
      double z1 = (double)blockEntity.method_11016().method_10260();
      double x2 = (double)(blockEntity.method_11016().method_10263() + 1);
      double y2 = (double)(blockEntity.method_11016().method_10264() + 1);
      double z2 = (double)(blockEntity.method_11016().method_10260() + 1);
      int excludeDir = 0;
      if (blockEntity instanceof class_2595) {
         class_2680 state = this.mc.field_1687.method_8320(blockEntity.method_11016());
         if ((state.method_26204() == class_2246.field_10034 || state.method_26204() == class_2246.field_10380) && state.method_11654(class_2281.field_10770) != class_2745.field_12569) {
            excludeDir = Dir.get(class_2281.method_9758(state));
         }
      }

      if (blockEntity instanceof class_2595 || blockEntity instanceof class_2611) {
         double a = (double)0.0625F;
         if (Dir.isNot(excludeDir, (byte)32)) {
            x1 += a;
         }

         if (Dir.isNot(excludeDir, (byte)8)) {
            z1 += a;
         }

         if (Dir.isNot(excludeDir, (byte)64)) {
            x2 -= a;
         }

         y2 -= a * (double)2.0F;
         if (Dir.isNot(excludeDir, (byte)16)) {
            z2 -= a;
         }
      }

      event.renderer.box(x1, y1, z1, x2, y2, z2, this.sideColor, this.lineColor, this.shapeMode.get(), excludeDir);
   }

   private void renderShader(Render3DEvent event, class_2586 blockEntity) {
      this.vertexConsumerProvider.setColor(this.lineColor);
      SimpleBlockRenderer.renderWithBlockEntity(blockEntity, event.tickDelta, this.vertexConsumerProvider);
   }

   public String getInfoString() {
      return Integer.toString(this.count);
   }

   public boolean isShader() {
      return this.isActive() && this.mode.get() == StorageESP.Mode.Shader;
   }

   public static enum Mode {
      Box,
      Shader;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Box, Shader};
      }
   }
}
