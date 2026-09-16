package meteordevelopment.meteorclient.gui.tabs;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import net.minecraft.class_437;

public abstract class Tab {
   public final String name;

   public Tab(String name) {
      this.name = name;
   }

   public void openScreen(GuiTheme theme) {
      TabScreen screen = this.createScreen(theme);
      screen.addDirect(theme.topBar()).top().centerX();
      MeteorClient.mc.method_1507(screen);
   }

   public abstract TabScreen createScreen(GuiTheme var1);

   public abstract boolean isScreen(class_437 var1);
}
