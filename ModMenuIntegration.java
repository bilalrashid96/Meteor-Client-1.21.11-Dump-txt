package meteordevelopment.meteorclient.modintegration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.screens.ModulesScreen;

public class ModMenuIntegration implements ModMenuApi {
   public ConfigScreenFactory<?> getModConfigScreenFactory() {
      return (screen) -> new ModulesScreen(GuiThemes.get());
   }
}
