package meteordevelopment.meteorclient.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.Settings;
import net.minecraft.class_2172;
import net.minecraft.class_2960;
import org.jetbrains.annotations.NotNull;

public class SettingValueArgumentType implements ArgumentType<String> {
   private static final SettingValueArgumentType INSTANCE = new SettingValueArgumentType();

   public static SettingValueArgumentType create() {
      return INSTANCE;
   }

   public static String get(CommandContext<?> context) {
      return (String)context.getArgument("value", String.class);
   }

   private SettingValueArgumentType() {
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      String text = reader.getRemaining();
      reader.setCursor(reader.getTotalLength());
      return text;
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      Setting<?> setting;
      try {
         setting = SettingArgumentType.get(context);
      } catch (CommandSyntaxException var5) {
         return Suggestions.empty();
      }

      return suggest(builder, setting);
   }

   public static <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder, Settings settings) {
      Setting<?> setting;
      try {
         setting = SettingArgumentType.get(context, settings);
      } catch (CommandSyntaxException var5) {
         return Suggestions.empty();
      }

      return suggest(builder, setting);
   }

   public static CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, @NotNull Setting<?> setting) {
      Iterable<class_2960> identifiers = setting.getIdentifierSuggestions();
      return identifiers != null ? class_2172.method_9270(identifiers, builder) : class_2172.method_9265(setting.getSuggestions(), builder);
   }
}
