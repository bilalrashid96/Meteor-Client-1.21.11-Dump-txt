package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_5250;

public class ModulesCommand extends Command {
   public ModulesCommand() {
      super("modules", "Displays a list of all modules.", "features");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.executes((context) -> {
         ChatUtils.info("--- Modules ((highlight)%d(default)) ---", Modules.get().getCount());
         Modules.loopCategories().forEach((category) -> {
            class_5250 categoryMessage = class_2561.method_43470("");
            Modules.get().getGroup(category).forEach((module) -> categoryMessage.method_10852(this.getModuleText(module)));
            ChatUtils.sendMsg(category.name, categoryMessage);
         });
         return 1;
      });
   }

   private class_5250 getModuleText(Module module) {
      class_5250 tooltip = class_2561.method_43470("");
      tooltip.method_10852(class_2561.method_43470(module.title).method_27695(new class_124[]{class_124.field_1078, class_124.field_1067})).method_27693("\n");
      tooltip.method_10852(class_2561.method_43470(module.name).method_27692(class_124.field_1080)).method_27693("\n\n");
      tooltip.method_10852(class_2561.method_43470(module.description).method_27692(class_124.field_1068));
      class_5250 finalModule = class_2561.method_43470(module.title);
      if (!module.isActive()) {
         finalModule.method_27692(class_124.field_1080);
      }

      if (!module.equals(Modules.get().getGroup(module.category).getLast())) {
         finalModule.method_10852(class_2561.method_43470(", ").method_27692(class_124.field_1080));
      }

      finalModule.method_10862(finalModule.method_10866().method_10949(new class_2568.class_10613(tooltip)));
      return finalModule;
   }
}
