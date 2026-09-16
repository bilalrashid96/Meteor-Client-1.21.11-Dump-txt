package meteordevelopment.meteorclient.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1291;
import net.minecraft.class_1299;
import net.minecraft.class_1320;
import net.minecraft.class_1887;
import net.minecraft.class_2172;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3195;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_7924;

public class RegistryEntryReferenceArgumentType<T> implements ArgumentType<class_6880.class_6883<T>> {
   private static final RegistryEntryReferenceArgumentType<class_1887> ENCHANTMENT;
   private static final RegistryEntryReferenceArgumentType<class_1320> ENTITY_ATTRIBUTE;
   private static final RegistryEntryReferenceArgumentType<class_3195> STRUCTURE;
   private static final RegistryEntryReferenceArgumentType<class_1299<?>> ENTITY_TYPE;
   private static final RegistryEntryReferenceArgumentType<class_1291> STATUS_EFFECT;
   private static final Collection<String> EXAMPLES;
   public static final Dynamic2CommandExceptionType NOT_FOUND_EXCEPTION;
   public static final Dynamic3CommandExceptionType INVALID_TYPE_EXCEPTION;
   private final class_5321<? extends class_2378<T>> registryRef;

   private RegistryEntryReferenceArgumentType(class_5321<? extends class_2378<T>> registryRef) {
      this.registryRef = registryRef;
   }

   public static RegistryEntryReferenceArgumentType<class_1887> enchantment() {
      return ENCHANTMENT;
   }

   public static RegistryEntryReferenceArgumentType<class_1320> entityAttribute() {
      return ENTITY_ATTRIBUTE;
   }

   public static RegistryEntryReferenceArgumentType<class_3195> structure() {
      return STRUCTURE;
   }

   public static RegistryEntryReferenceArgumentType<class_1299<?>> entityType() {
      return ENTITY_TYPE;
   }

   public static RegistryEntryReferenceArgumentType<class_1291> statusEffect() {
      return STATUS_EFFECT;
   }

   public static class_6880.class_6883<class_1887> getEnchantment(CommandContext<?> context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, class_7924.field_41265);
   }

   public static class_6880.class_6883<class_1320> getEntityAttribute(CommandContext<?> context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, class_7924.field_41251);
   }

   public static class_6880.class_6883<class_3195> getStructure(CommandContext<?> context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, class_7924.field_41246);
   }

   public static class_6880.class_6883<class_1299<?>> getEntityType(CommandContext<?> context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, class_7924.field_41266);
   }

   public static class_6880.class_6883<class_1291> getStatusEffect(CommandContext<?> context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, class_7924.field_41208);
   }

   private static <T> class_6880.class_6883<T> getRegistryEntry(CommandContext<?> context, String name, class_5321<class_2378<T>> registryRef) throws CommandSyntaxException {
      class_6880.class_6883<T> reference = (class_6880.class_6883)context.getArgument(name, class_6880.class_6883.class);
      class_5321<?> registryKey = reference.method_40237();
      if (registryKey.method_31163(registryRef)) {
         return reference;
      } else {
         throw INVALID_TYPE_EXCEPTION.create(registryKey.method_29177(), registryKey.method_41185(), registryRef.method_29177());
      }
   }

   public class_6880.class_6883<T> parse(StringReader reader) throws CommandSyntaxException {
      class_2960 identifier = class_2960.method_12835(reader);
      class_5321<T> registryKey = class_5321.method_29179(this.registryRef, identifier);
      return (class_6880.class_6883)class_310.method_1551().method_1562().method_29091().method_30530(this.registryRef).method_46746(registryKey).orElseThrow(() -> NOT_FOUND_EXCEPTION.createWithContext(reader, identifier, this.registryRef.method_29177()));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9257(class_310.method_1551().method_1562().method_29091().method_30530(this.registryRef).method_46754().map(class_5321::method_29177), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      ENCHANTMENT = new RegistryEntryReferenceArgumentType<class_1887>(class_7924.field_41265);
      ENTITY_ATTRIBUTE = new RegistryEntryReferenceArgumentType<class_1320>(class_7924.field_41251);
      STRUCTURE = new RegistryEntryReferenceArgumentType<class_3195>(class_7924.field_41246);
      ENTITY_TYPE = new RegistryEntryReferenceArgumentType<class_1299<?>>(class_7924.field_41266);
      STATUS_EFFECT = new RegistryEntryReferenceArgumentType<class_1291>(class_7924.field_41208);
      EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
      NOT_FOUND_EXCEPTION = new Dynamic2CommandExceptionType((element, type) -> class_2561.method_54159("argument.resource.not_found", new Object[]{element, type}));
      INVALID_TYPE_EXCEPTION = new Dynamic3CommandExceptionType((element, type, expectedType) -> class_2561.method_54159("argument.resource.invalid_type", new Object[]{element, type, expectedType}));
   }
}
