package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.ModuleArgumentType;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_2172;

public class BindCommand extends Command {
   public BindCommand() {
      super("bind", "Binds a specified module to the next pressed key.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(argument("module", ModuleArgumentType.create()).executes((context) -> {
         Module module = (Module)context.getArgument("module", Module.class);
         Modules.get().setModuleToBind(module);
         Modules.get().awaitKeyRelease();
         module.info("Press a key to bind the module to.");
         return 1;
      }));
   }
}
