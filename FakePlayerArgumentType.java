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
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerManager;
import net.minecraft.class_2172;

public class FakePlayerArgumentType implements ArgumentType<String> {
   private static final FakePlayerArgumentType INSTANCE = new FakePlayerArgumentType();
   private static final Collection<String> EXAMPLES = List.of("seasnail8169", "MineGame159");

   public static FakePlayerArgumentType create() {
      return INSTANCE;
   }

   public static FakePlayerEntity get(CommandContext<?> context) {
      return FakePlayerManager.get((String)context.getArgument("fp", String.class));
   }

   private FakePlayerArgumentType() {
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      return reader.readString();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9264(FakePlayerManager.stream().map((fakePlayerEntity) -> fakePlayerEntity.method_5477().getString()), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
