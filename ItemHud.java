package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.Objects;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.ItemSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;

public class ItemHud extends HudElement {
   public static final HudElementInfo<ItemHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgScale;
   private final SettingGroup sgBackground;
   private final Setting<class_1792> item;
   private final Setting<NoneMode> noneMode;
   public final Setting<Boolean> customScale;
   public final Setting<Double> scale;
   public final Setting<Boolean> background;
   public final Setting<SettingColor> backgroundColor;

   private ItemHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgScale = this.settings.createGroup("Scale");
      this.sgBackground = this.settings.createGroup("Background");
      this.item = this.sgGeneral.add(((ItemSetting.Builder)((ItemSetting.Builder)((ItemSetting.Builder)(new ItemSetting.Builder()).name("item")).description("Item to display")).defaultValue(class_1802.field_8288)).build());
      this.noneMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("none-mode")).description("How to render the item when you don't have the specified item in your inventory.")).defaultValue(ItemHud.NoneMode.HideCount)).build());
      this.customScale = this.sgScale.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-scale")).description("Applies a custom scale to this hud element.")).defaultValue(false)).onChanged((aBoolean) -> this.calculateSize())).build());
      SettingGroup var10001 = this.sgScale;
      DoubleSetting.Builder var10002 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Custom scale.");
      Setting var10003 = this.customScale;
      Objects.requireNonNull(var10003);
      this.scale = var10001.add(((DoubleSetting.Builder)((DoubleSetting.Builder)var10002.visible(var10003::get)).defaultValue((double)2.0F).onChanged((aDouble) -> this.calculateSize())).min((double)0.5F).sliderRange((double)0.5F, (double)3.0F).build());
      this.background = this.sgBackground.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      var10001 = this.sgBackground;
      ColorSetting.Builder var2 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var2.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
      this.calculateSize();
   }

   private void calculateSize() {
      this.setSize((double)(17.0F * this.getScale()), (double)(17.0F * this.getScale()));
   }

   public void render(HudRenderer renderer) {
      class_1799 itemStack = new class_1799((class_1935)this.item.get(), InvUtils.find(this.item.get()).count());
      if (this.noneMode.get() == ItemHud.NoneMode.HideItem && itemStack.method_7960()) {
         if (this.isInEditor()) {
            renderer.line((double)this.x, (double)this.y, (double)(this.x + this.getWidth()), (double)(this.y + this.getHeight()), Color.GRAY);
            renderer.line((double)this.x, (double)(this.y + this.getHeight()), (double)(this.x + this.getWidth()), (double)this.y, Color.GRAY);
         }
      } else {
         renderer.post(() -> this.render(renderer, itemStack, this.x, this.y));
      }

      if ((Boolean)this.background.get()) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), this.backgroundColor.get());
      }

   }

   private void render(HudRenderer renderer, class_1799 itemStack, int x, int y) {
      if (this.noneMode.get() == ItemHud.NoneMode.HideItem) {
         renderer.item(itemStack, x, y, this.getScale(), true);
      } else {
         String countOverride = null;
         boolean resetToZero = false;
         if (itemStack.method_7960()) {
            if (this.noneMode.get() == ItemHud.NoneMode.ShowCount) {
               countOverride = "0";
            }

            itemStack.method_7939(1);
            resetToZero = true;
         }

         renderer.item(itemStack, x, y, this.getScale(), true, countOverride);
         if (resetToZero) {
            itemStack.method_7939(0);
         }

      }
   }

   private float getScale() {
      return (Boolean)this.customScale.get() ? ((Double)this.scale.get()).floatValue() : ((Double)this.scale.getDefaultValue()).floatValue();
   }

   static {
      INFO = new HudElementInfo<ItemHud>(Hud.GROUP, "item", "Displays the item count.", ItemHud::new);
   }

   public static enum NoneMode {
      HideItem,
      HideCount,
      ShowCount;

      public String toString() {
         String var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = "Hide Item";
            case 1 -> var10000 = "Hide Count";
            case 2 -> var10000 = "Show Count";
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static NoneMode[] $values() {
         return new NoneMode[]{HideItem, HideCount, ShowCount};
      }
   }
}
