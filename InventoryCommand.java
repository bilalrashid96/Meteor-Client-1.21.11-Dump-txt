package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerArgumentType;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_2172;
import net.minecraft.class_490;

public class InventoryCommand extends Command {
   public InventoryCommand() {
      super("inventory", "Allows you to see parts of another player's inventory.", "inv", "invsee");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(argument("player", PlayerArgumentType.create()).executes((context) -> {
         Utils.screenToOpen = new class_490(PlayerArgumentType.get(context));
         return 1;
      }));
   }
}
