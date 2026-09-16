package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_2661;

public class DisconnectCommand extends Command {
   public DisconnectCommand() {
      super("disconnect", "Disconnect from the server", "dc");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.executes((context) -> {
         mc.field_1724.field_3944.method_52781(new class_2661(class_2561.method_43470("%s[%sDisconnectCommand%s] Disconnected by user.".formatted(class_124.field_1080, class_124.field_1078, class_124.field_1080))));
         return 1;
      });
      builder.then(argument("reason", StringArgumentType.greedyString()).executes((context) -> {
         mc.field_1724.field_3944.method_52781(new class_2661(class_2561.method_43470(StringArgumentType.getString(context, "reason"))));
         return 1;
      }));
   }
}
