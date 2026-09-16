package meteordevelopment.meteorclient.utils.tooltip;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import net.minecraft.class_10090;
import net.minecraft.class_10799;
import net.minecraft.class_1806;
import net.minecraft.class_22;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5684;
import net.minecraft.class_9209;

public class MapTooltipComponent implements class_5684, MeteorTooltipData {
   private static final class_2960 TEXTURE_MAP_BACKGROUND = class_2960.method_60654("textures/map/map_background.png");
   private final int mapId;
   private final class_10090 mapRenderState = new class_10090();

   public MapTooltipComponent(int mapId) {
      this.mapId = mapId;
   }

   public int method_32661(class_327 textRenderer) {
      double scale = (Double)((BetterTooltips)Modules.get().get(BetterTooltips.class)).mapsScale.get();
      return (int)((double)144.0F * scale) + 2;
   }

   public int method_32664(class_327 textRenderer) {
      double scale = (Double)((BetterTooltips)Modules.get().get(BetterTooltips.class)).mapsScale.get();
      return (int)((double)144.0F * scale);
   }

   public class_5684 getComponent() {
      return this;
   }

   public void method_32666(class_327 textRenderer, int x, int y, int width, int height, class_332 context) {
      float scale = ((Double)((BetterTooltips)Modules.get().get(BetterTooltips.class)).mapsScale.get()).floatValue();
      int size = (int)(144.0F * scale);
      context.method_25290(class_10799.field_56883, TEXTURE_MAP_BACKGROUND, x, y, 0.0F, 0.0F, size, size, size, size);
      class_22 mapState = class_1806.method_7997(new class_9209(this.mapId), MeteorClient.mc.field_1687);
      if (mapState != null) {
         context.method_51448().pushMatrix();
         context.method_51448().translate((float)x, (float)y);
         context.method_51448().scale(scale, scale);
         context.method_51448().translate(8.0F, 8.0F);
         MeteorClient.mc.method_61965().method_62230(new class_9209(this.mapId), mapState, this.mapRenderState);
         context.method_70857(this.mapRenderState);
         context.method_51448().popMatrix();
      }
   }
}
