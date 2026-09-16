package meteordevelopment.meteorclient.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import meteordevelopment.meteorclient.utils.misc.ComponentMapReader;
import net.minecraft.class_2172;
import net.minecraft.class_7157;
import net.minecraft.class_9323;

public class ComponentMapArgumentType implements ArgumentType<class_9323> {
   private static final Collection<String> EXAMPLES = List.of("{foo=bar}");
   private final ComponentMapReader reader;

   public ComponentMapArgumentType(class_7157 commandRegistryAccess) {
      this.reader = new ComponentMapReader(commandRegistryAccess);
   }

   public static ComponentMapArgumentType componentMap(class_7157 commandRegistryAccess) {
      return new ComponentMapArgumentType(commandRegistryAccess);
   }

   public static <S extends class_2172> class_9323 getComponentMap(CommandContext<S> context, String name) {
      return (class_9323)context.getArgument(name, class_9323.class);
   }

   public class_9323 parse(StringReader reader) throws CommandSyntaxException {
      return this.reader.consume(reader);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return this.reader.getSuggestions(builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
