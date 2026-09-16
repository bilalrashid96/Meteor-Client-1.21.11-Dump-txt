package meteordevelopment.meteorclient.commands.arguments;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import meteordevelopment.meteorclient.systems.profiles.Profile;
import meteordevelopment.meteorclient.systems.profiles.Profiles;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class ProfileArgumentType implements ArgumentType<String> {
   private static final ProfileArgumentType INSTANCE = new ProfileArgumentType();
   private static final DynamicCommandExceptionType NO_SUCH_PROFILE = new DynamicCommandExceptionType((name) -> class_2561.method_43470("Profile with name " + String.valueOf(name) + " doesn't exist."));
   private static final Collection<String> EXAMPLES = List.of("pvp.meteorclient.com", "anarchy");

   public static ProfileArgumentType create() {
      return INSTANCE;
   }

   public static Profile get(CommandContext<?> context) {
      return Profiles.get().get((String)context.getArgument("profile", String.class));
   }

   private ProfileArgumentType() {
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.getRemaining();
      reader.setCursor(reader.getTotalLength());
      if (Profiles.get().get(argument) == null) {
         throw NO_SUCH_PROFILE.create(argument);
      } else {
         return argument;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9264(Streams.stream(Profiles.get()).map((profile) -> profile.name.get()), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
