package meteordevelopment.meteorclient.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class ModuleArgumentType implements ArgumentType<Module> {
   private static final ModuleArgumentType INSTANCE = new ModuleArgumentType();
   private static final DynamicCommandExceptionType NO_SUCH_MODULE = new DynamicCommandExceptionType((name) -> class_2561.method_43470("Module with name " + String.valueOf(name) + " doesn't exist."));
   private static final Collection<String> EXAMPLES = (Collection)Modules.get().getAll().stream().limit(3L).map((module) -> module.name).collect(Collectors.toList());

   public static ModuleArgumentType create() {
      return INSTANCE;
   }

   public static Module get(CommandContext<?> context) {
      return (Module)context.getArgument("module", Module.class);
   }

   private ModuleArgumentType() {
   }

   public Module parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.readString();
      Module module = Modules.get().get(argument);
      if (module == null) {
         throw NO_SUCH_MODULE.create(argument);
      } else {
         return module;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9264(Modules.get().getAll().stream().map((module) -> module.name), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
