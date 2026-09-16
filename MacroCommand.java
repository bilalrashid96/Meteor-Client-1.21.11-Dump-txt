package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.MacroArgumentType;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.macros.Macro;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2172;
import net.minecraft.class_2245;

public class MacroCommand extends Command {
   List<ScheduledMacro> scheduleQueue = new ArrayList();
   List<ScheduledMacro> scheduledMacros = new ArrayList();

   public MacroCommand() {
      super("macro", "Allows you to execute macros.");
      MeteorClient.EVENT_BUS.subscribe(this);
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)builder.then(((LiteralArgumentBuilder)literal("clear").executes((context) -> {
         if (this.scheduleQueue.isEmpty() && this.scheduledMacros.isEmpty()) {
            this.error("No macros are currently scheduled.", new Object[0]);
            return 1;
         } else {
            this.clearAll();
            this.info("Cleared all scheduled macros.", new Object[0]);
            return 1;
         }
      })).then(argument("macro", MacroArgumentType.create()).executes((context) -> {
         Macro macro = MacroArgumentType.get(context);
         if (!this.isScheduled(macro)) {
            this.error("This macro is not currently scheduled.", new Object[0]);
            return 1;
         } else {
            this.clear(macro);
            this.info("Cleared scheduled macro.", new Object[0]);
            return 1;
         }
      })))).then(((RequiredArgumentBuilder)argument("macro", MacroArgumentType.create()).executes((context) -> {
         Macro macro = MacroArgumentType.get(context);
         this.scheduleQueue.add(new ScheduledMacro(0, macro));
         return 1;
      })).then(argument("delay", class_2245.method_9489()).executes((context) -> {
         Macro macro = MacroArgumentType.get(context);
         this.scheduleQueue.add(new ScheduledMacro(IntegerArgumentType.getInteger(context, "delay"), macro));
         return 1;
      })));
   }

   public void clearAll() {
      this.scheduleQueue.clear();
      this.scheduledMacros.clear();
   }

   public boolean isScheduled(Macro macro) {
      return this.scheduleQueue.stream().anyMatch((element) -> element.macro == macro) || this.scheduledMacros.stream().anyMatch((element) -> element.macro == macro);
   }

   public void clear(Macro macro) {
      this.scheduleQueue.removeIf((scheduledMacro) -> scheduledMacro.macro == macro);
      this.scheduledMacros.removeIf((scheduledMacro) -> scheduledMacro.macro == macro);
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (!this.scheduleQueue.isEmpty()) {
         this.scheduledMacros.addAll(this.scheduleQueue);
         this.scheduleQueue.clear();
      }

      if (!this.scheduledMacros.isEmpty()) {
         this.runMacros();
      }

      this.scheduledMacros.forEach(ScheduledMacro::tick);
   }

   private void runMacros() {
      this.scheduledMacros.removeIf(ScheduledMacro::run);
   }
}
