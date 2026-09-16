package meteordevelopment.meteorclient.gui.tabs.builtin;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.class_437;

public class GuiTab extends Tab {
   public GuiTab() {
      super("GUI");
   }

   public TabScreen createScreen(GuiTheme theme) {
      return new GuiScreen(theme, this);
   }

   public boolean isScreen(class_437 screen) {
      return screen instanceof GuiScreen;
   }

   private static class GuiScreen extends WindowTabScreen {
      public GuiScreen(GuiTheme theme, Tab tab) {
         super(theme, tab);
         theme.settings.onActivated();
      }

      public void initWidgets() {
         WHorizontalList opts = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
         opts.add(this.theme.label("Theme:"));
         WDropdown<String> themeW = (WDropdown)opts.add(this.theme.dropdown(GuiThemes.getNames(), GuiThemes.get().name)).widget();
         themeW.action = () -> {
            GuiThemes.select(themeW.get());
            MeteorClient.mc.method_1507((class_437)null);
            this.tab.openScreen(GuiThemes.get());
         };
         WButton resetLayout = (WButton)opts.add(this.theme.button("Reset Layout")).expandX().widget();
         GuiTheme var10001 = this.theme;
         Objects.requireNonNull(var10001);
         resetLayout.action = var10001::clearWindowConfigs;
         WButton reset = (WButton)opts.add(this.theme.button("Reset Colors")).right().widget();
         reset.action = () -> {
            this.theme.settings.reset();
            MeteorClient.mc.method_1507((class_437)null);
            this.tab.openScreen(GuiThemes.get());
         };
         WButton copyButton = (WButton)opts.add(this.theme.button(GuiRenderer.COPY)).widget();
         copyButton.action = this::toClipboard;
         copyButton.tooltip = "Copy config";
         WButton pasteButton = (WButton)opts.add(this.theme.button(GuiRenderer.PASTE)).right().widget();
         pasteButton.action = this::fromClipboard;
         pasteButton.tooltip = "Paste config";
         this.add(this.theme.settings(this.theme.settings)).expandX();
      }

      public boolean toClipboard() {
         return NbtUtils.toClipboard(this.theme);
      }

      public boolean fromClipboard() {
         return NbtUtils.fromClipboard(this.theme);
      }
   }
}
