package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.commands.arguments.CommandArgumentType;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_5250;

public class HelpCommand extends Command {
   public HelpCommand() {
      super("help", "Shows you what a command does.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(argument("command", CommandArgumentType.create()).executes((context) -> {
         this.showHelp(CommandArgumentType.get(context));
         return 1;
      }));
      builder.executes((context) -> {
         this.showHelp(this);
         return 1;
      });
   }

   private void showHelp(Command cmd) {
      class_5250 msg = class_2561.method_43470("");
      msg.method_10852(class_2561.method_43470("Help for ").method_27692(class_124.field_1080).method_10852(class_2561.method_43470(cmd.getName()).method_27692(class_124.field_1054)));
      msg.method_10852(class_2561.method_43470("\n ")).method_10852(class_2561.method_43470("Description: ").method_27692(class_124.field_1080).method_10852(class_2561.method_43470(cmd.getDescription()).method_27692(class_124.field_1068)));
      if (!cmd.getAliases().isEmpty()) {
         msg.method_10852(class_2561.method_43470("\n ")).method_10852(class_2561.method_43470("Aliases: ").method_27692(class_124.field_1080));
         msg.method_10852(class_2561.method_43470(String.join(", ", cmd.getAliases())).method_27692(class_124.field_1075));
      }

      msg.method_10852(this.getUsageText(cmd));
      ChatUtils.sendMsg(msg);
   }

   private class_5250 getUsageText(Command cmd) {
      class_2172 source = mc.method_1562().method_2875();
      CommandNode<class_2172> root = Commands.DISPATCHER.getRoot();
      CommandNode<class_2172> node = root.getChild(cmd.getName());
      class_5250 usagesText = class_2561.method_43470("");
      if (node != null) {
         Map<CommandNode<class_2172>, String> usages = Commands.DISPATCHER.getSmartUsage(node, source);

         for(String usage : usages.values()) {
            usagesText.method_10852(class_2561.method_43470("\n " + String.valueOf(cmd) + " ").method_27692(class_124.field_1060)).method_10852(class_2561.method_43470(usage).method_27692(class_124.field_1060));
         }
      }

      if (usagesText.getString().isEmpty()) {
         usagesText.method_10852(class_2561.method_43470("\n " + String.valueOf(cmd)).method_27692(class_124.field_1060));
      }

      return class_2561.method_43470("\n Usage:").method_27692(class_124.field_1080).method_10852(usagesText);
   }
}
