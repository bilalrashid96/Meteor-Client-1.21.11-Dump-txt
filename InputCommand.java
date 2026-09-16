package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.KeyBindingAccessor;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.IEventBus;
import net.minecraft.class_1074;
import net.minecraft.class_2172;
import net.minecraft.class_304;

public class InputCommand extends Command {
   private static final List<KeypressHandler> activeHandlers = new ArrayList();
   private static final List<Pair<class_304, String>> holdKeys;
   private static final List<Pair<class_304, String>> pressKeys;

   public InputCommand() {
      super("input", "Keyboard input simulation.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      for(Pair<class_304, String> keyBinding : holdKeys) {
         builder.then(((LiteralArgumentBuilder)literal((String)keyBinding.getSecond()).executes((context) -> {
            activeHandlers.add(new KeypressHandler((class_304)keyBinding.getFirst(), 1));
            return 1;
         })).then(argument("ticks", IntegerArgumentType.integer(1)).executes((context) -> {
            activeHandlers.add(new KeypressHandler((class_304)keyBinding.getFirst(), (Integer)context.getArgument("ticks", Integer.class)));
            return 1;
         })));
      }

      for(Pair<class_304, String> keyBinding : pressKeys) {
         builder.then(literal((String)keyBinding.getSecond()).executes((context) -> {
            press((class_304)keyBinding.getFirst());
            return 1;
         }));
      }

      for(class_304 keyBinding : mc.field_1690.field_1852) {
         builder.then(literal(keyBinding.method_1431().substring(4)).executes((context) -> {
            press(keyBinding);
            return 1;
         }));
      }

      builder.then(literal("clear").executes((ctx) -> {
         if (activeHandlers.isEmpty()) {
            this.warning("No active keypress handlers.", new Object[0]);
         } else {
            this.info("Cleared all keypress handlers.", new Object[0]);
            List var10000 = activeHandlers;
            IEventBus var10001 = MeteorClient.EVENT_BUS;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::unsubscribe);
            activeHandlers.clear();
         }

         return 1;
      }));
      builder.then(literal("list").executes((ctx) -> {
         if (activeHandlers.isEmpty()) {
            this.warning("No active keypress handlers.", new Object[0]);
         } else {
            this.info("Active keypress handlers: ", new Object[0]);

            for(int i = 0; i < activeHandlers.size(); ++i) {
               KeypressHandler handler = (KeypressHandler)activeHandlers.get(i);
               this.info("(highlight)%d(default) - (highlight)%s %d(default) ticks left out of (highlight)%d(default).", new Object[]{i, class_1074.method_4662(handler.key.method_1431(), new Object[0]), handler.ticks, handler.totalTicks});
            }
         }

         return 1;
      }));
      builder.then(literal("remove").then(argument("index", IntegerArgumentType.integer(0)).executes((ctx) -> {
         int index = IntegerArgumentType.getInteger(ctx, "index");
         if (index >= activeHandlers.size()) {
            this.warning("Index out of range.", new Object[0]);
         } else {
            this.info("Removed keypress handler.", new Object[0]);
            MeteorClient.EVENT_BUS.unsubscribe(activeHandlers.get(index));
            activeHandlers.remove(index);
         }

         return 1;
      })));
   }

   private static void press(class_304 keyBinding) {
      KeyBindingAccessor accessor = (KeyBindingAccessor)keyBinding;
      accessor.meteor$setTimesPressed(accessor.meteor$getTimesPressed() + 1);
   }

   static {
      holdKeys = List.of(new Pair(mc.field_1690.field_1894, "forwards"), new Pair(mc.field_1690.field_1881, "backwards"), new Pair(mc.field_1690.field_1913, "left"), new Pair(mc.field_1690.field_1849, "right"), new Pair(mc.field_1690.field_1903, "jump"), new Pair(mc.field_1690.field_1832, "sneak"), new Pair(mc.field_1690.field_1867, "sprint"), new Pair(mc.field_1690.field_1904, "use"), new Pair(mc.field_1690.field_1886, "attack"));
      pressKeys = List.of(new Pair(mc.field_1690.field_1831, "swap"), new Pair(mc.field_1690.field_1869, "drop"));
   }

   private static class KeypressHandler {
      private final class_304 key;
      private final int totalTicks;
      private int ticks;

      public KeypressHandler(class_304 key, int ticks) {
         this.key = key;
         this.totalTicks = ticks;
         this.ticks = ticks;
         MeteorClient.EVENT_BUS.subscribe(this);
      }

      @EventHandler
      private void onTick(TickEvent.Post event) {
         if (this.ticks == this.totalTicks) {
            InputCommand.press(this.key);
         }

         if (this.ticks-- > 0) {
            this.key.method_23481(true);
         } else {
            this.key.method_23481(false);
            MeteorClient.EVENT_BUS.unsubscribe(this);
            InputCommand.activeHandlers.remove(this);
         }

      }
   }
}
