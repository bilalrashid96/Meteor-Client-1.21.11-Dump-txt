package meteordevelopment.meteorclient.gui.widgets;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;

public abstract class WTopBar extends WHorizontalList {
   protected abstract Color getButtonColor(boolean var1, boolean var2);

   protected abstract Color getNameColor();

   public WTopBar() {
      this.spacing = (double)0.0F;
   }

   public void init() {
      for(Tab tab : Tabs.get()) {
         this.add(new WTopBarButton(tab));
      }

   }

   protected class WTopBarButton extends WPressable {
      private final Tab tab;

      public WTopBarButton(Tab tab) {
         this.tab = tab;
      }

      protected void onCalculateSize() {
         double pad = this.pad();
         this.width = pad + this.theme.textWidth(this.tab.name) + pad;
         this.height = pad + this.theme.textHeight() + pad;
      }

      protected void onPressed(int button) {
         class_437 screen = MeteorClient.mc.field_1755;
         if (!(screen instanceof TabScreen) || ((TabScreen)screen).tab != this.tab) {
            double mouseX = MeteorClient.mc.field_1729.method_1603();
            double mouseY = MeteorClient.mc.field_1729.method_1604();
            this.tab.openScreen(this.theme);
            GLFW.glfwSetCursorPos(MeteorClient.mc.method_22683().method_4490(), mouseX, mouseY);
         }

      }

      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         double pad = this.pad();
         Color color = WTopBar.this.getButtonColor(this.pressed || MeteorClient.mc.field_1755 instanceof TabScreen && ((TabScreen)MeteorClient.mc.field_1755).tab == this.tab, this.mouseOver);
         renderer.quad(this.x, this.y, this.width, this.height, color);
         renderer.text(this.tab.name, this.x + pad, this.y + pad, WTopBar.this.getNameColor(), false);
      }
   }
}
