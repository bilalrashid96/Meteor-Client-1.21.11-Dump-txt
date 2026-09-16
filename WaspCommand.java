package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerArgumentType;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.AutoWasp;
import net.minecraft.class_1657;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class WaspCommand extends Command {
   private static final SimpleCommandExceptionType CANT_WASP_SELF = new SimpleCommandExceptionType(class_2561.method_43470("You cannot target yourself!"));

   public WaspCommand() {
      super("wasp", "Sets the auto wasp target.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      AutoWasp wasp = (AutoWasp)Modules.get().get(AutoWasp.class);
      builder.then(literal("reset").executes((context) -> {
         wasp.disable();
         return 1;
      }));
      builder.then(argument("player", PlayerArgumentType.create()).executes((context) -> {
         class_1657 player = PlayerArgumentType.get(context);
         if (player == mc.field_1724) {
            throw CANT_WASP_SELF.create();
         } else {
            wasp.target = player;
            wasp.enable();
            this.info(player.method_5477().getString() + " set as target.", new Object[0]);
            return 1;
         }
      }));
   }
}
