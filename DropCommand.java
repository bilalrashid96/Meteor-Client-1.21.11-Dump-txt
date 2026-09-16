package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2172;
import net.minecraft.class_2287;
import net.minecraft.class_2561;
import net.minecraft.class_746;
import net.minecraft.class_9274;
import net.minecraft.class_1304.class_1305;

public class DropCommand extends Command {
   private static final SimpleCommandExceptionType NOT_SPECTATOR = new SimpleCommandExceptionType(class_2561.method_43470("Can't drop items while in spectator."));
   private static final SimpleCommandExceptionType NO_SUCH_ITEM = new SimpleCommandExceptionType(class_2561.method_43470("Could not find an item with that name!"));

   public DropCommand() {
      super("drop", "Automatically drops specified items.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("hand").executes((context) -> this.drop((player) -> player.method_7290(true))));
      builder.then(literal("offhand").executes((context) -> this.drop((player) -> InvUtils.drop().slotOffhand())));
      builder.then(literal("hotbar").executes((context) -> this.drop((player) -> {
            for(int i = 0; i < 9; ++i) {
               InvUtils.drop().slotHotbar(i);
            }

         })));
      builder.then(literal("inventory").executes((context) -> this.drop((player) -> {
            for(int i = 0; i < 27; ++i) {
               InvUtils.drop().slotMain(i);
            }

         })));
      builder.then(literal("all").executes((context) -> this.drop((player) -> {
            for(int i = 0; i < player.method_31548().method_5439(); ++i) {
               InvUtils.drop().slot(i);
            }

            if (!mc.field_1724.method_6079().method_7960()) {
               InvUtils.drop().slotOffhand();
            }

         })));
      builder.then(literal("armor").executes((context) -> this.drop((player) -> {
            for(class_1304 equipmentSlot : class_9274.field_49224) {
               if (equipmentSlot.method_5925() == class_1305.field_6178) {
                  InvUtils.drop().slotArmor(equipmentSlot.method_5927());
               }
            }

         })));
      builder.then(((RequiredArgumentBuilder)argument("item", class_2287.method_9776(REGISTRY_ACCESS)).executes((context) -> this.drop((player) -> this.dropItem(player, context, Integer.MAX_VALUE)))).then(argument("amount", IntegerArgumentType.integer(1)).executes((context) -> this.drop((player) -> {
            int amount = IntegerArgumentType.getInteger(context, "amount");
            this.dropItem(player, context, amount);
         }))));
   }

   private void dropItem(class_746 player, CommandContext<class_2172> context, int amount) throws CommandSyntaxException {
      class_1799 stack = class_2287.method_9777(context, "item").method_9781(1, false);
      if (stack != null && stack.method_7909() != class_1802.field_8162) {
         for(int i = 0; i < player.method_31548().method_5439() && amount > 0; ++i) {
            class_1799 invStack = player.method_31548().method_5438(i);
            if (!invStack.method_7960() && stack.method_7909() == invStack.method_7909()) {
               int dropCount = Math.min(amount, invStack.method_7947());
               if (dropCount == invStack.method_7947()) {
                  InvUtils.drop().slot(i);
               } else {
                  for(int j = 0; j < dropCount; ++j) {
                     InvUtils.dropOne().slot(i);
                  }
               }

               amount -= dropCount;
            }
         }

      } else {
         throw NO_SUCH_ITEM.create();
      }
   }

   private int drop(PlayerConsumer consumer) throws CommandSyntaxException {
      if (mc.field_1724.method_7325()) {
         throw NOT_SPECTATOR.create();
      } else {
         consumer.accept(mc.field_1724);
         return 1;
      }
   }

   @FunctionalInterface
   private interface PlayerConsumer {
      void accept(class_746 var1) throws CommandSyntaxException;
   }
}
