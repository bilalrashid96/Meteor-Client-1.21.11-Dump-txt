package meteordevelopment.meteorclient.systems.modules.render.marker;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockPosSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_2338;

public class CuboidMarker extends BaseMarker {
   public static final String type = "Cuboid";
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<class_2338> pos1;
   private final Setting<class_2338> pos2;
   private final Setting<Mode> mode;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;

   public CuboidMarker() {
      super("Cuboid");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.pos1 = this.sgGeneral.add(((BlockPosSetting.Builder)((BlockPosSetting.Builder)(new BlockPosSetting.Builder()).name("pos-1")).description("1st corner of the cuboid")).build());
      this.pos2 = this.sgGeneral.add(((BlockPosSetting.Builder)((BlockPosSetting.Builder)(new BlockPosSetting.Builder()).name("pos-2")).description("2nd corner of the cuboid")).build());
      this.mode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("What mode to use for this marker.")).defaultValue(CuboidMarker.Mode.Full)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The color of the sides of the blocks being rendered.")).defaultValue(new SettingColor(0, 100, 255, 50)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The color of the lines of the blocks being rendered.")).defaultValue(new SettingColor(0, 100, 255, 255)).build());
   }

   public String getTypeName() {
      return "Cuboid";
   }

   protected void render(Render3DEvent event) {
      int minX = Math.min(((class_2338)this.pos1.get()).method_10263(), ((class_2338)this.pos2.get()).method_10263());
      int minY = Math.min(((class_2338)this.pos1.get()).method_10264(), ((class_2338)this.pos2.get()).method_10264());
      int minZ = Math.min(((class_2338)this.pos1.get()).method_10260(), ((class_2338)this.pos2.get()).method_10260());
      int maxX = Math.max(((class_2338)this.pos1.get()).method_10263(), ((class_2338)this.pos2.get()).method_10263());
      int maxY = Math.max(((class_2338)this.pos1.get()).method_10264(), ((class_2338)this.pos2.get()).method_10264());
      int maxZ = Math.max(((class_2338)this.pos1.get()).method_10260(), ((class_2338)this.pos2.get()).method_10260());
      event.renderer.box((double)minX, (double)minY, (double)minZ, (double)(maxX + 1), (double)(maxY + 1), (double)(maxZ + 1), this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
   }

   public static enum Mode {
      Full;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Full};
      }
   }
}
