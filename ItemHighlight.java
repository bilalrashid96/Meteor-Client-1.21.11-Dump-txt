package meteordevelopment.meteorclient.systems.modules.render;

import java.util.List;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1792;
import net.minecraft.class_1799;

public class ItemHighlight extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<class_1792>> items;
   private final Setting<SettingColor> color;

   public ItemHighlight() {
      super(Categories.Render, "item-highlight", "Highlights selected items when in guis");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.items = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("items")).description("Items to highlight.")).build());
      this.color = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color")).description("The color to highlight the items with.")).defaultValue(new SettingColor(225, 25, 255, 50)).build());
   }

   public int getColor(class_1799 stack) {
      return stack != null && ((List)this.items.get()).contains(stack.method_7909()) && this.isActive() ? ((SettingColor)this.color.get()).getPacked() : -1;
   }
}
