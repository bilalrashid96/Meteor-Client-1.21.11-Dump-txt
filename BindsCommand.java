package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_5250;

public class BindsCommand extends Command {
   public BindsCommand() {
      super("binds", "List of all bound modules.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.executes((context) -> {
         List<Module> modules = Modules.get().getAll().stream().filter((modulex) -> modulex.keybind.isSet()).toList();
         ChatUtils.info("--- Bound Modules ((highlight)%d(default)) ---", modules.size());

         for(Module module : modules) {
            class_2568 hoverEvent = new class_2568.class_10613(this.getTooltip(module));
            class_5250 text = class_2561.method_43470(module.title).method_27692(class_124.field_1068);
            text.method_10862(text.method_10866().method_10949(hoverEvent));
            class_5250 sep = class_2561.method_43470(" - ");
            sep.method_10862(sep.method_10866().method_10949(hoverEvent));
            text.method_10852(sep.method_27692(class_124.field_1080));
            class_5250 key = class_2561.method_43470(module.keybind.toString());
            key.method_10862(key.method_10866().method_10949(hoverEvent));
            text.method_10852(key.method_27692(class_124.field_1080));
            ChatUtils.sendMsg(text);
         }

         return 1;
      });
   }

   private class_5250 getTooltip(Module module) {
      class_5250 tooltip = class_2561.method_43470(Utils.nameToTitle(module.title)).method_27695(new class_124[]{class_124.field_1078, class_124.field_1067}).method_27693("\n\n");
      tooltip.method_10852(class_2561.method_43470(module.description).method_27692(class_124.field_1068));
      return tooltip;
   }
}
