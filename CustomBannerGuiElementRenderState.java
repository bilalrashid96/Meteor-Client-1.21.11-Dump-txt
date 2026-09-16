package meteordevelopment.meteorclient.utils.render;

import net.minecraft.class_10377;
import net.minecraft.class_11256;
import net.minecraft.class_1767;
import net.minecraft.class_8030;
import net.minecraft.class_9307;
import org.jetbrains.annotations.Nullable;

public record CustomBannerGuiElementRenderState(class_10377 flag, class_1767 baseColor, class_9307 resultBannerPatterns, int x1, int y1, int x2, int y2, @Nullable class_8030 scissorArea, @Nullable class_8030 bounds, float scale) implements class_11256 {
   public CustomBannerGuiElementRenderState(class_10377 bannerFlagBlockModel, class_1767 color, class_9307 bannerPatterns, int x1, int y1, int x2, int y2, @Nullable class_8030 scissorArea, float scale) {
      this(bannerFlagBlockModel, color, bannerPatterns, x1, y1, x2, y2, scissorArea, class_11256.method_71535(x1, y1, x2, y2, scissorArea), scale);
   }

   public int comp_4122() {
      return this.x1;
   }

   public int comp_4123() {
      return this.y1;
   }

   public int comp_4124() {
      return this.x2;
   }

   public int comp_4125() {
      return this.y2;
   }

   public @Nullable class_8030 comp_4128() {
      return this.scissorArea;
   }

   public @Nullable class_8030 comp_4274() {
      return this.bounds;
   }

   public float comp_4133() {
      return this.scale;
   }
}
