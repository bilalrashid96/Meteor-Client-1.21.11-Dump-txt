package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerArgumentType;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class SpectateCommand extends Command {
   private final StaticListener shiftListener = new StaticListener();

   public SpectateCommand() {
      super("spectate", "Allows you to spectate nearby players");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("reset").executes((context) -> {
         mc.method_1504(mc.field_1724);
         return 1;
      }));
      builder.then(argument("player", PlayerArgumentType.create()).executes((context) -> {
         mc.method_1504(PlayerArgumentType.get(context));
         mc.field_1724.method_7353(class_2561.method_43470("Sneak to un-spectate."), true);
         MeteorClient.EVENT_BUS.subscribe(this.shiftListener);
         return 1;
      }));
   }

   private static class StaticListener {
      @EventHandler
      private void onKey(KeyEvent event) {
         if (Input.isPressed(SpectateCommand.mc.field_1690.field_1832)) {
            SpectateCommand.mc.method_1504(SpectateCommand.mc.field_1724);
            event.cancel();
            MeteorClient.EVENT_BUS.unsubscribe(this);
         }

      }

      @EventHandler
      private void onMouse(MouseClickEvent event) {
         if (Input.isPressed(SpectateCommand.mc.field_1690.field_1832)) {
            SpectateCommand.mc.method_1504(SpectateCommand.mc.field_1724);
            event.cancel();
            MeteorClient.EVENT_BUS.unsubscribe(this);
         }

      }
   }
}
