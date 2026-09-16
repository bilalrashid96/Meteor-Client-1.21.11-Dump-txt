package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerListEntryArgumentType;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import net.minecraft.class_2558;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import net.minecraft.class_640;

public class NameHistoryCommand extends Command {
   public NameHistoryCommand() {
      super("name-history", "Provides a list of a players previous names from the laby.net api.", "history", "names");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(argument("player", PlayerListEntryArgumentType.create()).executes((context) -> {
         MeteorExecutor.execute(() -> {
            class_640 lookUpTarget = PlayerListEntryArgumentType.get(context);
            UUID uuid = lookUpTarget.method_2966().id();
            NameHistory history = (NameHistory)Http.get("https://laby.net/api/v2/user/" + String.valueOf(uuid) + "/get-profile").exceptionHandler((e) -> this.error("There was an error fetching that users name history.", new Object[0])).sendJson(NameHistory.class);
            if (history != null) {
               if (history.username_history == null || history.username_history.length == 0) {
                  this.error("There was an error fetching that users name history.", new Object[0]);
               }

               String name = lookUpTarget.method_2966().name();
               class_5250 initial = class_2561.method_43470(name);
               initial.method_10852(class_2561.method_43470(name.endsWith("s") ? "'" : "'s"));
               Color nameColor = PlayerUtils.getPlayerColor(mc.field_1687.method_18470(uuid), Utils.WHITE);
               initial.method_10862(initial.method_10866().method_27703(class_5251.method_27717(nameColor.getPacked())).method_10958(new class_2558.class_10608(URI.create("https://laby.net/@" + name))).method_10949(new class_2568.class_10613(class_2561.method_43470("View on laby.net").method_27692(class_124.field_1054).method_27692(class_124.field_1056))));
               this.info(initial.method_10852(class_2561.method_43470(" Username History:").method_27692(class_124.field_1080)));

               for(Name entry : history.username_history) {
                  class_5250 nameText = class_2561.method_43470(entry.name);
                  nameText.method_27692(class_124.field_1075);
                  if (entry.changed_at != null && entry.changed_at.getTime() != 0L) {
                     class_5250 changed = class_2561.method_43470("Changed at: ");
                     changed.method_27692(class_124.field_1080);
                     DateFormat formatter = new SimpleDateFormat("hh:mm:ss, dd/MM/yyyy");
                     changed.method_10852(class_2561.method_43470(formatter.format(entry.changed_at)).method_27692(class_124.field_1068));
                     nameText.method_10862(nameText.method_10866().method_10949(new class_2568.class_10613(changed)));
                  }

                  if (!entry.accurate) {
                     class_5250 text = class_2561.method_43470("*").method_27692(class_124.field_1068);
                     text.method_10862(text.method_10866().method_10949(new class_2568.class_10613(class_2561.method_43470("This name history entry is not accurate according to laby.net"))));
                     nameText.method_10852(text);
                  }

                  ChatUtils.sendMsg(nameText);
               }

            }
         });
         return 1;
      }));
   }

   private static class NameHistory {
      public Name[] username_history;
   }

   private static class Name {
      public String name;
      public Date changed_at;
      public boolean accurate;
   }
}
