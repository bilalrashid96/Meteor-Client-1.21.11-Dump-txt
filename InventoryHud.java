package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2960;

public class InventoryHud extends HudElement {
   public static final HudElementInfo<InventoryHud> INFO;
   private static final class_2960 TEXTURE;
   private static final class_2960 TEXTURE_TRANSPARENT;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgScale;
   private final SettingGroup sgBackground;
   private final Setting<Boolean> containers;
   public final Setting<Boolean> customScale;
   public final Setting<Double> scale;
   private final Setting<Background> background;
   public final Setting<SettingColor> backgroundColor;
   private final class_1799[] containerItems;

   private InventoryHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgScale = this.settings.createGroup("Scale");
      this.sgBackground = this.settings.createGroup("Background");
      this.containers = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("containers")).description("Shows the contents of a container when holding them.")).defaultValue(false)).build());
      this.customScale = this.sgScale.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-scale")).description("Applies a custom scale to this hud element.")).defaultValue(false)).onChanged((aBoolean) -> this.calculateSize())).build());
      SettingGroup var10001 = this.sgScale;
      DoubleSetting.Builder var10002 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Custom scale.");
      Setting var10003 = this.customScale;
      Objects.requireNonNull(var10003);
      this.scale = var10001.add(((DoubleSetting.Builder)((DoubleSetting.Builder)var10002.visible(var10003::get)).defaultValue((double)2.0F).onChanged((aDouble) -> this.calculateSize())).min((double)0.5F).sliderRange((double)0.5F, (double)3.0F).build());
      this.background = this.sgBackground.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("background")).description("Background of inventory viewer.")).defaultValue(InventoryHud.Background.Texture)).onChanged((bg) -> this.calculateSize())).build());
      this.backgroundColor = this.sgBackground.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.")).visible(() -> this.background.get() != InventoryHud.Background.None)).defaultValue(new SettingColor(255, 255, 255)).build());
      this.containerItems = new class_1799[27];
      this.calculateSize();
   }

   public void render(HudRenderer renderer) {
      double x = (double)this.x;
      double y = (double)this.y;
      class_1799 container = this.getContainer();
      boolean hasContainer = (Boolean)this.containers.get() && container != null;
      if (hasContainer) {
         Utils.getItemsInContainerItem(container, this.containerItems);
      }

      Color drawColor = hasContainer ? Utils.getShulkerColor(container) : (Color)this.backgroundColor.get();
      if (this.background.get() != InventoryHud.Background.None) {
         this.drawBackground(renderer, (int)x, (int)y, drawColor);
      }

      if (MeteorClient.mc.field_1724 != null) {
         renderer.post(() -> {
            for(int row = 0; row < 3; ++row) {
               for(int i = 0; i < 9; ++i) {
                  int index = row * 9 + i;
                  class_1799 stack = hasContainer ? this.containerItems[index] : MeteorClient.mc.field_1724.method_31548().method_5438(index + 9);
                  if (stack != null) {
                     int itemX = this.background.get() == InventoryHud.Background.Texture ? (int)(x + (double)(8 + i * 18) * this.getScale()) : (int)(x + (double)(1 + i * 18) * this.getScale());
                     int itemY = this.background.get() == InventoryHud.Background.Texture ? (int)(y + (double)(7 + row * 18) * this.getScale()) : (int)(y + (double)(1 + row * 18) * this.getScale());
                     renderer.item(stack, itemX, itemY, (float)this.getScale(), true);
                  }
               }
            }

         });
      }
   }

   private void calculateSize() {
      this.setSize((double)(this.background.get()).width * this.getScale(), (double)(this.background.get()).height * this.getScale());
   }

   private void drawBackground(HudRenderer renderer, int x, int y, Color color) {
      int w = this.getWidth();
      int h = this.getHeight();
      switch (((Background)this.background.get()).ordinal()) {
         case 1:
         case 2:
            renderer.texture(this.background.get() == InventoryHud.Background.Texture ? TEXTURE : TEXTURE_TRANSPARENT, (double)x, (double)y, (double)w, (double)h, color);
            break;
         case 3:
            renderer.quad((double)x, (double)y, (double)w, (double)h, color);
      }

   }

   private class_1799 getContainer() {
      if (!this.isInEditor() && MeteorClient.mc.field_1724 != null) {
         class_1799 stack = MeteorClient.mc.field_1724.method_6079();
         if (!Utils.hasItems(stack) && stack.method_7909() != class_1802.field_8466) {
            stack = MeteorClient.mc.field_1724.method_6047();
            return !Utils.hasItems(stack) && stack.method_7909() != class_1802.field_8466 ? null : stack;
         } else {
            return stack;
         }
      } else {
         return null;
      }
   }

   private double getScale() {
      return (Boolean)this.customScale.get() ? (Double)this.scale.get() : (Double)this.scale.getDefaultValue();
   }

   static {
      INFO = new HudElementInfo<InventoryHud>(Hud.GROUP, "inventory", "Displays your inventory.", InventoryHud::new);
      TEXTURE = MeteorClient.identifier("textures/container.png");
      TEXTURE_TRANSPARENT = MeteorClient.identifier("textures/container-transparent.png");
   }

   public static enum Background {
      None(162, 54),
      Texture(176, 67),
      Outline(162, 54),
      Flat(162, 54);

      private final int width;
      private final int height;

      private Background(int width, int height) {
         this.width = width;
         this.height = height;
      }

      // $FF: synthetic method
      private static Background[] $values() {
         return new Background[]{None, Texture, Outline, Flat};
      }
   }
}
