package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.ToIntFunction;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.RegistryEntryReferenceArgumentType;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_1799;
import net.minecraft.class_1887;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_437;
import net.minecraft.class_490;
import net.minecraft.class_6880;
import net.minecraft.class_7924;

public class EnchantCommand extends Command {
   private static final SimpleCommandExceptionType NOT_IN_CREATIVE = new SimpleCommandExceptionType(class_2561.method_43470("You must be in creative mode to use this."));
   private static final SimpleCommandExceptionType NOT_HOLDING_ITEM = new SimpleCommandExceptionType(class_2561.method_43470("You need to hold some item to enchant."));

   public EnchantCommand() {
      super("enchant", "Enchants the item in your hand. REQUIRES Creative mode.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("one").then(((RequiredArgumentBuilder)argument("enchantment", RegistryEntryReferenceArgumentType.enchantment()).then(literal("level").then(argument("level", IntegerArgumentType.integer()).executes((context) -> {
         this.one(context, (enchantment) -> (Integer)context.getArgument("level", Integer.class));
         return 1;
      })))).then(literal("max").executes((context) -> {
         this.one(context, class_1887::method_8183);
         return 1;
      }))));
      builder.then(((LiteralArgumentBuilder)literal("all_possible").then(literal("level").then(argument("level", IntegerArgumentType.integer()).executes((context) -> {
         this.all(true, (enchantment) -> (Integer)context.getArgument("level", Integer.class));
         return 1;
      })))).then(literal("max").executes((context) -> {
         this.all(true, class_1887::method_8183);
         return 1;
      })));
      builder.then(((LiteralArgumentBuilder)literal("all").then(literal("level").then(argument("level", IntegerArgumentType.integer()).executes((context) -> {
         this.all(false, (enchantment) -> (Integer)context.getArgument("level", Integer.class));
         return 1;
      })))).then(literal("max").executes((context) -> {
         this.all(false, class_1887::method_8183);
         return 1;
      })));
      builder.then(literal("clear").executes((context) -> {
         class_1799 itemStack = this.tryGetItemStack();
         Utils.clearEnchantments(itemStack);
         this.syncItem();
         return 1;
      }));
      builder.then(literal("remove").then(argument("enchantment", RegistryEntryReferenceArgumentType.enchantment()).executes((context) -> {
         class_1799 itemStack = this.tryGetItemStack();
         class_6880.class_6883<class_1887> enchantment = RegistryEntryReferenceArgumentType.getEnchantment(context, "enchantment");
         Utils.removeEnchantment(itemStack, (class_1887)enchantment.comp_349());
         this.syncItem();
         return 1;
      })));
   }

   private void one(CommandContext<class_2172> context, ToIntFunction<class_1887> level) throws CommandSyntaxException {
      class_1799 itemStack = this.tryGetItemStack();
      class_6880.class_6883<class_1887> enchantment = RegistryEntryReferenceArgumentType.getEnchantment(context, "enchantment");
      Utils.addEnchantment(itemStack, enchantment, level.applyAsInt((class_1887)enchantment.comp_349()));
      this.syncItem();
   }

   private void all(boolean onlyPossible, ToIntFunction<class_1887> level) throws CommandSyntaxException {
      class_1799 itemStack = this.tryGetItemStack();
      mc.method_1562().method_29091().method_46759(class_7924.field_41265).ifPresent((registry) -> registry.method_42017().forEach((enchantment) -> {
            if (!onlyPossible || ((class_1887)enchantment.comp_349()).method_8192(itemStack)) {
               Utils.addEnchantment(itemStack, enchantment, level.applyAsInt((class_1887)enchantment.comp_349()));
            }

         }));
      this.syncItem();
   }

   private void syncItem() {
      mc.method_1507(new class_490(mc.field_1724));
      mc.method_1507((class_437)null);
   }

   private class_1799 tryGetItemStack() throws CommandSyntaxException {
      if (!mc.field_1724.method_68878()) {
         throw NOT_IN_CREATIVE.create();
      } else {
         class_1799 itemStack = this.getItemStack();
         if (itemStack == null) {
            throw NOT_HOLDING_ITEM.create();
         } else {
            return itemStack;
         }
      }
   }

   private class_1799 getItemStack() {
      class_1799 itemStack = mc.field_1724.method_6047();
      if (itemStack == null) {
         itemStack = mc.field_1724.method_6079();
      }

      return itemStack.method_7960() ? null : itemStack;
   }
}
