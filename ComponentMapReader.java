package meteordevelopment.meteorclient.utils.misc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.class_2172;
import net.minecraft.class_2509;
import net.minecraft.class_2520;
import net.minecraft.class_2522;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7157;
import net.minecraft.class_7923;
import net.minecraft.class_9323;
import net.minecraft.class_9331;

public class ComponentMapReader {
   private static final DynamicCommandExceptionType UNKNOWN_COMPONENT_EXCEPTION = new DynamicCommandExceptionType((id) -> class_2561.method_54159("arguments.item.component.unknown", new Object[]{id}));
   private static final SimpleCommandExceptionType COMPONENT_EXPECTED_EXCEPTION = new SimpleCommandExceptionType(class_2561.method_43471("arguments.item.component.expected"));
   private static final DynamicCommandExceptionType REPEATED_COMPONENT_EXCEPTION = new DynamicCommandExceptionType((type) -> class_2561.method_54159("arguments.item.component.repeated", new Object[]{type}));
   private static final Dynamic2CommandExceptionType MALFORMED_COMPONENT_EXCEPTION = new Dynamic2CommandExceptionType((type, error) -> class_2561.method_54159("arguments.item.component.malformed", new Object[]{type, error}));
   private static final class_2522<class_2520> SNBT_READER;
   private final DynamicOps<class_2520> nbtOps;

   public ComponentMapReader(class_7157 commandRegistryAccess) {
      this.nbtOps = commandRegistryAccess.method_57093(class_2509.field_11560);
   }

   public class_9323 consume(StringReader reader) throws CommandSyntaxException {
      int cursor = reader.getCursor();

      try {
         return (new Reader(reader, this.nbtOps)).read();
      } catch (CommandSyntaxException e) {
         reader.setCursor(cursor);
         throw e;
      }
   }

   public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder) {
      StringReader stringReader = new StringReader(builder.getInput());
      stringReader.setCursor(builder.getStart());
      Reader reader = new Reader(stringReader, this.nbtOps);

      try {
         reader.read();
      } catch (CommandSyntaxException var5) {
      }

      return (CompletableFuture)reader.suggestor.apply(builder.createOffset(stringReader.getCursor()));
   }

   static {
      SNBT_READER = class_2522.method_68662(class_2509.field_11560);
   }

   private static class Reader {
      private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_DEFAULT = SuggestionsBuilder::buildFuture;
      private final StringReader reader;
      private final DynamicOps<class_2520> nbtOps;
      public Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestor = this::suggestBracket;

      public Reader(StringReader reader, DynamicOps<class_2520> nbtOps) {
         this.reader = reader;
         this.nbtOps = nbtOps;
      }

      public class_9323 read() throws CommandSyntaxException {
         class_9323.class_9324 builder = class_9323.method_57827();
         this.reader.expect('[');
         this.suggestor = this::suggestComponentType;
         Set<class_9331<?>> set = new ReferenceArraySet();

         while(this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            class_9331<?> dataComponentType = readComponentType(this.reader);
            if (!set.add(dataComponentType)) {
               throw ComponentMapReader.REPEATED_COMPONENT_EXCEPTION.create(dataComponentType);
            }

            this.suggestor = this::suggestEqual;
            this.reader.skipWhitespace();
            this.reader.expect('=');
            this.suggestor = SUGGEST_DEFAULT;
            this.reader.skipWhitespace();
            this.readComponentValue(this.reader, builder, dataComponentType);
            this.reader.skipWhitespace();
            this.suggestor = this::suggestEndOfComponent;
            if (!this.reader.canRead() || this.reader.peek() != ',') {
               break;
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestor = this::suggestComponentType;
            if (!this.reader.canRead()) {
               throw ComponentMapReader.COMPONENT_EXPECTED_EXCEPTION.createWithContext(this.reader);
            }
         }

         this.reader.expect(']');
         this.suggestor = SUGGEST_DEFAULT;
         return builder.method_57838();
      }

      public static class_9331<?> readComponentType(StringReader reader) throws CommandSyntaxException {
         if (!reader.canRead()) {
            throw ComponentMapReader.COMPONENT_EXPECTED_EXCEPTION.createWithContext(reader);
         } else {
            int i = reader.getCursor();
            class_2960 identifier = class_2960.method_12835(reader);
            class_9331<?> dataComponentType = (class_9331)class_7923.field_49658.method_63535(identifier);
            if (dataComponentType != null && !dataComponentType.method_57877()) {
               return dataComponentType;
            } else {
               reader.setCursor(i);
               throw ComponentMapReader.UNKNOWN_COMPONENT_EXCEPTION.createWithContext(reader, identifier);
            }
         }
      }

      private CompletableFuture<Suggestions> suggestComponentType(SuggestionsBuilder builder) {
         String string = builder.getRemaining().toLowerCase(Locale.ROOT);
         class_2172.method_9268(class_7923.field_49658.method_29722(), string, (entry) -> ((class_5321)entry.getKey()).method_29177(), (entry) -> {
            class_9331<?> dataComponentType = (class_9331)entry.getValue();
            if (dataComponentType.method_57875() != null) {
               class_2960 identifier = ((class_5321)entry.getKey()).method_29177();
               builder.suggest(identifier.toString() + "=");
            }

         });
         return builder.buildFuture();
      }

      private <T> void readComponentValue(StringReader reader, class_9323.class_9324 builder, class_9331<T> type) throws CommandSyntaxException {
         int i = reader.getCursor();
         class_2520 nbtElement = (class_2520)ComponentMapReader.SNBT_READER.method_67319(reader);
         DataResult<T> dataResult = type.method_57876().parse(this.nbtOps, nbtElement);
         builder.method_57840(type, dataResult.getOrThrow((error) -> {
            reader.setCursor(i);
            return ComponentMapReader.MALFORMED_COMPONENT_EXCEPTION.createWithContext(reader, type.toString(), error);
         }));
      }

      private CompletableFuture<Suggestions> suggestBracket(SuggestionsBuilder builder) {
         if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('['));
         }

         return builder.buildFuture();
      }

      private CompletableFuture<Suggestions> suggestEndOfComponent(SuggestionsBuilder builder) {
         if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf(','));
            builder.suggest(String.valueOf(']'));
         }

         return builder.buildFuture();
      }

      private CompletableFuture<Suggestions> suggestEqual(SuggestionsBuilder builder) {
         if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('='));
         }

         return builder.buildFuture();
      }
   }
}
