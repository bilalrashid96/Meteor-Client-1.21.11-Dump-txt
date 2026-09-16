package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.ComponentMapArgumentType;
import meteordevelopment.meteorclient.utils.misc.text.MeteorClickEvent;
import net.minecraft.class_124;
import net.minecraft.class_1799;
import net.minecraft.class_2172;
import net.minecraft.class_2203;
import net.minecraft.class_2378;
import net.minecraft.class_2512;
import net.minecraft.class_2520;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2583;
import net.minecraft.class_2873;
import net.minecraft.class_3162;
import net.minecraft.class_3169;
import net.minecraft.class_3902;
import net.minecraft.class_5250;
import net.minecraft.class_5321;
import net.minecraft.class_7079;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_9323;
import net.minecraft.class_9326;
import net.minecraft.class_9331;
import net.minecraft.class_9335;
import net.minecraft.class_9336;
import net.minecraft.class_2203.class_2209;

public class NbtCommand extends Command {
   private static final DynamicCommandExceptionType MALFORMED_ITEM_EXCEPTION = new DynamicCommandExceptionType((error) -> class_2561.method_54159("arguments.item.malformed", new Object[]{error}));
   private final class_2561 copyButton;

   public NbtCommand() {
      super("nbt", "Modifies NBT data for an item, example: .nbt add {display:{Name:'{\"text\":\"$cRed Name\"}'}}");
      this.copyButton = class_2561.method_43470("NBT").method_10862(class_2583.field_24360.method_27706(class_124.field_1073).method_10958(new MeteorClickEvent(this.toString(new String[]{"copy"}))).method_10949(new class_2568.class_10613(class_2561.method_43470("Copy the NBT data to your clipboard."))));
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("add").then(argument("component", ComponentMapArgumentType.componentMap(REGISTRY_ACCESS)).executes((ctx) -> {
         class_1799 stack = mc.field_1724.method_31548().method_7391();
         if (this.validBasic(stack)) {
            class_9323 itemComponents = stack.method_57353();
            class_9323 newComponents = ComponentMapArgumentType.getComponentMap(ctx, "component");
            class_9323 testComponents = class_9323.method_59771(itemComponents, newComponents);
            DataResult<class_3902> dataResult = class_1799.method_59691(testComponents);
            DynamicCommandExceptionType var10001 = MALFORMED_ITEM_EXCEPTION;
            Objects.requireNonNull(var10001);
            dataResult.getOrThrow(var10001::create);
            stack.method_57365(testComponents);
            this.setStack(stack);
         }

         return 1;
      })));
      builder.then(literal("set").then(argument("component", ComponentMapArgumentType.componentMap(REGISTRY_ACCESS)).executes((ctx) -> {
         class_1799 stack = mc.field_1724.method_31548().method_7391();
         if (this.validBasic(stack)) {
            class_9323 components = ComponentMapArgumentType.getComponentMap(ctx, "component");
            class_9335 stackComponents = (class_9335)stack.method_57353();
            DataResult<class_3902> dataResult = class_1799.method_59691(components);
            DynamicCommandExceptionType var10001 = MALFORMED_ITEM_EXCEPTION;
            Objects.requireNonNull(var10001);
            dataResult.getOrThrow(var10001::create);
            class_9326.class_9327 changesBuilder = class_9326.method_57841();
            Set<class_9331<?>> types = stackComponents.method_57831();

            for(class_9336<?> entry : components) {
               changesBuilder.method_57855(entry);
               types.remove(entry.comp_2443());
            }

            for(class_9331<?> type : types) {
               changesBuilder.method_57853(type);
            }

            stackComponents.method_57936(changesBuilder.method_57852());
            this.setStack(stack);
         }

         return 1;
      })));
      builder.then(literal("remove").then(((RequiredArgumentBuilder)argument("component", class_7079.method_41224(class_7924.field_49659)).executes((ctx) -> {
         class_1799 stack = mc.field_1724.method_31548().method_7391();
         if (this.validBasic(stack)) {
            class_5321<class_9331<?>> componentTypeKey = (class_5321)ctx.getArgument("component", class_5321.class);
            class_9331<?> componentType = (class_9331)class_7923.field_49658.method_29107(componentTypeKey);
            class_9335 components = (class_9335)stack.method_57353();
            components.method_57936(class_9326.method_57841().method_57853(componentType).method_57852());
            this.setStack(stack);
         }

         return 1;
      })).suggests((ctx, suggestionsBuilder) -> {
         class_1799 stack = mc.field_1724.method_31548().method_7391();
         if (stack != class_1799.field_8037) {
            class_9323 components = stack.method_57353();
            String remaining = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
            Stream var10000 = components.method_57831().stream();
            class_2378 var10001 = class_7923.field_49658;
            Objects.requireNonNull(var10001);
            class_2172.method_9268(var10000.map(var10001::method_47983).toList(), remaining, (entry) -> entry.method_40230().isPresent() ? ((class_5321)entry.method_40230().get()).method_29177() : null, (entry) -> {
               class_9331<?> dataComponentType = (class_9331)entry.comp_349();
               if (dataComponentType.method_57875() != null && entry.method_40230().isPresent()) {
                  suggestionsBuilder.suggest(((class_5321)entry.method_40230().get()).method_29177().toString());
               }

            });
         }

         return suggestionsBuilder.buildFuture();
      })));
      builder.then(literal("get").executes((context) -> {
         class_3162 dataCommandObject = new class_3169(mc.field_1724);
         class_2203.class_2209 handPath = class_2209.method_58472("SelectedItem");
         class_5250 text = class_2561.method_43473().method_10852(this.copyButton);

         try {
            List<class_2520> nbtElement = handPath.method_9366(dataCommandObject.method_13881());
            if (!nbtElement.isEmpty()) {
               text.method_27693(" ").method_10852(class_2512.method_32270((class_2520)nbtElement.getFirst()));
            }
         } catch (CommandSyntaxException var6) {
            text.method_27693("{}");
         }

         this.info(text);
         return 1;
      }));
      builder.then(literal("copy").executes((context) -> {
         class_3162 dataCommandObject = new class_3169(mc.field_1724);
         class_2203.class_2209 handPath = class_2209.method_58472("SelectedItem");
         class_5250 text = class_2561.method_43473().method_10852(this.copyButton);
         String nbt = "{}";

         try {
            List<class_2520> nbtElement = handPath.method_9366(dataCommandObject.method_13881());
            if (!nbtElement.isEmpty()) {
               text.method_27693(" ").method_10852(class_2512.method_32270((class_2520)nbtElement.getFirst()));
               nbt = ((class_2520)nbtElement.getFirst()).toString();
            }
         } catch (CommandSyntaxException var7) {
            text.method_27693("{}");
         }

         mc.field_1774.method_1455(nbt);
         text.method_27693(" data copied!");
         this.info(text);
         return 1;
      }));
      builder.then(literal("count").then(argument("count", IntegerArgumentType.integer(-127, 127)).executes((context) -> {
         class_1799 stack = mc.field_1724.method_31548().method_7391();
         if (this.validBasic(stack)) {
            int count = IntegerArgumentType.getInteger(context, "count");
            stack.method_7939(count);
            this.setStack(stack);
            this.info("Set mainhand stack count to %s.", new Object[]{count});
         }

         return 1;
      })));
   }

   private void setStack(class_1799 stack) {
      mc.field_1724.field_3944.method_52787(new class_2873(36 + mc.field_1724.method_31548().method_67532(), stack));
   }

   private boolean validBasic(class_1799 stack) {
      if (!mc.field_1724.method_31549().field_7477) {
         this.error("Creative mode only.", new Object[0]);
         return false;
      } else if (stack == class_1799.field_8037) {
         this.error("You must hold an item in your main hand.", new Object[0]);
         return false;
      } else {
         return true;
      }
   }
}
