package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
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
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_1802;

public class ArmorHud extends HudElement {
   public static final HudElementInfo<ArmorHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgDurability;
   private final SettingGroup sgScale;
   private final SettingGroup sgBackground;
   private final Setting<Orientation> orientation;
   private final Setting<Boolean> flipOrder;
   private final Setting<Boolean> showEmpty;
   private final Setting<Durability> durability;
   private final Setting<SettingColor> durabilityColor;
   private final Setting<Boolean> durabilityShadow;
   private final Setting<Boolean> customScale;
   private final Setting<Double> scale;
   private final Setting<Boolean> background;
   private final Setting<SettingColor> backgroundColor;

   public ArmorHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgDurability = this.settings.createGroup("Durability");
      this.sgScale = this.settings.createGroup("Scale");
      this.sgBackground = this.settings.createGroup("Background");
      this.orientation = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("orientation")).description("How to display armor.")).defaultValue(ArmorHud.Orientation.Horizontal)).onChanged((val) -> this.calculateSize())).build());
      this.flipOrder = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("flip-order")).description("Flips the order of armor items.")).defaultValue(true)).build());
      this.showEmpty = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-empty")).description("Renders barrier icons for empty slots.")).defaultValue(false)).build());
      this.durability = this.sgDurability.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("durability")).description("How to display armor durability.")).defaultValue(ArmorHud.Durability.Bar)).onChanged((durability1) -> this.calculateSize())).build());
      this.durabilityColor = this.sgDurability.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("durability-color")).description("Color of the text.")).visible(() -> this.durability.get() == ArmorHud.Durability.Total || this.durability.get() == ArmorHud.Durability.Percentage)).defaultValue(new SettingColor()).build());
      this.durabilityShadow = this.sgDurability.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("durability-shadow")).description("Text shadow.")).visible(() -> this.durability.get() == ArmorHud.Durability.Total || this.durability.get() == ArmorHud.Durability.Percentage)).defaultValue(true)).build());
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
      switch (((Orientation)this.orientation.get()).ordinal()) {
         case 0 -> this.setSize((double)(72.0F * this.getScale()), (double)(16.0F * this.getScale()));
         case 1 -> this.setSize((double)(16.0F * this.getScale()), (double)(72.0F * this.getScale()));
      }

   }

   public void render(HudRenderer renderer) {
      int emptySlots = 0;
      class_1799[] armor = (Boolean)this.flipOrder.get() ? new class_1799[]{this.getItem(class_1304.field_6169), this.getItem(class_1304.field_6174), this.getItem(class_1304.field_6172), this.getItem(class_1304.field_6166)} : new class_1799[]{this.getItem(class_1304.field_6166), this.getItem(class_1304.field_6172), this.getItem(class_1304.field_6174), this.getItem(class_1304.field_6169)};

      for(class_1799 stack : armor) {
         if (stack.method_7960()) {
            ++emptySlots;
         }
      }

      if ((Boolean)this.background.get() && emptySlots < 4) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), this.backgroundColor.get());
      }

      renderer.post(() -> {
         double x = (double)this.x;
         double y = (double)this.y;

         for(int position = 0; position < 4; ++position) {
            class_1799 itemStack = armor[position];
            double armorX;
            double armorY;
            if (this.orientation.get() == ArmorHud.Orientation.Vertical) {
               armorX = x;
               armorY = y + (double)((float)(position * 18) * this.getScale());
            } else {
               armorX = x + (double)((float)(position * 18) * this.getScale());
               armorY = y;
            }

            renderer.item(itemStack, (int)armorX, (int)armorY, this.getScale(), itemStack.method_7963() && this.durability.get() == ArmorHud.Durability.Bar);
            if (itemStack.method_7963() && this.durability.get() != ArmorHud.Durability.Bar && this.durability.get() != ArmorHud.Durability.None) {
               String var10000;
               switch (((Durability)this.durability.get()).ordinal()) {
                  case 2 -> var10000 = Integer.toString(itemStack.method_7936() - itemStack.method_7919());
                  case 3 -> var10000 = Integer.toString(Math.round((float)(itemStack.method_7936() - itemStack.method_7919()) * 100.0F / (float)itemStack.method_7936()));
                  default -> var10000 = "err";
               }

               String message = var10000;
               double messageWidth = renderer.textWidth(message);
               if (this.orientation.get() == ArmorHud.Orientation.Vertical) {
                  armorX = x + (double)(8.0F * this.getScale()) - messageWidth / (double)2.0F;
                  armorY = y + (double)((float)(18 * position) * this.getScale()) + ((double)(18.0F * this.getScale()) - renderer.textHeight());
               } else {
                  armorX = x + (double)((float)(18 * position) * this.getScale()) + (double)(8.0F * this.getScale()) - messageWidth / (double)2.0F;
                  armorY = y + ((double)this.getHeight() - renderer.textHeight());
               }

               TextRenderer.get().render(message, armorX, armorY, this.durabilityColor.get(), (Boolean)this.durabilityShadow.get());
            }
         }

      });
   }

   private class_1799 getItem(class_1304 slot) {
      if (this.isInEditor()) {
         class_1799 var10000;
         switch (slot.method_5927()) {
            case 1 -> var10000 = class_1802.field_22029.method_7854();
            case 2 -> var10000 = class_1802.field_22028.method_7854();
            case 3 -> var10000 = class_1802.field_22027.method_7854();
            default -> var10000 = class_1802.field_22030.method_7854();
         }

         return var10000;
      } else {
         class_1799 stack = MeteorClient.mc.field_1724.method_6118(slot);
         return stack.method_7960() && (Boolean)this.showEmpty.get() ? class_1802.field_8077.method_7854() : stack;
      }
   }

   private float getScale() {
      return (Boolean)this.customScale.get() ? ((Double)this.scale.get()).floatValue() : ((Double)this.scale.getDefaultValue()).floatValue();
   }

   static {
      INFO = new HudElementInfo<ArmorHud>(Hud.GROUP, "armor", "Displays your armor.", ArmorHud::new);
   }

   public static enum Durability {
      None,
      Bar,
      Total,
      Percentage;

      // $FF: synthetic method
      private static Durability[] $values() {
         return new Durability[]{None, Bar, Total, Percentage};
      }
   }

   public static enum Orientation {
      Horizontal,
      Vertical;

      // $FF: synthetic method
      private static Orientation[] $values() {
         return new Orientation[]{Horizontal, Vertical};
      }
   }
}
