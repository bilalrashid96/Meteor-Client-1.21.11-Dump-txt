package meteordevelopment.meteorclient.gui.utils;

import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.class_2487;

public class WindowConfig implements ISerializable<WindowConfig> {
   public boolean expanded = true;
   public double x = (double)-1.0F;
   public double y = (double)-1.0F;

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10556("expanded", this.expanded);
      tag.method_10549("x", this.x);
      tag.method_10549("y", this.y);
      return tag;
   }

   public WindowConfig fromTag(class_2487 tag) {
      tag.method_10577("expanded").ifPresent((bool) -> this.expanded = bool);
      tag.method_10574("x").ifPresent((x1) -> this.x = x1);
      tag.method_10574("y").ifPresent((y1) -> this.y = y1);
      return this;
   }
}
