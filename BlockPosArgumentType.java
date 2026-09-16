package meteordevelopment.meteorclient.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2170;
import net.minecraft.class_2172;
import net.minecraft.class_2277;
import net.minecraft.class_2278;
import net.minecraft.class_2338;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_3532;
import net.minecraft.class_638;
import net.minecraft.class_2172.class_2173;
import net.minecraft.class_2183.class_2184;

public class BlockPosArgumentType implements ArgumentType<PosArgument> {
   private static final BlockPosArgumentType INSTANCE = new BlockPosArgumentType();
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
   public static final SimpleCommandExceptionType UNLOADED_EXCEPTION = new SimpleCommandExceptionType(class_2561.method_43471("argument.pos.unloaded"));
   public static final SimpleCommandExceptionType OUT_OF_WORLD_EXCEPTION = new SimpleCommandExceptionType(class_2561.method_43471("argument.pos.outofworld"));
   public static final SimpleCommandExceptionType OUT_OF_BOUNDS_EXCEPTION = new SimpleCommandExceptionType(class_2561.method_43471("argument.pos.outofbounds"));

   private BlockPosArgumentType() {
   }

   public static BlockPosArgumentType blockPos() {
      return INSTANCE;
   }

   public static <S> class_2338 getLoadedBlockPos(CommandContext<S> context, String name) throws CommandSyntaxException {
      class_638 clientLevel = MeteorClient.mc.field_1687;
      return getLoadedBlockPos(context, clientLevel, name);
   }

   public static <S> class_2338 getLoadedBlockPos(CommandContext<S> context, class_638 level, String name) throws CommandSyntaxException {
      class_2338 blockPos = getBlockPos(context, name);
      class_1923 chunkPos = new class_1923(blockPos);
      if (!level.method_2935().method_12123(chunkPos.field_9181, chunkPos.field_9180)) {
         throw UNLOADED_EXCEPTION.create();
      } else if (!level.method_24794(blockPos)) {
         throw OUT_OF_WORLD_EXCEPTION.create();
      } else {
         return blockPos;
      }
   }

   public static <S> class_2338 getBlockPos(CommandContext<S> context, String name) {
      return ((PosArgument)context.getArgument(name, PosArgument.class)).getBlockPos(context.getSource());
   }

   public static <S> class_2338 getValidBlockPos(CommandContext<S> context, String name) throws CommandSyntaxException {
      class_2338 blockPos = getBlockPos(context, name);
      if (!class_1937.method_25953(blockPos)) {
         throw OUT_OF_BOUNDS_EXCEPTION.create();
      } else {
         return blockPos;
      }
   }

   public PosArgument parse(StringReader stringReader) throws CommandSyntaxException {
      return (PosArgument)(stringReader.canRead() && stringReader.peek() == '^' ? BlockPosArgumentType.LookingPosArgument.parse(stringReader) : BlockPosArgumentType.DefaultPosArgument.parse(stringReader));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      if (!(context.getSource() instanceof class_2172)) {
         return Suggestions.empty();
      } else {
         String string = builder.getRemaining();
         Collection<class_2172.class_2173> collection;
         if (!string.isEmpty() && string.charAt(0) == '^') {
            collection = Collections.singleton(class_2173.field_9834);
         } else {
            collection = ((class_2172)context.getSource()).method_17771();
         }

         return class_2172.method_9260(string, collection, builder, class_2170.method_9238(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public interface PosArgument {
      <S> class_243 getPosition(S var1);

      <S> class_241 getRotation(S var1);

      default <S> class_2338 getBlockPos(S source) {
         return class_2338.method_49638(this.getPosition(source));
      }

      boolean isXRelative();

      boolean isYRelative();

      boolean isZRelative();
   }

   public static class DefaultPosArgument implements PosArgument {
      private final class_2278 x;
      private final class_2278 y;
      private final class_2278 z;

      public DefaultPosArgument(class_2278 x, class_2278 y, class_2278 z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }

      public <S> class_243 getPosition(S source) {
         class_243 vec3 = MeteorClient.mc.field_1724.method_73189();
         return new class_243(this.x.method_9740(vec3.field_1352), this.y.method_9740(vec3.field_1351), this.z.method_9740(vec3.field_1350));
      }

      public <S> class_241 getRotation(S source) {
         class_241 vec2 = MeteorClient.mc.field_1724.method_5802();
         return new class_241((float)this.x.method_9740((double)vec2.field_1343), (float)this.y.method_9740((double)vec2.field_1342));
      }

      public boolean isXRelative() {
         return this.x.method_9741();
      }

      public boolean isYRelative() {
         return this.y.method_9741();
      }

      public boolean isZRelative() {
         return this.z.method_9741();
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof DefaultPosArgument)) {
            return false;
         } else {
            DefaultPosArgument defaultPosArgument = (DefaultPosArgument)o;
            return this.x.equals(defaultPosArgument.x) && this.y.equals(defaultPosArgument.y) && this.z.equals(defaultPosArgument.z);
         }
      }

      public static DefaultPosArgument parse(StringReader reader) throws CommandSyntaxException {
         int cursor = reader.getCursor();
         class_2278 worldCoordinate = class_2278.method_9739(reader);
         if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            class_2278 worldCoordinate2 = class_2278.method_9739(reader);
            if (reader.canRead() && reader.peek() == ' ') {
               reader.skip();
               class_2278 worldCoordinate3 = class_2278.method_9739(reader);
               return new DefaultPosArgument(worldCoordinate, worldCoordinate2, worldCoordinate3);
            }
         }

         reader.setCursor(cursor);
         throw class_2277.field_10755.createWithContext(reader);
      }

      public static DefaultPosArgument parse(StringReader reader, boolean centerIntegers) throws CommandSyntaxException {
         int cursor = reader.getCursor();
         class_2278 worldCoordinate = class_2278.method_9743(reader, centerIntegers);
         if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            class_2278 worldCoordinate2 = class_2278.method_9743(reader, false);
            if (reader.canRead() && reader.peek() == ' ') {
               reader.skip();
               class_2278 worldCoordinate3 = class_2278.method_9743(reader, centerIntegers);
               return new DefaultPosArgument(worldCoordinate, worldCoordinate2, worldCoordinate3);
            }
         }

         reader.setCursor(cursor);
         throw class_2277.field_10755.createWithContext(reader);
      }

      public static DefaultPosArgument absolute(double x, double y, double z) {
         return new DefaultPosArgument(new class_2278(false, x), new class_2278(false, y), new class_2278(false, z));
      }

      public static DefaultPosArgument absolute(class_241 vec) {
         return new DefaultPosArgument(new class_2278(false, (double)vec.field_1343), new class_2278(false, (double)vec.field_1342), new class_2278(true, (double)0.0F));
      }

      public static DefaultPosArgument current() {
         return new DefaultPosArgument(new class_2278(true, (double)0.0F), new class_2278(true, (double)0.0F), new class_2278(true, (double)0.0F));
      }

      public int hashCode() {
         int i = this.x.hashCode();
         i = 31 * i + this.y.hashCode();
         return 31 * i + this.z.hashCode();
      }
   }

   public static class LookingPosArgument implements PosArgument {
      private final double x;
      private final double y;
      private final double z;

      public LookingPosArgument(double x, double y, double z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }

      public <S> class_243 getPosition(S source) {
         class_241 vec2 = MeteorClient.mc.field_1724.method_5802();
         class_243 vec3 = class_2184.field_9853.method_9302(MeteorClient.mc.field_1724);
         float f = class_3532.method_15362((double)((vec2.field_1342 + 90.0F) * ((float)Math.PI / 180F)));
         float g = class_3532.method_15374((double)((vec2.field_1342 + 90.0F) * ((float)Math.PI / 180F)));
         float h = class_3532.method_15362((double)(-vec2.field_1343 * ((float)Math.PI / 180F)));
         float i = class_3532.method_15374((double)(-vec2.field_1343 * ((float)Math.PI / 180F)));
         float j = class_3532.method_15362((double)((-vec2.field_1343 + 90.0F) * ((float)Math.PI / 180F)));
         float k = class_3532.method_15374((double)((-vec2.field_1343 + 90.0F) * ((float)Math.PI / 180F)));
         class_243 vec32 = new class_243((double)(f * h), (double)i, (double)(g * h));
         class_243 vec33 = new class_243((double)(f * j), (double)k, (double)(g * j));
         class_243 vec34 = vec32.method_1036(vec33).method_1021((double)-1.0F);
         double d = vec32.field_1352 * this.z + vec33.field_1352 * this.y + vec34.field_1352 * this.x;
         double e = vec32.field_1351 * this.z + vec33.field_1351 * this.y + vec34.field_1351 * this.x;
         double l = vec32.field_1350 * this.z + vec33.field_1350 * this.y + vec34.field_1350 * this.x;
         return new class_243(vec3.field_1352 + d, vec3.field_1351 + e, vec3.field_1350 + l);
      }

      public <S> class_241 getRotation(S source) {
         return class_241.field_1340;
      }

      public boolean isXRelative() {
         return true;
      }

      public boolean isYRelative() {
         return true;
      }

      public boolean isZRelative() {
         return true;
      }

      public static LookingPosArgument parse(StringReader reader) throws CommandSyntaxException {
         int cursor = reader.getCursor();
         double d = readCoordinate(reader, cursor);
         if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            double e = readCoordinate(reader, cursor);
            if (reader.canRead() && reader.peek() == ' ') {
               reader.skip();
               double f = readCoordinate(reader, cursor);
               return new LookingPosArgument(d, e, f);
            } else {
               reader.setCursor(cursor);
               throw class_2277.field_10755.createWithContext(reader);
            }
         } else {
            reader.setCursor(cursor);
            throw class_2277.field_10755.createWithContext(reader);
         }
      }

      private static double readCoordinate(StringReader reader, int startingCursorPos) throws CommandSyntaxException {
         if (!reader.canRead()) {
            throw class_2278.field_10759.createWithContext(reader);
         } else if (reader.peek() != '^') {
            reader.setCursor(startingCursorPos);
            throw class_2277.field_10757.createWithContext(reader);
         } else {
            reader.skip();
            return reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : (double)0.0F;
         }
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof LookingPosArgument)) {
            return false;
         } else {
            LookingPosArgument lookingPosArgument = (LookingPosArgument)o;
            return this.x == lookingPosArgument.x && this.y == lookingPosArgument.y && this.z == lookingPosArgument.z;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.x, this.y, this.z});
      }
   }
}
