package meteordevelopment.meteorclient.systems.modules.render.marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockPosSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_2338;

public class Sphere2dMarker extends BaseMarker {
   public static final String type = "Sphere-2D";
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final SettingGroup sgKeybinding;
   private final Setting<class_2338> center;
   private final Setting<Integer> radius;
   private final Setting<Integer> layer;
   private final Setting<Boolean> limitRenderRange;
   private final Setting<Integer> renderRange;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<Keybind> nextLayerKey;
   private final Setting<Keybind> prevLayerKey;
   private final List<Block> blocks;
   private boolean dirty;
   private boolean calculating;

   public Sphere2dMarker() {
      super("Sphere-2D");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.sgKeybinding = this.settings.createGroup("Keybinding");
      this.center = this.sgGeneral.add(((BlockPosSetting.Builder)((BlockPosSetting.Builder)((BlockPosSetting.Builder)(new BlockPosSetting.Builder()).name("center")).description("Center of the sphere")).onChanged((bp) -> this.dirty = true)).build());
      this.radius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("radius")).description("Radius of the sphere")).defaultValue(20)).min(1).noSlider().onChanged((r) -> this.dirty = true)).build());
      this.layer = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("layer")).description("Which layer to render")).defaultValue(0)).min(0).noSlider().onChanged((l) -> this.dirty = true)).build());
      this.limitRenderRange = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("limit-render-range")).description("Whether to limit rendering range (useful in very large circles)")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgRender;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("render-range")).description("Rendering range")).defaultValue(10)).min(1).sliderRange(1, 20);
      Setting var10003 = this.limitRenderRange;
      Objects.requireNonNull(var10003);
      this.renderRange = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The color of the sides of the blocks being rendered.")).defaultValue(new SettingColor(0, 100, 255, 50)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The color of the lines of the blocks being rendered.")).defaultValue(new SettingColor(0, 100, 255, 255)).build());
      this.nextLayerKey = this.sgKeybinding.add(((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("next-layer-keybind")).description("Keybind to increment layer")).action(() -> {
         if (this.isVisible() && (Integer)this.layer.get() < (Integer)this.radius.get() * 2) {
            this.layer.set((Integer)this.layer.get() + 1);
         }

      }).build());
      this.prevLayerKey = this.sgKeybinding.add(((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("prev-layer-keybind")).description("Keybind to increment layer")).action(() -> {
         if (this.isVisible()) {
            this.layer.set((Integer)this.layer.get() - 1);
         }

      }).build());
      this.blocks = new ArrayList();
      this.dirty = true;
   }

   protected void render(Render3DEvent event) {
      if (this.dirty && !this.calculating) {
         this.calcCircle();
      }

      synchronized(this.blocks) {
         for(Block block : this.blocks) {
            if (!(Boolean)this.limitRenderRange.get() || PlayerUtils.isWithin((double)block.x, (double)block.y, (double)block.z, (double)(Integer)this.renderRange.get())) {
               event.renderer.box((double)block.x, (double)block.y, (double)block.z, (double)(block.x + 1), (double)(block.y + 1), (double)(block.z + 1), this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), block.excludeDir);
            }
         }

      }
   }

   public String getTypeName() {
      return "Sphere-2D";
   }

   private void calcCircle() {
      this.calculating = true;
      this.blocks.clear();
      Runnable action = () -> {
         int cX = ((class_2338)this.center.get()).method_10263();
         int cY = ((class_2338)this.center.get()).method_10264();
         int cZ = ((class_2338)this.center.get()).method_10260();
         int rSq = (Integer)this.radius.get() * (Integer)this.radius.get();
         int dY = -(Integer)this.radius.get() + (Integer)this.layer.get();
         int dX = 0;

         while(true) {
            int dZ = (int)Math.round(Math.sqrt((double)(rSq - (dX * dX + dY * dY))));
            synchronized(this.blocks) {
               this.add(cX + dX, cY + dY, cZ + dZ);
               this.add(cX + dZ, cY + dY, cZ + dX);
               this.add(cX - dX, cY + dY, cZ - dZ);
               this.add(cX - dZ, cY + dY, cZ - dX);
               this.add(cX + dX, cY + dY, cZ - dZ);
               this.add(cX + dZ, cY + dY, cZ - dX);
               this.add(cX - dX, cY + dY, cZ + dZ);
               this.add(cX - dZ, cY + dY, cZ + dX);
            }

            if (dX >= dZ) {
               synchronized(this.blocks) {
                  for(Block block : this.blocks) {
                     for(Block b : this.blocks) {
                        if (b != block) {
                           if (b.x == block.x + 1 && b.z == block.z) {
                              block.excludeDir |= 64;
                           }

                           if (b.x == block.x - 1 && b.z == block.z) {
                              block.excludeDir |= 32;
                           }

                           if (b.x == block.x && b.z == block.z + 1) {
                              block.excludeDir |= 16;
                           }

                           if (b.x == block.x && b.z == block.z - 1) {
                              block.excludeDir |= 8;
                           }
                        }
                     }
                  }
               }

               this.dirty = false;
               this.calculating = false;
               return;
            }

            ++dX;
         }
      };
      if ((Integer)this.radius.get() <= 50) {
         action.run();
      } else {
         MeteorExecutor.execute(action);
      }

   }

   private void add(int x, int y, int z) {
      for(Block b : this.blocks) {
         if (b.x == x && b.y == y && b.z == z) {
            return;
         }
      }

      this.blocks.add(new Block(x, y, z));
   }

   private static class Block {
      public final int x;
      public final int y;
      public final int z;
      public int excludeDir;

      public Block(int x, int y, int z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }
   }
}
