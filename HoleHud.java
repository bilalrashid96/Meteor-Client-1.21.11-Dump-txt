package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.WorldRendererAccessor;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2350;
import net.minecraft.class_3532;

public class HoleHud extends HudElement {
   public static final HudElementInfo<HoleHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgScale;
   private final SettingGroup sgBackground;
   public final Setting<List<class_2248>> safe;
   public final Setting<Boolean> customScale;
   public final Setting<Double> scale;
   public final Setting<Boolean> background;
   public final Setting<SettingColor> backgroundColor;
   private final Color BG_COLOR;
   private final Color OL_COLOR;

   public HoleHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgScale = this.settings.createGroup("Scale");
      this.sgBackground = this.settings.createGroup("Background");
      this.safe = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("safe-blocks")).description("Which blocks to consider safe.")).defaultValue(class_2246.field_10540, class_2246.field_9987, class_2246.field_22423, class_2246.field_22108).build());
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
      this.BG_COLOR = new Color(255, 25, 25, 100);
      this.OL_COLOR = new Color(255, 25, 25, 255);
      this.calculateSize();
   }

   private void calculateSize() {
      this.setSize((double)(48.0F * this.getScale()), (double)(48.0F * this.getScale()));
   }

   public void render(HudRenderer renderer) {
      renderer.post(() -> {
         this.drawBlock(renderer, this.get(HoleHud.Facing.Left), (double)this.x, (double)((float)this.y + 16.0F * this.getScale()));
         this.drawBlock(renderer, this.get(HoleHud.Facing.Front), (double)((float)this.x + 16.0F * this.getScale()), (double)this.y);
         this.drawBlock(renderer, this.get(HoleHud.Facing.Right), (double)((float)this.x + 32.0F * this.getScale()), (double)((float)this.y + 16.0F * this.getScale()));
         this.drawBlock(renderer, this.get(HoleHud.Facing.Back), (double)((float)this.x + 16.0F * this.getScale()), (double)((float)this.y + 32.0F * this.getScale()));
      });
      if ((Boolean)this.background.get()) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), this.backgroundColor.get());
      }

   }

   private class_2350 get(Facing dir) {
      return this.isInEditor() ? class_2350.field_11033 : class_2350.method_10150((double)class_3532.method_15393(MeteorClient.mc.field_1724.method_36454() + (float)dir.offset));
   }

   private void drawBlock(HudRenderer renderer, class_2350 dir, double x, double y) {
      class_2248 block = dir == class_2350.field_11033 ? class_2246.field_10540 : MeteorClient.mc.field_1687.method_8320(MeteorClient.mc.field_1724.method_24515().method_10093(dir)).method_26204();
      if (((List)this.safe.get()).contains(block)) {
         renderer.item(block.method_8389().method_7854(), (int)x, (int)y, this.getScale(), false);
         if (dir != class_2350.field_11033) {
            ((WorldRendererAccessor)MeteorClient.mc.field_1769).meteor$getBlockBreakingInfos().values().forEach((info) -> {
               if (info.method_13991().equals(MeteorClient.mc.field_1724.method_24515().method_10093(dir))) {
                  this.renderBreaking(renderer, x, y, (double)((float)info.method_13988() / 9.0F));
               }

            });
         }
      }
   }

   private void renderBreaking(HudRenderer renderer, double x, double y, double percent) {
      renderer.quad(x, y, (double)16.0F * percent * (double)this.getScale(), (double)(16.0F * this.getScale()), this.BG_COLOR);
      renderer.quad(x, y, (double)(16.0F * this.getScale()), (double)(1.0F * this.getScale()), this.OL_COLOR);
      renderer.quad(x, y + (double)(15.0F * this.getScale()), (double)(16.0F * this.getScale()), (double)(1.0F * this.getScale()), this.OL_COLOR);
      renderer.quad(x, y, (double)(1.0F * this.getScale()), (double)(16.0F * this.getScale()), this.OL_COLOR);
      renderer.quad(x + (double)(15.0F * this.getScale()), y, (double)(1.0F * this.getScale()), (double)(16.0F * this.getScale()), this.OL_COLOR);
   }

   private float getScale() {
      return (Boolean)this.customScale.get() ? ((Double)this.scale.get()).floatValue() : ((Double)this.scale.getDefaultValue()).floatValue();
   }

   static {
      INFO = new HudElementInfo<HoleHud>(Hud.GROUP, "hole", "Displays information about the hole you are standing in.", HoleHud::new);
   }

   private static enum Facing {
      Left(-90),
      Right(90),
      Front(0),
      Back(180);

      public final int offset;

      private Facing(int offset) {
         this.offset = offset;
      }

      // $FF: synthetic method
      private static Facing[] $values() {
         return new Facing[]{Left, Right, Front, Back};
      }
   }
}
