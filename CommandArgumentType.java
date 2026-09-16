package meteordevelopment.meteorclient.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class CommandArgumentType implements ArgumentType<Command> {
   private static final CommandArgumentType INSTANCE = new CommandArgumentType();
   private static final DynamicCommandExceptionType NO_SUCH_COMMAND = new DynamicCommandExceptionType((name) -> class_2561.method_43470("Command with name " + String.valueOf(name) + " doesn't exist."));
   private static final Collection<String> EXAMPLES;

   private CommandArgumentType() {
   }

   public static CommandArgumentType create() {
      return INSTANCE;
   }

   public static Command get(CommandContext<?> context) {
      return (Command)context.getArgument("command", Command.class);
   }

   public Command parse(StringReader reader) throws CommandSyntaxException {
      String name = reader.readString();
      Command command = Commands.get(name);
      if (command == null) {
         for(Command c : Commands.COMMANDS) {
            for(String alias : c.getAliases()) {
               if (alias.equals(name)) {
                  command = c;
                  break;
               }
            }

            if (command != null) {
               break;
            }
         }
      }

      if (command == null) {
         throw NO_SUCH_COMMAND.create(name);
      } else {
         return command;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      Set<String> suggestions = new LinkedHashSet();

      for(Command c : Commands.COMMANDS) {
         suggestions.add(c.getName());
         suggestions.addAll(c.getAliases());
      }

      return class_2172.method_9265(suggestions, builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      EXAMPLES = (Collection)Commands.COMMANDS.stream().limit(3L).map(Command::getName).collect(Collectors.toList());
   }
}
