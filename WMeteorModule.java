package meteordevelopment.meteorclient.gui.themes.meteor.widgets;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.utils.AlignmentX;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.class_3532;

public class WMeteorModule extends WPressable implements MeteorWidget {
   private final Module module;
   private final String title;
   private double titleWidth;
   private double animationProgress1;
   private double animationProgress2;

   public WMeteorModule(Module module, String title) {
      this.module = module;
      this.title = title;
      this.tooltip = module.description;
      if (module.isActive()) {
         this.animationProgress1 = (double)1.0F;
         this.animationProgress2 = (double)1.0F;
      } else {
         this.animationProgress1 = (double)0.0F;
         this.animationProgress2 = (double)0.0F;
      }

   }

   public double pad() {
      return this.theme.scale((double)4.0F);
   }

   protected void onCalculateSize() {
      double pad = this.pad();
      if (this.titleWidth == (double)0.0F) {
         this.titleWidth = this.theme.textWidth(this.title);
      }

      this.width = pad + this.titleWidth + pad;
      this.height = pad + this.theme.textHeight() + pad;
   }

   protected void onPressed(int button) {
      if (button == 0) {
         this.module.toggle();
      } else if (button == 1) {
         MeteorClient.mc.method_1507(this.theme.moduleScreen(this.module));
      }

   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      MeteorGuiTheme theme = this.theme();
      double pad = this.pad();
      this.animationProgress1 += delta * (double)4.0F * (double)(!this.module.isActive() && !this.mouseOver ? -1 : 1);
      this.animationProgress1 = class_3532.method_15350(this.animationProgress1, (double)0.0F, (double)1.0F);
      this.animationProgress2 += delta * (double)6.0F * (double)(this.module.isActive() ? 1 : -1);
      this.animationProgress2 = class_3532.method_15350(this.animationProgress2, (double)0.0F, (double)1.0F);
      if (this.animationProgress1 > (double)0.0F) {
         renderer.quad(this.x, this.y, this.width * this.animationProgress1, this.height, theme.moduleBackground.get());
      }

      if (this.animationProgress2 > (double)0.0F) {
         renderer.quad(this.x, this.y + this.height * ((double)1.0F - this.animationProgress2), theme.scale((double)2.0F), this.height * this.animationProgress2, theme.accentColor.get());
      }

      double x = this.x + pad;
      double w = this.width - pad * (double)2.0F;
      if (theme.moduleAlignment.get() == AlignmentX.Center) {
         x += w / (double)2.0F - this.titleWidth / (double)2.0F;
      } else if (theme.moduleAlignment.get() == AlignmentX.Right) {
         x += w - this.titleWidth;
      }

      renderer.text(this.title, x, this.y + pad, theme.textColor.get(), false);
   }
}
