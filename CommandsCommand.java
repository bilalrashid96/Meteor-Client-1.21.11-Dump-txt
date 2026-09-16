package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import net.minecraft.class_2558;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2583;
import net.minecraft.class_5250;

public class CommandsCommand extends Command {
   public CommandsCommand() {
      super("commands", "List of all commands.", "help");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.executes((context) -> {
         ChatUtils.info("--- Commands ((highlight)%d(default)) ---", Commands.COMMANDS.size());
         class_5250 commands = class_2561.method_43470("");
         Commands.COMMANDS.forEach((command) -> commands.method_10852(this.getCommandText(command)));
         ChatUtils.sendMsg(commands);
         return 1;
      });
   }

   private class_5250 getCommandText(Command command) {
      class_5250 tooltip = class_2561.method_43470("");
      tooltip.method_10852(class_2561.method_43470(Utils.nameToTitle(command.getName())).method_27695(new class_124[]{class_124.field_1078, class_124.field_1067})).method_27693("\n");
      String var10000 = Config.get().prefix.get();
      class_5250 aliases = class_2561.method_43470(var10000 + command.getName());
      if (!command.getAliases().isEmpty()) {
         aliases.method_27693(", ");

         for(String alias : command.getAliases()) {
            if (!alias.isEmpty()) {
               String var10001 = Config.get().prefix.get();
               aliases.method_27693(var10001 + alias);
               if (!alias.equals(command.getAliases().getLast())) {
                  aliases.method_27693(", ");
               }
            }
         }
      }

      tooltip.method_10852(aliases.method_27692(class_124.field_1080)).method_27693("\n\n");
      tooltip.method_10852(class_2561.method_43470(command.getDescription()).method_27692(class_124.field_1068));
      class_5250 text = class_2561.method_43470(Utils.nameToTitle(command.getName()));
      if (command != Commands.COMMANDS.getLast()) {
         text.method_10852(class_2561.method_43470(", ").method_27692(class_124.field_1080));
      }

      class_2583 var7 = text.method_10866().method_10949(new class_2568.class_10613(tooltip));
      String var10004 = Config.get().prefix.get();
      text.method_10862(var7.method_10958(new class_2558.class_10610(var10004 + command.getName())));
      return text;
   }
}
