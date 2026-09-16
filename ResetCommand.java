package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.ModuleArgumentType;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_2172;

public class ResetCommand extends Command {
   public ResetCommand() {
      super("reset", "Resets specified settings.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(((LiteralArgumentBuilder)literal("settings").then(argument("module", ModuleArgumentType.create()).executes((context) -> {
         Module module = (Module)context.getArgument("module", Module.class);
         module.settings.forEach((group) -> group.forEach(Setting::reset));
         module.info("Reset all settings.");
         return 1;
      }))).then(literal("all").executes((context) -> {
         Modules.get().getAll().forEach((module) -> module.settings.forEach((group) -> group.forEach(Setting::reset)));
         ChatUtils.infoPrefix("Modules", "Reset all module settings");
         return 1;
      })))).then(literal("gui").executes((context) -> {
         GuiThemes.get().clearWindowConfigs();
         GuiThemes.get().settings.reset();
         ChatUtils.info("Reset all GUI settings.");
         return 1;
      }))).then(((LiteralArgumentBuilder)literal("bind").then(argument("module", ModuleArgumentType.create()).executes((context) -> {
         Module module = (Module)context.getArgument("module", Module.class);
         module.keybind.reset();
         module.info("Reset bind.");
         return 1;
      }))).then(literal("all").executes((context) -> {
         Modules.get().getAll().forEach((module) -> module.keybind.reset());
         ChatUtils.infoPrefix("Modules", "Reset all binds.");
         return 1;
      })))).then(literal("hud").executes((context) -> {
         Hud.get().resetToDefaultElements();
         ChatUtils.infoPrefix("HUD", "Reset all elements.");
         return 1;
      }));
   }
}
