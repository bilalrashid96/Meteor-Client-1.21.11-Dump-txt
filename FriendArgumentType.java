package meteordevelopment.meteorclient.commands.arguments;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import net.minecraft.class_2172;

public class FriendArgumentType implements ArgumentType<String> {
   private static final FriendArgumentType INSTANCE = new FriendArgumentType();
   private static final Collection<String> EXAMPLES = List.of("seasnail8169", "MineGame159");

   public static FriendArgumentType create() {
      return INSTANCE;
   }

   public static Friend get(CommandContext<?> context) {
      return Friends.get().get((String)context.getArgument("friend", String.class));
   }

   private FriendArgumentType() {
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      return reader.readString();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9264(Streams.stream(Friends.get()).map(Friend::getName), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
