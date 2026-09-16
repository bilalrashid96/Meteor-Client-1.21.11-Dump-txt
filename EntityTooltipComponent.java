package meteordevelopment.meteorclient.utils.tooltip;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_10042;
import net.minecraft.class_1309;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5684;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityTooltipComponent implements MeteorTooltipData, class_5684 {
   protected final class_1309 entity;
   private static double spin;

   public EntityTooltipComponent(class_1309 entity) {
      this.entity = entity;
   }

   public class_5684 getComponent() {
      return this;
   }

   public int method_32661(class_327 textRenderer) {
      return 48;
   }

   public int method_32664(class_327 textRenderer) {
      return 64;
   }

   public void method_32666(class_327 textRenderer, int x, int y, int width, int height, class_332 context) {
      class_10042 state = (class_10042)MeteorClient.mc.method_1561().method_3953(this.entity).method_62425(this.entity, 1.0F);
      state.field_61820 = 15728880;
      state.field_61823.clear();
      state.field_61821 = 0;
      state.field_53446 = (float)(spin % (double)360.0F);
      state.field_53447 = 0.0F;
      state.field_53448 = 0.0F;
      x += (width - this.method_32664((class_327)null)) / 2;
      y += 4;
      width = this.method_32664((class_327)null);
      height = this.method_32661((class_327)null);
      float scale = (float)Math.max(width, height) / 2.0F * 1.25F;
      Vector3f translation = new Vector3f(0.0F, 0.1F, 0.0F);
      Quaternionf rotation = (new Quaternionf()).rotateZ((float)Math.PI);
      context.method_70856(state, scale, translation, rotation, (Quaternionf)null, x, y, x + width, y + height);
      spin += (double)(3.0F * MeteorClient.mc.method_61966().method_60636());
   }
}
